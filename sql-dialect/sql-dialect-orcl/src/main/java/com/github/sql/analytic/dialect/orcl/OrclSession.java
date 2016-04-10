package com.github.sql.analytic.dialect.orcl;

import java.sql.Connection;
import java.util.List;

import com.github.sql.analytic.session.ParamsDeparser;
import com.github.sql.analytic.session.SQLSession;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.statement.select.PlainSelect;
import com.github.sql.analytic.transform.policy.SessionContext;
import com.github.sql.analytic.util.deparser.SelectDeParser;

public class OrclSession extends SQLSession {

	public OrclSession(SessionContext context, Connection connection, List<CreatePolicy> policy) {
		super(context, connection, policy);	
	}
	
	@Override
	protected ParamsDeparser createDeparser(StringBuffer buffer) {
		
		return new ParamsDeparser(buffer){
			
			@Override
			protected SelectDeParser createSelectDeParser() {				
				return new SelectDeParser(){
			
					protected void deparseFrom(PlainSelect plainSelect) {
						if (plainSelect.getFromItem() != null) {
							buffer.append("FROM ");
							plainSelect.getFromItem().accept(this);
						}else {
							buffer.append(" FROM DUAL ");
						}
					}
					
				};
			}
			
		};
	}

}
