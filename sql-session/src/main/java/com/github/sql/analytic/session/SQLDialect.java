package com.github.sql.analytic.session;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.transform.policy.SessionContext;

public interface SQLDialect {
	
	SQLSession geSQLSession(SessionContext context, Connection connection, List<CreatePolicy> policy)
			throws SQLException;
	boolean acceptsURL(String url) throws SQLException;
	

}
