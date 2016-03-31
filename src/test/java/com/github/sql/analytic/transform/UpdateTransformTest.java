package com.github.sql.analytic.transform;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.test.TestUtil;

import junit.framework.TestCase;

public class UpdateTransformTest extends TestCase {

	public void testTransform() throws JSQLParserException {
		TestUtil.assertTransformable("UPDATE TEST SET A = 1, B = 2");
		TestUtil.assertTransformable("UPDATE TEST SET A = 1, B = 2 WHERE A=B");
	}

}
