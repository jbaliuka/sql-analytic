package com.github.sql.analytic.transform.policy;

public interface SessionContext {

	String getCurrentUser();
	boolean isUserInRole(String role);
	Object getParameter(String name);
	String getDefaultSchema();
	
}
