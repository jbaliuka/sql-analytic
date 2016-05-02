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

package com.github.sql.analytic.statement.insert;

import java.util.List;

import com.github.sql.analytic.expression.operators.relational.ItemsList;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.SQLStatement;
import com.github.sql.analytic.statement.StatementVisitor;
import com.github.sql.analytic.statement.select.PlainSelect;



/**
 * The insert statement.
 * Every column name in <code>columnNames</code> matches an item in <code>itemsList</code>
 */

public class Insert implements SQLStatement {
	
	private Table table;	
	private List<Column> columns;
	private ItemsList itemsList;
	private boolean useValues = true;
	
	public void accept(StatementVisitor statementVisitor) {
		statementVisitor.visit(this);
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table name) {
		table = name;
	}

	
	public List<Column> getColumns() {
		return columns;
	}

	
	public void setColumns(List<Column> list) {
		columns = list;
	}

	/**
	 * Get the values (as VALUES (...) or SELECT) 
	 * @return the values of the insert
	 */
	public ItemsList getItemsList() {
		return itemsList;
	}

	public void setItemsList(ItemsList list) {
		itemsList = list;
	}

    public boolean isUseValues() {
        return useValues;
    }
    
    public void setUseValues(boolean useValues) {
        this.useValues = useValues;
    }
    
	public String toString() {
		StringBuilder sql = new StringBuilder();

		sql.append("INSERT INTO ");
		sql.append(table + " ");
		if(columns!=null){
			sql.append(PlainSelect.getStringList(columns, true, true) + " ");	
		}
		
		
		if(useValues) {
			sql.append("VALUES " + itemsList);
		} else {
			sql.append(itemsList);
		}
		
		return sql.toString();
	}

}
