package com.github.sql.analytic.transform;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.test.TestUtil;

import junit.framework.TestCase;

public class InsertTransformTest extends TestCase {

	public void testTransform() throws JSQLParserException {
		TestUtil.assertTransformable("INSERT INTO TEST (a,b) VALUES(1,2)");
		TestUtil.assertTransformable("INSERT INTO TEST  VALUES(1,2)");
		TestUtil.assertTransformable("INSERT INTO TEST (a,b) SELECT 1,2");
		TestUtil.assertTransformable("INSERT INTO TEST  SELECT 1,2 FROM DUAL");
	}

}
