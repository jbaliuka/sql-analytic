package com.github.sql.analytic.transform.policy;

import java.util.List;

import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.select.WithItem;
import com.github.sql.analytic.transform.FromItemTransform;

public class PolicyFromItemTransfrom extends FromItemTransform {

	private SelectPolicy policySelectTransform;
	
	
	public PolicyFromItemTransfrom(SelectPolicy policySelectTransform) {
		super(policySelectTransform);
		this.policySelectTransform = policySelectTransform;
	}
	
	@Override
	public void visit(Table tableName) {		
		super.visit(tableName);
		List<WithItem> withItems = policySelectTransform.getPolicyTransform().getWithItems();
		boolean cte = false;
		for(WithItem item: withItems ){
			cte = cte || item.getName().equalsIgnoreCase(tableName.getWholeTableName());
		}
		if(!cte){
		  policySelectTransform.addTable(tableName);
		}
	}

}
