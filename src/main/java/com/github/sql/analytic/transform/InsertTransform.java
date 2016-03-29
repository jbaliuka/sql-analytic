package com.github.sql.analytic.transform;

import java.util.ArrayList;
import java.util.List;

import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.expression.operators.relational.ExpressionList;
import com.github.sql.analytic.expression.operators.relational.ItemsListVisitor;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.Statement;
import com.github.sql.analytic.statement.insert.Insert;
import com.github.sql.analytic.statement.select.SubSelect;



public class InsertTransform implements ItemsListVisitor {

	private StatementTransform statementTransform;
	private Insert newInsert = new Insert();
	private Table table;

	public InsertTransform(StatementTransform statementTransform) {
		super();
		this.statementTransform = statementTransform;
	}


	public Statement transform(Insert insert) {

		newInsert.setTable(statementTransform.copy(insert.getTable()));
		table = newInsert.getTable();
		newInsert.setUseValues(insert.isUseValues());

		if (insert.getColumns() != null) {	
			newInsert.setColumns(new ArrayList<Column>());
			int i = 0;
			for (Column column : insert.getColumns()) {			
				newInsert.getColumns().add(column);				
			}

		}		
		insert.getItemsList().accept(this);
		return newInsert;
	}

	protected List<NewValue> transformItems(List<NewValue> newValues) {
		return newValues;
	}


	public void visit(ExpressionList expressionList) {

		ExpressionList newList = new ExpressionList(); 
		newInsert.setItemsList(newList);
		newList.setExpressions(new ArrayList<Expression>());

		if(newInsert.getColumns() != null){
			List<NewValue> newValues = new ArrayList<NewValue>();
			for (int i = 0;  i < newInsert.getColumns().size(); i++) {
				NewValue value = new NewValue(newInsert.getColumns().get(i),
						statementTransform.transform(expressionList.getExpressions().get(i)));
				newValues.add(value);
			}
			newValues = transformItems(newValues);			
			for (NewValue next : newValues) {
				newList.getExpressions().add(next.getExpression());
			}
		}else {
			for( Expression e : expressionList.getExpressions()){
				newList.getExpressions().add(statementTransform.transform(e));
			}

		}

	}

	public void visit(SubSelect subSelect) {
		SubSelect newSubselect = new SubSelect();
		newSubselect.setSelectBody(statementTransform.transform(subSelect.getSelectBody()));		
		newInsert.setItemsList(newSubselect);

	}


	public Table getTable() {
		return table;
	}


	public void setTable(Table table) {
		this.table = table;
	}
	public Insert getNewInsert() {
		return newInsert;
	}


	public void setNewInsert(Insert newInsert) {
		this.newInsert = newInsert;
	}


}
