package com.github.sql.analytic.transform;

import java.util.ArrayList;
import java.util.List;

import com.github.sql.analytic.expression.SQLExpression;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.SQLStatement;
import com.github.sql.analytic.statement.update.Update;



public class UpdateTransform {

	private StatementTransform statementTransform;
	private Table table;
	

	public UpdateTransform(StatementTransform statementTransform) {
		this.statementTransform = statementTransform;
	}

	public SQLStatement transform(Update update) {

		Update newUpdate = new Update();		
		newUpdate.setTable(transformTable(update.getTable()));
		setTable(newUpdate.getTable());
		newUpdate.setColumns(new ArrayList<Column>());
		newUpdate.setExpressions(new ArrayList<SQLExpression>());
		List<NewValue> newValues = new ArrayList<NewValue>();

		for (int i = 0; i < update.getColumns().size(); i++) {
			Column column = (Column) update.getColumns().get(i);
			SQLExpression expression = (SQLExpression) update.getExpressions().get(i);			
			NewValue newItem = transfromItem(new NewValue(column,statementTransform.transform(expression)));
			if(newItem != null){
				newValues.add(newItem);				
			}				

		}
		newValues = transformItems(newValues);
		for (NewValue value : newValues) {
			newUpdate.getColumns().add(value.getColumn());
			newUpdate.getExpressions().add(value.getExpression());
		}


		newUpdate.setWhere(transformWhere(update.getWhere()));

		return newUpdate;

	}

	protected List<NewValue> transformItems(List<NewValue> newValues) {		
		return newValues;
	}

	protected NewValue transfromItem(NewValue updateListItem) {

		updateListItem.setExpression(statementTransform.transform(updateListItem.getExpression()));				
		updateListItem.setColumn(updateListItem.getColumn());

		return updateListItem;
	}

	protected Table transformTable(Table table) {

		Table newTable = new Table(table.getSchemaName(),table.getName());
		newTable.setAlias(table.getAlias());
		newTable.setPartition(table.getPartition());
		newTable.setPartitionFor(table.isPartitionFor());

		return newTable;
	}
	protected SQLExpression transformWhere(SQLExpression where) {		
		if(where == null){
			return null;
		}
		return statementTransform.transform(where);
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}



}
