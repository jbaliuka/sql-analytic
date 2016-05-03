package com.github.sql.analytic.util.deparser;

import com.github.sql.analytic.expression.SQLExpression;
import com.github.sql.analytic.expression.ExpressionVisitor;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.statement.update.Update;


/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a string)
 * an {@link com.github.sql.analytic.statement.update.Update}
 */

public class UpdateDeParser {
	protected StringBuffer buffer;
	protected ExpressionVisitor expressionVisitor;
	
	public UpdateDeParser() {
	}
	
	/**
	 * @param expressionVisitor a {@link ExpressionVisitor} to de-parse expressions. It has to share the same<br>
	 * StringBuffer (buffer parameter) as this object in order to work
	 * @param buffer the buffer that will be filled with the select
	 */
	public UpdateDeParser(ExpressionVisitor expressionVisitor, StringBuffer buffer) {
		this.buffer = buffer;
		this.expressionVisitor = expressionVisitor;
	}

	public StringBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(StringBuffer buffer) {
		this.buffer = buffer;
	}

	public void deParse(Update update) {
		buffer.append("UPDATE ").append(update.getTable().getWholeTableName()).append(" SET ");
		for (int i = 0; i < update.getColumns().size(); i++) {
			Column column = (Column) update.getColumns().get(i);
			buffer.append(column.getWholeColumnName()).append("=");

			SQLExpression expression = (SQLExpression) update.getExpressions().get(i);
			expression.accept(expressionVisitor);
			if (i < update.getColumns().size() - 1) {
				buffer.append(", ");
			}

		}
		
		if (update.getWhere() != null) {
			buffer.append(" WHERE ");
			update.getWhere().accept(expressionVisitor);
		}

	}

	public ExpressionVisitor getExpressionVisitor() {
		return expressionVisitor;
	}

	public void setExpressionVisitor(ExpressionVisitor visitor) {
		expressionVisitor = visitor;
	}

}
