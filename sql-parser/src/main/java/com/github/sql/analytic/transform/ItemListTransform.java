package com.github.sql.analytic.transform;

import java.util.ArrayList;

import com.github.sql.analytic.expression.SQLExpression;
import com.github.sql.analytic.expression.operators.relational.ExpressionList;
import com.github.sql.analytic.expression.operators.relational.ItemsList;
import com.github.sql.analytic.expression.operators.relational.ItemsListVisitor;
import com.github.sql.analytic.statement.select.SubSelect;

public class ItemListTransform implements ItemsListVisitor{

	private StatementTransform statementTransform;
	private ItemsList itemList;

	public ItemListTransform(StatementTransform statementTransform) {
		this.statementTransform = statementTransform;
	}

	public void visit(SubSelect subSelect) {
		
		SubSelect newList = new SubSelect();
		itemList = newList;
		newList.setAlias(subSelect.getAlias());
		newList.setExpression(subSelect.isExpression());
		SelectTransform transform = statementTransform.createSelectTransform();
		subSelect.getSelectBody().accept(transform);		
		newList.setSelectBody(transform.getSelectBody());
		
	}

	public void visit(ExpressionList expressionList) {
		ExpressionList newList = new ExpressionList();
		itemList = newList;
		newList.setExpressions(new ArrayList<SQLExpression>());
		for( SQLExpression expr : expressionList.getExpressions()){
			newList.getExpressions().add(statementTransform.transform(expr));
		}
		
	}

	public ItemsList getItemList() {		
		return itemList;
	}

}
