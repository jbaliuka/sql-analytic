package com.github.sql.analytic.test;

import java.io.StringReader;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.parser.CCJSqlParserManager;
import com.github.sql.analytic.statement.Statement;
import com.github.sql.analytic.transform.StatementTransform;
import com.github.sql.analytic.util.deparser.StatementDeParser;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

public class TestUtil {

	public static void  assertParseable(String sql) throws JSQLParserException{
		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		Statement stmt =  parserManager.parse(new StringReader(sql));
		doAssert(sql, stmt);
	}

	public static void  assertTransformable(String sql) throws JSQLParserException{

		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		Statement stmt =  parserManager.transform(new StringReader(sql), new StatementTransform());
		doAssert(sql, stmt);
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
	
	private static void doAssert(String sql, Statement stmt) throws ComparisonFailure {		
		String deparsed = deparse(stmt);
		try{			
			TestCase.assertEquals(normalize(sql),normalize(deparsed));
		}catch(ComparisonFailure cf){
			throw new ComparisonFailure("deparser",sql,deparsed);
		}
	}

}
