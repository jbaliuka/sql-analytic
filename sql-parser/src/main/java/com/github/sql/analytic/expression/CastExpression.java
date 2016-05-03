package com.github.sql.analytic.expression;

public class CastExpression implements SQLExpression {

	private SQLExpression expression;
	private String type;
	
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);		
	}

	public SQLExpression getExpression() {
		return expression;
	}

	public void setExpression(SQLExpression expression) {
		this.expression = expression;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	@Override
	public String toString() {		
		return "CAST( " + expression + " AS " + type + ")";
	}
	
}
