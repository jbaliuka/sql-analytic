package com.github.sql.analytic.odata.ser;

import java.util.List;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.edm.EdmEntityType;
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
import org.apache.olingo.server.api.uri.queryoption.SelectItem;

import com.github.sql.analytic.odata.ResultSetIterator;

public class SerializeEntityCollectionCommand {

	private ODataRequest request;	
	private ODataResponse response;
	private UriInfo uriInfo;
	private ContentType contentType;	
	private OData odata;
	private ServiceMetadata metadata;
	private EdmEntityType entityType;

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

	public void serialize(ResultSetIterator iterator) throws SerializerException, ODataApplicationException {

		ContextURL contextUrl = ContextURL.with().
				entitySetOrSingletonOrType(entityType.getName()).selectList(getSelectList()).			
				build();

		final String id = request.getRawBaseUri() + "/" + entityType.getName();
		EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with()
				.id(id)
				.select(getUriInfo().getSelectOption())
				.expand(getUriInfo().getExpandOption())
				.contextURL(contextUrl)
				.writeContentErrorCallback(iterator)
				.build();

		if(contentType.isCompatible(ContentType.create("text/csv"))){
			response.setODataContent(new CSVContent(opts,iterator));
			response.addHeader("Content-Disposition", 
	                   "attachment; filename=\"" + entityType.getName() + ".csv\"");
		}else{
			ODataSerializer serializer = odata.createSerializer(contentType);
			SerializerStreamResult serializerResult = serializer.entityCollectionStreamed(metadata, 
					getEntityType(), iterator, opts);		
			response.setODataContent(serializerResult.getODataContent());			
		}
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, contentType.toContentTypeString());
	}

	public EdmEntityType getEntityType() {		
		return entityType;
	}

	protected String getSelectList() throws SerializerException {
		return odata.createUriHelper().buildContextURLSelectList(getEntityType(),
				getUriInfo().getExpandOption(), getUriInfo().getSelectOption());
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

	public void setEntityType(EdmEntityType entityType) {
		this.entityType = entityType;

	}

}
