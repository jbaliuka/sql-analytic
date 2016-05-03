package com.github.sql.analytic.expression;

import java.util.List;


public interface GroupingExpression extends SQLExpression {
	
	List<SQLExpression> getExpressions();

}
