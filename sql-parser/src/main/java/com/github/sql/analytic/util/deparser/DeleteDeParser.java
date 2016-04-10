package com.github.sql.analytic.util.deparser;

import com.github.sql.analytic.expression.ExpressionVisitor;
import com.github.sql.analytic.statement.delete.Delete;


/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a string)
 * a {@link com.github.sql.analytic.statement.delete.Delete}
 */

public class DeleteDeParser {
	protected StringBuffer buffer;
	protected ExpressionVisitor expressionVisitor;

	public DeleteDeParser() {
	}

	/**
	 * @param expressionVisitor a {@link ExpressionVisitor} to de-parse expressions. It has to share the same<br>
	 * StringBuffer (buffer parameter) as this object in order to work
	 * @param buffer the buffer that will be filled with the select
	 */
	public DeleteDeParser(ExpressionVisitor expressionVisitor, StringBuffer buffer) {
		this.buffer = buffer;
		this.expressionVisitor = expressionVisitor;
	}
	
	public StringBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(StringBuffer buffer) {
		this.buffer = buffer;
	}

	public void deParse(Delete delete) {
		buffer.append("DELETE FROM ").append(delete.getTable().getWholeTableName());
		if (delete.getWhere() != null) {
			buffer.append(" WHERE ");
			delete.getWhere().accept(expressionVisitor);
		}

	}
	public ExpressionVisitor getExpressionVisitor() {
		return expressionVisitor;
	}

	public void setExpressionVisitor(ExpressionVisitor visitor) {
		expressionVisitor = visitor;
	}

}
