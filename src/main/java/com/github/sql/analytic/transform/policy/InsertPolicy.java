package com.github.sql.analytic.transform.policy;

import java.util.ArrayList;
import java.util.List;

import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.expression.LongValue;
import com.github.sql.analytic.expression.StringValue;
import com.github.sql.analytic.expression.operators.relational.ExpressionList;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.statement.select.FromItem;
import com.github.sql.analytic.statement.select.PlainSelect;
import com.github.sql.analytic.statement.select.SelectBody;
import com.github.sql.analytic.statement.select.SelectExpressionItem;
import com.github.sql.analytic.statement.select.SelectItem;
import com.github.sql.analytic.statement.select.SubSelect;
import com.github.sql.analytic.transform.InsertTransform;
import com.github.sql.analytic.transform.NewValue;
import com.github.sql.analytic.transform.UpdateTransform;

public class InsertPolicy extends InsertTransform {

	private Policy policyTransform;
	private List<NewValue> values;

	public InsertPolicy(Policy policyTransform) {
		super(policyTransform);
		this.policyTransform = policyTransform;
	}
	
	@Override
	protected List<NewValue> transformItems(List<NewValue> newValues) {
	    values = newValues;
		return newValues;
	}
	
	@Override
	public void visit(ExpressionList expressionList) {		
		super.visit(expressionList);
		
		SubSelect newSubselect = new SubSelect();
		PlainSelect body = new PlainSelect();
		
		SubSelect dual = buildDual();		
		body.setFromItem(dual);		
		body.setSelectItems(new ArrayList<SelectItem>());		
		
		for(Expression next: expressionList.getExpressions()){
			SelectExpressionItem item = new SelectExpressionItem();
			item.setExpression(next);
			body.getSelectItems().add(item);
		}
		
		SelectPolicy policy = new SelectPolicy("INSERT", false,values, policyTransform);
		policy.getTables().add(getTable());			
		policy.visit(body);
		body = (PlainSelect) policy.getSelectBody();
		newSubselect.setSelectBody(body);
		
		getNewInsert().setItemsList(newSubselect);
		
		
	}
	
	@Override
	public void visit(SubSelect subSelect) {		
		super.visit(subSelect);
		
	  List<NewValue> newValues = new ArrayList<NewValue>();
		
		int index = 0;
		for (Column column : getNewInsert().getColumns()) {		
			SelectItem item = ((PlainSelect) subSelect.getSelectBody()).getSelectItems().get(index++);
			Expression expr;
			if(item instanceof Column){
				expr = (Expression) item;
			}else if (item instanceof SelectExpressionItem){
				expr = ((SelectExpressionItem) item).getExpression();
			}else {
				throw new PolicyException(" Access denied: " + item);
			}
			NewValue value = new NewValue(column, expr);
			newValues.add(value);				
		}		
		SelectPolicy policy = new SelectPolicy("INSERT", true,newValues, policyTransform);
		policy.getTables().add(getTable());
		policy.visit((PlainSelect)subSelect.getSelectBody());		
		SubSelect newSubselect = new SubSelect();
		PlainSelect selectBody = (PlainSelect) policy.getSelectBody();
		
		if(selectBody.getFromItem() == null){
			SubSelect dual = buildDual();
			selectBody.setFromItem(dual);
		}
		
		newSubselect.setSelectBody(selectBody);
		
		
		getNewInsert().setItemsList(newSubselect);
	}

	protected SubSelect buildDual() {
		
		SubSelect dual = new SubSelect();
		
		PlainSelect dualBody = new PlainSelect();
		List<SelectItem> dualList = new ArrayList<SelectItem>();
		SelectExpressionItem dualItem = new SelectExpressionItem();
		dualItem.setExpression( new LongValue("1"));
		dualList.add(dualItem );
		dualBody.setSelectItems(dualList );
		dual.setSelectBody(dualBody );
		
		return dual;
	}

}
