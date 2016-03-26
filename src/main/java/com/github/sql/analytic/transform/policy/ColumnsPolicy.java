package com.github.sql.analytic.transform.policy;

import java.util.List;

import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.statement.select.AllColumns;
import com.github.sql.analytic.statement.select.AllTableColumns;
import com.github.sql.analytic.statement.select.SelectExpressionItem;
import com.github.sql.analytic.transform.ExpressionTransform;
import com.github.sql.analytic.transform.SelectItemTransfrom;

public final class ColumnsPolicy extends SelectItemTransfrom {
	
	private Policy statementTransform; 
	private SelectPolicy selectTransform;
	
	public ColumnsPolicy(SelectPolicy selectTransform) {
		super(selectTransform.getStatementTransform());
		this.statementTransform = selectTransform.getStatementTransform();
		this.selectTransform = selectTransform;
	}

	@Override
	public void visit(AllColumns allColumns) {
		for(Table table : selectTransform.getTables()){
			List<CreatePolicy> policyList = statementTransform.findTablePolicies("select", table);				
				for(CreatePolicy policy : policyList){
					if(policy.getColumns() == null){
						return;
					}
				}				
		}
		throw new PolicyException(" Access denied: " + allColumns);
	}

	@Override
	public void visit(AllTableColumns allTableColumns) {
		for(Table table : selectTransform.getTables()){
			List<CreatePolicy> policyList = statementTransform.findTablePolicies("select", table);
			if(table.getWholeTableName().equalsIgnoreCase(allTableColumns.getTable().getWholeTableName())||
					allTableColumns.getTable().getName().equalsIgnoreCase(table.getAlias())
					){
				for(CreatePolicy policy : policyList){
					if(policy.getColumns() == null){
						return;
					}
				}
			}
		}

		throw new PolicyException(" Access denied: column " + allTableColumns);
	}

	@Override
	public void visit(Column column) {
		if(column.getTable() == null || column.getTable().getName() == null){
			throw new PolicyException(" Access denied: " + column);
		}
		
		for(Table table : selectTransform.getTables()){
			List<CreatePolicy> policyList = statementTransform.findTablePolicies("select", table);
			if(table.getWholeTableName().equalsIgnoreCase(column.getTable().getWholeTableName())||
					column.getTable().getName().equalsIgnoreCase(table.getAlias())
					){
				for(CreatePolicy policy : policyList){
					if(policy.getColumns() == null){
						return;
					}else {
						for(Column pcol : policy.getColumns()){
						   if(pcol.getColumnName().equalsIgnoreCase(column.getColumnName())){
							   return;
						   }										
						}
					}
				}
			}
		}

		throw new PolicyException(" Access denied: " + column);

	}

	@Override
	public void visit(SelectExpressionItem selectExpressionItem) {
		selectExpressionItem.getExpression().accept(new ExpressionTransform(statementTransform){				
			public void visit(Column column){
				ColumnsPolicy.this.visit(column); 
			 }
			
		});
	}
}