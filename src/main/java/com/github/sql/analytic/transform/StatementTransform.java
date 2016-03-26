package com.github.sql.analytic.transform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.sql.analytic.expression.Expression;
import com.github.sql.analytic.expression.operators.relational.ItemsList;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.Statement;
import com.github.sql.analytic.statement.StatementVisitor;
import com.github.sql.analytic.statement.create.table.CreateTable;
import com.github.sql.analytic.statement.create.view.CreateView;
import com.github.sql.analytic.statement.delete.Delete;
import com.github.sql.analytic.statement.drop.Drop;
import com.github.sql.analytic.statement.insert.Insert;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.statement.replace.Replace;
import com.github.sql.analytic.statement.select.Select;
import com.github.sql.analytic.statement.select.SelectBody;
import com.github.sql.analytic.statement.select.SelectItem;
import com.github.sql.analytic.statement.select.WithItem;
import com.github.sql.analytic.statement.truncate.Truncate;
import com.github.sql.analytic.statement.update.Update;



public class StatementTransform  implements StatementVisitor {

	private Statement statement;

	public void visit(CreateTable createTable) {

	}

	public void visit(Delete delete) {
		DeleteTransform deleteTransform = createDeleteTransform();
		statement = deleteTransform.transform(delete);
	}

	protected DeleteTransform createDeleteTransform() {
		return new DeleteTransform(this);
	}

	public void visit(Drop drop) {


	}

	public void visit(Insert insert) {
		statement = new InsertTransform(this).transform(insert);
	}

	public void visit(Replace replace) {

	}


	protected SelectTransform createSelectTransform(){
		return  new SelectTransform(this);
	}


	public SelectBody transform(SelectBody selectBody) {
		
		SelectTransform selectTranssform = createSelectTransform();		
		selectBody.accept(selectTranssform);
		
		return selectTranssform.getSelectBody();
		
	}
	
	
	public void visit(Select select) {
		
		statement = new Select();
		SelectTransform selectTranssform = createSelectTransform();

		if (select.getWithItemsList() != null && !select.getWithItemsList().isEmpty()) {
			List<WithItem> withItems = new ArrayList<WithItem>();
			((Select)statement).setWithItemsList(withItems);			
			for (Iterator<WithItem> iter = select.getWithItemsList().iterator(); iter.hasNext();) {
				WithItem withItem = iter.next();
				WithItem newItem = new WithItem();
				newItem.setName(withItem.getName());
				if(withItem.getWithItemList() != null && !withItem.getWithItemList().isEmpty()){
					List<SelectItem> selectItems = new ArrayList<SelectItem>();
					for(SelectItem item: withItem.getWithItemList()){
						selectItems.add(item);
					}
				}
				selectTranssform.visit(newItem);

				withItem.getSelectBody().accept(selectTranssform);
				newItem.setSelectBody(selectTranssform.getSelectBody());
				withItems.add(newItem);

			}
		}
		
		((Select)statement).setSelectBody(transform(select.getSelectBody()));

	}

	protected ExpressionTransform createExpressionTransform() {

		return new ExpressionTransform(this);

	}

	public final Expression transform(Expression epr){
		ExpressionTransform transform = createExpressionTransform();
		epr.accept(transform);
		return transform.getExpression(); 
	} 

	public void visit(Truncate truncate) {
		// TODO Auto-generated method stub

	}

	public void visit(Update update) {

		UpdateTransform updateTransform = createUpdateTransform();
		statement = updateTransform.transform(update);

	}

	protected UpdateTransform createUpdateTransform() {
		return new UpdateTransform(this);
	}



	public void visit(CreateView createView) {

	}

	

	public ItemListTransform createItemListTransform() {		
		return new ItemListTransform(this);
	}

	public ItemsList transform(ItemsList itemsList) {

		ItemListTransform transform  = createItemListTransform();
		itemsList.accept(transform);
		return transform.getItemList();

	}

	public Statement trasform(Statement statement) {
		statement.accept(this);
		return this.statement;
	}

	protected SelectItemTransfrom createSelectItemTransform() {		
		return new SelectItemTransfrom(this);
	}

	

	protected Table copy(Table table) {
		
		Table newTable = new Table(table.getSchemaName(),table.getName());
		newTable.setAlias(table.getAlias());
		newTable.setPartition(table.getPartition());
		newTable.setPartitionFor(table.isPartitionFor());

		return newTable;
	}

	public void visit(CreatePolicy policy) {
		// TODO Auto-generated method stub
		
	}

	

}
