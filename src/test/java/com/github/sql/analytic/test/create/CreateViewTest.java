package com.github.sql.analytic.test.create;

import java.io.StringReader;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.parser.CCJSqlParserManager;
import com.github.sql.analytic.statement.create.view.CreateView;

import junit.framework.TestCase;

public class CreateViewTest extends TestCase {
	
	CCJSqlParserManager parserManager = new CCJSqlParserManager();

	public CreateViewTest(String arg0) {
		super(arg0);
	}

	public void testCreateView() throws JSQLParserException {
		String statement =
			"CREATE VIEW TEST(tst) AS SELECT * FROM DUAL";
		CreateView createTable = (CreateView) parserManager.parse(new StringReader(statement));
		
		assertEquals(statement, ""+createTable);
	}

	

	

}
