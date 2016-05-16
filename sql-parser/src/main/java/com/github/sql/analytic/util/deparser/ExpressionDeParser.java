package com.github.sql.analytic.util.deparser;

import java.util.Iterator;
import java.util.List;

import com.github.sql.analytic.expression.AllComparisonExpression;
import com.github.sql.analytic.expression.AnalyticClause;
import com.github.sql.analytic.expression.AnyComparisonExpression;
import com.github.sql.analytic.expression.BinaryExpression;
import com.github.sql.analytic.expression.CaseExpression;
import com.github.sql.analytic.expression.CastExpression;
import com.github.sql.analytic.expression.DateValue;
import com.github.sql.analytic.expression.DoubleValue;
import com.github.sql.analytic.expression.ExpressionVisitor;
import com.github.sql.analytic.expression.Function;
import com.github.sql.analytic.expression.GroupingExpression;
import com.github.sql.analytic.expression.InverseExpression;
import com.github.sql.analytic.expression.JdbcParameter;
import com.github.sql.analytic.expression.LongValue;
import com.github.sql.analytic.expression.NamedParameter;
import com.github.sql.analytic.expression.NullValue;
import com.github.sql.analytic.expression.Parenthesis;
import com.github.sql.analytic.expression.QueryPartitionClause;
import com.github.sql.analytic.expression.SQLExpression;
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
import com.github.sql.analytic.expression.operators.relational.ExpressionList;
import com.github.sql.analytic.expression.operators.relational.GreaterThan;
import com.github.sql.analytic.expression.operators.relational.GreaterThanEquals;
import com.github.sql.analytic.expression.operators.relational.InExpression;
import com.github.sql.analytic.expression.operators.relational.IsNullExpression;
import com.github.sql.analytic.expression.operators.relational.ItemsListVisitor;
import com.github.sql.analytic.expression.operators.relational.LikeExpression;
import com.github.sql.analytic.expression.operators.relational.MinorThan;
import com.github.sql.analytic.expression.operators.relational.MinorThanEquals;
import com.github.sql.analytic.expression.operators.relational.NotEqualsTo;
import com.github.sql.analytic.expression.operators.string.Concat;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.statement.select.ColumnIndex;
import com.github.sql.analytic.statement.select.OrderByElement;
import com.github.sql.analytic.statement.select.SelectVisitor;
import com.github.sql.analytic.statement.select.SubSelect;


/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a string)
 * an {@link com.github.sql.analytic.expression.SQLExpression}
 */

public class ExpressionDeParser implements ExpressionVisitor, ItemsListVisitor {

	protected StringBuffer buffer;
	protected SelectVisitor selectVisitor;

	public ExpressionDeParser() {
	}

	/**
	 * @param selectVisitor a SelectVisitor to de-parse SubSelects. It has to share the same<br>
	 * StringBuffer as this object in order to work, as:
	 * <pre>
	 * <code>
	 * StringBuffer myBuf = new StringBuffer();
	 * MySelectDeparser selectDeparser = new  MySelectDeparser();
	 * selectDeparser.setBuffer(myBuf);
	 * ExpressionDeParser expressionDeParser = new ExpressionDeParser(selectDeparser, myBuf);
	 * </code>
	 * </pre>
	 * @param buffer the buffer that will be filled with the expression
	 */
	public ExpressionDeParser(SelectVisitor selectVisitor, StringBuffer buffer) {
		this.selectVisitor = selectVisitor;
		this.buffer = buffer;
	}

	public void visit(Concat concat) {
		visitBinaryExpression(concat, " || ");

	}

	public StringBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(StringBuffer buffer) {
		this.buffer = buffer;
	}

	public void visit(Addition addition) {
		visitBinaryExpression(addition, "+");
	}

	public void visit(AndExpression andExpression) {
		visitBinaryExpression(andExpression, " AND ");
	}

