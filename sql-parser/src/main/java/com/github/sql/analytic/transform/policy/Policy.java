package com.github.sql.analytic.transform.policy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.statement.select.WithItem;
import com.github.sql.analytic.transform.DeleteTransform;
import com.github.sql.analytic.transform.InsertTransform;
import com.github.sql.analytic.transform.SelectTransform;
import com.github.sql.analytic.transform.StatementTransform;
import com.github.sql.analytic.transform.UpdateTransform;

public class Policy extends StatementTransform {

	private List<CreatePolicy> policyList;
	private SessionContext sessionContext;
	private Set<Table> tables = new HashSet<Table>();
	private List<WithItem> withItems = new ArrayList<WithItem>();	
	private boolean checkColumns;

	public boolean isCheckColumns() {
		return checkColumns;
	}


	public Policy(List<CreatePolicy> policyList, SessionContext sessionContext) {
		super();
		this.policyList = policyList;
		this.sessionContext = sessionContext;
	}


	public void enablePolicy(String name){
		for(CreatePolicy policy : policyList){
			if(policy.getName().equalsIgnoreCase(name)){
				policy.setEnabled(true);
			}
		}
	}

	public void disablePolicy(String name){
		for(CreatePolicy policy : policyList){
			if(policy.getName().equalsIgnoreCase(name)){
				policy.setEnabled(false);
			}
		}
	}

	public List<CreatePolicy> allTablePolicies(Table table){

		List<CreatePolicy> newPolicyList = new ArrayList<CreatePolicy>();
		for(CreatePolicy policy : policyList){							
			if(match(table, policy)){		
				if(policy.getRoles() != null){
					for(String role: policy.getRoles()){
						if(getSessionContext().isUserInRole(role)){								
							newPolicyList.add(policy);								
						}
					}
				}else {						
					newPolicyList.add(policy);
				}
			}
		}		
		return newPolicyList;

	}

	public List<CreatePolicy> currentPolicies(String action,Table table) {

		List<CreatePolicy> allPolicies = policyList;
		List<CreatePolicy> newPolicyList = new ArrayList<CreatePolicy>();

		for(CreatePolicy policy : allPolicies){
			if( applicableFor(policy,action)){				
				if(match(table, policy)){		
					if(policy.getRoles() != null){
						for(String role: policy.getRoles()){
							if(getSessionContext().isUserInRole(role)){
								checkColumns = checkColumns || policy.getColumns() != null;
								newPolicyList.add(policy);								
							}
						}
					}else {
						checkColumns = checkColumns || policy.getColumns() != null;
						newPolicyList.add(policy);
					}
				}
			}			

		}
		if(newPolicyList.isEmpty()){
			throw new PolicyException(String.format("Unable to find policy list for %s on %s ", table, action)); 
		}

		return newPolicyList;
	}

	public boolean hasPolicy(Table table){		
		return allTablePolicies(table).size() > 0;
	}

	private  boolean match(Table table, CreatePolicy policy) {

		if(policy.isEnabled() && table.getName().equalsIgnoreCase(policy.getTable().getName())){			
			if(table.getSchemaName() == policy.getTable().getSchemaName()){
				return true; 
			}
			String tableSchema = table.getSchemaName() == null ? sessionContext.getDefaultSchema() : table.getSchemaName();
			String policySchema =  policy.getTable().getSchemaName() == null ? sessionContext.getDefaultSchema() : policy.getTable().getSchemaName();
			if(tableSchema != null){
				return tableSchema.equalsIgnoreCase(policySchema);
			}else {
				return false;
			}
		}else {
			return false;
		}



	}

	public boolean applicableFor(CreatePolicy policy,String action) {
		if(policy.isEnabled()){
			if(policy.getActions() == null ){
				return true;
			}
			for( String next : policy.getActions()){
				return action.equalsIgnoreCase(next) ||	"ALL".equalsIgnoreCase(next);
			}
		}
		return false;
	}



	@Override
	protected SelectTransform createSelectTransform() {
		return new SelectPolicy("SELECT",null,this);
	}

	@Override
	protected DeleteTransform createDeleteTransform() {		 
		return new DeletePolicy(this);
	}

	@Override
	protected UpdateTransform createUpdateTransform() {		
		return new UpdatePolicy(this);
	}

	protected InsertTransform createInsertTransform() {

		return new InsertPolicy(this);

	};



	public List<CreatePolicy> getPolicyList() {
		return policyList;
	}

	public SessionContext getSessionContext() {
		return sessionContext;
	}

	public Set<Table> getTables() {		
		return tables;
	}


	public List<WithItem> getWithItems() {		
		return withItems ;
	}


}
