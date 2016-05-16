package com.github.sql.analytic.util.deparser;

import java.util.Iterator;

import com.github.sql.analytic.statement.create.table.ColumnDefinition;
import com.github.sql.analytic.statement.create.table.CreateTable;
import com.github.sql.analytic.statement.create.table.Index;


/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a string)
 * a {@link com.github.sql.analytic.statement.create.table.CreateTable}
 */

public class CreateTableDeParser {
	protected StringBuffer buffer;

	/**
	 * @param buffer the buffer that will be filled with the select
	 */
	public CreateTableDeParser(StringBuffer buffer) {
		this.buffer = buffer;
	}

	public void deParse(CreateTable createTable) {
		buffer.append("CREATE TABLE ").append(createTable.getTable().getWholeTableName());
		if (createTable.getColumnDefinitions() != null) {
			buffer.append(" { ");
			for (Iterator<ColumnDefinition> iter = createTable.getColumnDefinitions().iterator(); iter.hasNext();) {
				ColumnDefinition columnDefinition = (ColumnDefinition) iter.next();
				buffer.append(columnDefinition.getColumnName());
				buffer.append(" ");
				buffer.append(columnDefinition.getColDataType().getDataType());
				if (columnDefinition.getColDataType().getArgumentsStringList() != null) {
                    for (String s : columnDefinition.getColDataType().getArgumentsStringList()) {
                        buffer.append(" ");
                        buffer.append(s);
                    }
				}
				if (columnDefinition.getColumnSpecStrings() != null) {
                    for (Object o : columnDefinition.getColumnSpecStrings()) {
                        buffer.append(" ");
                        buffer.append((String) o);
                    }
				}

				if (iter.hasNext()){
					buffer.append(",\n");
				}

			}

			for (Iterator iter = createTable.getIndexes().iterator(); iter.hasNext();) {
				buffer.append(",\n");
				Index index = (Index) iter.next();
				buffer.append(index.getType()).append(" ").append(index.getName());
				buffer.append("(");
				for (Iterator iterator = index.getColumnsNames().iterator(); iterator.hasNext();) {
					buffer.append((String) iterator.next());
					if (iterator.hasNext()) {
						buffer.append(", ");
					}
				}
				buffer.append(")");

				if (iter.hasNext()){
					buffer.append(",\n");
				}
			}

			buffer.append(" \n} ");
		}
	}
	
	public StringBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(StringBuffer buffer) {
		this.buffer = buffer;
	}

}
