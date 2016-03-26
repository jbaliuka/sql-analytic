package com.github.sql.analytic.transform;

import java.util.ArrayList;

import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.expression.operators.relational.ExpressionList;
import com.github.sql.analytic.expression.operators.relational.ItemsListVisitor;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.statement.Statement;
import com.github.sql.analytic.statement.insert.Insert;
import com.github.sql.analytic.statement.select.SubSelect;



public class InsertTransform implements ItemsListVisitor {

	private StatementTransform statementTransform;
	private Insert newInsert = new Insert();

	public InsertTransform(StatementTransform statementTransform) {
		super();
		this.statementTransform = statementTransform;
	}


	public Statement transform(Insert insert) {
		
		newInsert.setTable(statementTransform.copy(insert.getTable()));
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


	public void visit(ExpressionList expressionList) {

		ExpressionList newList = new ExpressionList(); 
		newInsert.setItemsList(newList);
		newList.setExpressions(new ArrayList<Expression>());
		
		for (Expression next : expressionList.getExpressions()) {
			newList.getExpressions().add(statementTransform.transform(next));
		}
		
	}

	public void visit(SubSelect subSelect) {
		SubSelect newSubselect = new SubSelect();
		newSubselect.setSelectBody(statementTransform.transform(subSelect.getSelectBody()));		
		newInsert.setItemsList(newSubselect);
		
	}


}
