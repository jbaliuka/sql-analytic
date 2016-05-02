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

package com.github.sql.analytic.statement.update;

import java.util.List;

import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.SQLStatement;
import com.github.sql.analytic.statement.StatementVisitor;


/**
 * The update statement.
 */

public class Update implements SQLStatement {
	private Table table;
	private Expression where;
	private List<Column> columns;
	private List<Expression> expressions;

	public void accept(StatementVisitor statementVisitor) {
		statementVisitor.visit(this);
	}

	public Table getTable() {
		return table;
	}

	public Expression getWhere() {
		return where;
	}

	public void setTable(Table name) {
		table = name;
	}

	public void setWhere(Expression expression) {
		where = expression;
	}

	/**
	 * The {@link com.github.sql.analytic.schema.Column}s in this update (as col1 and col2 in UPDATE col1='a', col2='b')
	 * @return a list of {@link com.github.sql.analytic.schema.Column}s
	 */
	public List<Column> getColumns() {
		return columns;
	}

	/**
	 * The {@link Expression}s in this update (as 'a' and 'b' in UPDATE col1='a', col2='b')
	 * @return a list of {@link Expression}s
	 */
	
	public List<Expression> getExpressions() {
		return expressions;
	}

	public void setColumns(List<Column> list) {
		columns = list;
	}

	public void setExpressions(List<Expression> list) {
		expressions = list;
	}
	
	@Override
	public String toString() {		
		StringBuilder buffer = new StringBuilder("UPDATE ");
		buffer.append(table);
		buffer.append(" SET ");
		for(int i = 0; i < columns.size(); i++ ){
			buffer.append(columns.get(i));
			buffer.append("=");
			buffer.append(expressions.get(i));
			if( i < columns.size() - 1){
				buffer.append(",");
			}
		}
		if(where != null){
			buffer.append(" WHERE ");
			buffer.append(where);
		}
		
		return buffer.toString();		
	}

}
