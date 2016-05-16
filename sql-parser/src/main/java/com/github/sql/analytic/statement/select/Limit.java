package com.github.sql.analytic.statement.select;

/**
 * A limit clause in the form [LIMIT {[offset,] row_count) | (row_count | ALL) OFFSET offset}]
 */

public class Limit {
	private long offset;
	private long rowCount ;
	private boolean rowCountJdbcParameter = false;
	private boolean offsetJdbcParameter = false;
	private boolean limitAll;
	
	public Limit(int i, int j) {
		rowCount = i;
		offset = j;
	}

	public Limit() {		
	}

	public long getOffset() {
		return offset;
	}

	public long getRowCount() {
		return rowCount;
	}

	public Limit setOffset(long l) {
		offset = l;
		return this;
	}

	public Limit setRowCount(long l) {
		rowCount = l;
		return this;
	}

	public boolean isOffsetJdbcParameter() {
		return offsetJdbcParameter;
	}

	public boolean isRowCountJdbcParameter() {
		return rowCountJdbcParameter;
	}

	public void setOffsetJdbcParameter(boolean b) {
		offsetJdbcParameter = b;
	}

	public void setRowCountJdbcParameter(boolean b) {
		rowCountJdbcParameter = b;
	}


	/**
	 * @return true if the limit is "LIMIT ALL [OFFSET ...])
	 */
	public boolean isLimitAll() {
		return limitAll;
	}

	public void setLimitAll(boolean b) {
		limitAll = b;
	}

	public String toString() {
	    StringBuilder retVal = new StringBuilder();
	    if (rowCount > 0 || rowCountJdbcParameter ) {
	        retVal.append(" LIMIT "+(rowCountJdbcParameter?"?":rowCount));
	    }
	    if (offset > 0 || offsetJdbcParameter) {
	    	retVal.append(" OFFSET "+ ( offsetJdbcParameter ? "?" : offset ));
	    }
	    return retVal.toString();
	}
}
