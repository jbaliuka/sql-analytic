package com.github.sql.analytic.transform.policy;

import java.util.ArrayList;
import java.util.List;

import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.expression.Parenthesis;
import com.github.sql.analytic.expression.operators.conditional.AndExpression;
import com.github.sql.analytic.expression.operators.conditional.OrExpression;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.statement.select.PlainSelect;
import com.github.sql.analytic.statement.select.SelectItem;
import com.github.sql.analytic.statement.select.WithItem;
import com.github.sql.analytic.transform.ExpressionTransform;
import com.github.sql.analytic.transform.FromItemTransform;
import com.github.sql.analytic.transform.NewValue;
import com.github.sql.analytic.transform.SelectTransform;
import com.github.sql.analytic.transform.StatementTransform;

public class SelectPolicy extends SelectTransform {

	private Policy statementTransform; 
	private Table toTable ;
	private List<Table> fromTables = new ArrayList<Table>();
	private List<CreatePolicy> appliedPolicies = new ArrayList<CreatePolicy>();
	private String action;
	private boolean checkColumns;	
	private List<NewValue> newValues;



	public SelectPolicy(String action,boolean checkColumns,List<NewValue> newValues,Policy statementTransform) {
		super(statementTransform);
		this.statementTransform = statementTransform;
		this.action = action;
		this.checkColumns = checkColumns;
		this.newValues = newValues;
		
	}

	

	public Policy getStatementTransform() {
		return statementTransform;
	}

	@Override
	protected FromItemTransform createFromItemTransform() {		
		return new PolicyFromItemTransfrom(this);
	}

	public Policy getPolicyTransform(){
		return statementTransform;
	}

	public void addFrom(Table table) {		
		statementTransform.getTables().add(table);
		fromTables.add(table);
	}

	@Override
	public void visit(WithItem item){
		statementTransform.getWithItems().add(item);
	}

	public List<CreatePolicy> getAppliedPolicies() {
		return appliedPolicies;
	}

	@Override
	public void visit(PlainSelect plainSelect) {	
		super.visit(plainSelect);		
	};


	private Expression or(Expression expression1,Expression expression2){

		OrExpression newExpression = new OrExpression();

		Parenthesis rprt = new Parenthesis();
		newExpression.setRightExpression(rprt);
		rprt.setExpression(statementTransform.transform(expression1));	

		Parenthesis lprt = new Parenthesis();
		newExpression.setLeftExpression(lprt);			
		lprt.setExpression(expression2);

		return newExpression;

	}

	private Expression and(Expression expression1,Expression expression2){

		AndExpression newExpression = new AndExpression();

		Parenthesis rprt = new Parenthesis();
		newExpression.setRightExpression(rprt);
		rprt.setExpression(statementTransform.transform(expression1));	

		Parenthesis lprt = new Parenthesis();
		newExpression.setLeftExpression(lprt);			
		lprt.setExpression(expression2);

		return newExpression;

	}

	protected List<SelectItem> transformSelectItems(List<SelectItem> list) {

		List<SelectItem> newList = super.transformSelectItems(list);

		if(checkColumns && statementTransform.isCheckColumns()){
			for(SelectItem item : list){			
				item.accept(new ColumnsPolicy(this));
			}
		}

		return newList;
	}

	@Override
	protected Expression transformWhere(Expression expression) {

		Expression filter = getPolicyFilter();		
		if(expression != null && filter != null){
			return and(statementTransform.transform(expression),filter);
		}else if (expression != null && filter == null){
			return statementTransform.transform(expression);
		}
		else {
			return filter;
		}
	}

	private Expression getPolicyFilter() {

		Expression filter = null;
		List<Table> tables = getAllTables();
		for(Table table: tables){
			Expression tableFilter = getTablePolicyFilter(table);
			if(tableFilter != null){
				if(filter != null){
					filter = and(filter,tableFilter);
				}else {
					filter = tableFilter;
				}
			}
		}

		return filter;
	}



	protected List<Table> getAllTables() {
		
		List<Table> tables = new ArrayList<Table>(fromTables);
		if(toTable != null){
			tables.add(toTable);
		}
		return tables;
	}

	private Expression getTablePolicyFilter(Table table) {

		Expression filter = null;
		List<CreatePolicy> list = statementTransform.findTablePolicies(action, table);		
		boolean oldValues = false;
		
		for( Table next : fromTables ){
			if(table.getWholeTableName().equalsIgnoreCase(next.getWholeTableName())){
				oldValues = true;
				break;
			}
		}
		
		if(oldValues){
			for(CreatePolicy policy: list){
				if(policy.getUsing() != null){
					if(filter == null){
						filter = getUsing(table,policy.getUsing());
					}else {
						filter = or(filter,getUsing(table,policy.getUsing()));
					}
				}
			}
		}

		if( newValues != null ){
			Expression checkFilter = null;		
			for(CreatePolicy policy: list){
				if(policy.getUsing() != null){
					if(checkFilter == null){
						checkFilter = getCheckNewValues(table,policy.getUsing());						
					}else {						
						checkFilter = or(checkFilter,getCheckNewValues(table,policy.getUsing()));						
					}
				}
				if(policy.getCheck() != null){
					if(checkFilter == null){
						checkFilter = getCheckNewValues(table,policy.getCheck());						
					}else {						
						checkFilter = or(checkFilter,getCheckNewValues(table,policy.getCheck()));						
					}
				}
			}

			if(filter == null){
				filter = checkFilter;
			}else if(checkFilter != null){
				filter = and(filter,checkFilter);
			}
		}
		return filter;
	}

	private Expression getCheckNewValues(final Table table,Expression check) {

		return  new StatementTransform(){
			@Override
			protected ExpressionTransform createExpressionTransform() {		    		
				return new ExpressionTransform(this){		    			
					public void visit(Column column){				
						for(NewValue value : newValues){
							if(value.getColumn().getColumnName().equalsIgnoreCase(column.getColumnName())){
								setExpression(value.getExpression());
							}
						}
					}

				};
			}
		}.transform(check);


	}

	private Expression getUsing(final Table table,Expression using) {


		return  new StatementTransform(){
			@Override
			protected ExpressionTransform createExpressionTransform() {		    		
				return new ExpressionTransform(this){		    			
					public void visit(Column column){		    				
						Table newTable;
						if(table.getAlias() != null){
							newTable = new Table(null, table.getAlias());
						}else{
							newTable = new Table(table.getSchemaName(),table.getName());	
						} 

						setExpression(new Column(newTable,column.getColumnName()));
					}

				};
			}
		}.transform(using);



	}

	public boolean isCheckColumns() {
		return checkColumns;
	}

	public void setCheckColumns(boolean checkColumns) {
		this.checkColumns = checkColumns;
	}



	public Table getToTable() {
		return toTable;
	}



	public void setToTable(Table table) {
		statementTransform.getTables().add(table);
		this.toTable = table;
	}



	public List<Table> getFromTables() {		
		return fromTables;
	}






}
