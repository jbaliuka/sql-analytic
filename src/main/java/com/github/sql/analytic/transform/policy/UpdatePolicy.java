package com.github.sql.analytic.transform.policy;

import java.util.List;

import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.transform.NewValue;
import com.github.sql.analytic.transform.UpdateTransform;

public class UpdatePolicy extends UpdateTransform {

	private Policy policyTransform;
	private List<NewValue> values;

	public UpdatePolicy(Policy policyTransform) {
		super(policyTransform);
		this.policyTransform = policyTransform;
	}
	
	@Override
	protected List<NewValue> transformItems(List<NewValue> newValues) {
	    values = newValues;
		return newValues;
	}
	
	@Override
	protected Expression transformWhere(Expression where) {
		
		SelectPolicy policy = new SelectPolicy("UPDATE", false,values, policyTransform);
		policy.addFrom(getTable());		
		
		return policy.transformWhere(where);
	}

}
