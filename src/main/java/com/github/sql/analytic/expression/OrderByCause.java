package com.github.sql.analytic.expression;

import java.util.List;

import com.github.sql.analytic.statement.select.OrderByElement;
import com.github.sql.analytic.statement.select.PlainSelect;

public class OrderByCause {
	
	private List<OrderByElement> elements;
	private WindowCause windowCause;

	public List<OrderByElement> getElements() {
		return elements;
	}

	public void setElements(List<OrderByElement> elements) {
		this.elements = elements;
	}
	
	@Override
	public String toString() {
		
		StringBuffer  buffer = new StringBuffer("ORDER BY ");
		buffer.append(PlainSelect.getStringList(elements, true, false));
		if(windowCause != null){
			buffer.append(' ');
			buffer.append(windowCause);
		}
		return buffer.toString();
	}

	public WindowCause getWindowCause() {
		return windowCause;
	}

	public void setWindowCause(WindowCause windowCause) {
		this.windowCause = windowCause;
	}

}
