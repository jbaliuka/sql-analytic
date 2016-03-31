package com.github.sql.analytic.transform;

import java.util.List;

import com.github.sql.analytic.expression.AllComparisonExpression;
import com.github.sql.analytic.expression.AnalyticClause;
import com.github.sql.analytic.expression.AnyComparisonExpression;
import com.github.sql.analytic.expression.CaseExpression;
import com.github.sql.analytic.expression.CastExpression;
import com.github.sql.analytic.expression.DateValue;
import com.github.sql.analytic.expression.DoubleValue;
import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.expression.ExpressionVisitor;
import com.github.sql.analytic.expression.Function;
import com.github.sql.analytic.expression.GroupingExpression;
import com.github.sql.analytic.expression.InverseExpression;
import com.github.sql.analytic.expression.JdbcParameter;
import com.github.sql.analytic.expression.LongValue;
import com.github.sql.analytic.expression.NamedParameter;
import com.github.sql.analytic.expression.NullValue;
import com.github.sql.analytic.expression.Parenthesis;
import com.github.sql.analytic.expression.StringValue;
import com.github.sql.analytic.expression.TimeValue;
import com.github.sql.analytic.expression.TimestampValue;
import com.github.sql.analytic.expression.WhenClause;
import com.github.sql.analytic.expression.operators.arithmetic.Addition;
import com.github.sql.analytic.expression.operators.arithmetic.Division;
import com.github.sql.analytic.expression.operators.arithmetic.Multiplication;
import com.github.sql.analytic.expression.operators.arithmetic.Subtraction;
import com.github.sql.analytic.expression.operators.conditional.AndExpression;
import com.github.sql.analytic.expression.operators.conditional.OrExpression;
import com.github.sql.analytic.expression.operators.relational.Between;
import com.github.sql.analytic.expression.operators.relational.EqualsTo;
import com.github.sql.analytic.expression.operators.relational.ExistsExpression;
import com.github.sql.analytic.expression.operators.relational.GreaterThan;
import com.github.sql.analytic.expression.operators.relational.GreaterThanEquals;
import com.github.sql.analytic.expression.operators.relational.InExpression;
import com.github.sql.analytic.expression.operators.relational.IsNullExpression;
import com.github.sql.analytic.expression.operators.relational.LikeExpression;
import com.github.sql.analytic.expression.operators.relational.MinorThan;
import com.github.sql.analytic.expression.operators.relational.MinorThanEquals;
import com.github.sql.analytic.expression.operators.relational.NotEqualsTo;
import com.github.sql.analytic.expression.operators.string.Concat;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.statement.select.AllColumns;
import com.github.sql.analytic.statement.select.AllTableColumns;
import com.github.sql.analytic.statement.select.ColumnIndex;
import com.github.sql.analytic.statement.select.OrderByElement;
import com.github.sql.analytic.statement.select.SelectExpressionItem;
import com.github.sql.analytic.statement.select.SelectItem;
import com.github.sql.analytic.statement.select.SelectItemVisitor;
import com.github.sql.analytic.statement.select.SubSelect;

public class SelectItemListTransform implements SelectItemVisitor{
	private List<SelectItem> itemList;

	public SelectItemListTransform(List<SelectItem> itemList) {
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

	public List<SelectItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<SelectItem> itemList) {
		this.itemList = itemList;
	}
	
	
}