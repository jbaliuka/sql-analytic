package com.github.sql.analytic.util.deparser;

import java.util.Iterator;
import java.util.List;

import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.expression.ExpressionVisitor;
import com.github.sql.analytic.expression.Function;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.select.AllColumns;
import com.github.sql.analytic.statement.select.AllTableColumns;
import com.github.sql.analytic.statement.select.ColumnIndex;
import com.github.sql.analytic.statement.select.ColumnReference;
import com.github.sql.analytic.statement.select.FromItem;
import com.github.sql.analytic.statement.select.FromItemVisitor;
import com.github.sql.analytic.statement.select.Join;
import com.github.sql.analytic.statement.select.Limit;
import com.github.sql.analytic.statement.select.OrderByElement;
import com.github.sql.analytic.statement.select.OrderByVisitor;
import com.github.sql.analytic.statement.select.PlainSelect;
import com.github.sql.analytic.statement.select.SelectExpressionItem;
import com.github.sql.analytic.statement.select.SelectItem;
import com.github.sql.analytic.statement.select.SelectItemVisitor;
import com.github.sql.analytic.statement.select.SelectVisitor;
import com.github.sql.analytic.statement.select.SubJoin;
import com.github.sql.analytic.statement.select.SubSelect;
import com.github.sql.analytic.statement.select.Top;
import com.github.sql.analytic.statement.select.Union;


/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a string)
 * a {@link com.github.sql.analytic.statement.select.Select}
 */

public class SelectDeParser implements SelectVisitor, OrderByVisitor, SelectItemVisitor, FromItemVisitor {
	protected StringBuffer buffer;
	protected ExpressionVisitor expressionVisitor;

	public SelectDeParser() {
	}

	/**
	 * @param expressionVisitor a {@link ExpressionVisitor} to de-parse expressions. It has to share the same<br>
	 * StringBuffer (buffer parameter) as this object in order to work
	 * @param buffer the buffer that will be filled with the select
	 */
	public SelectDeParser(ExpressionVisitor expressionVisitor, StringBuffer buffer) {
		this.buffer = buffer;
		this.expressionVisitor = expressionVisitor;
	}

