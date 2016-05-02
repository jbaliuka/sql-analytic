package com.github.sql.analytic.odata;

import java.util.Locale;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
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

import com.github.sql.analytic.session.SQLSession;



public class SQLEntityCollectionProcessor implements EntityCollectionProcessor {

	private OData odata;
	private ServiceMetadata metadata;
	private SQLSession connection;

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
		if(firstResourceSegment instanceof UriResourceEntitySet) {
			try {
				
				ReadEntityCollectionCommand cmd = new ReadEntityCollectionCommand(request, response, uriInfo, contentType);
				cmd.init(odata,metadata);
				cmd.execute(connection);
				
			} catch (EdmPrimitiveTypeException e) {
				throw new ODataApplicationException(e.getMessage(), 
						HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), 
						Locale.ROOT,e);
			}	     
		} else {
			throw new ODataApplicationException("Not implemented", 
					HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), 
					Locale.ROOT);
		}
	}

}
