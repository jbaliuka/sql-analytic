package com.github.sql.analytic.transform;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.test.TestUtil;

import junit.framework.TestCase;

public class DeleteTransformTest extends TestCase {

	public void testTransform() throws JSQLParserException {
		TestUtil.assertTransformable("DELETE FROM TEST");
		TestUtil.assertTransformable("DELETE FROM TEST WHERE 1 = 2");
	}

}
