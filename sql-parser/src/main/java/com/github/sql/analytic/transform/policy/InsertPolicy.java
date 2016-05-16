package com.github.sql.analytic.transform.policy;

import java.util.ArrayList;
import java.util.List;

import com.github.sql.analytic.expression.SQLExpression;
import com.github.sql.analytic.expression.LongValue;
import com.github.sql.analytic.expression.NullValue;
import com.github.sql.analytic.expression.operators.relational.ExpressionList;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.statement.select.PlainSelect;
import com.github.sql.analytic.statement.select.SelectExpressionItem;
import com.github.sql.analytic.statement.select.SelectListItem;
import com.github.sql.analytic.statement.select.SubSelect;
import com.github.sql.analytic.transform.ExpressionTransform;
import com.github.sql.analytic.transform.InsertTransform;
import com.github.sql.analytic.transform.NewValue;
import com.github.sql.analytic.transform.StatementTransform;

public class InsertPolicy extends InsertTransform {

	public static final class InsertPolicyTransform extends SelectPolicy {
		public InsertPolicyTransform(String action,  List<NewValue> newValues,
				Policy statementTransform) {
			super(action,  newValues, statementTransform);
		}
		
		protected SQLExpression getCheckNewValues(final Table table,SQLExpression check) {

			return  new StatementTransform(){
				@Override
				protected ExpressionTransform createExpressionTransform() {		    		
					return new ExpressionTransform(this){		    			
						public void visit(Column column){				
							setExpression(new NullValue());
							for(NewValue value : getNewValues()){
								if(value.getColumn().getColumnName().equalsIgnoreCase(column.getColumnName())){
									setExpression(value.getExpression());
								}
							}
						}
					};
				}
			}.transform(check);


		}

	}

	private Policy policyTransform;
	private List<NewValue> values;

	public InsertPolicy(Policy policyTransform) {
		super(policyTransform);
		this.policyTransform = policyTransform;
	}
	
	@Override
	protected List<NewValue> transformItems(List<NewValue> newValues) {
	    values = newValues;
		return newValues;
	}
	
	@Override
	public void visit(ExpressionList expressionList) {		
		super.visit(expressionList);
		checkColumns();
		chechColumnsPolicy();
		
		SubSelect newSubselect = new SubSelect();
		PlainSelect body = new PlainSelect();
		
		SubSelect dual = buildDual();		
		body.setFromItem(dual);		
		body.setSelectItems(new ArrayList<SelectListItem>());		
		
		for(SQLExpression next: expressionList.getExpressions()){
			SelectExpressionItem item = new SelectExpressionItem();
			item.setExpression(next);
			body.getSelectItems().add(item);
		}
		
		InsertPolicyTransform policy = new InsertPolicyTransform("INSERT",  values, policyTransform);
		policy.setToTable(getTable());			
		policy.visit(body);
		body = (PlainSelect) policy.getSelectBody();
		newSubselect.setSelectBody(body);
		
		getNewInsert().setItemsList(newSubselect);
		
		
	}
	
	protected void chechColumnsPolicy(){
		List<CreatePolicy> list = policyTransform.currentPolicies("INSERT", getTable());
		for( NewValue value : values ){
			boolean auth = false;
			for(CreatePolicy p : list ){
				if(p.getColumns() == null){
					auth = true;
					break;
				}
				for( Column next : p.getColumns() ){
					if(next.getColumnName().equalsIgnoreCase(value.getColumn().getColumnName())){
						auth = true;
						break;
					}
				}
				if(auth){
					break;
				}
			}
			if(!auth){
				throw new PolicyException(" Access denied: column " + value.getColumn());
			}
		}
		
	}
	
	@Override
	public void visit(SubSelect subSelect) {		
		super.visit(subSelect);
		checkColumns();
		
	  List<NewValue> newValues = new ArrayList<NewValue>();
		
		int index = 0;
		for (Column column : getNewInsert().getColumns()) {		
			SelectListItem item = ((PlainSelect) subSelect.getSelectBody()).getSelectItems().get(index++);
			SQLExpression expr;
			if(item instanceof Column){
				expr = (SQLExpression) item;
			}else if (item instanceof SelectExpressionItem){
				expr = ((SelectExpressionItem) item).getExpression();
			}else {
				throw new PolicyException(" Access denied: " + item);
			}
			NewValue value = new NewValue(column, expr);
			newValues.add(value);				
		}	
		values = newValues;
		chechColumnsPolicy();
		InsertPolicyTransform policy = new InsertPolicyTransform("INSERT",  newValues, policyTransform);
		policy.setToTable(getTable());
		policy.visit((PlainSelect)subSelect.getSelectBody());		
		SubSelect newSubselect = new SubSelect();
		PlainSelect selectBody = (PlainSelect) policy.getSelectBody();
		
		if(selectBody.getFromItem() == null){
			SubSelect dual = buildDual();
			selectBody.setFromItem(dual);
		}
		
		newSubselect.setSelectBody(selectBody);
		
		
		getNewInsert().setItemsList(newSubselect);
	}

	protected void checkColumns() {
		if(getNewInsert().getColumns() == null || getNewInsert().getColumns().isEmpty()){
			throw new PolicyException("column list must be present for insert");
		}
	}

	protected SubSelect buildDual() {
		
		SubSelect dual = new SubSelect();
		
		PlainSelect dualBody = new PlainSelect();
		List<SelectListItem> dualList = new ArrayList<SelectListItem>();
		SelectExpressionItem dualItem = new SelectExpressionItem();
		dualItem.setExpression( new LongValue("1"));
		dualList.add(dualItem );
		dualBody.setSelectItems(dualList );
		dual.setSelectBody(dualBody );
		dual.setAlias("DUAL");
		
		return dual;
	}

}
