package com.github.sql.analytic.statement.create.view;

import java.util.ArrayList;
import java.util.List;

import com.github.sql.analytic.statement.Statement;
import com.github.sql.analytic.statement.StatementVisitor;
import com.github.sql.analytic.statement.select.PlainSelect;
import com.github.sql.analytic.statement.select.Select;



public class CreateView implements Statement{

	private String schema,name;
	private Select select;
	
	@SuppressWarnings("rawtypes")
	private List fields;
	
	@SuppressWarnings("rawtypes")
	private List options;
	
	
	@SuppressWarnings("rawtypes")
	public List getOptions() {
		if(options == null){
			options = new ArrayList();
		}
		return options;
	}

	public Select getSelect() {
		return select;
	}

	public void setSelect(Select select) {
		this.select = select;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	@SuppressWarnings("rawtypes")
	public List getFields() {
		return fields;
	}

	
	@SuppressWarnings("rawtypes")
	public void setFields(List fields) {
		this.fields = fields;
	}

	
	
	public void accept(StatementVisitor statementVisitor) {
		 statementVisitor.visit(this);		
	}

	@Override
	public String toString() {
	
		StringBuilder builder = new StringBuilder();
		builder.append("CREATE VIEW ").append(options != null ? PlainSelect.getStringList(fields, false, false) : "").append(schema == null ? "" : schema + ".").append(name);
		if(fields != null){
			builder.append(PlainSelect.getStringList(fields,true,true));
		}
		builder.append(" AS ");
		builder.append(select.toString());
		
		return builder.toString();
		
	}

}
