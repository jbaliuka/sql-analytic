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

import com.github.sql.analytic.expression.operators.relational.ExpressionList;
import com.github.sql.analytic.statement.select.FromItem;
import com.github.sql.analytic.statement.select.FromItemVisitor;

/**
 * A function as MAX,COUNT...
 */

public class Function implements SQLExpression,FromItem {

	private String name;
	private ExpressionList parameters;
	private boolean allColumns = false;
	private boolean isEscaped = false;
	private String alias;
	private boolean pipeline = false;
	private boolean distinct = false;
	private AnalyticClause analyticCause;
	
	public Function(String name) {
		this.name = name;
	}

	public Function() {		
	}

	public boolean isDistinct() {
		return distinct;
	}

	public Function setDistinct(boolean distinct) {
		this.distinct = distinct;
		return this;
	}

	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}
	
	/**
	 * The name of he function, i.e. "MAX"
	 * @return the name of he function
	 */
	public String getName() {
		return name;
	}

	public Function setName(String string) {
		name = string;
		return this;
	}

	/**
	 * true if the parameter to the function is "*"
	 * @return true if the parameter to the function is "*"
	 */
	public boolean isAllColumns() {
		return allColumns;
	}

	public Function setAllColumns(boolean b) {
		allColumns = b;
		return this;
	}

	/**
	 * The list of parameters of the function (if any, else null)
	 * If the parameter is "*", allColumns is set to true
	 * @return the list of parameters of the function (if any, else null)
	 */
	public ExpressionList getParameters() {
		return parameters;
	}

	public Function setParameters(ExpressionList list) {
		parameters = list;
		return this;
	}

    /**
     * Return true if it's in the form "{fn function_body() }" 
     * @return true if it's java-escaped
     */
    public boolean isEscaped() {
        return isEscaped;
    }
    
    public Function setEscaped(boolean isEscaped) {
        this.isEscaped = isEscaped;
        return this;
    }

    public String toString() {
    	String params = "";
    	
    	if(allColumns) {
    		params = "(*)";
    	}else if(parameters != null) {    		
    		params = parameters.toString();    		
    	}else if (parameters == null){
    		params="()";
    	}
    	
    	String ans = name.toString() + params.toString();
    	
    	if(analyticCause != null){
    		
    		ans = ans +  analyticCause.toString();
    	}
    	
    	if(isEscaped) {
    		ans = "{fn "+ans+"}"; 
    	}
    	
    	ans = pipeline ? "TABLE(" + ans + ")" : ans;
    	ans = alias == null ?  ans : ans + " " + alias + " ";
    	
    	return ans;
    }

	public void accept(FromItemVisitor fromItemVisitor) {
		fromItemVisitor.visit(this);
	}

	public String getAlias() {		
		return alias;
	}

	public void setAlias(String alias) {
	  this.alias = alias;	  
	}

	public Function setPipeline(boolean pipeline) {
		this.pipeline = pipeline;
		return this;
	}

	public boolean isPipeline() {
		return pipeline;
	}

	public AnalyticClause getAnalyticCause() {
		return analyticCause;
	}

	public Function setAnalyticClause(AnalyticClause analyticCause) {
		this.analyticCause = analyticCause;
		return this;
	}
}
