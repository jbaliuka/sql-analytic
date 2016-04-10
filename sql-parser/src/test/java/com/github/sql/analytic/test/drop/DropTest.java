package com.github.sql.analytic.test.drop;

import java.io.StringReader;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.parser.CCJSqlParserManager;
import com.github.sql.analytic.statement.drop.Drop;

import junit.framework.TestCase;

public class DropTest extends TestCase {
	CCJSqlParserManager parserManager = new CCJSqlParserManager();

	public DropTest(String arg0) {
		super(arg0);
	}

	public void testDrop() throws JSQLParserException {
		String statement =
			"DROP TABLE mytab";
		Drop drop = (Drop) parserManager.parse(new StringReader(statement));
		assertEquals("TABLE", drop.getType());
		assertEquals("mytab", drop.getName());
		assertEquals(statement, ""+drop);
		
		statement =
					"DROP INDEX myindex CASCADE";
		drop = (Drop) parserManager.parse(new StringReader(statement));
		assertEquals("INDEX", drop.getType());
		assertEquals("myindex", drop.getName());
		assertEquals("CASCADE", drop.getParameters().get(0));
		assertEquals(statement, ""+drop);
	}

	

}
