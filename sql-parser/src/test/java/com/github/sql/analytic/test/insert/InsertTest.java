package com.github.sql.analytic.test.insert;

import java.io.StringReader;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.expression.DoubleValue;
import com.github.sql.analytic.expression.JdbcParameter;
import com.github.sql.analytic.expression.LongValue;
import com.github.sql.analytic.expression.StringValue;
import com.github.sql.analytic.expression.operators.relational.ExpressionList;
import com.github.sql.analytic.parser.CCJSqlParserManager;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.insert.Insert;
import com.github.sql.analytic.statement.select.PlainSelect;
import com.github.sql.analytic.statement.select.SubSelect;

import junit.framework.TestCase;

public class InsertTest extends TestCase {
	CCJSqlParserManager parserManager = new CCJSqlParserManager();

	public InsertTest(String arg0) {
		super(arg0);
	}

	public void testRegularInsert() throws JSQLParserException {
		String statement = "INSERT INTO mytable (col1, col2, col3) VALUES (?, 'sadfsd', 234)";
		Insert insert = (Insert) parserManager.parse(new StringReader(statement));
		assertEquals("mytable", insert.getTable().getName());
		assertEquals(3, insert.getColumns().size());
		assertEquals("col1", ((Column) insert.getColumns().get(0)).getColumnName());
		assertEquals("col2", ((Column) insert.getColumns().get(1)).getColumnName());
		assertEquals("col3", ((Column) insert.getColumns().get(2)).getColumnName());
		assertEquals(3, ((ExpressionList) insert.getItemsList()).getExpressions().size());
		assertTrue (((ExpressionList) insert.getItemsList()).getExpressions().get(0) instanceof JdbcParameter);
		assertEquals("sadfsd", ((StringValue) ((ExpressionList) insert.getItemsList()).getExpressions().get(1)).getValue());
		assertEquals(234, ((LongValue) ((ExpressionList) insert.getItemsList()).getExpressions().get(2)).getValue());
		assertEquals(statement, ""+insert);

		 statement = "INSERT INTO myschema.mytable VALUES (?, ?, 2.3)";
		 insert = (Insert) parserManager.parse(new StringReader(statement));
		assertEquals("myschema.mytable", insert.getTable().getWholeTableName());
		assertEquals(3, ((ExpressionList) insert.getItemsList()).getExpressions().size());
		assertTrue (((ExpressionList) insert.getItemsList()).getExpressions().get(0) instanceof JdbcParameter);
		assertEquals(2.3, ((DoubleValue) ((ExpressionList) insert.getItemsList()).getExpressions().get(2)).getValue(), 0.0);
		assertEquals(statement, ""+insert);

	}

	public void testInsertFromSelect() throws JSQLParserException {
		String statement = "INSERT INTO mytable (col1, col2, col3) SELECT * FROM mytable2";
		Insert insert = (Insert) parserManager.parse(new StringReader(statement));
		assertEquals("mytable", insert.getTable().getName());
		assertEquals(3, insert.getColumns().size());
		assertEquals("col1", ((Column) insert.getColumns().get(0)).getColumnName());
		assertEquals("col2", ((Column) insert.getColumns().get(1)).getColumnName());
		assertEquals("col3", ((Column) insert.getColumns().get(2)).getColumnName());
		assertTrue (insert.getItemsList() instanceof SubSelect);
		assertEquals("mytable2", ((Table) ((PlainSelect) ((SubSelect)insert.getItemsList()).getSelectBody()).getFromItem()).getName());
		
		//toString uses brakets
		String statementToString = "INSERT INTO mytable (col1, col2, col3) (SELECT * FROM mytable2)";
		assertEquals(statementToString, ""+insert);
	}

	

}
