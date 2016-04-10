package com.github.sql.analytic.test.create;

import java.io.StringReader;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.parser.CCJSqlParserManager;
import com.github.sql.analytic.statement.create.table.ColumnDefinition;
import com.github.sql.analytic.statement.create.table.CreateTable;
import com.github.sql.analytic.statement.create.table.Index;

import junit.framework.TestCase;

public class CreateTableTest extends TestCase {
	CCJSqlParserManager parserManager = new CCJSqlParserManager();

	public CreateTableTest(String arg0) {
		super(arg0);
	}

	public void testCreateTable() throws JSQLParserException {
		String statement =
			"CREATE TABLE mytab (mycol a (10, 20) c nm g, mycol2 mypar1 mypar2 (23,323,3) asdf ('23','123') dasd, "
				+ "PRIMARY KEY (mycol2, mycol)) type = myisam";
		CreateTable createTable = (CreateTable) parserManager.parse(new StringReader(statement));
		assertEquals(2, createTable.getColumnDefinitions().size());
		assertEquals("mycol", ((ColumnDefinition) createTable.getColumnDefinitions().get(0)).getColumnName());
		assertEquals("mycol2", ((ColumnDefinition) createTable.getColumnDefinitions().get(1)).getColumnName());
		assertEquals("PRIMARY KEY", ((Index) createTable.getIndexes().get(0)).getType());
		assertEquals("mycol", ((Index) createTable.getIndexes().get(0)).getColumnsNames().get(1));
		assertEquals(statement, ""+createTable);
	}

	


}
