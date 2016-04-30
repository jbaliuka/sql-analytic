package com.github.sql.analytic.odata;

import java.sql.Connection;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.uri.UriInfo;

public class SQLEntityCollectionProcessor implements EntityCollectionProcessor {

	private OData odata;
	private ServiceMetadata metadata;
	private Connection connection;
	
	public SQLEntityCollectionProcessor(Connection connection){
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
	
		
	}

}
