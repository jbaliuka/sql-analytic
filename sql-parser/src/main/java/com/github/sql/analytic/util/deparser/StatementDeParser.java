package com.github.sql.analytic.util.deparser;

import java.util.Iterator;

import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.statement.Cursor;
import com.github.sql.analytic.statement.StatementVisitor;
import com.github.sql.analytic.statement.Variable;
import com.github.sql.analytic.statement.create.table.CreateTable;
import com.github.sql.analytic.statement.create.view.CreateView;
import com.github.sql.analytic.statement.delete.Delete;
import com.github.sql.analytic.statement.drop.Drop;
import com.github.sql.analytic.statement.insert.Insert;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.statement.replace.Replace;
import com.github.sql.analytic.statement.select.PlainSelect;
import com.github.sql.analytic.statement.select.Select;
import com.github.sql.analytic.statement.select.SelectListItem;
import com.github.sql.analytic.statement.select.WithItem;
import com.github.sql.analytic.statement.truncate.Truncate;
import com.github.sql.analytic.statement.update.Update;



public class StatementDeParser implements StatementVisitor {
	protected StringBuffer buffer;

	public StatementDeParser(StringBuffer buffer) {
		this.buffer = buffer;
	}

	public void visit(CreateTable createTable) {
		CreateTableDeParser createTableDeParser = new CreateTableDeParser(buffer);
		createTableDeParser.deParse(createTable);
	}

	public void visit(Delete delete) {
		SelectDeParser selectDeParser = createSelectDeParser();
		selectDeParser.setBuffer(buffer);
		ExpressionDeParser expressionDeParser = createExpressionDeparser(selectDeParser, buffer);
		selectDeParser.setExpressionVisitor(expressionDeParser);
		DeleteDeParser deleteDeParser = new DeleteDeParser(expressionDeParser, buffer);
		deleteDeParser.deParse(delete);
	}

	public void visit(Drop drop) {
		// TODO Auto-generated method stub

	}

	public void visit(Insert insert) {
		SelectDeParser selectDeParser = createSelectDeParser();
		selectDeParser.setBuffer(buffer);
		ExpressionDeParser expressionDeParser = createExpressionDeparser(selectDeParser, buffer);
		selectDeParser.setExpressionVisitor(expressionDeParser);
		InsertDeParser insertDeParser = new InsertDeParser(expressionDeParser, selectDeParser, buffer);
		insertDeParser.deParse(insert);

	}

	public void visit(Replace replace) {
		SelectDeParser selectDeParser = createSelectDeParser();
		selectDeParser.setBuffer(buffer);
		ExpressionDeParser expressionDeParser = createExpressionDeparser(selectDeParser, buffer);
		selectDeParser.setExpressionVisitor(expressionDeParser);
		ReplaceDeParser replaceDeParser = new ReplaceDeParser(expressionDeParser, selectDeParser, buffer);
		replaceDeParser.deParse(replace);
	}


	protected SelectDeParser createSelectDeParser(){
		return  new SelectDeParser();
	}


	public void visit(Select select) {
		SelectDeParser selectDeParser = createSelectDeParser();
		selectDeParser.setBuffer(buffer);
		ExpressionDeParser expressionDeParser = createExpressionDeparser(selectDeParser,buffer);

		selectDeParser.setExpressionVisitor(expressionDeParser);
		if (select.getWithItemsList() != null && !select.getWithItemsList().isEmpty()) {
			buffer.append("WITH ");			
			for (Iterator<WithItem> iter = select.getWithItemsList().iterator(); iter.hasNext();) {
				WithItem withItem = iter.next();
				buffer.append(withItem.getName());
				if(withItem.getWithItemList() != null){
					buffer.append("(");					
					buffer.append(PlainSelect.getStringList(withItem.getWithItemList(),true,false));					
					buffer.append(")");
				}
				buffer.append(" AS (");							
				withItem.getSelectBody().accept(selectDeParser);
				buffer.append(" )");

				if (iter.hasNext()){
					buffer.append(",");
				}
				buffer.append(" ");
			}
		}
		select.getSelectBody().accept(selectDeParser);

	}

	protected ExpressionDeParser createExpressionDeparser(SelectDeParser selectDeParser, StringBuffer buffer2) {

		return new ExpressionDeParser(selectDeParser, buffer);

	}

	public void visit(Truncate truncate) {
		// TODO Auto-generated method stub

	}

	public void visit(Update update) {
		SelectDeParser selectDeParser = createSelectDeParser();
		selectDeParser.setBuffer(buffer);
		ExpressionDeParser expressionDeParser = createExpressionDeparser(selectDeParser, buffer);
		UpdateDeParser updateDeParser = new UpdateDeParser(expressionDeParser, buffer);
		selectDeParser.setExpressionVisitor(expressionDeParser);
		updateDeParser.deParse(update);

	}

	public StringBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(StringBuffer buffer) {
		this.buffer = buffer;
	}

	public void visit(CreateView createView) {
		// TODO Auto-generated method stub

	}

	public void visit(CreatePolicy policy) {

		buffer.append("CREATE POLICY ").
		append(policy.getName()).append(" ON ").append(policy.getTable());

		if(policy.getColumns() != null){
			buffer.append("(");
			int i = 0;
			for(Column column : policy.getColumns()){
				buffer.append(column);
				if(i++ < policy.getColumns().size() - 1){
					buffer.append(",");	
				}
			}
			buffer.append(")");
		}

		if(policy.getAction() != null){
			buffer.append(" FOR ").append(policy.getAction());	
		}		
		if( policy.getRoles() != null ){
			buffer.append(" TO ");
			int i = 0;
			for(String role : policy.getRoles()){
				buffer.append(role);
				if(i++ < policy.getRoles().size() - 1){
					buffer.append(",");	
				}
			}
		}
		if(policy.getUsing() != null){
			buffer.append(" USING(");
			buffer.append(policy.getUsing());
			buffer.append(")");
		}
		if(policy.getCheck() != null){
			buffer.append(" WITH CHECK(");
			buffer.append(policy.getCheck());
			buffer.append(")");
		}



	}

	@Override
	public void visit(Cursor cursor) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(Variable variable) {
		// TODO Auto-generated method stub

	}



}
