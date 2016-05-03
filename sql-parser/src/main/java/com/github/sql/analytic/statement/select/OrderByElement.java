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

import com.github.sql.analytic.expression.SQLExpression;
import com.github.sql.analytic.expression.ExpressionVisitor;
import com.github.sql.analytic.statement.StatementVisitor;


/**
 * An element (column reference) in an "ORDER BY" clause.
 */

public class OrderByElement implements SQLExpression{
	
	public enum NullOrdering{
		
		NULLS_FIRST,
		NULLS_LAST
		
	}
	
	private SQLExpression columnReference;
	private boolean asc = true; 
	private NullOrdering nullOrdering;
	

	public boolean isAsc() {
		return asc;
	}

	public void setAsc(boolean b) {
		asc = b;
	}


	public void accept(ExpressionVisitor orderByVisitor){
		orderByVisitor.visit(this);
	}

	public SQLExpression getColumnReference() {
		return columnReference;
	}

	public void setColumnReference(SQLExpression columnReference) {
		this.columnReference = columnReference;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder(columnReference.toString());
		if(!asc){
			builder.append(" DESC");
		}
		if(nullOrdering != null){
			builder.append(' ');
			builder.append(nullOrdering == NullOrdering.NULLS_FIRST ? "NULLS FIRST" : "NULLS LAST");
			builder.append(' ');
		}
		
		return builder.toString();
	}

	public NullOrdering getNullOrdering() {
		return nullOrdering;
	}

	public void setNullOrdering(NullOrdering nullOrdering) {
		this.nullOrdering = nullOrdering;
	}

	
}
