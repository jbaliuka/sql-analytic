package com.github.sql.analytic.transform.policy;

import java.util.List;

import com.github.sql.analytic.expression.SQLExpression;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.statement.policy.CreatePolicy;
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
	protected SQLExpression transformWhere(SQLExpression where) {

		SelectPolicy policy = new SelectPolicy("UPDATE", values, policyTransform);
		policy.addFrom(getTable());		
		policy.setToTable(getTable());

		checkColumnPolicy();

		return policy.transformWhere(where);
	}

	protected void checkColumnPolicy() {
		
		List<CreatePolicy> list = policyTransform.currentPolicies("UPDATE", getTable());
		for( NewValue value : values ){
			boolean auth = false;
			for(CreatePolicy p : list ){
				if(p.getColumns() == null){
					auth = true;
					break;
				}
				for( Column next : p.getColumns() ){
					if(next.getColumnName().equalsIgnoreCase(value.getColumn().getColumnName())){
						auth = true;
						break;
					}
				}
				if(auth){
					break;
				}
			}
			if(!auth){
				throw new PolicyException(" Access denied: column " + value.getColumn());
			}
		}
	}

}
