package com.github.sql.analytic.dialect.orcl;

import java.util.ArrayList;

import com.github.sql.analytic.expression.AnalyticClause;
import com.github.sql.analytic.expression.Function;
import com.github.sql.analytic.expression.LongValue;
import com.github.sql.analytic.expression.OrderByClause;
import com.github.sql.analytic.session.ParamsDeparser;
import com.github.sql.analytic.statement.select.Limit;
import com.github.sql.analytic.statement.select.OrderByElement;
import com.github.sql.analytic.statement.select.PlainSelect;
import com.github.sql.analytic.statement.select.SelectExpressionItem;
import com.github.sql.analytic.util.deparser.SelectDeParser;

public class OrclDeparser extends ParamsDeparser {
	public OrclDeparser(StringBuffer buffer) {
		super(buffer);
	}

	@Override
	protected SelectDeParser createSelectDeParser() {				
		return new SelectDeParser(){

			@Override
			protected void deparseSelect(PlainSelect plainSelect) {
				buffer.append("SELECT ");
				if(plainSelect.getHints() != null){
					buffer.append(plainSelect.getHints());
				}

			}
			@Override
			public void visit(PlainSelect plainSelect){
				Limit limit = plainSelect.getLimit();
				if(limit == null){
					super.visit(plainSelect);
				}else {
					plainSelect.setLimit(null);	
					if(plainSelect.getOrderByElements() == null || plainSelect.getOrderByElements().isEmpty()){
						plainSelect.setOrderByElements( new ArrayList<OrderByElement>());
						plainSelect.getOrderByElements().add(new OrderByElement().setColumnReference( new LongValue("1") ));
					}

					if(limit.getRowCount() > 0){
						plainSelect.setHints("/*+ first_rows("+ limit.getRowCount() +") */");
					}	

					Function rowNum = new Function("row_number");
					rowNum.setAnalyticClause( new AnalyticClause().
							setOrderByClause( new OrderByClause().setElements(plainSelect.getOrderByElements()) ));

					plainSelect.getSelectItems().add(new SelectExpressionItem(rowNum).setAlias("row_num_"));

					buffer.append("SELECT * FROM (");
					super.visit(plainSelect);							
					buffer.append(" ) WHERE  ");							
					buffer.append(" row_num_ > " + limit.getOffset());
					if(limit.getRowCount() > 0){
						buffer.append(" AND row_num_ <= " + (limit.getRowCount() + ( limit.getOffset() < 0 ? 0 :  limit.getOffset())) );
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