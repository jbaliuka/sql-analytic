package com.github.sql.analytic.odata;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.Literal;

import com.github.sql.analytic.session.SQLSession;
import com.github.sql.analytic.statement.Cursor;
import com.github.sql.analytic.statement.select.PlainSelect;
import com.github.sql.analytic.statement.select.Select;

public class CallCursorFunctionCommand extends ReadCommand {

	private Map<String, Cursor> cursors;
	private UriInfo uriInfo;
	private EdmEntityType entityType;

	public CallCursorFunctionCommand(UriInfo uriInfo) {
		this.uriInfo = uriInfo;
	}

	@Override
	public ResultSetIterator execute(SQLSession connection) throws ODataApplicationException {
		try{
			UriResource segment = uriInfo.getUriResourceParts().get(0);
			String functionName;
			if (segment instanceof UriResourceFunction){				
				EdmFunction function = ((UriResourceFunction) segment).getFunction();
				entityType = (EdmEntityType) function.getReturnType().getType();
				functionName = function.getName();
				setSelect((PlainSelect) cursors.get(functionName).getSelect().getSelectBody());
				for( UriParameter param : ((UriResourceFunction) segment).getParameters()){
					Expression expr = param.getExpression();
					if(expr instanceof Literal ){			
						EdmPrimitiveType type = (EdmPrimitiveType) ((Literal) expr).getType();
						Object value = type.valueOfString(((Literal) expr).getText(),
								true, null, null, null, true, type.getDefaultType());
						getStatementParams().put(param.getName(),value);
					}
				}
			}else {
				throw new ODataApplicationException(segment.toString(), HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), 
						Locale.ROOT);
			}
		//	processUriInfo(uriInfo, getEntityType(), null);
			PreparedStatement statement = connection.create(new Select().setSelectBody(getSelect()), getStatementParams() );			
			ResultSet rs = statement.executeQuery();			
			statement.closeOnCompletion();
			return new ResultSetIterator(connection, rs, getEntityType(),uriInfo.getExpandOption());

		} catch (Exception e) {
			e.printStackTrace();
			throw internalError(e);
		}
	}

	public void setCursors(Map<String, Cursor> cursors) {
		this.cursors = cursors;

	}

	public EdmEntityType getEntityType() {		
		return entityType;
	}

}
