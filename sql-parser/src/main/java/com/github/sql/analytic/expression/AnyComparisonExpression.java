package com.github.sql.analytic.expression;

import com.github.sql.analytic.statement.select.SubSelect;


public class AnyComparisonExpression implements SQLExpression {
	private SubSelect subSelect;
	
	public AnyComparisonExpression(SubSelect subSelect) {
		this.subSelect = subSelect;
	}
	
	public AnyComparisonExpression() {		
	}

	public SubSelect getSubSelect() {
		return subSelect;
	}

	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}

	public void setSubSelect(SubSelect sub) {
		subSelect = sub;		
	}
	
	@Override
	public String toString() {		
		return " ANY " + subSelect;
	}
	
}
