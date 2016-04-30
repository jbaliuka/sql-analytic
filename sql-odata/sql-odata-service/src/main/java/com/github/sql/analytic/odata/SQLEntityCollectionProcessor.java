package com.github.sql.analytic.odata;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;

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

		final UriResource firstResourceSegment = uriInfo.getUriResourceParts().get(0);	    
		if(firstResourceSegment instanceof UriResourceEntitySet) {
			readEntityCollectionInternal(request, response, uriInfo, contentType);	     
		} else {
			throw new ODataApplicationException("Not implemented", 
					HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), 
					Locale.ENGLISH);
		}

	}

	private void readEntityCollectionInternal(ODataRequest request, ODataResponse response, UriInfo uriInfo,
			ContentType contentType) throws SerializerException, ODataApplicationException {

		List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0); // in our example, the first segment is the EntitySet
		EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
		EdmEntityType edmEntityType = edmEntitySet.getEntityType();		  
		EntityCollection collection = new EntityCollection();	  

		StringBuilder sql = new StringBuilder("SELECT ");
		for(int i = 0; i < edmEntityType.getPropertyNames().size(); i++ ){
			sql.append(edmEntityType.getPropertyNames().get(i));
			if( i < edmEntityType.getPropertyNames().size() - 1){
				sql.append(",");
			}
		}
		sql.append(" FROM ").append(edmEntityType.getFullQualifiedName());

		try(PreparedStatement statement = connection.prepareStatement(sql.toString()) ){
			try(ResultSet rs = statement.executeQuery()){		  
				while(rs.next()){
					Entity entity = createEntity(edmEntityType,rs);
					entity.setId(createId(edmEntitySet.getName(),rs.getObject(edmEntityType.getKeyPropertyRefs().get(0).getName())));
					collection.getEntities().add(entity);
				}
			}	   
		} catch (SQLException e) {
			throw new ODataApplicationException("Internal Error", HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), 
					Locale.ENGLISH,e);
		}


		ODataSerializer serializer = odata.createSerializer(contentType);

		ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();

		final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
		EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with().id(id).contextURL(contextUrl).build();
		SerializerResult serializerResult = serializer.entityCollection(metadata, edmEntityType, collection, opts);
		InputStream serializedContent = serializerResult.getContent();


		response.setContent(serializedContent);
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, contentType.toContentTypeString());

	}

	private Entity createEntity(EdmEntityType edmEntityType, ResultSet rs) throws SQLException {
		
		Entity entity = new Entity();
		entity.setType(edmEntityType.getFullQualifiedName().toString());
		for( String name : edmEntityType.getPropertyNames()){
			EdmElement prop = edmEntityType.getProperty(name);
			entity.addProperty( new Property(null,prop.getName(),ValueType.PRIMITIVE,rs.getObject(prop.getName())));
		}
		for( EdmKeyPropertyRef ref : edmEntityType.getKeyPropertyRefs()){
				entity.addProperty( new Property(null,ref.getName(),ValueType.PRIMITIVE,rs.getObject(ref.getName())));		  
		}		
		
		return entity;
	}
	
	private URI createId(String entitySetName, Object id) {
	    try {
	        return new URI(entitySetName + "(" + String.valueOf(id) + ")");
	    } catch (URISyntaxException e) {
	        throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
	    }
	}

}
