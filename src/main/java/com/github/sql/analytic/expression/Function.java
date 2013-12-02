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

public class Function implements Expression,FromItem {

	private String name;
	private ExpressionList parameters;
	private boolean allColumns = false;
	private boolean isEscaped = false;
	private String alias;
	private boolean pipeline = false;
	private boolean distinct = false;
	private AnalyticCause analyticCause;
	
	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
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

	public void setName(String string) {
		name = string;
	}

	/**
	 * true if the parameter to the function is "*"
	 * @return true if the parameter to the function is "*"
	 */
	public boolean isAllColumns() {
		return allColumns;
	}

	public void setAllColumns(boolean b) {
		allColumns = b;
	}

	/**
	 * The list of parameters of the function (if any, else null)
	 * If the parameter is "*", allColumns is set to true
	 * @return the list of parameters of the function (if any, else null)
	 */
	public ExpressionList getParameters() {
		return parameters;
	}

	public void setParameters(ExpressionList list) {
		parameters = list;
	}

    /**
     * Return true if it's in the form "{fn function_body() }" 
     * @return true if it's java-escaped
     */
    public boolean isEscaped() {
        return isEscaped;
    }
    
    public void setEscaped(boolean isEscaped) {
        this.isEscaped = isEscaped;
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

	public void setPipeline(boolean pipeline) {
		this.pipeline = pipeline;
	}

	public boolean isPipeline() {
		return pipeline;
	}

	public AnalyticCause getAnalyticCause() {
		return analyticCause;
	}

	public void setAnalyticCause(AnalyticCause analyticCause) {
		this.analyticCause = analyticCause;
	}
}
