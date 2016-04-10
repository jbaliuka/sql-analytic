package com.github.sql.analytic.session;

import java.util.ArrayList;
import java.util.List;

public class DeparsedSQL {
	
	private List<Integer> positionalParams = new ArrayList<>();
	private List<ParamNamePosition> sessionParams = new ArrayList<>();
	private String sql;
	
	public List<Integer> getPositionalParams() {
		return positionalParams;
	}
	public void setPositionalParams(List<Integer> positionalParams) {
		this.positionalParams = positionalParams;
	}
	public List<ParamNamePosition> getSessionParams() {
		return sessionParams;
	}
	public void setSessionParams(List<ParamNamePosition> namedParams) {
		this.sessionParams = namedParams;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	

}
