package com.github.sql.analytic.odata;

import java.io.InputStream;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;

public class ReadEntityCommand extends ReadEntityCollectionCommand {

	public ReadEntityCommand(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType contentType) {
		super(request, response, uriInfo, contentType);		
	}

	@Override
	protected void serialize(EntityCollection collection, String selectList) throws SerializerException {

		
		if(collection.getEntities().size() == 0){
			new ODataApplicationException("Not Found",
					HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
		}
		EdmEntityType entityType = getEdmEntitySet().getEntityType();

		ContextURL contextUrl = ContextURL.with().entitySet(getEdmEntitySet()).selectList(selectList).build();
		
		EntitySerializerOptions options = EntitySerializerOptions.with().
				contextURL(contextUrl).select(getUriInfo().getSelectOption()).
				build();

		ODataSerializer serializer = getOdata().createSerializer(getContentType());
		
		SerializerResult serializerResult = serializer.entity(getMetadata(), entityType, 
				collection.getEntities().get(0) , options);
		InputStream entityStream = serializerResult.getContent();

		
		getResponse().setContent(entityStream);
		getResponse().setStatusCode(HttpStatusCode.OK.getStatusCode());
		getResponse().setHeader(HttpHeader.CONTENT_TYPE, getContentType().toContentTypeString());

	}

}
