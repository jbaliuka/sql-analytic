package com.github.sql.analytic.dialect.sqlserver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.github.sql.analytic.session.SQLDialect;
import com.github.sql.analytic.session.SQLSession;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.transform.policy.SessionContext;

public class SQLSrvDialect implements SQLDialect{

	private static final String JDBC_SRV = "jdbc:sqlserver:";

	@Override
	public SQLSession geSQLSession(SessionContext context, Connection connection, List<CreatePolicy> policy)
			throws SQLException {
		
		return new SQLSrvSession(context, connection, policy);
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {		
		return url != null && url.startsWith(JDBC_SRV);
	}

}
