package com.github.sql.analytic.session;

import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.sql.analytic.parser.CCJSqlParserManager;
import com.github.sql.analytic.statement.Statement;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.transform.policy.SessionContext;

import junit.framework.TestCase;

public class ParamsDeparserTest extends TestCase {

	SQLSession session;

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
		};
		List<CreatePolicy> policyList = new ArrayList<>();
		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		String policy = "CREATE POLICY TEST_POLICY ON TEST_TABLE USING(user = :session_user)";
		Statement stmt =  parserManager.parse(new StringReader(policy));
		policyList.add((CreatePolicy) stmt);
		session = new SQLSession(context , null, policyList );
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetDeparsedSQL() throws SQLException {
		DeparsedSQL deparsed = session.transform("SELECT A.*,? FROM TEST_TABLE A WHERE ID=?");
		//SELECT A.*,? FROM TEST_TABLE A WHERE (A.user = ?) AND (ID = ?) 
		List<ParamNamePosition> named = deparsed.getSessionParams();
		List<Integer> positional = deparsed.getPositionalParams();
		assertEquals(1, named.get(0).getPosition());
		assertEquals(Arrays.asList(0,2) , positional);
		
		
	}

}
