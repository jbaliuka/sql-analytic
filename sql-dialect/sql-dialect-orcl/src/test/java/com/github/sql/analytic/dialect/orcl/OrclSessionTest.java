package com.github.sql.analytic.dialect.orcl;

import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.sql.analytic.parser.CCJSqlParserManager;
import com.github.sql.analytic.session.DeparsedSQL;
import com.github.sql.analytic.session.ParamNamePosition;
import com.github.sql.analytic.session.SQLSession;
import com.github.sql.analytic.statement.SQLStatement;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.test.TestUtil;
import com.github.sql.analytic.transform.policy.SessionContext;

import junit.framework.TestCase;

public class OrclSessionTest extends TestCase {
	
	OrclSession session;

	protected void setUp() throws Exception {
		super.setUp();
		SessionContext context = new SessionContext() {

			@Override
			public boolean isUserInRole(String role) {				
				return true;
			}

			@Override
			public Object getParameter(String name) {
				return "testUser";
			}

			@Override
			public String getCurrentUser() {				
				return "testUser";
			}

			@Override
			public String getDefaultSchema() {			
				return null;
			}
		};
		List<CreatePolicy> policyList = new ArrayList<>();
		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		String policy = "CREATE POLICY TEST_POLICY ON TEST_TABLE USING(user = :session_user)";
		SQLStatement stmt =  parserManager.parse(new StringReader(policy));
		policyList.add((CreatePolicy) stmt);
		session = new OrclSession(context , null, policyList );
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetDeparsedSQL() throws SQLException {
		DeparsedSQL deparsed = session.transform("INSERT INTO TEST_TABLE(ID) SELECT 1 ");
		TestUtil.assertSQLEqual("INSERT INTO TEST_TABLE(ID) SELECT 1 FROM (SELECT 1  FROM DUAL ) DUAL",
				deparsed.getSql());
		
	}


}
