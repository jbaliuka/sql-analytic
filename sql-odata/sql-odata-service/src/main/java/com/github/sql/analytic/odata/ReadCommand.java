package com.github.sql.analytic.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;

import com.github.sql.analytic.expression.BinaryExpression;
import com.github.sql.analytic.expression.Parenthesis;
import com.github.sql.analytic.expression.SQLExpression;
import com.github.sql.analytic.expression.operators.conditional.AndExpression;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.session.SQLSession;
import com.github.sql.analytic.statement.select.Limit;
import com.github.sql.analytic.statement.select.OrderByElement;
import com.github.sql.analytic.statement.select.PlainSelect;
import com.github.sql.analytic.statement.select.SelectExpressionItem;
import com.github.sql.analytic.statement.select.SelectListItem;

public abstract class ReadCommand {

	private Map<String, Object> statementParams = new HashMap<>();
	
	private PlainSelect select = new PlainSelect().setSelectItems(new ArrayList<SelectListItem>());

	public abstract ResultSetIterator execute(SQLSession connection) throws ODataApplicationException ;

	public PlainSelect getSelect() {
		return select;
	}

	public void setSelect(PlainSelect select){
		this.select = select;
	}

	public ODataApplicationException internalError(Exception e) {
		return new ODataApplicationException(e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), 
				Locale.ROOT,e);
	}

	public void appendFilter(String alias,FilterOption filterOption) throws ODataApplicationException {
		if(filterOption != null) {
			try {
				appendFilter(alias,filterOption.getExpression());
			} catch (ExpressionVisitException e) {
				throw internalError(e);
			}
		}
	}
	private void appendFilter(String alias,Expression filterExpression) throws ExpressionVisitException, ODataApplicationException {

		FilterExpressionVisitor expressionVisitor = new FilterExpressionVisitor(alias);
		SQLExpression filter = filterExpression.accept(expressionVisitor);

		if(getSelect().getWhere() == null ){
			getSelect().setWhere(filter);
		}else {
			BinaryExpression where = new AndExpression().setLeftExpression(new Parenthesis().setExpression(getSelect().getWhere())).
					setRightExpression( new Parenthesis().setExpression(filter));
			getSelect().setWhere(where);
		}

	}
	public void appendOrdering(String alias,OrderByOption orderByOption) throws ODataApplicationException {

		if(orderByOption != null){
			getSelect().setOrderByElements(new ArrayList<OrderByElement>());
			for( OrderByItem item: orderByOption.getOrders()){
				OrderByElement element = new OrderByElement();
				element.setAsc(!item.isDescending());
				FilterExpressionVisitor expressionVisitor = new FilterExpressionVisitor(alias);
				try {
					SQLExpression orderByItem = item.getExpression().accept(expressionVisitor);
					element.setColumnReference(orderByItem);
					getSelect().getOrderByElements().add(element);
				} catch (ExpressionVisitException  e) {
					throw internalError(e);
				}

			}
		}
	}

	public void appendLimit(TopOption top,SkipOption skip) {

		Limit limit = new Limit();
		if(top != null){
			getSelect().setLimit( limit.setRowCount(top.getValue()) );
		}

		if(skip != null){
			getSelect().setLimit( limit.setOffset(skip.getValue()) );
		}

	}

	public void appendProjection(EdmEntityType type, String alias,SelectOption selectOption) {

		for(String name : type.getPropertyNames()){
			if(EntityData.inSelection(selectOption, name)){
				SelectExpressionItem item = new SelectExpressionItem().setAlias(name);
				Column column = new Column();
				if(alias != null ){
					column.setTable(new Table(null,alias));
				}
				getSelect().getSelectItems().add(item.setExpression(column.setColumnName(name)));

			}
		}

	}



	protected void processUriInfo(UriInfoResource uriInfo,EdmEntityType entityType,String alias) throws ODataApplicationException {

		appendFilter(alias,uriInfo.getFilterOption());
		appendProjection(entityType,alias, uriInfo.getSelectOption());
		appendLimit(uriInfo.getTopOption(),uriInfo.getSkipOption());
		appendOrdering(alias,uriInfo.getOrderByOption());

	}

	public Map<String, Object> getStatementParams() {
		return statementParams;
	}

	public void setStatementParams(Map<String, Object> statementParams) {
		this.statementParams = statementParams;
	}

}
