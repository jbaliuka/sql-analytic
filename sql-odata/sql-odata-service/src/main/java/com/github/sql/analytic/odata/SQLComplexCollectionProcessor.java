package com.github.sql.analytic.odata;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.ComplexCollectionProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceFunction;

import com.github.sql.analytic.session.SQLSession;
import com.github.sql.analytic.statement.Cursor;

public class SQLComplexCollectionProcessor implements ComplexCollectionProcessor {

	private OData odata;
	private ServiceMetadata serviceMetadata;
	private Map<String, Cursor> cursors ;
	public Map<String, Cursor> getCursors() {
		return cursors;
	}

	public void setCursors(Map<String, Cursor> cursors) {
		this.cursors = cursors;
	}

	private SQLSession session;

	public SQLComplexCollectionProcessor(SQLSession session){		
		this.session = session;
	}

	@Override
	public void init(OData odata, ServiceMetadata serviceMetadata) {
		this.odata = odata;
		this.serviceMetadata= serviceMetadata;
	}

	@Override
	public void readComplexCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {

		SerializeComplexCollectionCommand command = new SerializeComplexCollectionCommand(request, response, uriInfo, responseFormat);
		command.init(odata, serviceMetadata);		
		UriResourceFunction part = (UriResourceFunction) uriInfo.getUriResourceParts().get(0);
		Cursor cursor = cursors.get(part.getFunction().getName());
		Map<String, Object> statementParams = new HashMap<>();
		for( UriParameter param : part.getParameters() ){
			statementParams.put(param.getName(),param.getExpression().toString());
		}
		try {
			try(PreparedStatement statement = session.create(cursor.getSelect(), statementParams )){
				try(ResultSet rs = statement.executeQuery()){			
					command.serialize(rs);
				}
			}			
		} catch (SQLException e) {
			throw new ODataApplicationException(e.getMessage(), 500, Locale.ROOT, e);
		}

	}

	@Override
	public void updateComplexCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo,
			ContentType requestFormat, ContentType responseFormat)
					throws ODataApplicationException, ODataLibraryException {
		throw new ODataApplicationException("Not implemented", 
				HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), 
				Locale.ROOT);
	}

	@Override
	public void deleteComplexCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo)
			throws ODataApplicationException, ODataLibraryException {
		throw new ODataApplicationException("Not implemented", 
				HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), 
				Locale.ROOT);
	}

}
