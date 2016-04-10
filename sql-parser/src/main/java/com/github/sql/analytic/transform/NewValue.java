package com.github.sql.analytic.transform;

import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.schema.Column;

public class NewValue{
	private Column column;
	private Expression expression;

	public NewValue(Column column, Expression expression) {
		super();
		this.column = column;
		this.expression = expression;
	}
	public Column getColumn() {
		return column;
	}
	public void setColumn(Column column) {
		this.column = column;
	}
	public Expression getExpression() {
		return expression;
	}
	public void setExpression(Expression expression) {
		this.expression = expression;
	}

}