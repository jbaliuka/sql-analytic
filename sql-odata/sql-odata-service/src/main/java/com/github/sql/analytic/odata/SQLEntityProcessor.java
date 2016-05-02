package com.github.sql.analytic.odata;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
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
		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(resourcePaths.size() - 1);
		EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
		EdmEntityType entityType = edmEntitySet.getEntityType();	    
		List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
		Entity entity = null;

		StringBuilder sql = EntityData.buildSelect(entityType);

		EntityData.buildWhere(keyPredicates, sql);

		try(PreparedStatement ps = session.prepareStatement(sql.toString())){
			setKeyParams(entityType, keyPredicates, ps,0);
			try(ResultSet rs = ps.executeQuery()){
				if(rs.next()){
					entity = EntityData.createEntity(edmEntitySet,null,rs); 
				}else {
					throw new ODataApplicationException("Not Found", HttpStatusCode.NOT_FOUND.getStatusCode(), 
							Locale.ENGLISH);
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

	private void setKeyParams(EdmEntityType entityType, List<UriParameter> keyPredicates, PreparedStatement ps, int first)
			throws EdmPrimitiveTypeException, SQLException {
		for(int i = 0; i < keyPredicates.size(); i++){
			UriParameter key = keyPredicates.get(i);
			EdmProperty keyProperty = (EdmProperty)entityType.getProperty(key.getName());	
			EdmPrimitiveType type = (EdmPrimitiveType) keyProperty.getType();
			Object value = type.valueOfString(key.getText(), 
					keyProperty.isNullable(),
					keyProperty.getMaxLength(),
					keyProperty.getPrecision(), 
					keyProperty.getScale(), true, type.getDefaultType());
			ps.setObject(first + i + 1, value);
		}
	}



	@Override
	public void createEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {

		List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(resourcePaths.size() - 1);
		EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
		EdmEntityType entityType = edmEntitySet.getEntityType();	
		InputStream requestInputStream = request.getBody();
		ODataDeserializer deserializer = this.odata.createDeserializer(requestFormat);
		DeserializerResult result = deserializer.entity(requestInputStream, entityType);
		Entity requestEntity = result.getEntity();		  
		Entity createdEntity = createEntityData(edmEntitySet, requestEntity);		  
		ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();		  
		EntitySerializerOptions options = EntitySerializerOptions.with().contextURL(contextUrl).build();
		ODataSerializer serializer = this.odata.createSerializer(responseFormat);
		SerializerResult serializedResponse = serializer.entity(serviceMetadata, entityType, createdEntity, options);
		response.setContent(serializedResponse.getContent());
		response.setStatusCode(HttpStatusCode.CREATED.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());

	}

	private Entity createEntityData(EdmEntitySet edmEntitySet, Entity requestEntity) throws ODataApplicationException {

		EdmEntityType entityType = edmEntitySet.getEntityType();
		List<String> propertyNames = entityType.getPropertyNames();
		StringBuilder builder = new StringBuilder("INSERT INTO ");		
		builder.append(entityType.getFullQualifiedName());
		builder.append("(");
		for(int i = 0; i < propertyNames.size(); i++){
			builder.append(propertyNames.get(i));
			if( i < propertyNames.size() - 1){
				builder.append(",");
			}
		}

		builder.append(")VALUES(");
		for(int i = 0; i < propertyNames.size(); i++){
			builder.append("?");
			if( i < propertyNames.size() - 1){
				builder.append(",");
			}
		}
		builder.append(")");
		try(PreparedStatement ps = session.prepareStatement(builder.toString())){

			for(int i = 0; i < propertyNames.size(); i++){				
				Property p = requestEntity.getProperty(propertyNames.get(i));	
				if(p != null){
					ps.setObject(i + 1, p.getValue());
				}else {
					ps.setNull(i + 1, Types.CHAR);
				}
			}
			if(ps.executeUpdate() != 1){
				throw new ODataApplicationException("Unable to insert", HttpStatusCode.CONFLICT.getStatusCode(), 
						Locale.ENGLISH);
			}

		} catch (SQLException e) {
			throw new ODataApplicationException("Internal Error", HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), 
					Locale.ENGLISH,e);
		}

		return requestEntity;
	}

	@Override
	public void updateEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {

		List<UriResource> resourcePaths = uriInfo.getUriResourceParts();		 
		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(resourcePaths.size() - 1);
		EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
		EdmEntityType edmEntityType = edmEntitySet.getEntityType();

		InputStream requestInputStream = request.getBody();
		ODataDeserializer deserializer = this.odata.createDeserializer(requestFormat);
		DeserializerResult result = deserializer.entity(requestInputStream, edmEntityType);
		Entity requestEntity = result.getEntity();		
		List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();		  
		HttpMethod httpMethod = request.getMethod();
		List<String> propertyNames = edmEntityType.getPropertyNames();
		List<String> pachedProperties = new ArrayList<>();

		for( int i = 0; i < propertyNames.size(); i++ ){
			Property p = requestEntity.getProperty(propertyNames.get(i));
			if(httpMethod == HttpMethod.PATCH && p != null){
				pachedProperties.add(propertyNames.get(i));
			}else if( httpMethod == HttpMethod.PUT ){
				pachedProperties.add(propertyNames.get(i));
			}
		}

		StringBuilder builder = new StringBuilder("UPDATE ").
				append(edmEntityType.getFullQualifiedName()).
				append(" SET ");

		for( int i = 0; i < pachedProperties.size(); i++ ){
			builder.append(pachedProperties.get(i));
			builder.append("=?");
			if( i < pachedProperties.size() - 1){
				builder.append(",");
			}
		}
		EntityData.buildWhere(keyPredicates, builder);


		try(PreparedStatement ps = session.prepareStatement(builder.toString())){

			for(int i = 0; i < pachedProperties.size(); i++){				
				Property p = requestEntity.getProperty(pachedProperties.get(i));	
				if(p != null){
					ps.setObject(i + 1, p.getValue());
				}else {
					ps.setNull(i + 1, Types.CHAR);
				}
			}
			setKeyParams(edmEntityType, keyPredicates, ps,pachedProperties.size());
			if(ps.executeUpdate() != 1){
				throw new ODataApplicationException("Unable to update", HttpStatusCode.CONFLICT.getStatusCode(), 
						Locale.ENGLISH);
			}

		} catch (SQLException | EdmPrimitiveTypeException e) {
			throw new ODataApplicationException("Internal Error", HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), 
					Locale.ENGLISH,e);
		}


		response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());

	}

	@Override
	public void deleteEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo)
			throws ODataApplicationException, ODataLibraryException {

		List<UriResource> resourcePaths = uriInfo.getUriResourceParts();		
		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
		EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();		
		List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();           
		StringBuilder builder = new StringBuilder("DELETE FROM ").
				append(edmEntitySet.getEntityType().getFullQualifiedName());
		EntityData.buildWhere(keyPredicates, builder);			

		try(PreparedStatement ps = session.prepareStatement(builder.toString())){
			setKeyParams(edmEntitySet.getEntityType(), keyPredicates, ps,0);
			if(ps.executeUpdate() != 1){
				throw new ODataApplicationException("Unable to delete", HttpStatusCode.CONFLICT.getStatusCode(), 
						Locale.ENGLISH);
			}
		}catch(SQLIntegrityConstraintViolationException e){
			throw new ODataApplicationException("Unable to delete", HttpStatusCode.CONFLICT.getStatusCode(), 
					Locale.ENGLISH);
		} catch (SQLException | EdmPrimitiveTypeException e) {
			throw new ODataApplicationException("Internal Error", HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), 
					Locale.ENGLISH,e);
		}		
		response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
	}

}
