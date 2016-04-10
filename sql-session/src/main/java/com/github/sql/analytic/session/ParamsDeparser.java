package com.github.sql.analytic.session;

import java.util.ArrayList;
import java.util.List;

import com.github.sql.analytic.expression.JdbcParameter;
import com.github.sql.analytic.expression.NamedParameter;
import com.github.sql.analytic.util.deparser.ExpressionDeParser;
import com.github.sql.analytic.util.deparser.SelectDeParser;
import com.github.sql.analytic.util.deparser.StatementDeParser;

public class ParamsDeparser extends StatementDeParser{

	private List<Integer> positionalParams = new ArrayList<>();
	private List<ParamNamePosition> namedParams = new ArrayList<>();
	private int position = 0;

	public ParamsDeparser(StringBuffer buffer) {
		super(buffer);		
	}


	public List<Integer> getPositionalParams() {
		return positionalParams;
	}

	public List<ParamNamePosition> getNamedParams() {
		return namedParams;
	}

	@Override
	protected ExpressionDeParser createExpressionDeparser(SelectDeParser selectDeParser, StringBuffer buffer2) {


		return  new ExpressionDeParser(selectDeParser,buffer){
			@Override
			public void visit(JdbcParameter jdbcParameter) {				
				super.visit(jdbcParameter);
				positionalParams.add(position++);
			}

			@Override
			public void visit(NamedParameter namedParameter) {
				if(namedParameter.getName().startsWith("session_")){
					super.visit(new JdbcParameter(null));
					namedParams.add(new ParamNamePosition(namedParameter.getName(), position ++ ));
				}else {
					super.visit(namedParameter);
				}
			}

		};
	}


	public DeparsedSQL getDeparsedSQL() {
		DeparsedSQL deparsed =  new DeparsedSQL();
		deparsed.setSql(getBuffer().toString());
		deparsed.setPositionalParams(positionalParams);
		deparsed.setSessionParams(namedParams);		
		return deparsed;
	}

}
