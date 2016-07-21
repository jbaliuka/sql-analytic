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

import com.github.sql.analytic.expression.Function;
import com.github.sql.analytic.expression.SQLExpression;

/**
 * An expression as in "SELECT expr1 AS EXPR"
 */

public class SelectExpressionItem implements SelectListItem {
	private SQLExpression expression;
	private String alias;
	
	public SelectExpressionItem(Function expr) {
		this.expression = expr;
	}

	public SelectExpressionItem() {
		
	}

	public String getAlias() {
		return alias;
	}

	public SQLExpression getExpression() {
		return expression;
	}

	public SelectExpressionItem setAlias(String string) {
		alias = string;
		return this;
	}

	public SelectExpressionItem setExpression(SQLExpression expression) {
		this.expression = expression;
		return this;
	}

	public void accept(SelectItemVisitor selectItemVisitor) {
		selectItemVisitor.visit(this);
	}
	
	public String toString() {
		return expression+((alias!=null)?" AS "+alias:"");
	}
}