	public void visit(Between between) {
		between.getLeftExpression().accept(this);
		if (between.isNot()){
			buffer.append(" NOT");
		}

		buffer.append(" BETWEEN ");
		between.getBetweenExpressionStart().accept(this);
		buffer.append(" AND ");
		between.getBetweenExpressionEnd().accept(this);

	}

	public void visit(Division division) {
		visitBinaryExpression(division, "/");

	}

	public void visit(DoubleValue doubleValue) {
		buffer.append(doubleValue.getValue());

	}

	public void visit(EqualsTo equalsTo) {
		visitBinaryExpression(equalsTo, " = ");
	}

	public void visit(GreaterThan greaterThan) {
		visitBinaryExpression(greaterThan, " > ");
	}

	public void visit(GreaterThanEquals greaterThanEquals) {
		visitBinaryExpression(greaterThanEquals, " >= ");

	}

	public void visit(InExpression inExpression) {

		inExpression.getLeftExpression().accept(this);
		if (inExpression.isNot()){
			buffer.append(" NOT");
		}
		buffer.append(" IN ");

		inExpression.getItemsList().accept(this);
	}

	public void visit(InverseExpression inverseExpression) {
		buffer.append("-");
		inverseExpression.getExpression().accept(this);
	}

	public void visit(IsNullExpression isNullExpression) {
		isNullExpression.getLeftExpression().accept(this);
		if (isNullExpression.isNot()) {
			buffer.append(" IS NOT NULL");
		} else {
			buffer.append(" IS NULL");
		}
	}

	public void visit(JdbcParameter jdbcParameter) {
		buffer.append("?");

	}

	public void visit(LikeExpression likeExpression) {
		visitBinaryExpression(likeExpression, " LIKE ");

	}

	public void visit(ExistsExpression existsExpression) {
		if (existsExpression.isNot()) {
			buffer.append(" NOT EXISTS ");
		} else {
			buffer.append(" EXISTS ");
		}
		existsExpression.getRightExpression().accept(this);
	}

	public void visit(LongValue longValue) {
		buffer.append(longValue.getStringValue());

	}

	public void visit(MinorThan minorThan) {
		visitBinaryExpression(minorThan, " < ");

	}

	public void visit(MinorThanEquals minorThanEquals) {
		visitBinaryExpression(minorThanEquals, " <= ");

	}

	public void visit(Multiplication multiplication) {
		visitBinaryExpression(multiplication, "*");

	}

	public void visit(NotEqualsTo notEqualsTo) {
		visitBinaryExpression(notEqualsTo, " <> ");

	}

	public void visit(NullValue nullValue) {
		buffer.append("NULL");

	}

	public void visit(OrExpression orExpression) {
		visitBinaryExpression(orExpression, " OR ");

	}

	public void visit(Parenthesis parenthesis) {
		if (parenthesis.isNot()){
			buffer.append(" NOT ");
		}

		buffer.append("(");
		parenthesis.getExpression().accept(this);
		buffer.append(")");

	}

	public void visit(StringValue stringValue) {
		buffer.append("'").append(stringValue.getValue()).append("'");

	}

	public void visit(Subtraction subtraction) {
		visitBinaryExpression(subtraction, "-");

	}

	private void visitBinaryExpression(BinaryExpression binaryExpression, String operator) {
		if (binaryExpression.isNot()){
			buffer.append(" NOT ");
		}
		if(binaryExpression != null && 
				binaryExpression.getLeftExpression() != null && 
				binaryExpression.getRightExpression() != null){
			binaryExpression.getLeftExpression().accept(this);
			buffer.append(operator);
			binaryExpression.getRightExpression().accept(this);
		}

	}

	public void visit(SubSelect subSelect) {
		buffer.append("(");
		subSelect.getSelectBody().accept(selectVisitor);
		buffer.append(")");
	}

