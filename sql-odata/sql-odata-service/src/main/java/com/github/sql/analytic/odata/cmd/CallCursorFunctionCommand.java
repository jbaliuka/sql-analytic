package com.github.sql.analytic.odata.cmd;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceFunction;

import com.github.sql.analytic.odata.ResultSetIterator;
import com.github.sql.analytic.session.SQLSession;
import com.github.sql.analytic.statement.Cursor;
import com.github.sql.analytic.statement.SQLStatement;
import com.github.sql.analytic.statement.select.Select;
import com.github.sql.analytic.statement.select.SubSelect;
import com.github.sql.analytic.statement.select.WithItem;

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
			EdmFunction function;
			if (segment instanceof UriResourceFunction){				
				function = ((UriResourceFunction) segment).getFunction();
				entityType = (EdmEntityType) function.getReturnType().getType();

				for( UriParameter param : ((UriResourceFunction) segment).getParameters()){
					if (param.getExpression() != null){
						throw new IllegalArgumentException(param.getExpression().toString());
					}

					EdmPrimitiveType type = (EdmPrimitiveType) function.getParameter(param.getName()).getType();
					Object value = type.valueOfString(param.getText(), true,null, null, null, true, type.getDefaultType());					
					getStatementParams().put(param.getName(),toJdbcValue(value));
				}
			}else {
				throw new ODataApplicationException(segment.toString(), HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), 
						Locale.ROOT);
			}
			processUriInfo(uriInfo, getEntityType(), function.getName());
			PreparedStatement statement = connection.create( buildSelect(function), getStatementParams() );			
			ResultSet rs = statement.executeQuery();			
			statement.closeOnCompletion();
			return new ResultSetIterator(connection, rs, getEntityType(),uriInfo.getExpandOption());

		} catch (Exception e) {
			throw internalError(e);
		}
	}

	private SQLStatement buildSelect(EdmFunction function) {
		Cursor cursor = cursors.get(function.getName());
		Select newSelect = new Select();
		newSelect.setWithItemsList(new ArrayList<WithItem>());
			
		if(cursor.getSelect().getWithItemsList() != null){
			for( WithItem item : cursor.getSelect().getWithItemsList()){
				newSelect.getWithItemsList().add(item);
			}
		}		
		newSelect.setSelectBody(getSelect());
		SubSelect subSelect = new SubSelect();
		subSelect.setSelectBody(cursor.getSelect().getSelectBody());
		subSelect.setAlias(function.getName());
		getSelect().setFromItem(subSelect);
		
		return newSelect;
	}

	protected Object toJdbcValue(Object value) {
		if(value instanceof Calendar){
			return new java.sql.Timestamp(((Calendar) value).getTimeInMillis());
		}

		return value;
	}

	public void setCursors(Map<String, Cursor> cursors) {
		this.cursors = cursors;

	}

	public EdmEntityType getEntityType() {		
		return entityType;
	}

}
