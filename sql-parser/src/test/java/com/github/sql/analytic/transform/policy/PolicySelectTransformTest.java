package com.github.sql.analytic.transform.policy;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.parser.CCJSqlParserManager;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.SQLStatement;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.test.TestUtil;

import junit.framework.TestCase;

public class PolicySelectTransformTest extends TestCase {


	public void testGetTables() throws JSQLParserException {

		assertTables("SELECT * FROM TEST","TEST");
		assertTables("SELECT * FROM ( SELECT 1 FROM TEST )","TEST");
		assertTables("SELECT (SELECT * FROM TEST) ","TEST");
		assertTables("SELECT * FROM TEST1 WHERE 1 IN (SELECT 1 FROM TEST) ","TEST","TEST1");
		assertTables("WITH CTE AS (SELECT * FROM TEST) SELECT * FROM CTE ","TEST");				
		assertTables("SELECT * FROM TEST,TEST","TEST");

	}

	public void testUsing() throws JSQLParserException{

		String policy1 = "CREATE POLICY P_1 ON  TEST USING ( 1 = 1 ) ";
		String policy2 = "CREATE POLICY P_2 ON  TEST USING ( 2 = 2 ) ";
		String policy3 = "CREATE POLICY P_3 ON  TEST3 USING ( 3 = 3 ) ";



		assertTransform(Collections.<String>emptyList(),"SELECT 1","SELECT 1");
		assertTransform(Arrays.asList(policy1),"SELECT 1","SELECT 1");
		assertTransform(Arrays.asList(policy1),"SELECT 1 FROM TEST WHERE a=b","SELECT 1 FROM TEST WHERE (1=1) AND (a=b)");
		assertTransform(Arrays.asList(policy1),"SELECT * FROM TEST","SELECT * FROM TEST WHERE 1 = 1");
		assertTransform(Arrays.asList(policy1,policy2),"SELECT * FROM TEST","SELECT * FROM TEST WHERE (2 = 2) OR (1 = 1)");
		assertTransform(Arrays.asList(policy1,policy3),"SELECT * FROM TEST,TEST3","SELECT * FROM TEST,TEST3 WHERE (3 = 3) AND (1=1)");

		assertTransform(Arrays.asList(policy1),"DELETE FROM TEST","DELETE FROM TEST WHERE 1 = 1");
		assertTransform(Arrays.asList(policy1),"UPDATE TEST SET col=1","UPDATE TEST SET col=1 WHERE 1 = 1");		



	}

	public void testNewValus() throws JSQLParserException{

		String policy1 = "CREATE POLICY P_1 ON TEST USING(user = :current) WITH CHECK ( user = :current ) ";
		String policy2 = "CREATE POLICY P_2 ON TEST2 ";
		String policy3 = "CREATE POLICY P_3 ON TEST WITH CHECK ( user = :current AND role = :role )";

		assertTransform(Arrays.asList(policy1),"UPDATE TEST SET user = 'tom'",
				"UPDATE TEST SET user = 'tom' WHERE ('tom' = :current) AND (TEST.user = :current)");

		assertTransform(Arrays.asList(policy1),"INSERT INTO TEST(user) VALUES ('tom')",
				"INSERT INTO TEST(user) SELECT 'tom' FROM (SELECT 1) DUAL WHERE 'tom' = :current");

		assertTransform(Arrays.asList(policy1),"INSERT INTO TEST(user) SELECT 'tom'",
				"INSERT INTO TEST(user) SELECT 'tom' FROM (SELECT 1) DUAL WHERE 'tom' = :current");

		assertTransform(Arrays.asList(policy1,policy2),"INSERT INTO TEST(user) SELECT 'tom' FROM TEST2",
				"INSERT INTO TEST(user) SELECT 'tom' FROM TEST2 WHERE 'tom' = :current");

		assertTransform(Arrays.asList(policy3),"UPDATE TEST SET user = 'tom'",
				"UPDATE TEST SET user = 'tom' WHERE 'tom' = :current AND role = :role");

		assertTransform(Arrays.asList(policy3),"INSERT INTO TEST(user)VALUES('tom')",
				"INSERT INTO TEST(user) SELECT 'tom' FROM (SELECT 1) DUAL WHERE 'tom' = :current AND NULL = :role");



	}

