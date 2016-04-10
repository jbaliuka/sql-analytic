package com.github.sql.analytic.dialect.h2;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.parser.CCJSqlParserManager;
import com.github.sql.analytic.session.SQLSession;
import com.github.sql.analytic.statement.Statement;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.transform.policy.SessionContext;

import junit.framework.TestCase;

public class H2SessionTest extends TestCase {

	String policy = " CREATE POLICY TEST_POLICY ON TEST_TABLE USING( user = :session_user ) " +
			        "                                    WITH CHECK( user = :session_user ) ";


	public void testTransform() throws JSQLParserException, SQLException {

		try(SQLSession session = new H2Session(context , connection, policyList)){
			
			connection.createStatement().execute("create table test_table " +
								" ( id number ,user varchar(20), test varchar(20) )");
			
			parameters.put("session_user", "testUser");
			
			assertEquals(1,session.createStatement().
					executeUpdate("INSERT INTO TEST_TABLE(id,user,test)VALUES(1,'testUser','test')"));
			assertEquals(0,session.createStatement().
					executeUpdate("INSERT INTO TEST_TABLE(id,user,test)VALUES(2,'testUser1','test')"));
			assertEquals(1,session.createStatement().
					executeUpdate("UPDATE TEST_TABLE SET test = 'test' WHERE id = 1"));
			assertEquals(0,session.createStatement().
					executeUpdate("UPDATE TEST_TABLE SET user = 'testUser1'"));
			assertTrue(session.createStatement().
					executeQuery("SELECT * FROM TEST_TABLE ").next());
			
			parameters.put("session_user", "testUser1");
			
			assertFalse(session.createStatement().
					executeQuery("SELECT * FROM TEST_TABLE ").next());
		}
	}
	

	protected void setUp() throws Exception {
		super.setUp();		
		connection = new org.h2.Driver().connect(url, new Properties());
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();	
		connection.close();
	}

	List<CreatePolicy> policyList = new ArrayList<>();
	{
		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		Statement stmt;
		try {
			stmt = parserManager.parse(new StringReader(policy));
		} catch (JSQLParserException e) {
			throw new RuntimeException(e);
		}
		policyList.add((CreatePolicy) stmt);
	}

	Map<String,Object> parameters = new HashMap<>();


	private static final String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
	private Connection connection;


	SessionContext context = new SessionContext(){

		@Override
		public String getCurrentUser() {

			return "testUser";
		}

		@Override
		public boolean isUserInRole(String role) {				
			return true;
		}

		@Override
		public Map<String, Object> getParameters() {			
			return parameters;
		}

	};

}
