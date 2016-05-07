package com.github.sql.analytic.transform.policy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.test.TestUtil;

import junit.framework.TestCase;

public class PolicyTransformTest extends TestCase {

	public void testFindTablePolicies() throws JSQLParserException {
		
		String policy1 = "CREATE POLICY P_1 ON  TEST  FOR SELECT";
		String policy2 = "CREATE POLICY P_2 ON  TEST  FOR ALL";
		String policy3 = "CREATE POLICY P_3 ON  TEST TO testRole";
		String policy4 = "CREATE POLICY P_3 ON  TEST4 TO testRole";

		assertEquals(Arrays.asList("P_1"), getAppliedPolicyList(policy1));
		assertEquals(Arrays.asList("P_1","P_2"), getAppliedPolicyList(policy1,policy2));
		assertEquals(Arrays.asList("P_1","P_2","P_3"), getAppliedPolicyList(policy1,policy2,policy3));
		assertEquals(Arrays.asList("P_1","P_2","P_3"), getAppliedPolicyList(policy1,policy2,policy3,policy4));


	}
	List<String> getAppliedPolicyList(String ... policy  ) throws JSQLParserException{

		List<CreatePolicy> list = TestUtil.parsePolicyList(Arrays.asList(policy));
		Policy transform = new Policy(list, TestUtil.mockContext("testUser","testRole")  );
		List<String> names = new ArrayList<String>();	
		
		for (CreatePolicy next : transform.currentPolicies("SELECT", new Table(null, "TEST"))){
			names.add(next.getName());
		}

		return names;
	}

}
