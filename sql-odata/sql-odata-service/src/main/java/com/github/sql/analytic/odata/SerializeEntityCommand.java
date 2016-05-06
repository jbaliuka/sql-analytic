package com.github.sql.analytic.odata;

import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
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

public class SerializeEntityCommand extends SerializeEntityCollectionCommand {

	public SerializeEntityCommand(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType contentType) {
		super(request, response, uriInfo, contentType);		
	}

	@Override
	protected void serialize(ResultSetIterator iterator) throws SerializerException, ODataApplicationException {

		try{
			if(!iterator.hasNext()){
				throw new ODataApplicationException("Not Found",
						HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
			}

			ContextURL contextUrl = ContextURL.with().
					entitySetOrSingletonOrType(getEntityType().getName()).selectList(getSelectList()).
					build();

			EntitySerializerOptions options = EntitySerializerOptions.with().
					contextURL(contextUrl).select(getUriInfo().getSelectOption()).
					expand(getUriInfo().getExpandOption()).
					build();

			ODataSerializer serializer = getOdata().createSerializer(getContentType());

			SerializerResult serializerResult = serializer.entity(getMetadata(), getEntityType(), 
					iterator.next() , options);


			getResponse().setContent(serializerResult.getContent());
			getResponse().setStatusCode(HttpStatusCode.OK.getStatusCode());
			getResponse().setHeader(HttpHeader.CONTENT_TYPE, getContentType().toContentTypeString());
			
		}finally{
			iterator.close();
		}

	}

}
