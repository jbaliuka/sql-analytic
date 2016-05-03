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
 
package com.github.sql.analytic.schema;

import com.github.sql.analytic.expression.SQLExpression;
import com.github.sql.analytic.statement.select.FromItem;
import com.github.sql.analytic.statement.select.FromItemVisitor;
import com.github.sql.analytic.statement.select.IntoTableVisitor;

/**
 * A table. It can have an alias and the schema name it belongs to. 
 */

public class Table implements FromItem {
	private String schemaName;
	private String name;
	private String alias;
	private SQLExpression partition;
	private boolean partitionFor;
	
	public Table() {
	}

	public Table(String schemaName, String name) {
		this.schemaName = schemaName;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public Table setName(String string) {
		name = string;
		return this;
	}

	public Table setSchemaName(String string) {
		schemaName = string;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String string) {
		alias = string;
	}
	
	public String getWholeTableName() {

		String tableWholeName = null;
		if (name == null) {
			return null;
		}
		if (schemaName != null) {
			tableWholeName = schemaName + "." + name;
		} else {
			tableWholeName = name;
		}
		
		return tableWholeName;

	}

	public void accept(FromItemVisitor fromItemVisitor) {
		fromItemVisitor.visit(this);
	}
	
	public void accept(IntoTableVisitor intoTableVisitor) {
		intoTableVisitor.visit(this);
	}
	
	
	public String toString() {
		String tableName = getWholeTableName()+((alias!=null)?" AS "+alias:"");
		if(partition != null){
			tableName = tableName + " PARTITION ";
			if(partitionFor){
				tableName = tableName + " FOR";
			}
			
			tableName = tableName + "(" + partition + ")";
		}
		return tableName;
	}

	public SQLExpression getPartition() {
		return partition;
	}

	public void setPartition(SQLExpression partition) {
		this.partition = partition;
	}

	public boolean isPartitionFor() {
		return partitionFor;
	}

	public void setPartitionFor(boolean partitionFor) {
		this.partitionFor = partitionFor;
	}
}
