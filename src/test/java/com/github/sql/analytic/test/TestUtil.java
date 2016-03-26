package com.github.sql.analytic.test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.parser.CCJSqlParserManager;
import com.github.sql.analytic.parser.ParseException;
import com.github.sql.analytic.statement.Statement;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.transform.StatementTransform;
import com.github.sql.analytic.transform.policy.SessionContext;
import com.github.sql.analytic.util.deparser.StatementDeParser;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

public class TestUtil {

	public static void  assertParseable(String sql) throws JSQLParserException{
		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		Statement stmt =  parserManager.parse(new StringReader(sql));
		assertEqual(sql, stmt);
	}

	public static void  assertTransformable(String sql) throws JSQLParserException{

		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		Statement stmt =  parserManager.transform(new StringReader(sql), new StatementTransform());
		assertEqual(sql, stmt);
	}

	protected static String deparse(Statement stmt) {
		StringBuffer buffer = new StringBuffer();
		StatementDeParser deparser = new StatementDeParser(buffer );
		stmt.accept(deparser);		
		return buffer.toString();
	}

	private static  String normalize(String str){
		return str.replaceAll("\\s+","");
	}

	public static void assertSQLEqual(String sql1,String sql2){
		try{			
			TestCase.assertEquals(normalize(sql1),normalize(sql2));
		}catch(ComparisonFailure cf){
			throw new ComparisonFailure("deparser",sql1,sql2);
		}
	}

	public static void assertEqual(String sql, Statement stmt) throws ComparisonFailure {		
		String deparsed = deparse(stmt);					
		assertSQLEqual(sql,deparsed);		
	}

	public static SessionContext mockContext(final String user,final String... roles){
		SessionContext context = new SessionContext() {

			public boolean isUserInRole(String role) {				
				for(String next : roles){
					if(next.equalsIgnoreCase(role)){
						return true;
					}
				}
				return false;
			}

			public String getCurrentUser() {				
				return user;
			}

		};
		return context;
	}


	public static List<CreatePolicy> parsePolicyList(List<String> policyList) throws JSQLParserException {
		List<CreatePolicy> list = new ArrayList<CreatePolicy>();	
		for(String policy : policyList){		
			CCJSqlParserManager parserManager = new CCJSqlParserManager();
			Statement stmt =  parserManager.parse(new StringReader(policy));
			list.add((CreatePolicy) stmt);
		}
		return list;

	}

}
