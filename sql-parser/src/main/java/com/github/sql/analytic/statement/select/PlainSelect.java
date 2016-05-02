/* ================================================================
 * JSQLParser : java based sql parser 
 * ================================================================
 *
 * Project Info:  http://jsqlparser.sourceforge.net
 * Project Lead:  Leonardo Francalanci (leoonardoo@yahoo.it);
 *
 * (C) Copyright 2004, by Leonardo Francalanci
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.github.sql.analytic.statement.select;

import java.util.Collection;
import java.util.List;

import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.schema.Table;


/**
 */
/**
 * The core of a "SELECT" statement (no UNION, no ORDER BY) 
 */

public class PlainSelect implements SelectBody {
	private Distinct distinct = null;

	private List<SelectListItem> selectItems;
	private Table into;
	private FromItem fromItem;

	private List<Join> joins;
	private Expression where;

	private List<Expression> groupByColumnReferences;

	private List<OrderByElement> orderByElements;
	private Expression having;
	private Limit limit;
	private Top top;
	private String hints;



	/**
	 * The {@link FromItem} in this query
	 * @return the {@link FromItem}
	 */
	public FromItem getFromItem() {
		return fromItem;
	}

	public Table getInto() {
		return into;
	}

	public String getHints() {
		return hints;
	}

	public void setHints(String hints) {
		this.hints = hints;
	}

	/**
	 * The {@link SelectListItem}s in this query (for example the A,B,C in "SELECT A,B,C")
	 * @return a list of {@link SelectListItem}s
	 */

	public List<SelectListItem> getSelectItems() {
		return selectItems;
	}

	public Expression getWhere() {
		return where;
	}

	public PlainSelect setFromItem(FromItem item) {
		fromItem = item;
		return this;
	}

	public PlainSelect setInto(Table table) {
		into = table;
		return this;
	}



	public PlainSelect setSelectItems(List<SelectListItem> list) {
		selectItems = list;
		return this;
	}

	public PlainSelect setWhere(Expression where) {
		this.where = where;
		return this;
	}


	/**
	 * The list of {@link Join}s
	 * @return the list of {@link Join}s
	 */

	public List<Join> getJoins() {
		return joins;
	}


	public PlainSelect setJoins(List<Join> list) {
		joins = list;
		return this;
	}

	public void accept(SelectVisitor selectVisitor){
		selectVisitor.visit(this);
	}


	public List<OrderByElement> getOrderByElements() {
		return orderByElements;
	}


	public PlainSelect setOrderByElements(List<OrderByElement> orderByElements) {
		this.orderByElements = orderByElements;
		return this;
	}

	public Limit getLimit() {
		return limit;
	}

	public PlainSelect setLimit(Limit limit) {
		this.limit = limit;
		return this;
	}

	public Top getTop() {
		return top;
	}

	public PlainSelect setTop(Top top) {
		this.top = top;
		return this;
	}

	public Distinct getDistinct() {
		return distinct;
	}

	public PlainSelect setDistinct(Distinct distinct) {
		this.distinct = distinct;
		return this;
	}

	public Expression getHaving() {
		return having;
	}

	public PlainSelect setHaving(Expression expression) {
		having = expression;
		return this;
	}

	/**
	 * A list of {@link ColumnReference}s of the GROUP BY clause.
	 * It is null in case there is no GROUP BY clause
	 * @return a list of {@link ColumnReference}s 
	 */

	public List<Expression> getGroupByColumnReferences() {
		return groupByColumnReferences;
	}


	public PlainSelect setGroupByColumnReferences(List<Expression> list) {
		groupByColumnReferences = list;
		return this;
	}

	public String toString() {
		StringBuilder sql = new StringBuilder("SELECT ");

		if(hints != null) {
			sql.append(hints).append(" ");
		}

		if(distinct != null){
			sql.append(distinct + " ");
		}
		if(top != null){
			sql.append(top);
		}

		sql.append(getStringList(selectItems));
		if(fromItem != null ){
			sql.append(" FROM " + fromItem);
			sql.append(getFormatedList(joins, "", false, false));
			if(where != null){
				sql.append(" WHERE " + where);	
			}

			sql.append(getFormatedList(groupByColumnReferences, "GROUP BY"));
			if(having != null){
				sql.append(" HAVING " + having);	
			}	
			sql.append(orderByToString(orderByElements));
			if(limit != null){
				sql.append(limit);
			}
		}

		return sql.toString();
	}



	public static String orderByToString(List<?> orderByElements) {
		return getFormatedList(orderByElements, "ORDER BY");
	}



	public static String getFormatedList(List<?> list, String expression) {
		return getFormatedList(list, expression, true, false);
	}



	public static String getFormatedList(List<?> list, String expression, boolean useComma, boolean useBrackets) {
		String sql = getStringList(list, useComma, useBrackets);

		if (sql.length() > 0) {
			if (expression.length() > 0) {
				sql = " " + expression + " " + sql;
			} else { 
				sql = " " + sql;
			}
		}

		return sql;
	}

	/**
	 * List the toString out put of the objects in the List comma separated. If
	 * the List is null or empty an empty string is returned.
	 * 
	 * The same as getStringList(list, true, false)
	 * @see #getStringList(List, boolean, boolean)
	 * @param list
	 *            list of objects with toString methods
	 * @return comma separated list of the elements in the list
	 */

	public static String getStringList(List<?> list) {
		return getStringList(list, true, false);
	}

	/**
	 * List the toString out put of the objects in the List that can be comma separated. If
	 * the List is null or empty an empty string is returned.
	 * 
	 * @param list list of objects with toString methods
	 * @param useComma true if the list has to be comma separated
	 * @param useBrackets true if the list has to be enclosed in brackets
	 * @return comma separated list of the elements in the list
	 */

	public static String getStringList(Collection<?> list, boolean useComma, boolean useBrackets) {
		StringBuilder ans = new StringBuilder();
		String comma = ",";
		if (!useComma) {
			comma = "";
		}
		if (list != null) {
			if (useBrackets) {
				ans.append("(");
			}

			int i = 0;
			for (Object element : list) {
				ans.append(element); 
				if(i++ < list.size() - 1){
					ans.append(comma + " ");	
				}				
			}

			if (useBrackets) {
				ans.append(")");
			}
		}

		return ans.toString();
	}
}
