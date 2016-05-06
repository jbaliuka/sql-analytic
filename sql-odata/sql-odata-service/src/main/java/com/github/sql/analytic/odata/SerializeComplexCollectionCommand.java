package com.github.sql.analytic.odata;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ComplexSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResourceFunction;

public class SerializeComplexCollectionCommand  {

	private ODataRequest request;
	private ODataResponse response;
	private UriInfo uriInfo;
	private ContentType contentType;
	private OData odata;
	private ServiceMetadata metadata;
	private EdmComplexType type;


	public SerializeComplexCollectionCommand(ODataRequest request, ODataResponse response, UriInfo uriInfo,
			ContentType contentType) {
		this.setRequest(request);
		this.setResponse(response);
		this.setUriInfo(uriInfo);
		this.setContentType(contentType);

	}

	public void init(OData odata, ServiceMetadata serviceMetadata) {
		this.setOdata(odata);
		this.setMetadata(serviceMetadata);
		type = metadata.getEdm().getComplexType( new FullQualifiedName("PUBLIC.KeyValue"));
	}


	protected void serialize(ResultSet rs) throws SerializerException, ODataApplicationException, SQLException {	

		UriResourceFunction part = (UriResourceFunction) uriInfo.getUriResourceParts().get(0);

		ContextURL contextUrl = ContextURL.with().
				entitySetOrSingletonOrType(part.getSegmentValue()).
				build();

		ComplexSerializerOptions options = ComplexSerializerOptions.with().
				contextURL(contextUrl).select(getUriInfo().getSelectOption()).
				expand(getUriInfo().getExpandOption()).
				build();

		ODataSerializer serializer = getOdata().createSerializer(getContentType());
		List<ComplexValue> values = new ArrayList<>();
		ResultSetMetaData md = rs.getMetaData();
		while(rs.next()){
			ComplexValue value = new ComplexValue();
			for(int i = 0; i < md.getColumnCount(); i ++ ){				
				Property prop = new Property();
				prop.setName(md.getColumnName(i + 1));
				prop.setValue(ValueType.PRIMITIVE, rs.getObject(i + 1));			
				value.getValue().add(prop);

			}
			values.add(value);
		}


		Property property = new Property(null,"result",ValueType.COLLECTION_COMPLEX,values);


		SerializerResult serializerResult = serializer.complexCollection(metadata, type, property, options);


		getResponse().setContent(serializerResult.getContent());
		getResponse().setStatusCode(HttpStatusCode.OK.getStatusCode());
		getResponse().setHeader(HttpHeader.CONTENT_TYPE, getContentType().toContentTypeString());
	}
	public ODataRequest getRequest() {
		return request;
	}
	public void setRequest(ODataRequest request) {
		this.request = request;
	}
	public ODataResponse getResponse() {
		return response;
	}
	public void setResponse(ODataResponse response) {
		this.response = response;
	}
	public UriInfo getUriInfo() {
		return uriInfo;
	}
	public void setUriInfo(UriInfo uriInfo) {
		this.uriInfo = uriInfo;
	}
	public ContentType getContentType() {
		return contentType;
	}
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public ServiceMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(ServiceMetadata metadata) {
		this.metadata = metadata;
	}

	public OData getOdata() {
		return odata;
	}

	public void setOdata(OData odata) {
		this.odata = odata;
	}

}
