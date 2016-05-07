package com.github.sql.analytic.statement.drop;

import java.util.List;

import com.github.sql.analytic.statement.SQLStatement;
import com.github.sql.analytic.statement.StatementVisitor;
import com.github.sql.analytic.statement.select.PlainSelect;



public class Drop implements SQLStatement {
	private String type;
	private String name;
	private List<String> parameters;
	
	public void accept(StatementVisitor statementVisitor) {
		statementVisitor.visit(this);
	}

	public String getName() {
		return name;
	}

	public List<String> getParameters() {
		return parameters;
	}

	public String getType() {
		return type;
	}

	public void setName(String string) {
		name = string;
	}

	public void setParameters(List<String> list) {
		parameters = list;
	}

	public void setType(String string) {
		type = string;
	}

	public String toString() {
		String sql = "DROP "+type+" "+name;
		
		if( parameters != null && parameters.size() > 0) {
			sql += " "+PlainSelect.getStringList(parameters);
		}
		
		return sql;
	}
}
