package com.github.sql.analytic.transform;

import com.github.sql.analytic.statement.Statement;
import com.github.sql.analytic.statement.delete.Delete;


public class DeleteTransform {
	
	private StatementTransform statementTransform;

	public DeleteTransform(StatementTransform statementTransform) {
		super();
		this.statementTransform = statementTransform;
	}

	public Statement transform(Delete delete) {
		
		Delete newDelete = new Delete();
		newDelete.setTable(statementTransform.copy(delete.getTable()));
		
		if (delete.getWhere() != null) {			
			newDelete.setWhere(statementTransform.transform(delete.getWhere()));
		}

		return newDelete;
	}
	
}
