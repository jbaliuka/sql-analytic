package com.github.sql.analytic.expression;

import com.github.sql.analytic.expression.operators.relational.ExpressionList;
import com.github.sql.analytic.statement.select.PlainSelect;

public class QueryPartitionClause implements SQLExpression{

	private ExpressionList expressionList;

	public ExpressionList getExpressionList() {
		return expressionList;
	}

	public void setExpressionList(ExpressionList expressionList) {
		this.expressionList = expressionList;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("PARTITION BY ");
		if(expressionList != null){
			buffer.append(PlainSelect.getStringList(expressionList.getExpressions(), true, false));
		}

		return buffer.toString();
	}

	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);		
	}

}
