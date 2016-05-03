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

package com.github.sql.analytic.expression;


/**
 * A basic class for binary expressions, that is expressions having a left member and a right member
 * wich are in turn expressions. 
 */

public abstract class BinaryExpression implements SQLExpression {
	private SQLExpression leftExpression;
	private SQLExpression rightExpression;
	private boolean not = false;

	public BinaryExpression() {
	}
	

	public SQLExpression getLeftExpression() {
		return leftExpression;
	}

	public SQLExpression getRightExpression() {
		return rightExpression;
	}

	public BinaryExpression setLeftExpression(SQLExpression expression) {
		leftExpression = expression;
		return this;
	}

	public BinaryExpression setRightExpression(SQLExpression expression) {
		rightExpression = expression;
		return this;
	}

	public BinaryExpression setNot() {
		not = true;
		return this;
	}

	public boolean isNot() {
		return not;
	}

	public String toString() {
		return (not? "NOT ":"") + getLeftExpression()+" "+getStringExpression()+" "+getRightExpression();
	}

	public abstract String getStringExpression();
	
}
