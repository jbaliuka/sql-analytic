package com.github.sql.analytic.statement.truncate;

import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.Statement;
import com.github.sql.analytic.statement.StatementVisitor;


/**
 * A TRUNCATE TABLE statement
 */

public class Truncate implements Statement {
	private Table table;

	public void accept(StatementVisitor statementVisitor) {
		statementVisitor.visit(this);
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public String toString() {
		return "TRUNCATE TABLE "+table;
	}
}
