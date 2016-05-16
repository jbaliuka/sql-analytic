package com.github.sql.analytic.transform;

import java.util.List;

import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.statement.select.AllColumns;
import com.github.sql.analytic.statement.select.AllTableColumns;
import com.github.sql.analytic.statement.select.SelectExpressionItem;
import com.github.sql.analytic.statement.select.SelectItemVisitor;
import com.github.sql.analytic.statement.select.SelectListItem;

public class SelectItemListTransform implements SelectItemVisitor{
	private List<SelectListItem> itemList;

	public SelectItemListTransform(List<SelectListItem> itemList) {
		super();
		this.itemList = itemList;
	}

	public void visit(AllColumns allColumns) {
		itemList.add(allColumns);
		
	}

	public void visit(AllTableColumns allTableColumns) {
		itemList.add(allTableColumns);
		
	}
	
	
	public void visit(SelectExpressionItem selectExpressionItem) {		
		SelectExpressionItem item = new SelectExpressionItem();
		item.setAlias(selectExpressionItem.getAlias());		
		ExpressionTransform expressionTransform = new ExpressionTransform();		
		item.getExpression().accept(expressionTransform );
		item.setExpression(expressionTransform.getExpression());
		itemList.add(item);
	}

	public void visit(Column column) {
		itemList.add(column);
		
	}

	public List<SelectListItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<SelectListItem> itemList) {
		this.itemList = itemList;
	}
	
	
}