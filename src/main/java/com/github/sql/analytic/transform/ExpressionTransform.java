package com.github.sql.analytic.transform;

import java.util.ArrayList;
import java.util.List;

import com.github.sql.analytic.expression.AllComparisonExpression;
import com.github.sql.analytic.expression.AnalyticClause;
import com.github.sql.analytic.expression.AnyComparisonExpression;
import com.github.sql.analytic.expression.BinaryExpression;
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
import com.github.sql.analytic.expression.OrderByClause;
import com.github.sql.analytic.expression.Parenthesis;
import com.github.sql.analytic.expression.QueryPartitionClause;
import com.github.sql.analytic.expression.StringValue;
import com.github.sql.analytic.expression.TimeValue;
import com.github.sql.analytic.expression.TimestampValue;
import com.github.sql.analytic.expression.WhenClause;
import com.github.sql.analytic.expression.WindowClause;
import com.github.sql.analytic.expression.operators.arithmetic.Addition;
import com.github.sql.analytic.expression.operators.arithmetic.Division;
import com.github.sql.analytic.expression.operators.arithmetic.Multiplication;
import com.github.sql.analytic.expression.operators.arithmetic.Subtraction;
import com.github.sql.analytic.expression.operators.conditional.AndExpression;
import com.github.sql.analytic.expression.operators.conditional.OrExpression;
import com.github.sql.analytic.expression.operators.relational.Between;
import com.github.sql.analytic.expression.operators.relational.EqualsTo;
import com.github.sql.analytic.expression.operators.relational.ExistsExpression;
import com.github.sql.analytic.expression.operators.relational.ExpressionList;
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
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.select.ColumnIndex;
import com.github.sql.analytic.statement.select.OrderByElement;
import com.github.sql.analytic.statement.select.SubSelect;


/**
 * A class to transform from JSqlParser hierarchy into a new AST)
 * an {@link com.github.sql.analytic.expression.Expression}
 */

public class ExpressionTransform implements ExpressionVisitor {


	private Expression expression;
	protected StatementTransform statementTransform;


	ExpressionTransform(){}

	public ExpressionTransform(StatementTransform statementTransform) {
		this.statementTransform = statementTransform;
	}


	public void visit(Concat concat) {
		Concat newConcat = new Concat();    
		setExpression(newConcat);
		visitBinaryExpression(concat);


	}



	protected void setExpression(Expression expr) {
		expression = expr;		
	}


	public void visit(Addition addition) {
		setExpression(new Addition());
		visitBinaryExpression(addition);
	}

	public void visit(AndExpression andExpression) {
		setExpression(new AndExpression());
		visitBinaryExpression(andExpression);
	}

	public void visit(Between between) {

		Between newExpression = new Between();
		setExpression(newExpression);		
		newExpression.setNot(between.isNot());		
		newExpression.setLeftExpression(statementTransform.transform(between.getLeftExpression()));		
		newExpression.setBetweenExpressionStart(statementTransform.transform(between.getBetweenExpressionStart()));
		newExpression.setBetweenExpressionEnd(statementTransform.transform(between.getBetweenExpressionEnd()));

	}

	public void visit(Division division) {
		setExpression(new Division());
		visitBinaryExpression(division);
	}

	public void visit(DoubleValue doubleValue) {
		setExpression(doubleValue);
	}

	public void visit(EqualsTo equalsTo) {
		setExpression(new EqualsTo());
		visitBinaryExpression(equalsTo);
	}

	public void visit(GreaterThan greaterThan) {
		setExpression(new GreaterThan());
		visitBinaryExpression(greaterThan);
	}

	public void visit(GreaterThanEquals greaterThanEquals) {
		setExpression(new GreaterThanEquals());
		visitBinaryExpression(greaterThanEquals);
	}

	public void visit(InExpression inExpression) {

		InExpression newExpression = new InExpression();
		setExpression(newExpression);		
		newExpression.setNot(inExpression.isNot());
		newExpression.setLeftExpression(statementTransform.transform(inExpression.getLeftExpression()));		
		newExpression.setItemsList(statementTransform.transform(inExpression.getItemsList()));
	}

	public void visit(InverseExpression inverseExpression) {
		InverseExpression newExpression = new InverseExpression();
		setExpression(newExpression);		
		newExpression.setExpression(statementTransform.transform(inverseExpression.getExpression()));
	}

