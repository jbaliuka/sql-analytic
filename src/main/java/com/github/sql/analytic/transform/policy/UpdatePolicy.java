package com.github.sql.analytic.transform.policy;

import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.transform.UpdateTransform;

public class UpdatePolicy extends UpdateTransform {

	private Policy policyTransform;

	public UpdatePolicy(Policy policyTransform) {
		super(policyTransform);
		this.policyTransform = policyTransform;
	}
	
	@Override
	protected Expression transformWhere(Expression where) {
		SelectPolicy policy = new SelectPolicy("UPDATE", false, policyTransform);
		policy.getTables().add(getTable());		
		return policy.transformWhere(where);
	}

}
