package com.github.sql.analytic.transform;

import org.junit.Test;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.test.TestUtil;

import junit.framework.TestCase;

public class ExpressionTransformTest extends TestCase {

	@Test
	public void testVisitConcat() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT 'a' || 'b'");
	}

	@Test
	public void testVisitAddition() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT 2 + 2");
	}

	@Test
	public void testVisitAndExpression() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT CASE WHEN COL1 > 1 AND COL2 > 1 THEN 1 ELSE 0 END");
	}

	@Test
	public void testVisitBetween() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT CASE WHEN COL BETWEEN 1 AND 2 THEN 1 ELSE 0 END");
	}

	@Test
	public void testVisitDivision() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT 2 / 2");
	}

	@Test
	public void testVisitDoubleValue() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT 1.0");
	}

	@Test
	public void testVisitEqualsTo() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT CASE WHEN 2 = 2 THEN 1 ELSE 0 END");
	}

	@Test
	public void testVisitGreaterThan() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT CASE WHEN 2 > 2 THEN 1 ELSE 0 END");
	}

	@Test
	public void testVisitGreaterThanEquals() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT CASE WHEN 2 >= 2 THEN 1 ELSE 0 END");
	}

	@Test
	public void testVisitInExpression() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT CASE WHEN COL IN (1, 2, 3) THEN 1 ELSE 0 END");
		TestUtil.assertTransformable("SELECT CASE WHEN NULL IN (SELECT 1) THEN 1 ELSE 0 END");
	}

	@Test
	public void testVisitInverseExpression() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT -2");
	}

	@Test
	public void testVisitIsNullExpression() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT CASE WHEN NULL IS NOT NULL THEN 1 ELSE 0 END");
		TestUtil.assertTransformable("SELECT CASE WHEN NULL IS NULL THEN 1 ELSE 0 END");
	}

	@Test
	public void testVisitJdbcParameter() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT ?");
	}

	@Test
	public void testVisitLikeExpression() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT CASE WHEN 'A' LIKE '%' THEN 1 ELSE 0 END");
	}

	@Test
	public void testVisitExistsExpression() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT CASE WHEN  EXISTS (SELECT 1) THEN 1 ELSE 0 END");
	}

	@Test
	public void testVisitLongValue() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT 1");
	}

	@Test
	public void testVisitMinorThan() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT CASE WHEN 2 < 2 THEN 1 ELSE 0 END");
	}

	@Test
	public void testVisitMinorThanEquals() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT  CASE WHEN 2 <= 2 THEN 1 ELSE 0 END");
	}

	@Test
	public void testVisitMultiplication() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT 2 * 2");
	}

	@Test
	public void testVisitNotEqualsTo() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT  CASE WHEN 2 <> 2 THEN 1 ELSE 0 END");
	}

	@Test
	public void testVisitNullValue() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT NULL");
	}

	@Test
	public void testVisitOrExpression() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT CASE WHEN COL1 > 1 OR COL2 > 1 THEN 1 ELSE 0 END");
	}

	@Test
	public void testVisitParenthesis() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT (NULL)");
	}

	@Test
	public void testVisitStringValue() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT ''");
	}

	@Test
	public void testVisitSubtraction() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT 2 - 2");
	}

	@Test
	public void testVisitSubSelect() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT (SELECT 1)");
	}

	@Test
	public void testVisitColumn() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT MYCOL");
	}

	@Test
	public void testVisitFunction() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT SIN(0)");
	}

	@Test
	public void testVisitExpressionList() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT 2, A");
	}

	@Test
	public void testVisitDateValue() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT '2016-01-01'");
	}

	
	@Test
	public void testVisitCastExpression() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT CAST( 2 AS FLOAT)");
	}

	@Test
	public void testVisitNamedParameter() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT :param");
	}

	@Test
	public void testAny() throws JSQLParserException {
		TestUtil.assertTransformable("SELECT CASE WHEN 1 = ANY (SELECT 1) THEN 1 ELSE 0 END");
		TestUtil.assertTransformable("SELECT CASE WHEN 1 = ALL (SELECT 1) THEN 1 ELSE 0 END");
	}

	

}
