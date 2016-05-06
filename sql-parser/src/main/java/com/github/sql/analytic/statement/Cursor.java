package com.github.sql.analytic.statement;

import java.util.List;

import com.github.sql.analytic.statement.create.table.ColumnDefinition;
import com.github.sql.analytic.statement.select.Select;

public class Cursor implements SQLStatement{

	
	private Select select;
	private List<ColumnDefinition> columnDefinitions;
	private String name;
	
	@Override
	public void accept(StatementVisitor statementVisitor) {
		
		statementVisitor.visit(this);
		
	}

	public Select getSelect() {
		return select;
	}

	public Cursor setSelect(Select select) {
		this.select = select;
		return this;
	}

	public List<ColumnDefinition> getColumnDefinitions() {
		return columnDefinitions;
	}

	public Cursor setColumnDefinitions(List<ColumnDefinition> columnDefinitions) {
		this.columnDefinitions = columnDefinitions;
		return this;
	}

	public String getName() {
		return name;
	}

	public Cursor setName(String name) {
		this.name = name;
		return this;
	}
	

}
