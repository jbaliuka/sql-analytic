package com.github.sql.analytic.test.update;

import java.io.StringReader;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.expression.JdbcParameter;
import com.github.sql.analytic.expression.LongValue;
import com.github.sql.analytic.expression.StringValue;
import com.github.sql.analytic.expression.operators.relational.GreaterThanEquals;
import com.github.sql.analytic.parser.CCJSqlParserManager;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.statement.update.Update;

import junit.framework.TestCase;

public class UpdateTest extends TestCase {
	CCJSqlParserManager parserManager = new CCJSqlParserManager();

	public UpdateTest(String arg0) {
		super(arg0);
	}
	public void testUpdate() throws JSQLParserException {
		String statement = "UPDATE mytable set col1='as', col2=?, col3=565 Where o >= 3";
		Update update = (Update) parserManager.parse(new StringReader(statement));
		assertEquals("mytable", update.getTable().getName());
		assertEquals(3, update.getColumns().size());
		assertEquals("col1", ((Column) update.getColumns().get(0)).getColumnName());
		assertEquals("col2", ((Column) update.getColumns().get(1)).getColumnName());
		assertEquals("col3", ((Column) update.getColumns().get(2)).getColumnName());
		assertEquals("as", ((StringValue) update.getExpressions().get(0)).getValue());
		assertTrue(update.getExpressions().get(1) instanceof JdbcParameter);
		assertEquals(565, ((LongValue) update.getExpressions().get(2)).getValue());

		assertTrue(update.getWhere() instanceof GreaterThanEquals);
	}

	
}
