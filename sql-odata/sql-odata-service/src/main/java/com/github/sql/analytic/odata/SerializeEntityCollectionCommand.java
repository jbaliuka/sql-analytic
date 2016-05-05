package com.github.sql.analytic.odata;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerStreamResult;
import org.apache.olingo.server.api.uri.UriInfo;

public class SerializeEntityCollectionCommand {
	
	private ODataRequest request;	
	private ODataResponse response;
	private UriInfo uriInfo;
	private ContentType contentType;
	private EdmEntitySet edmEntitySet;
	private OData odata;
	private ServiceMetadata metadata;

	public SerializeEntityCollectionCommand(ODataRequest request, ODataResponse response, UriInfo uriInfo,
			ContentType contentType) {
		this.request = request;
		this.response = response;
		this.uriInfo = uriInfo;
		this.contentType = contentType;
	}
	
	
	public void init(OData odata, ServiceMetadata metadata) {
		this.odata = odata;
		this.metadata = metadata;

	}
	
	protected void serialize(ResultSetIterator iterator) throws SerializerException, ODataApplicationException {

		ContextURL contextUrl = ContextURL.with().
				entitySet(getEdmEntitySet()).selectList(getSelectList()).			
				build();

		final String id = request.getRawBaseUri() + "/" + getEdmEntitySet().getName();
		EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with()
				.id(id)
				.select(uriInfo.getSelectOption())
				.expand(getUriInfo().getExpandOption())
				.contextURL(contextUrl)
				.writeContentErrorCallback(iterator)
				.build();

		ODataSerializer serializer = odata.createSerializer(contentType);
		SerializerStreamResult serializerResult = serializer.entityCollectionStreamed(metadata, 
				getEdmEntitySet().getEntityType(), iterator, opts);

		response.setODataContent(serializerResult.getODataContent());
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, contentType.toContentTypeString());
	}

	public EdmEntitySet getEdmEntitySet() {
		return edmEntitySet;
	}

	public void setEdmEntitySet(EdmEntitySet edmEntitySet) {
		this.edmEntitySet = edmEntitySet;
	}

	protected String getSelectList() throws SerializerException {
		return odata.createUriHelper().buildContextURLSelectList(edmEntitySet.getEntityType(),
				uriInfo.getExpandOption(), uriInfo.getSelectOption());
	}
	
	public ODataRequest getRequest() {
		return request;
	}


	public ODataResponse getResponse() {
		return response;
	}


	public UriInfo getUriInfo() {
		return uriInfo;
	}


	public ContentType getContentType() {
		return contentType;
	}


	public OData getOdata() {
		return odata;
	}

	public ServiceMetadata getMetadata() {
		return metadata;
	}

	
}
