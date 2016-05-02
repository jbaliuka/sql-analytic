package com.github.sql.analytic.statement.select;

import java.util.List;

/**
 * A DISTINCT [ON (expression, ...)] clause
 */

public class Distinct {
	private List<SelectListItem> onSelectItems;
	
	/**
	 * A list of {@link SelectListItem}s expressions, as in "select DISTINCT ON (a,b,c) a,b FROM..." 
	 * @return a list of {@link SelectListItem}s expressions
	 */
	public List<SelectListItem> getOnSelectItems() {
		return onSelectItems;
	}

	public void setOnSelectItems(List<SelectListItem> list) {
		onSelectItems = list;
	}

	public String toString() {
		String sql = "DISTINCT";
		
		if(onSelectItems != null && onSelectItems.size() > 0) {
			sql += " ON ("+PlainSelect.getStringList(onSelectItems)+")";
		}
		
		return sql;
	}
}
