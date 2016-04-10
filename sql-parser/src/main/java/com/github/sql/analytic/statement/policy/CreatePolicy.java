package com.github.sql.analytic.statement.policy;

import java.util.List;

import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.Statement;
import com.github.sql.analytic.statement.StatementVisitor;

public class CreatePolicy implements Statement{

	private String name;
	private Table table;
	private List<Column> columns;
	private String action;
	private List<String> roles;
	private Expression using;
	private Expression check;

	public void accept(StatementVisitor statementVisitor) {
		statementVisitor.visit(this);

	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public Expression getUsing() {
		return using;
	}

	public void setUsing(Expression using) {
		this.using = using;
	}

	public Expression getCheck() {
		return check;
	}

	public void setCheck(Expression check) {
		this.check = check;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}





}
