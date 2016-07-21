package com.github.sql.analytic.expression;

import java.util.List;

import com.github.sql.analytic.statement.select.OrderByElement;
import com.github.sql.analytic.statement.select.PlainSelect;

public class OrderByClause {
	
	private List<OrderByElement> elements;
	private WindowClause windowClause;

	public List<OrderByElement> getElements() {
		return elements;
	}

	public OrderByClause setElements(List<OrderByElement> elements) {
		this.elements = elements;
		return this;
	}
	
	@Override
	public String toString() {
		
		StringBuffer  buffer = new StringBuffer("ORDER BY ");
		buffer.append(PlainSelect.getStringList(elements, true, false));
		if(windowClause != null){
			buffer.append(' ');
			buffer.append(windowClause);
		}
		return buffer.toString();
	}

	public WindowClause getWindowClause() {
		return windowClause;
	}

	public void setWindowClause(WindowClause windowCause) {
		this.windowClause = windowCause;
	}

}
