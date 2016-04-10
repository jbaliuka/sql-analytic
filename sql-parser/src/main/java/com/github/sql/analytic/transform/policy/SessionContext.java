package com.github.sql.analytic.transform.policy;

import java.util.Map;

public interface SessionContext {

	String getCurrentUser();
	boolean isUserInRole(String role);
	Map<String, Object> getParameters();
	
	
}
