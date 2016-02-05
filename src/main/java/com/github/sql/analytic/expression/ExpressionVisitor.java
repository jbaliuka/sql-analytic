/* ================================================================
 * JSQLParser : java based sql parser 
 * ================================================================
 *
 * Project Info:  http://jsqlparser.sourceforge.net
 * Project Lead:  Leonardo Francalanci (leoonardoo@yahoo.it);
 *
 * (C) Copyright 2004, by Leonardo Francalanci
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */
 
 package com.github.sql.analytic.expression;

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
import com.github.sql.analytic.statement.select.ColumnIndex;
import com.github.sql.analytic.statement.select.OrderByElement;
import com.github.sql.analytic.statement.select.SubSelect;



public interface ExpressionVisitor {
	public void visit(Concat concat);
	public void visit(Function function);
	public void visit(InverseExpression inverseExpression);
	public void visit(JdbcParameter jdbcParameter);
	public void visit(DoubleValue doubleValue);
	public void visit(LongValue longValue);
	public void visit(DateValue dateValue);
	public void visit(TimeValue timeValue);
	public void visit(TimestampValue timestampValue);
	public void visit(Parenthesis parenthesis);
	public void visit(StringValue stringValue);
	public void visit(Addition addition);
	public void visit(Division division);
	public void visit(Multiplication multiplication);
	public void visit(Subtraction subtraction);
	public void visit(AndExpression andExpression);
	public void visit(OrExpression orExpression);
	public void visit(Between between);
	public void visit(EqualsTo equalsTo);
	public void visit(GreaterThan greaterThan);
	public void visit(GreaterThanEquals greaterThanEquals);
	public void visit(InExpression inExpression);
	public void visit(IsNullExpression isNullExpression);
	public void visit(LikeExpression likeExpression);
	public void visit(MinorThan minorThan);
	public void visit(MinorThanEquals minorThanEquals);
	public void visit(NotEqualsTo notEqualsTo);
	public void visit(Column tableColumn);
	public void visit(SubSelect subSelect);
	public void visit(CaseExpression caseExpression);
	public void visit(WhenClause whenClause);
	public void visit(ExistsExpression existsExpression);
	public void visit(AllComparisonExpression allComparisonExpression);
	public void visit(AnyComparisonExpression anyComparisonExpression);
	public void visit(NullValue nullValue);
	public void visit(ColumnIndex columnIndex);
	public void visit(OrderByElement orderByElement);
	public void visit(GroupingExpression groupingExpression);
	public void visit(AnalyticCause analyticCause);
	public void visit(CastExpression castExpression);


}
