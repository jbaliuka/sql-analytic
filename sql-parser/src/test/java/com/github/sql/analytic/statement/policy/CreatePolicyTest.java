package com.github.sql.analytic.statement.policy;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.test.TestUtil;

import junit.framework.TestCase;

public class CreatePolicyTest extends TestCase {

	public void testAccept() throws JSQLParserException {
		TestUtil.assertParseable("CREATE POLICY USERS1 ON MY_USER");
		TestUtil.assertParseable("CREATE POLICY USERS2 ON MY_USER FOR SELECT");
		TestUtil.assertParseable("CREATE POLICY USERS3 ON MY_USER TO PUBLIC");
		TestUtil.assertParseable("CREATE POLICY USERS4 ON MY_USER USING( user_id = SESSION_USER )");
		
		TestUtil.assertParseable("CREATE POLICY PASSWORDS1 ON MY_USER(ID,NANE,PASSWORD) FOR INSERT WITH CHECK( LENGHT(password) > 6 )");		
		TestUtil.assertParseable("CREATE POLICY PASSWORDS2 ON MY_USER(ID,NANE,PASSWORD) FOR UPDATE"
				+ " USING (user_id = SESSION_USER)"
		 );
	}

}
