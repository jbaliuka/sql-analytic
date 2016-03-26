package com.github.sql.analytic.transform.policy;

public interface SessionContext {

	String getCurrentUser();
	boolean isUserInRole(String role);
	
	
}
