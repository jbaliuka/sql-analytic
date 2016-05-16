package com.github.sql.analytic.statement.policy;

import java.util.List;

import com.github.sql.analytic.expression.SQLExpression;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.SQLStatement;
import com.github.sql.analytic.statement.StatementVisitor;

public class CreatePolicy implements SQLStatement{

	private String name;
	private Table table;
	private List<Column> columns;
	private List<String> actions;
	private List<String> roles;
	private SQLExpression using;
	private SQLExpression check;
	private boolean enabled = true;

	public void accept(StatementVisitor statementVisitor) {
		statementVisitor.visit(this);

	}

	public Table getTable() {
		return table;
	}

	public CreatePolicy setTable(Table table) {
		this.table = table;
		return this;
	}
	

	public List<String> getRoles() {
		return roles;
	}

	public CreatePolicy setRoles(List<String> roles) {
		this.roles = roles;
		return this;
	}

	public SQLExpression getUsing() {
		return using;
	}

	public CreatePolicy setUsing(SQLExpression using) {
		this.using = using;
		return this;
	}

	public SQLExpression getCheck() {
		return check;
	}

	public CreatePolicy setCheck(SQLExpression check) {
		this.check = check;
		return this;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public CreatePolicy setColumns(List<Column> columns) {
		this.columns = columns;
		return this;
	}

	public String getName() {
		return name;
	}

	public CreatePolicy setName(String name) {
		this.name = name;
		return this;
	}

	public List<String> getActions() {
		return actions;
	}

	public CreatePolicy setActions(List<String> actions) {
		this.actions = actions;
		return this;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}





}