	public void visit(IsNullExpression isNullExpression) {
		IsNullExpression newExpression = new IsNullExpression();
		setExpression(newExpression);		
		newExpression.setNot(isNullExpression.isNot());
		newExpression.setLeftExpression(statementTransform.transform(isNullExpression.getLeftExpression()));

	}

	public void visit(JdbcParameter jdbcParameter) {
		expression = jdbcParameter;
	}

	public void visit(LikeExpression likeExpression) {
		setExpression(new LikeExpression());
		visitBinaryExpression(likeExpression);
	}

	public void visit(ExistsExpression existsExpression) {

		ExistsExpression newExpression = new ExistsExpression();
		setExpression(newExpression);		
		newExpression.setNot(existsExpression.isNot());
		newExpression.setRightExpression(statementTransform.transform(existsExpression.getRightExpression())); 

	}

	public void visit(LongValue longValue) {
		setExpression(longValue);				
	}

	public void visit(MinorThan minorThan) {
		setExpression( new MinorThan());
		visitBinaryExpression(minorThan);

	}

	public void visit(MinorThanEquals minorThanEquals) {
		setExpression(new MinorThanEquals());
		visitBinaryExpression(minorThanEquals);
	}

	public void visit(Multiplication multiplication) {
		setExpression(new Multiplication());
		visitBinaryExpression(multiplication);

	}

	public void visit(NotEqualsTo notEqualsTo) {
		setExpression( new NotEqualsTo());
		visitBinaryExpression(notEqualsTo);

	}

	public void visit(NullValue nullValue) {
		expression = nullValue;

	}

	public void visit(OrExpression orExpression) {
		setExpression(new OrExpression());
		visitBinaryExpression(orExpression);

	}

	public void visit(Parenthesis parenthesis) {

		Parenthesis newParenthesis = new Parenthesis();
		setExpression(newParenthesis);

		if (parenthesis.isNot()){
			newParenthesis.setNot();
		}    		
		newParenthesis.setExpression(statementTransform.transform(parenthesis.getExpression()));

	}



	public void visit(StringValue stringValue) {

		StringValue value = new StringValue();
		value.setValue(stringValue.getValue());
		expression = value;
	}

	public void visit(Subtraction subtraction) {
		setExpression(new Subtraction());
		visitBinaryExpression(subtraction);

	}

	private void visitBinaryExpression(BinaryExpression binaryExpression) {
		BinaryExpression expr = (BinaryExpression) expression;
		if (binaryExpression.isNot()){        	
			expr.setNot();
		}
		expr.setLeftExpression(statementTransform.transform(binaryExpression.getLeftExpression()));
		expr.setRightExpression(statementTransform.transform(binaryExpression.getRightExpression()));

	}

	public void visit(SubSelect subSelect) {
		SubSelect newExpression = new SubSelect();    	
		setExpression(newExpression);

		newExpression.setAlias(subSelect.getAlias());
		newExpression.setExpression(true);

		SelectTransform selectVisitor = statementTransform.createSelectTransform();
		subSelect.getSelectBody().accept(selectVisitor);		
		newExpression.setSelectBody(selectVisitor.getSelectBody());

	}

	public void visit(Column tableColumn) {

		Column newColumn = new Column();
		expression = newColumn;
		newColumn.setColumnName(tableColumn.getColumnName());
		Table table = new Table(tableColumn.getTable().getSchemaName(),tableColumn.getTable().getName());		
		newColumn.setTable(table);

	}

	public void visit(Function function) {

		Function newFunction = new Function();
		setExpression(newFunction);
		newFunction.setEscaped(function.isEscaped());
		newFunction.setName(function.getName());
		newFunction.setAlias(function.getAlias());
		newFunction.setAllColumns(function.isAllColumns());
		newFunction.setPipeline(function.isPipeline());

		ExpressionList newList = new ExpressionList();	
		newList.setExpressions(new ArrayList<Expression>());

		if(function.getParameters() != null){
			for ( Expression next :  function.getParameters().getExpressions()) {
				newList.getExpressions().add(statementTransform.transform(next));
			}	
		}

		newFunction.setParameters(newList);
		
		if(function.getAnalyticCause() != null){
			newFunction.setAnalyticClause((AnalyticClause) statementTransform.transform(function.getAnalyticCause()));
			newFunction.getAnalyticCause().setFunction(newFunction);
		}


	}

	public void visit(ExpressionList expressionList) {

		ExpressionList newList = new ExpressionList();	
		newList.setExpressions(new ArrayList<Expression>());
		
		for ( Expression next :  expressionList.getExpressions()) {
			newList.getExpressions().add(statementTransform.transform(next));
		}


	}


