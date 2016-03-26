package com.github.sql.analytic.transform;

import java.util.ArrayList;

import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.Statement;
import com.github.sql.analytic.statement.update.Update;



public class UpdateTransform {

	public static class UpdateListItem{
		private Column column;
		private Expression expression;

		public UpdateListItem(Column column, Expression expression) {
			super();
			this.column = column;
			this.expression = expression;
		}
		public Column getColumn() {
			return column;
		}
		public void setColumn(Column column) {
			this.column = column;
		}
		public Expression getExpression() {
			return expression;
		}
		public void setExpression(Expression expression) {
			this.expression = expression;
		}

	}

	private StatementTransform statementTransform;
	private Table table;


	public UpdateTransform(StatementTransform statementTransform) {
		this.statementTransform = statementTransform;
	}

	public Statement transform(Update update) {

		Update newUpdate = new Update();
		newUpdate.setTable(transformTable(update.getTable()));
		setTable(newUpdate.getTable());
		newUpdate.setColumns(new ArrayList<Column>());
		newUpdate.setExpressions(new ArrayList<Expression>());

		for (int i = 0; i < update.getColumns().size(); i++) {
			Column column = (Column) update.getColumns().get(i);
			Expression expression = (Expression) update.getExpressions().get(i);			
			UpdateListItem newItem = transfromItem(new UpdateListItem(column,expression));
			if(newItem != null){
				newUpdate.getColumns().add(newItem.getColumn());
				newUpdate.getExpressions().add(newItem.getExpression());
			}				

		}


		newUpdate.setWhere(transformWhere(update.getWhere()));

		return newUpdate;

	}

	private UpdateListItem transfromItem(UpdateListItem updateListItem) {

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
	protected Expression transformWhere(Expression where) {		
		return statementTransform.transform(where);
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}



}
