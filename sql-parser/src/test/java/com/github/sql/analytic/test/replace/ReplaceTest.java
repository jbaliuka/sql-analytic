package com.github.sql.analytic.test.replace;

import java.io.StringReader;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.expression.JdbcParameter;
import com.github.sql.analytic.expression.LongValue;
import com.github.sql.analytic.expression.StringValue;
import com.github.sql.analytic.expression.operators.relational.ExpressionList;
import com.github.sql.analytic.parser.CCJSqlParserManager;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.statement.replace.Replace;
import com.github.sql.analytic.statement.select.SubSelect;

import junit.framework.TestCase;

public class ReplaceTest extends TestCase {
	CCJSqlParserManager parserManager = new CCJSqlParserManager();

	public ReplaceTest(String arg0) {
		super(arg0);
	}

	public void testReplaceSyntax1() throws JSQLParserException {
		String statement = "REPLACE mytable SET col1='as', col2=?, col3=565";
		Replace replace = (Replace) parserManager.parse(new StringReader(statement));
		assertEquals("mytable", replace.getTable().getName());
		assertEquals(3, replace.getColumns().size());
		assertEquals("col1", ((Column) replace.getColumns().get(0)).getColumnName());
		assertEquals("col2", ((Column) replace.getColumns().get(1)).getColumnName());
		assertEquals("col3", ((Column) replace.getColumns().get(2)).getColumnName());
		assertEquals("as", ((StringValue)replace.getExpressions().get(0)).getValue());
		assertTrue(replace.getExpressions().get(1) instanceof JdbcParameter);
		assertEquals(565, ((LongValue)replace.getExpressions().get(2)).getValue());
		assertEquals(statement, ""+replace);

	}

	public void testReplaceSyntax2() throws JSQLParserException {
		String statement = "REPLACE mytable (col1, col2, col3) VALUES ('as', ?, 565)";
		Replace replace = (Replace) parserManager.parse(new StringReader(statement));
		assertEquals("mytable", replace.getTable().getName());
		assertEquals(3, replace.getColumns().size());
		assertEquals("col1", ((Column) replace.getColumns().get(0)).getColumnName());
		assertEquals("col2", ((Column) replace.getColumns().get(1)).getColumnName());
		assertEquals("col3", ((Column) replace.getColumns().get(2)).getColumnName());
		assertEquals("as", ((StringValue) ((ExpressionList)replace.getItemsList()).getExpressions().get(0)).getValue());
		assertTrue(((ExpressionList)replace.getItemsList()).getExpressions().get(1) instanceof JdbcParameter);
		assertEquals(565, ((LongValue) ((ExpressionList)replace.getItemsList()).getExpressions().get(2)).getValue());
		assertEquals(statement, ""+replace);
	}


	public void testReplaceSyntax3() throws JSQLParserException {
		String statement = "REPLACE mytable (col1, col2, col3) SELECT * FROM mytable3";
		Replace replace = (Replace) parserManager.parse(new StringReader(statement));
		assertEquals("mytable", replace.getTable().getName());
		assertEquals(3, replace.getColumns().size());
		assertEquals("col1", ((Column) replace.getColumns().get(0)).getColumnName());
		assertEquals("col2", ((Column) replace.getColumns().get(1)).getColumnName());
		assertEquals("col3", ((Column) replace.getColumns().get(2)).getColumnName());
		assertTrue(replace.getItemsList() instanceof SubSelect);
		//TODO:
		//assertEquals(statement, ""+replace);
	}

	

}
