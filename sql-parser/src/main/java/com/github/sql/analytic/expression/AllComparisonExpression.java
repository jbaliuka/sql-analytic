package com.github.sql.analytic.expression;

import com.github.sql.analytic.statement.select.SubSelect;


public class AllComparisonExpression implements Expression {
	private SubSelect subSelect;
	
	public AllComparisonExpression(SubSelect subSelect) {
		this.subSelect = subSelect;
	}
	
	public AllComparisonExpression() {		
	}

	public SubSelect getSubSelect() {
		return subSelect;
	}
	
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}

	public void setSubSelect(SubSelect epr) {
		subSelect = epr;
		
	}
	
	@Override
	public String toString() {		
		return " ALL " + subSelect;
	}

}