	public void visit(DateValue dateValue) {
		setExpression(dateValue);
	}

	public void visit(TimestampValue timestampValue) {
		setExpression(timestampValue);
	}
	public void visit(TimeValue timeValue) {
		setExpression(timeValue);
	}

	public void visit(CaseExpression caseExpression) {

		CaseExpression newExpression = new CaseExpression();
		setExpression(newExpression);	

		Expression switchExp = caseExpression.getSwitchExpression();
		if( switchExp != null ) {			
			newExpression.setSwitchExpression(statementTransform.transform(switchExp));
		}

		List<WhenClause> clauses = caseExpression.getWhenClauses();
		List<WhenClause> newCauses = new ArrayList<WhenClause>();

		for (WhenClause clause : clauses) {
			newCauses.add((WhenClause)statementTransform.transform(clause));			
		}
		newExpression.setWhenClauses(newCauses);

		Expression elseExp = caseExpression.getElseExpression();
		if( elseExp != null ) {
			newExpression.setElseExpression(statementTransform.transform(elseExp));
		}

	}

	public void visit(WhenClause whenClause) {
		WhenClause newExpression = new WhenClause();
		setExpression(newExpression);
		newExpression.setWhenExpression(statementTransform.transform(whenClause.getWhenExpression()));
		newExpression.setThenExpression(statementTransform.transform(whenClause.getThenExpression()));		
	}

	public void visit(AllComparisonExpression allComparisonExpression) {
		AllComparisonExpression newExpression = new AllComparisonExpression();
		setExpression(newExpression);		
		newExpression.setSubSelect((SubSelect) statementTransform.transform((Expression)allComparisonExpression.getSubSelect()));
	}

	public void visit(AnyComparisonExpression anyComparisonExpression) {
		AnyComparisonExpression newExpression = new AnyComparisonExpression();
		setExpression(newExpression);
		newExpression.setSubSelect((SubSelect) statementTransform.transform((Expression)anyComparisonExpression.getSubSelect()));
	}

	public void visit(ColumnIndex columnIndex) {
		ColumnIndex newIndex = new ColumnIndex();
		newIndex.setIndex(columnIndex.getIndex());
		expression = newIndex;
	}

	public void visit(OrderByElement orderByElement) {

		OrderByElement newElement = new OrderByElement();
		setExpression(newElement);
		newElement.setNullOrdering(orderByElement.getNullOrdering());		
		newElement.setAsc(orderByElement.isAsc());
		newElement.setColumnReference(statementTransform.transform(orderByElement.getColumnReference()));		
	}


	public void visit(GroupingExpression groupingExpression) {
		ExpressionList newExpression = new ExpressionList();
		setExpression(newExpression);
		newExpression.setExpressions(new ArrayList<Expression>());
		for(Expression next : groupingExpression.getExpressions()){
			newExpression.getExpressions().add(next);
		}
			
	}

	public void visit(AnalyticClause analyticClause) {

		AnalyticClause newClause = new AnalyticClause();
		setExpression(newClause);	
		if(analyticClause.getQueryPartitionClause() != null){
		   newClause.setQueryPartitionClause((QueryPartitionClause)statementTransform.transform(analyticClause.getQueryPartitionClause()));
		}
		if(analyticClause.getOrderByClause() != null){
			OrderByClause orderBy = new OrderByClause();
			newClause.setOrderByClause(orderBy);
			orderBy.setElements( new ArrayList<OrderByElement>());
			for(OrderByElement next : analyticClause.getOrderByClause().getElements() ){
				orderBy.getElements().add((OrderByElement) statementTransform.transform(next));
			}
			WindowClause oldWindow = analyticClause.getOrderByClause().getWindowClause();
			if(oldWindow != null){				
				orderBy.setWindowClause(oldWindow);				
			}
		}

	}

	public void visit(CastExpression castExpression) {
		CastExpression newCast = new CastExpression();
		setExpression(newCast);
		newCast.setType(castExpression.getType());
		newCast.setExpression(statementTransform.transform(castExpression.getExpression()));
	}

	public void visit(NamedParameter namedParameter) {

		expression = new NamedParameter(namedParameter.getName());

	}

	public Expression getExpression() {		
		return expression;
	}


	public void visit(QueryPartitionClause queryPartitionClause) {
		QueryPartitionClause newClause = new QueryPartitionClause();
		setExpression(newClause);
		newClause.setExpressionList((ExpressionList) statementTransform.transform((Expression)queryPartitionClause.getExpressionList()));

	}



}