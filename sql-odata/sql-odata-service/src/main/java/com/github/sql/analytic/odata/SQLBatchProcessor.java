package com.github.sql.analytic.odata;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.batch.BatchFacade;
import org.apache.olingo.server.api.deserializer.batch.BatchOptions;
import org.apache.olingo.server.api.deserializer.batch.BatchRequestPart;
import org.apache.olingo.server.api.deserializer.batch.ODataResponsePart;
import org.apache.olingo.server.api.processor.BatchProcessor;

public class SQLBatchProcessor implements BatchProcessor {

	private OData odata;
	private ServiceMetadata metadata;

	@Override
	public void init(OData odata, ServiceMetadata serviceMetadata) {
		this.odata = odata;
		this.setMetadata(serviceMetadata);
	}
	@Override
	public void processBatch(BatchFacade facade, ODataRequest request, ODataResponse response)
			throws ODataApplicationException, ODataLibraryException {
		String boundary = facade.extractBoundaryFromContentType(request.getHeader(HttpHeader.CONTENT_TYPE));
		BatchOptions options = BatchOptions.with().rawBaseUri(request.getRawBaseUri())
				.rawServiceResolutionUri(request.getRawServiceResolutionUri())
				.build();		
		List<BatchRequestPart> requestParts = odata.createFixedFormatDeserializer()
				.parseBatchRequest(request.getBody(), boundary, options);

		List<ODataResponsePart> responseParts = new ArrayList<ODataResponsePart>();
		for (final BatchRequestPart part : requestParts) {
			responseParts.add(facade.handleBatchRequest(part));
		}
		InputStream responseContent = odata.createFixedFormatSerializer().batchResponse(responseParts, boundary);
		String responseBoundary = "batch_" + UUID.randomUUID().toString();
		response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.MULTIPART_MIXED + ";boundary=" + responseBoundary);
		response.setContent(responseContent);
		response.setStatusCode(HttpStatusCode.ACCEPTED.getStatusCode());
	}

	@Override
	public ODataResponsePart processChangeSet(BatchFacade facade, List<ODataRequest> requests)
			throws ODataApplicationException, ODataLibraryException {
		final List<ODataResponse> responses = new ArrayList<ODataResponse>();
		for(final ODataRequest request : requests) {
			ODataResponse response = facade.handleODataRequest(request);
			final int statusCode = response.getStatusCode();
			if(statusCode < 400) {
				responses.add(response);
				return new ODataResponsePart(response, false);
			}
		}
		return new ODataResponsePart(responses, true);
	}

	public ServiceMetadata getMetadata() {
		return metadata;
	}
	public void setMetadata(ServiceMetadata metadata) {
		this.metadata = metadata;
	}

}
