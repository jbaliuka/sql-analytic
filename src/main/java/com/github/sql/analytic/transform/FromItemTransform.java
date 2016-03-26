package com.github.sql.analytic.transform;

import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.expression.Function;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.select.FromItem;
import com.github.sql.analytic.statement.select.FromItemVisitor;
import com.github.sql.analytic.statement.select.SubJoin;
import com.github.sql.analytic.statement.select.SubSelect;

public class FromItemTransform implements FromItemVisitor{

	private StatementTransform statementTransform;
	private FromItem fromItem ;

	public FromItemTransform(StatementTransform statementTransform) {
		this.statementTransform = statementTransform;
	}

	public void visit(Table tableName) {		
		Table newTable  = new Table(tableName.getSchemaName(),tableName.getName());
		newTable.setAlias(tableName.getAlias());
		newTable.setPartition(tableName.getPartition());
		newTable.setPartitionFor(tableName.isPartitionFor());
		fromItem = newTable;
		
	}

	public void visit(SubSelect subSelect) {
		fromItem = (FromItem) statementTransform.transform((Expression)subSelect);
		fromItem.setAlias(subSelect.getAlias());		
		
	}

	public void visit(SubJoin subjoin) {
		
	}

	public void visit(Function function) {
		fromItem = (Function)statementTransform.transform(function);		
	}

	public FromItem getFromItem() {		
		return fromItem;
	}

}