	public void visit(Column tableColumn) {
		if( tableColumn.getTable() != null){
			String tableName = tableColumn.getTable().getWholeTableName();
			if (tableName != null) {
				buffer.append(tableName).append(".");
			}
		}

		buffer.append(tableColumn.getColumnName());
	}

	public void visit(Function function) {
		if (function.isEscaped()) {
			buffer.append("{fn ");
		}

		buffer.append(function.getName());
		if (function.isAllColumns()) {
			buffer.append("(*)");
		} else if (function.getParameters() == null) {
			buffer.append("()");
		} else {
			visit(function.getParameters());
		}
		if(function.getAnalyticCause() != null){
			visit(function.getAnalyticCause());
		}

		if (function.isEscaped()) {
			buffer.append("}");
		}

	}

	public void visit(ExpressionList expressionList) {
		buffer.append("(");
		for (Iterator<SQLExpression> iter = expressionList.getExpressions().iterator(); iter.hasNext();) {
			SQLExpression expression = (SQLExpression) iter.next();
			expression.accept(this);
			if (iter.hasNext()){
				buffer.append(", ");
			}
		}
		buffer.append(")");

	}

	public SelectVisitor getSelectVisitor() {
		return selectVisitor;
	}

	public void setSelectVisitor(SelectVisitor visitor) {
		selectVisitor = visitor;
	}

	public void visit(DateValue dateValue) {
		buffer.append("{d '").append(dateValue.getValue().toString()).append("'}");
	}
	public void visit(TimestampValue timestampValue) {
		buffer.append("{ts '").append(timestampValue.getValue().toString()).append("'}");
	}
	public void visit(TimeValue timeValue) {
		buffer.append("{t '").append(timeValue.getValue().toString()).append("'}");
	}

	public void visit(CaseExpression caseExpression) {
		buffer.append("CASE ");
		SQLExpression switchExp = caseExpression.getSwitchExpression();
		if( switchExp != null ) {
			switchExp.accept(this);
		}

		List<WhenClause> clauses = caseExpression.getWhenClauses();
		for (Object clause : clauses) {
			SQLExpression exp = (SQLExpression) clause;
			exp.accept(this);
		}

		SQLExpression elseExp = caseExpression.getElseExpression();
		if( elseExp != null ) {
			buffer.append(" ELSE ");
			elseExp.accept(this);
		}

		buffer.append(" END");
	}

	public void visit(WhenClause whenClause) {
		buffer.append(" WHEN ");
		whenClause.getWhenExpression().accept(this);
		buffer.append(" THEN ");
		whenClause.getThenExpression().accept(this);
	}

	public void visit(AllComparisonExpression allComparisonExpression) {
		buffer.append(" ALL ");
		allComparisonExpression.getSubSelect().accept((ExpressionVisitor)this);
	}

	public void visit(AnyComparisonExpression anyComparisonExpression) {
		buffer.append(" ANY ");
		anyComparisonExpression.getSubSelect().accept((ExpressionVisitor)this);
	}

	public void visit(ColumnIndex columnIndex) {

		buffer.append(" ").append(columnIndex.getIndex()).append(" ");
	}

	public void visit(OrderByElement orderByElement) {

		orderByElement.getColumnReference().accept(this);
		if(!orderByElement.isAsc()){
			buffer.append(" DESC" );
		}else {
			buffer.append(" ASC" );
		}

	}


	public void visit(GroupingExpression groupingExpression) {
		buffer.append(groupingExpression);		
	}

	public void visit(AnalyticClause analyticCause) {
		buffer.append(analyticCause.toString());
	}

	public void visit(CastExpression castExpression) {
		buffer.append("CAST (");
		castExpression.getExpression().accept(this);
		buffer.append(" AS ");
		buffer.append(castExpression.getType()).append(")");
	}

	public void visit(NamedParameter namedParameter) {

		buffer.append(namedParameter);

	}

	public void visit(QueryPartitionClause queryPartitionClause) {

		buffer.append(queryPartitionClause);
	}



}