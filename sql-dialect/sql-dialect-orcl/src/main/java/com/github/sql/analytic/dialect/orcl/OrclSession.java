package com.github.sql.analytic.dialect.orcl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.github.sql.analytic.session.ParamsDeparser;
import com.github.sql.analytic.session.SQLSession;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.transform.policy.SessionContext;

public class OrclSession extends SQLSession {

	public OrclSession(SessionContext context, Connection connection, List<CreatePolicy> policy) {
		super(context, connection, policy);	
	}

	@Override
	public String getSchema() throws SQLException {		
		return getMetaData().getUserName();
	}

	@Override
	public ParamsDeparser createDeparser(StringBuffer buffer) {

		return new OrclDeparser(buffer);
	}



}
