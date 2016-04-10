package com.github.sql.analytic.expression;


public class WindowOffset {
	
	public enum Type {
		PRECEDING,
		FOLLOWING,
		CURRENT,
		EXPR
		
	}	
	private Expression expression;
	private Type type;
	
	public Expression getExpression() {
		return expression;
	}
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if(expression != null){
			buffer.append(' ');
			buffer.append(expression);
			if(type != null){
				buffer.append(' ');
				buffer.append(type);
			}
			buffer.append(' ');
		}else {
			switch (type) {
			case PRECEDING:
				buffer.append(' ');
				buffer.append("UNBOUNDED");
				buffer.append(' ');
				buffer.append(Type.PRECEDING);
				break;
			case FOLLOWING:
				buffer.append(' ');
				buffer.append("UNBOUNDED");
				buffer.append(' ');
				buffer.append(Type.FOLLOWING);
				break;
			case CURRENT:
				buffer.append(' ');
				buffer.append("CURRENT ROW");
				buffer.append(' ');				
				break;
			default:
				break;
			}
			
		}
		
		
		return buffer.toString();
		
	}

}
