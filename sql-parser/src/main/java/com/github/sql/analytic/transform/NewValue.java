package com.github.sql.analytic.transform;

import com.github.sql.analytic.expression.SQLExpression;
import com.github.sql.analytic.schema.Column;

public class NewValue{
	private Column column;
	private SQLExpression expression;

	public NewValue(Column column, SQLExpression expression) {
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
	public SQLExpression getExpression() {
		return expression;
	}
	public void setExpression(SQLExpression expression) {
		this.expression = expression;
	}

}