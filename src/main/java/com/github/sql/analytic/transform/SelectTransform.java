package com.github.sql.analytic.transform;

import java.util.ArrayList;
import java.util.List;

import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.expression.ExpressionVisitor;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.statement.select.Distinct;
import com.github.sql.analytic.statement.select.Join;
import com.github.sql.analytic.statement.select.Limit;
import com.github.sql.analytic.statement.select.OrderByElement;
import com.github.sql.analytic.statement.select.PlainSelect;
import com.github.sql.analytic.statement.select.SelectBody;
import com.github.sql.analytic.statement.select.SelectItem;
import com.github.sql.analytic.statement.select.SelectVisitor;
import com.github.sql.analytic.statement.select.Top;
import com.github.sql.analytic.statement.select.Union;
import com.github.sql.analytic.statement.select.WithItem;


/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a string)
 * a {@link com.github.sql.analytic.statement.select.Select}
 */

public class SelectTransform implements SelectVisitor   {


	private SelectBody selectBody;

	public void setSelectBody(SelectBody selectBody) {
		this.selectBody = selectBody;
	}


	private StatementTransform statementTransform;



	/**
	 * @param expressionVisitor a {@link ExpressionVisitor} to de-parse expressions. It has to share the same<br>	 * 
	 * @param buffer the buffer that will be filled with the select
	 */
	public SelectTransform(StatementTransform statementTransform) {

		this.statementTransform = statementTransform;
	}


	protected FromItemTransform createFromItemTransform() {

		return new FromItemTransform(this);

	}

	public void visit(PlainSelect plainSelect) {

		selectBody = new PlainSelect();			

		if (plainSelect.getFromItem() != null) {
			FromItemTransform transform  = createFromItemTransform();
			plainSelect.getFromItem().accept(transform);
			((PlainSelect) selectBody).setFromItem(transform.getFromItem());
		}

		if (plainSelect.getJoins() != null) {
			((PlainSelect) selectBody).setJoins(new ArrayList<Join>());
			for (Join join : plainSelect.getJoins()) {
				transformJoin(join);
			}
		}

		((PlainSelect) selectBody).setWhere(transformWhere(plainSelect.getWhere()));


		Top top = plainSelect.getTop();
		if (top != null) {
			((PlainSelect) selectBody).setTop(top);
		}

		transformDistinct(plainSelect);

		((PlainSelect) selectBody).setSelectItems(transformSelectItems(plainSelect.getSelectItems()));		



		if (plainSelect.getGroupByColumnReferences() != null) {
			((PlainSelect) selectBody).setGroupByColumnReferences(new ArrayList<Expression>());
			for (Expression next : plainSelect.getGroupByColumnReferences()) {
				((PlainSelect) selectBody).getGroupByColumnReferences().add(statementTransform.transform(next));
			}
		}

		if (plainSelect.getHaving() != null) {			
			((PlainSelect) selectBody).setHaving(statementTransform.transform(plainSelect.getHaving()));
		}

		if (plainSelect.getOrderByElements() != null) {
			transformOrderBy(plainSelect.getOrderByElements());
		}

		if (plainSelect.getLimit() != null) {
			transformLimit(plainSelect.getLimit());
		}

	}


	protected List<SelectItem> transformSelectItems(List<SelectItem> list) {
		List<SelectItem> newList = new ArrayList<SelectItem>();
		for (SelectItem item : list) {
			SelectItemTransfrom transform = statementTransform.createSelectItemTransform();
			item.accept(transform);	
			newList.add(transform.getItem());					
		}
		return newList;
	}



	protected Expression transformWhere(Expression expression) {
		if(expression != null){
			return statementTransform.transform(expression);
		}else {
			return null;
		}

	}

	protected void transformDistinct(PlainSelect plainSelect) {
		if(plainSelect.getDistinct() != null){
			Distinct distinct = new Distinct();
			((PlainSelect) selectBody).setDistinct(distinct);

			if (plainSelect.getDistinct().getOnSelectItems() != null) {
				List<SelectItem> itemList = new ArrayList<SelectItem>();
				distinct.setOnSelectItems(itemList);
				for (SelectItem item : plainSelect.getDistinct().getOnSelectItems()) {	
					SelectItemTransfrom transform = statementTransform.createSelectItemTransform();
					item.accept(transform);
					itemList.add(transform.getItem());				
				}

			}
		}
	}

	public void visit(Union union) {

		Union newUnion = new Union();
		selectBody = newUnion;

		newUnion.setAll(union.isAll());
		newUnion.setDistinct(union.isDistinct());


		newUnion.setPlainSelects(new ArrayList<PlainSelect>());

		for (PlainSelect next : union.getPlainSelects()) {
			SelectTransform transform = statementTransform.createSelectTransform();
			next.accept(transform);			
			newUnion.getPlainSelects().add((PlainSelect) transform.getSelectBody());

		}

		if (union.getOrderByElements() != null) {
			transformOrderBy(union.getOrderByElements());
		}

		if (union.getLimit() != null) {
			transformLimit(union.getLimit());
		}

	}




	public void transformOrderBy(List<OrderByElement> orderByElements) {

		List<OrderByElement> newList = new ArrayList<OrderByElement>();

		for (OrderByElement next : orderByElements) {			
			newList.add((OrderByElement) statementTransform.transform(next));			
		}
		if(selectBody instanceof Union){
			((Union) selectBody).setOrderByElements(newList);
		}else {
			((PlainSelect) selectBody).setOrderByElements(newList);
		}

	}

	public void transformLimit(Limit limit) {

		if(selectBody instanceof Union){
			((Union) selectBody).setLimit(limit);
		}else {
			((PlainSelect) selectBody).setLimit(limit);
		}
	}



	public void transformJoin(Join join) {

		Join newJoin = new Join();
		((PlainSelect) selectBody).getJoins().add(newJoin);
		newJoin.setFull(join.isFull());
		newJoin.setInner(join.isInner());
		newJoin.setRight(join.isRight());
		newJoin.setLeft(join.isLeft());
		newJoin.setNatural(join.isNatural());
		newJoin.setOuter(join.isOuter());
		newJoin.setSimple(join.isSimple());

		FromItemTransform transform = createFromItemTransform();		
		join.getRightItem().accept(transform);		
		newJoin.setRightItem(transform.getFromItem());

		if (join.getOnExpression() != null) {			
			newJoin.setOnExpression(statementTransform.transform(join.getOnExpression()));			
		}

		if (join.getUsingColumns() != null) {
			newJoin.setUsingColumns(new ArrayList<Column>());
			for ( Column next : join.getUsingColumns()) {
				newJoin.getUsingColumns().add((Column) statementTransform.transform(next));
			}

		}

	}

	public SelectBody getSelectBody() {
		return selectBody;
	}


	public  StatementTransform getStatementTransform() {

		return statementTransform;
	}


	public void visit(WithItem newItem) {


	}



}