    public void testUsingAlias() throws JSQLParserException {

        String policy1 = "CREATE POLICY P_1 ON  TEST USING ( col = 1 ) ";
        String policy2 = "CREATE POLICY P_1 ON  TEST USING ( col = 1 OR col IN (SELECT col2 FROM TEST2)) ";
        String policy3 = "CREATE POLICY P_1 ON  TEST USING ( col = 1 OR col IN ('A', 'B', 'C')) ";
        
        assertTransform(Arrays.asList(policy1), "SELECT 1 FROM TEST A", "SELECT 1 FROM TEST A WHERE A.col = 1");
        assertTransform(Arrays.asList(policy1), "SELECT 1 FROM TEST", "SELECT 1 FROM TEST  WHERE TEST.col = 1");
        assertTransform(Arrays.asList(policy1), "SELECT 1 FROM TEST A,TEST B",
                "SELECT 1 FROM TEST A,TEST B WHERE (B.col = 1) AND (A.col = 1)");

        assertTransform(Arrays.asList(policy2), "SELECT 1 FROM TEST A",
                "SELECT 1 FROM TEST A WHERE A.col = 1 OR A.col IN (SELECT col2 FROM TEST2)");
        assertTransform(Arrays.asList(policy3), "SELECT 1 FROM TEST A",
                "SELECT 1 FROM TEST A WHERE A.col = 1 OR A.col IN ('A', 'B', 'C')");
    }

	public void testColumns() throws JSQLParserException{

		String policy1 = "CREATE POLICY P_1 ON  TEST ( col ) ";		
		assertTransform(Arrays.asList(policy1),"SELECT a.col FROM TEST A","SELECT a.col FROM TEST A");
		assertTransform(Arrays.asList(policy1),"SELECT TEST.col FROM TEST","SELECT TEST.col FROM TEST");
		assertTransform(Arrays.asList(policy1),"SELECT LENGHT(TEST.col) FROM TEST","SELECT LENGHT(TEST.col) FROM TEST");
		try{
			assertTransform(Arrays.asList(policy1),"SELECT (SELECT TEST.hidden FROM TEST ) FROM TEST","SELECT (SELECT TEST.hidden FROM TEST ) FROM TEST");
			assertTrue(false);
		}catch(PolicyException expected){
			assertTrue(true);
		}

		try{
			assertTransform(Arrays.asList(policy1),"UPDATE TEST  SET hidden = 1","UPDATE TEST SET hidden = 1");
			assertTrue(false);
		}catch(PolicyException expected){
			assertTrue(true);
		}

		try{
			assertTransform(Arrays.asList(policy1),"INSERT INTO TEST(hidden)VALUES(1)","INSERT INTO TEST(hidden)VALUES(1)");
			assertTrue(false);
		}catch(PolicyException expected){
			assertTrue(true);
		}
		try{
			assertTransform(Arrays.asList(policy1),"INSERT INTO TEST(hidden)select 1","INSERT INTO TEST(hidden)select 1");
			assertTrue(false);
		}catch(PolicyException expected){
			assertTrue(true);
		}



	}

	private void assertTransform(List<String> policyList,String sql,String expected) throws JSQLParserException{

		List<CreatePolicy> list = TestUtil.parsePolicyList(policyList);

		CCJSqlParserManager parserManager = new CCJSqlParserManager();				
		Policy transform = new Policy(list, TestUtil.mockContext("test") );		
		SQLStatement stmt = parserManager.transform(new StringReader(sql), transform);		
		TestUtil.assertEqual(expected, stmt);

	}



	private void assertTables(String sql,String ... tables) throws JSQLParserException{
		assertEquals(new HashSet<String>(Arrays.asList(tables)), getTableNames(sql));
	}

	private Set<String> getTableNames(String sql) throws JSQLParserException{

		Set<String> tableNames = new HashSet<String>();

		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		List<CreatePolicy> list = TestUtil.parsePolicyList(Arrays.asList("CREATE POLICY TST ON TEST","CREATE POLICY TST ON TEST1"));		
		Policy transform = new Policy(list, TestUtil.mockContext("test") );		
		parserManager.transform(new StringReader(sql), transform);

		for(Table table: transform.getTables()){
			tableNames.add(table.getWholeTableName());
		}

		return tableNames;

	}

}
