package com.github.sql.analytic.odata;

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
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;

import com.github.sql.analytic.odata.cmd.CallCursorFunctionCommand;
import com.github.sql.analytic.odata.cmd.ReadEntityCollectionCommand;
import com.github.sql.analytic.odata.ser.SerializeEntityCollectionCommand;
import com.github.sql.analytic.session.SQLSession;
import com.github.sql.analytic.statement.Cursor;



public class SQLEntityCollectionProcessor implements EntityCollectionProcessor {

	private OData odata;
	private ServiceMetadata metadata;
	private SQLSession connection;
	private Map<String, Cursor> cursors;

	public SQLEntityCollectionProcessor(SQLSession connection){
		this.connection = connection;
	}

	@Override
	public void init(OData odata, ServiceMetadata metadata) {
		this.odata = odata;
		this.metadata = metadata;

	}

	@Override
	public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType contentType)
			throws ODataApplicationException, ODataLibraryException {

		final UriResource firstResourceSegment = uriInfo.getUriResourceParts().get(0);
		
		if(firstResourceSegment instanceof UriResourceEntitySet	) {
			ReadEntityCollectionCommand command = new ReadEntityCollectionCommand(uriInfo);
			ResultSetIterator iterator = command.execute(connection);
			SerializeEntityCollectionCommand ser = new SerializeEntityCollectionCommand(request, response, uriInfo, contentType);
			ser.init(odata,metadata);
			ser.setEntityType(command.getEntityType());
			ser.serialize(iterator);
		}else if (firstResourceSegment instanceof UriResourceFunction){
			CallCursorFunctionCommand command = new CallCursorFunctionCommand(uriInfo);	
			command.setCursors(cursors);
			ResultSetIterator iterator = command.execute(connection);
			SerializeEntityCollectionCommand ser = new SerializeEntityCollectionCommand(request, response, uriInfo, contentType);
			ser.init(odata,metadata);
			ser.setEntityType(command.getEntityType());
			ser.serialize(iterator);	
		}else {
			throw new ODataApplicationException("Not implemented", 
					HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), 
					Locale.ROOT);
		}
	}

	public void setCursors(Map<String, Cursor> cursors) {
		this.cursors = cursors;

	}

}
