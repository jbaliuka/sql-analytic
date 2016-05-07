package com.github.sql.analytic.statement.select;

import java.util.List;
/**
 * One of the parts of a "WITH" clause of a "SELECT" statement  
 */

public class WithItem {
	private String name;
	
	private	List<SelectListItem> withItemList;
	private SelectBody selectBody;

	/**
	 * The name of this WITH item (for example, "myWITH" in "WITH myWITH AS (SELECT A,B,C))"
	 * @return the name of this WITH
	 */
	public String getName() {
		return name;
	}
	public WithItem setName(String name) {
		this.name = name;
		return this;
	}
	
	/**
	 * The {@link SelectBody} of this WITH item is the part after the "AS" keyword
	 * @return {@link SelectBody} of this WITH item
	 */
	public SelectBody getSelectBody() {
		return selectBody;
	}
	public WithItem setSelectBody(SelectBody selectBody) {
		this.selectBody = selectBody;
		return this;
	}
	
	/**
	 * The {@link SelectListItem}s in this WITH (for example the A,B,C in "WITH mywith (A,B,C) AS ...")
	 * @return a list of {@link SelectListItem}s
	 */
	
	public List<SelectListItem> getWithItemList() {
		return withItemList;
	}
	
	public void setWithItemList(List<SelectListItem> withItemList) {
		this.withItemList = withItemList;
	}
	
	public String toString() {
		return name + ((withItemList != null)?" " + PlainSelect.getStringList(withItemList, true, true):"") + " AS (" + selectBody + ")";
	}
	
	
}
