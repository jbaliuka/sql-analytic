package com.github.sql.analytic.statement.select;

/**
 * A top clause in the form [TOP row_count] 
 */

public class Top {
	private long rowCount ;
	private boolean rowCountJdbcParameter = false;

	public long getRowCount() {
		return rowCount;
	}

	
	public void setRowCount(long l) {
		rowCount = l;
	}

	public boolean isRowCountJdbcParameter() {
		return rowCountJdbcParameter;
	}

	public void setRowCountJdbcParameter(boolean b) {
		rowCountJdbcParameter = b;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder("TOP ");
		if(rowCountJdbcParameter){
			builder.append("?");			
		}else {
			builder.append(rowCount);
		}
		
		return builder.toString();
	}


}
