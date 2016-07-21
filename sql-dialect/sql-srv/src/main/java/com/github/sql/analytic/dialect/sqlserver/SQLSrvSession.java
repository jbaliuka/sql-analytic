package com.github.sql.analytic.dialect.sqlserver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.github.sql.analytic.session.ParamsDeparser;
import com.github.sql.analytic.session.SQLSession;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.transform.policy.SessionContext;

public class SQLSrvSession extends SQLSession{

	private String schema;

	public SQLSrvSession(SessionContext context, Connection connection, List<CreatePolicy> policy) {
		super(context, connection, policy);		
	}

	@Override
	public String getSchema() throws SQLException {
		if(schema == null){
			try(Statement stmt = connection.createStatement()){
			  try(ResultSet rs = stmt.executeQuery(" SELECT SCHEMA_NAME() ")){
				rs.next();
				schema = rs.getString(1);  
			  }
			}			
		}
		return schema;
	}
	
	@Override
	public ParamsDeparser createDeparser(StringBuffer buffer) {	
		return new SQlSrvDeparser(buffer);
	}

}
