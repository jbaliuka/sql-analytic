package com.github.sql.analytic.util.deparser;

import java.util.Iterator;

import com.github.sql.analytic.statement.StatementVisitor;
import com.github.sql.analytic.statement.create.table.CreateTable;
import com.github.sql.analytic.statement.create.view.CreateView;
import com.github.sql.analytic.statement.delete.Delete;
import com.github.sql.analytic.statement.drop.Drop;
import com.github.sql.analytic.statement.insert.Insert;
import com.github.sql.analytic.statement.replace.Replace;
import com.github.sql.analytic.statement.select.Select;
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
		SelectDeParser selectDeParser = new SelectDeParser();
		selectDeParser.setBuffer(buffer);
		ExpressionDeParser expressionDeParser = new ExpressionDeParser(selectDeParser, buffer);
		selectDeParser.setExpressionVisitor(expressionDeParser);
		DeleteDeParser deleteDeParser = new DeleteDeParser(expressionDeParser, buffer);
		deleteDeParser.deParse(delete);
	}

	public void visit(Drop drop) {
		// TODO Auto-generated method stub

	}

	public void visit(Insert insert) {
		SelectDeParser selectDeParser = new SelectDeParser();
		selectDeParser.setBuffer(buffer);
		ExpressionDeParser expressionDeParser = new ExpressionDeParser(selectDeParser, buffer);
		selectDeParser.setExpressionVisitor(expressionDeParser);
		InsertDeParser insertDeParser = new InsertDeParser(expressionDeParser, selectDeParser, buffer);
		insertDeParser.deParse(insert);

	}

	public void visit(Replace replace) {
		SelectDeParser selectDeParser = new SelectDeParser();
		selectDeParser.setBuffer(buffer);
		ExpressionDeParser expressionDeParser = new ExpressionDeParser(selectDeParser, buffer);
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
			for (Iterator iter = select.getWithItemsList().iterator(); iter.hasNext();) {
				WithItem withItem = (WithItem)iter.next();
				buffer.append(withItem);
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
		SelectDeParser selectDeParser = new SelectDeParser();
		selectDeParser.setBuffer(buffer);
		ExpressionDeParser expressionDeParser = new ExpressionDeParser(selectDeParser, buffer);
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

}
