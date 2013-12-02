package com.github.sql.analytic.expression;


public class AnalyticCause {
	
	private QueryPartitionCause queryPartitionCause;
	private OrderByCause orderByCause;
	
	public OrderByCause getOrderByCause() {
		return orderByCause;
	}

	public void setOrderByCause(OrderByCause orderByCause) {
		this.orderByCause = orderByCause;
	}

	private Function function;

	
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}

	public QueryPartitionCause getQueryPartitionCause() {
		return queryPartitionCause;
	}

	public void setQueryPartitionCause(QueryPartitionCause queryPartitionCause) {
		this.queryPartitionCause = queryPartitionCause;
	}

	
	
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer(" OVER (");
		if(queryPartitionCause != null){
			buffer.append(queryPartitionCause.toString());			
		}
		if(orderByCause != null){
			if(queryPartitionCause != null){
				buffer.append(' ');
			}
			buffer.append(orderByCause.toString());
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
