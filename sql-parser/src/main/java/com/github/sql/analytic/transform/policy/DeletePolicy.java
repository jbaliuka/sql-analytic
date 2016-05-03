package com.github.sql.analytic.transform.policy;

import com.github.sql.analytic.expression.SQLExpression;
import com.github.sql.analytic.transform.DeleteTransform;

public class DeletePolicy extends DeleteTransform {

	private Policy policyTransform;

	public DeletePolicy(Policy policyTransform) {
		super(policyTransform);		
		this.policyTransform = policyTransform;
	}
	
	@Override
	protected SQLExpression transformWhere(SQLExpression where) {
		SelectPolicy policy = new SelectPolicy("DELETE", null, policyTransform);
		policy.addFrom(getTable());
		
		return policy.transformWhere(where);
	}

}
