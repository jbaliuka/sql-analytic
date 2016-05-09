package com.github.sql.analytic.odata;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
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
import org.apache.olingo.server.api.processor.PrimitiveProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceFunction;

public class SQLPrimitiveProcessor implements PrimitiveProcessor {

	private Object odata;
	private Object serviceMetadata;
	private Map<String,FunctionCommand> javaFunctions;

	public SQLPrimitiveProcessor(Map<String, FunctionCommand> functions){
		this.javaFunctions = functions;	
	}

	@Override
	public void init(OData odata, ServiceMetadata serviceMetadata) {
		this.odata = odata;
		this.serviceMetadata = serviceMetadata;

	}

	@Override
	public void readPrimitive(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
			throws ODataApplicationException, ODataLibraryException {

		UriResource res = uriInfo.getUriResourceParts().get(0);
		if(res instanceof UriResourceFunction){
			EdmFunction function = ((UriResourceFunction) res).getFunction();
			FunctionCommand command = javaFunctions.get(function.getName());
			List<UriParameter> predicates = ((UriResourceFunction) res).getParameters();			
			if(command == null){
				notImplemented();
			}		
			Map<String,Object> cmdParams = new HashMap<>();
			for(UriParameter predicate : predicates){
				EdmParameter param = function.getParameter(predicate.getName());
				EdmPrimitiveType type = (EdmPrimitiveType)param.getType();
				try {
					Object value = type.valueOfString(predicate.getText(), true, null, null, null, true, type.getDefaultType());
					cmdParams.put(predicate.getName(), value);
				} catch (EdmPrimitiveTypeException e) {
					throw new ODataRuntimeException(e); 
				}
			}

			Object result = command.execute(cmdParams);

			if(result instanceof InputStream){
				response.setContent((InputStream) result);	
			}else {
				notImplemented();		
			}
			response.setStatusCode(HttpStatusCode.OK.getStatusCode());
			response.setHeader(HttpHeader.CONTENT_TYPE, command.getContentType());


		}


	}

	private void notImplemented() throws ODataApplicationException {
		throw new ODataApplicationException("Not implemented", 
				HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), 
				Locale.ROOT);
	}

	@Override
	public void updatePrimitive(ODataRequest request, ODataResponse response, UriInfo uriInfo,
			ContentType requestFormat, ContentType responseFormat)
					throws ODataApplicationException, ODataLibraryException {
		notImplemented();

	}

	@Override
	public void deletePrimitive(ODataRequest request, ODataResponse response, UriInfo uriInfo)
			throws ODataApplicationException, ODataLibraryException {
		notImplemented();

	}

}