	@SuppressWarnings("unchecked")
	public void visit(PlainSelect plainSelect) {
		buffer.append("SELECT ");
		Top top = plainSelect.getTop();
		if (top != null) {
			top.toString();
        }
		
	    deparseDistinct(plainSelect);

		

		for (Iterator iter = plainSelect.getSelectItems().iterator(); iter.hasNext();) {
			SelectItem selectItem = (SelectItem) iter.next();
			selectItem.accept(this);
			if (iter.hasNext()) {
				buffer.append(", ");
			}
		}

		buffer.append(" ");
		
		deparseFrom(plainSelect);

		if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                deparseJoin(join);
            }
		}

		deparseWhere(plainSelect);

		if (plainSelect.getGroupByColumnReferences() != null) {
			buffer.append(" GROUP BY ");
			int i = 0;
			for (Expression next  : plainSelect.getGroupByColumnReferences()) {				
				next.accept(expressionVisitor);
				if (i++ < plainSelect.getGroupByColumnReferences().size() - 1) {
					buffer.append(", ");
				}
			}
		}

		if (plainSelect.getHaving() != null) {
			buffer.append(" HAVING ");
			plainSelect.getHaving().accept(expressionVisitor);
		}

		if (plainSelect.getOrderByElements() != null) {
			deparseOrderBy(plainSelect.getOrderByElements());
		}

		if (plainSelect.getLimit() != null) {
			deparseLimit(plainSelect.getLimit());
		}

	}

	protected void deparseFrom(PlainSelect plainSelect) {
		if (plainSelect.getFromItem() != null) {
			buffer.append("FROM ");
			plainSelect.getFromItem().accept(this);
		}
	}

	protected void deparseWhere(PlainSelect plainSelect) {
		if (plainSelect.getWhere() != null) {
			buffer.append(" WHERE ");
			plainSelect.getWhere().accept(expressionVisitor);
		}
	}

	protected void deparseDistinct(PlainSelect plainSelect) {
		if(plainSelect.getDistinct() != null){
		buffer.append("DISTINCT ");
		if (plainSelect.getDistinct().getOnSelectItems() != null) {
			buffer.append("ON (");
			for (Iterator iter = plainSelect.getDistinct().getOnSelectItems().iterator(); iter.hasNext();) {
				SelectItem selectItem = (SelectItem) iter.next();
				selectItem.accept(this);
				if (iter.hasNext()) {
					buffer.append(", ");
				}
			}
			buffer.append(") ");
		}
		}
	}

	@SuppressWarnings("unchecked")
	public void visit(Union union) {
		for (Iterator iter = union.getPlainSelects().iterator(); iter.hasNext();) {
			buffer.append("(");
			PlainSelect plainSelect = (PlainSelect) iter.next();
			plainSelect.accept(this);
			buffer.append(")");
			if (iter.hasNext()) {				
				buffer.append(" UNION ");
				if(union.isAll()){
					buffer.append(" ALL ");	
				}
			}

		}

		if (union.getOrderByElements() != null) {
			deparseOrderBy(union.getOrderByElements());
		}

		if (union.getLimit() != null) {
			deparseLimit(union.getLimit());
		}

	}

	public void visit(OrderByElement orderBy) {
		orderBy.getColumnReference().accept(expressionVisitor);
		if (orderBy.isAsc()){
			buffer.append(" ASC");
		}else{    
			buffer.append(" DESC");
		}
	}

	public void visit(Column column) {
		buffer.append(column.getWholeColumnName());
	}

	public void visit(ColumnIndex columnIndex) {
		buffer.append(columnIndex.getIndex());
	}

	public void visit(AllColumns allColumns) {
		buffer.append("*");
	}

	public void visit(AllTableColumns allTableColumns) {
		buffer.append(allTableColumns.getTable().getWholeTableName()).append(".*");
	}

	public void visit(SelectExpressionItem selectExpressionItem) {
		selectExpressionItem.getExpression().accept(expressionVisitor);
		if (selectExpressionItem.getAlias() != null) {
			buffer.append(" AS ").append(selectExpressionItem.getAlias());
		}

	}

	public void visit(SubSelect subSelect) {
		buffer.append("(");
		subSelect.getSelectBody().accept(this);
		buffer.append(")");
		if(subSelect.getAlias() != null){
			buffer.append(" ");
			buffer.append(subSelect.getAlias());
		}
	}

	public void visit(Table tableName) {
		buffer.append(tableName.getWholeTableName());
		if(tableName.getAlias() != null){
			buffer.append(" ");
			buffer.append(tableName.getAlias());
		}
	}

	@SuppressWarnings("unchecked")
	public void deparseOrderBy(List orderByElements) {
		buffer.append(" ORDER BY ");
		for (Iterator iter = orderByElements.iterator(); iter.hasNext();) {
			OrderByElement orderByElement = (OrderByElement) iter.next();
			orderByElement.accept(expressionVisitor);
			if (iter.hasNext()) {
				buffer.append(", ");
			}
		}
	}

	public void deparseLimit(Limit limit) {
		// LIMIT n OFFSET skip 
		buffer.append(" LIMIT ");
		if (limit.isRowCountJdbcParameter()) {
			buffer.append("?");
		} else if (limit.getRowCount() != 0) {
			buffer.append(limit.getRowCount());
		} else {
			/*
			 from mysql docs:
			 For compatibility with PostgreSQL, MySQL also supports the LIMIT row_count OFFSET offset syntax.
			 To retrieve all rows from a certain offset up to the end of the result set, you can use some large number
			 for the second parameter. 
			 */
			buffer.append("18446744073709551615");
		}

		if (limit.isOffsetJdbcParameter()) {
			buffer.append(" OFFSET ?");
		} else if (limit.getOffset() != 0) {
			buffer.append(" OFFSET ").append(limit.getOffset());
		}

	}

	public StringBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(StringBuffer buffer) {
		this.buffer = buffer;
	}

	public ExpressionVisitor getExpressionVisitor() {
		return expressionVisitor;
	}

	public void setExpressionVisitor(ExpressionVisitor visitor) {
		expressionVisitor = visitor;
	}

	public void visit(SubJoin subjoin) {
		buffer.append("(");
		subjoin.getLeft().accept(this);
		buffer.append(" ");
		deparseJoin(subjoin.getJoin());
		buffer.append(")");
	}

	@SuppressWarnings("unchecked")
	public void deparseJoin(Join join) {
		if (join.isSimple()){
			buffer.append(", ");
		}else{
	
			if (join.isRight()){
				buffer.append("RIGHT ");
			}else if (join.isNatural()){
				buffer.append("NATURAL ");
			}else if (join.isFull()){
				buffer.append("FULL ");
			}else if (join.isLeft()){
				buffer.append("LEFT ");
			}
			
			if (join.isOuter()){
				buffer.append("OUTER ");
			}else if (join.isInner()){
				buffer.append("INNER ");
			}

			buffer.append("JOIN ");

		}
		
		FromItem fromItem = join.getRightItem();
		fromItem.accept(this);
		
		if (join.getOnExpression() != null) {
			buffer.append(" ON ");
			join.getOnExpression().accept(expressionVisitor);
		}
		if (join.getUsingColumns() != null) {
			buffer.append(" USING ( ");
			for (Iterator iterator = join.getUsingColumns().iterator(); iterator.hasNext();) {
				Column column = (Column) iterator.next();
				buffer.append(column.getWholeColumnName());
				if (iterator.hasNext()) {
					buffer.append(" ,");
				}
			}
			buffer.append(")");
		}

	}

	public void visit(Function function) {		
		buffer.append(function.toString() );
		
	}

}
