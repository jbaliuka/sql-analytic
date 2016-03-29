package com.github.sql.analytic.transform.policy;

import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.transform.DeleteTransform;

public class DeletePolicy extends DeleteTransform {

	private Policy policyTransform;

	public DeletePolicy(Policy policyTransform) {
		super(policyTransform);		
		this.policyTransform = policyTransform;
	}
	
	@Override
	protected Expression transformWhere(Expression where) {
		SelectPolicy policy = new SelectPolicy("DELETE", false,null, policyTransform);
		policy.addFrom(getTable());
		
		return policy.transformWhere(where);
	}

}
