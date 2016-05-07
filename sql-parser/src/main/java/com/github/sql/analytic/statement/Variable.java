package com.github.sql.analytic.statement;

import com.github.sql.analytic.statement.create.table.ColDataType;
import com.github.sql.analytic.statement.create.table.ColumnDefinition;

public class Variable implements SQLStatement{
	
	private ColumnDefinition definition;

	@Override
	public void accept(StatementVisitor statementVisitor) {
		statementVisitor.visit(this);
	}

	public ColumnDefinition getDefinition() {
		return definition;
	}

	public Variable setDefinition(ColumnDefinition definition) {
		this.definition = definition;
		return this;
	}

	public String getName() {
		return definition.getColumnName();
	}

	public ColDataType getType(){
		return definition.getColDataType();
	}

}
