package com.github.sql.analytic.dialect.sqlserver;

import java.util.ArrayList;

import com.github.sql.analytic.expression.AnalyticClause;
import com.github.sql.analytic.expression.Function;
import com.github.sql.analytic.expression.LongValue;
import com.github.sql.analytic.expression.OrderByClause;
import com.github.sql.analytic.expression.SQLExpression;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.session.ParamsDeparser;
import com.github.sql.analytic.statement.select.AllColumns;
import com.github.sql.analytic.statement.select.AllTableColumns;
import com.github.sql.analytic.statement.select.Limit;
import com.github.sql.analytic.statement.select.OrderByElement;
import com.github.sql.analytic.statement.select.PlainSelect;
import com.github.sql.analytic.statement.select.SelectExpressionItem;
import com.github.sql.analytic.statement.select.SelectItemVisitor;
import com.github.sql.analytic.statement.select.SelectListItem;
import com.github.sql.analytic.util.deparser.SelectDeParser;

public class SQlSrvDeparser extends ParamsDeparser {


	public SQlSrvDeparser(StringBuffer buffer) {
		super(buffer);	
	}

	@Override
	protected SelectDeParser createSelectDeParser() {				
		return new SelectDeParser(){

			@Override
			public void visit(final PlainSelect plainSelect){
				Limit limit = plainSelect.getLimit();
				if(limit == null){
					super.visit(plainSelect);
				}else {
					plainSelect.setLimit(null);	
					if(plainSelect.getOrderByElements() == null || plainSelect.getOrderByElements().isEmpty()){

						for(SelectListItem item : plainSelect.getSelectItems()){

							item.accept(new SelectItemVisitor(){

								@Override
								public void visit(AllColumns allColumns) {}

								@Override
								public void visit(
										AllTableColumns allTableColumns) {}

								@Override
								public void visit(
										SelectExpressionItem selectExpressionItem) {
									plainSelect.setOrderByElements( new ArrayList<OrderByElement>());
									OrderByElement element = new OrderByElement().setColumnReference(selectExpressionItem.getExpression());
									plainSelect.getOrderByElements().add(element);									
								}

								@Override
								public void visit(Column column) {		
									plainSelect.setOrderByElements( new ArrayList<OrderByElement>());
									OrderByElement element = new OrderByElement().setColumnReference(column);
									plainSelect.getOrderByElements().add(element);									
								}

							});

						}

					}

					Function rowNum = new Function("row_number");
					rowNum.setAnalyticClause( new AnalyticClause().
							setOrderByClause( new OrderByClause().setElements(plainSelect.getOrderByElements()) ));
					plainSelect.setOrderByElements(null);

					plainSelect.getSelectItems().add(new SelectExpressionItem(rowNum).setAlias("row_num_"));

					buffer.append("SELECT " + (limit.getRowCount() > 0 ? "TOP " + limit.getRowCount()  : "" ) + " * FROM (");
					super.visit(plainSelect);							
					buffer.append(" ) RNM_ WHERE  ");							
					buffer.append(" RNM_.row_num_ > " + limit.getOffset());
					if( limit.getRowCount() > 0){
						buffer.append(" AND RNM_.row_num_ <= " + (limit.getRowCount() + ( limit.getOffset() < 0 ? 0 :  limit.getOffset())) );
					}

				}

			}
			@Override
			protected void deparseFrom(PlainSelect plainSelect) {
				if (plainSelect.getFromItem() != null) {
					buffer.append("FROM ");
					plainSelect.getFromItem().accept(this);
				}else {
					buffer.append(" FROM DUAL ");
				}
			}

		};
	}
}