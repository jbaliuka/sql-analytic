package com.github.sql.analytic.expression;


public class AnalyticClause implements SQLExpression{
	
	private QueryPartitionClause queryPartitionClause;
	private OrderByClause orderByClause;
	
	public OrderByClause getOrderByClause() {
		return orderByClause;
	}

	public AnalyticClause setOrderByClause(OrderByClause orderByCause) {
		this.orderByClause = orderByCause;
		return this;
	}

	private Function function;

	
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}

	public QueryPartitionClause getQueryPartitionClause() {
		return queryPartitionClause;
	}

	public void setQueryPartitionClause(QueryPartitionClause queryPartitionCause) {
		this.queryPartitionClause = queryPartitionCause;
	}

	
	
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer(" OVER (");
		if(queryPartitionClause != null){
			buffer.append(queryPartitionClause.toString());			
		}
		if(orderByClause != null){
			if(queryPartitionClause != null){
				buffer.append(' ');
			}
			buffer.append(orderByClause.toString());
		}
		
		
		buffer.append(")");
		
		return buffer.toString();
		
	}

	public Function getFunction() {
		return function;
	}

	public void setFunction(Function function) {
		this.function = function;
	}

}
