package com.github.sql.analytic.dialect.h2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.github.sql.analytic.session.SQLSession;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.transform.policy.SessionContext;

public class H2Session extends SQLSession{

	private String schema = "PUBLIC";

	public H2Session(SessionContext context, Connection connection, List<CreatePolicy> policy) {
		super(context, connection, policy);		
	}
	
	@Override
	public String getSchema() throws SQLException {		
		return schema ;
	}	
	@Override
	public void setSchema(String schema) throws SQLException {
		this.schema = schema;
	}

}
