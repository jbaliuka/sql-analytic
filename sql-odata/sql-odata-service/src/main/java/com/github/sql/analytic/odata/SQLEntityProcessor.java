package com.github.sql.analytic.odata;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;

public class SQLEntityProcessor implements EntityProcessor {

	private Connection session;
	private ServiceMetadata serviceMetadata;
	private OData odata;

	public SQLEntityProcessor(Connection session) {
		this.session = session;
	}

	@Override
	public void init(OData odata, ServiceMetadata serviceMetadata) {
		this.serviceMetadata = serviceMetadata;
		this.odata = odata;

	}

	@Override
	public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
			throws ODataApplicationException, ODataLibraryException {
		
	    List<UriResource> resourcePaths = uriInfo.getUriResourceParts();	    
	    UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
	    EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
	    EdmEntityType entityType = edmEntitySet.getEntityType();	    
	    List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
	    Entity entity = null;
	    
	    StringBuilder sql = EntityData.buildSelect(entityType);
	    
	    EntityData.buildWhere(keyPredicates, sql);
	    
	    try(PreparedStatement ps = session.prepareStatement(sql.toString())){
	    	 for(int i = 0; i < keyPredicates.size(); i++){
	 	    	UriParameter key = keyPredicates.get(i);
	 	    	EdmProperty keyProperty = (EdmProperty)entityType.getProperty(key.getName());	
	 	    	EdmPrimitiveType type = (EdmPrimitiveType) keyProperty.getType();
	 	    	Object value = type.valueOfString(key.getText(), 
	 	    			keyProperty.isNullable(),
	 	    			keyProperty.getMaxLength(),
	 	    			keyProperty.getPrecision(), 
	 	    			keyProperty.getScale(), true, type.getDefaultType());
	 	    	ps.setObject(i + 1, value);
	 	    }
	    	 try(ResultSet rs = ps.executeQuery()){
	    		 if(rs.next()){
	    			 entity = EntityData.createEntity(edmEntitySet,entityType,rs); 
	    		 }
	    	 } 
	    	
	    } catch (SQLException | EdmPrimitiveTypeException | IOException e) {
	    	throw new ODataApplicationException("Internal Error", HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), 
					Locale.ENGLISH,e);
		}
	   

	    ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();	    
	    EntitySerializerOptions options = EntitySerializerOptions.with().contextURL(contextUrl).build();

	    ODataSerializer serializer = odata.createSerializer(responseFormat);
	    SerializerResult serializerResult = serializer.entity(serviceMetadata, entityType, entity, options);
	    InputStream entityStream = serializerResult.getContent();

	    
	    response.setContent(entityStream);
	    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
	    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());

	}

	

	@Override
	public void createEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo)
			throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub

	}

}
