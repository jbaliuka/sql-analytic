package com.github.sql.analytic.transform;

import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.select.AllColumns;
import com.github.sql.analytic.statement.select.AllTableColumns;
import com.github.sql.analytic.statement.select.SelectExpressionItem;
import com.github.sql.analytic.statement.select.SelectItem;
import com.github.sql.analytic.statement.select.SelectItemVisitor;

public class SelectItemTransfrom implements SelectItemVisitor{
	
	private StatementTransform transform;
	private SelectItem item;

	public SelectItemTransfrom(StatementTransform transform){
		this.transform = transform;
	}

	public void visit(AllColumns allColumns) {
		item =	allColumns;	
	}

	public void visit(AllTableColumns allTableColumns) {
		AllTableColumns newCols = new AllTableColumns();
		item = newCols;
		newCols.setTable(new Table(allTableColumns.getTable().getSchemaName(),allTableColumns.getTable().getName()));
	}

	public void visit(SelectExpressionItem selectExpressionItem) {
		SelectExpressionItem newItem = new SelectExpressionItem();
		item = newItem;
		newItem.setAlias(selectExpressionItem.getAlias());
		newItem.setExpression(transform.transform(selectExpressionItem.getExpression()));
	}

	public void visit(Column column) {
		Column newCol = new Column();
		item = newCol;
		newCol.setColumnName(column.getColumnName());
		newCol.setTable(new Table(column.getTable().getSchemaName(),column.getTable().getName()));
	}

	public SelectItem getItem() {
		return item;
	}

	

}
