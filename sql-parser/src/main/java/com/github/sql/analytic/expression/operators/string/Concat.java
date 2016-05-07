package com.github.sql.analytic.expression.operators.string;

import com.github.sql.analytic.expression.BinaryExpression;
import com.github.sql.analytic.expression.ExpressionVisitor;


public class Concat extends BinaryExpression{

	@Override
	public String getStringExpression() {
		
		return "||";
	}

	public void accept(ExpressionVisitor expressionVisitor) {		
		expressionVisitor.visit(this);
	}

	

}
