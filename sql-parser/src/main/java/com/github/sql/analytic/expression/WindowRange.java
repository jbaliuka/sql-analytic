package com.github.sql.analytic.expression;

public class WindowRange {

	private WindowOffset start;
	private WindowOffset end;


	public WindowOffset getEnd() {
		return end;
	}
	public void setEnd(WindowOffset end) {
		this.end = end;
	}
	public WindowOffset getStart() {
		return start;
	}
	public void setStart(WindowOffset start) {
		this.start = start;
	}

	@Override
	public String toString() {		
		StringBuffer buffer = new StringBuffer();		
		buffer.append(" BETWEEN");
		buffer.append(start);
		buffer.append(" AND ");
		buffer.append(end);		
		return buffer.toString();

	}

}
