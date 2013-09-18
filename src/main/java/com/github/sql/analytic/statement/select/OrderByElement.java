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

import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.expression.ExpressionVisitor;







/**
 * An element (column reference) in an "ORDER BY" clause.
 */

public class OrderByElement implements Expression{
	private Expression columnReference;
	private boolean asc = true; 
	

	public boolean isAsc() {
		return asc;
	}

	public void setAsc(boolean b) {
		asc = b;
	}


	public void accept(ExpressionVisitor orderByVisitor){
		orderByVisitor.visit(this);
	}

	public Expression getColumnReference() {
		return columnReference;
	}

	public void setColumnReference(Expression columnReference) {
		this.columnReference = columnReference;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder(columnReference.toString());
		if(!asc){
			builder.append(" DESC");
		}
		return builder.toString();
	}
}
