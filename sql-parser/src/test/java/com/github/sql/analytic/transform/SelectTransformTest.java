package com.github.sql.analytic.transform;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.test.TestUtil;

import junit.framework.TestCase;

public class SelectTransformTest extends TestCase {

	public void testVisitFrom() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT 2 + 2 FROM A");
		TestUtil.assertTransformable("SELECT 2 + 2 FROM A  B");
		TestUtil.assertTransformable("SELECT 2 + 2 FROM (SELECT 1)  B");
		TestUtil.assertTransformable("SELECT 2 + 2 FROM TABLE(TEST()) B ");
	}

	public void testTransformWhere() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT 2 + 2 FROM A WHERE 1 - 3 > 1");
	}

	public void testTransformDistinct() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT DISTINCT A, B");
		TestUtil.assertTransformable("SELECT DISTINCT ON (A, B) a, b, c FROM TEST");
	}

	public void testVisitUnion() throws JSQLParserException {
		TestUtil.assertTransformable("(SELECT 1 )UNION( SELECT 1)");
		TestUtil.assertTransformable("(SELECT 1 )UNION ALL ( SELECT 1)");
	}

		
	public void testTransformGroupBy() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT 1 FROM A GROUP BY a");
		TestUtil.assertTransformable("SELECT 1 FROM A GROUP BY a HAVING A > 1");
		TestUtil.assertTransformable("SELECT 1 FROM A GROUP BY CUBE(a, b)");
		TestUtil.assertTransformable("SELECT 1 FROM A GROUP BY GROUPING SETS(a, (b, c))");
		TestUtil.assertTransformable("SELECT 1 FROM A GROUP BY GROUPING SETS(a, CUBE(b, c))");
		
	}
	
	public void testTransformAnalytics() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT SUM(A) OVER () FROM A");
		TestUtil.assertTransformable("SELECT SUM(A) OVER (PARTITION BY A ORDER BY C) FROM A");
		
	}
	
	public void testCTE() throws JSQLParserException {
		TestUtil.assertTransformable("WITH TEST AS (SELECT 1 AS A, 2 AS B) SELECT * FROM TEST");
	}
	
	
	

	public void testTransformLimit() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT * FROM TEST LIMIT 1 OFFSET 1");
	}
	
	public void testTransformOrderBy() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT * FROM TEST ORDER BY A ASC, B DESC");
	}


	public void testTransformJoin() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT * FROM TEST  A, TEST  B WHERE A.ID = B.PARENT_ID");
		TestUtil.assertTransformable("SELECT * FROM TEST  A JOIN TEST  B WHERE A.ID = B.PARENT_ID");
		TestUtil.assertTransformable("SELECT * FROM TEST  A LEFT JOIN TEST  B WHERE A.ID = B.PARENT_ID");
		TestUtil.assertTransformable("SELECT * FROM TEST  A LEFT JOIN TEST  B ON A.ID = B.PARENT_ID");
		TestUtil.assertTransformable("SELECT * FROM TEST  A JOIN TEST  B USING (ID)");
	}

}
