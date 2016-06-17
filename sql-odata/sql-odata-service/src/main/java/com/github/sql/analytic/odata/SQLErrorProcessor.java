package com.github.sql.analytic.odata;

import javax.servlet.ServletConfig;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.processor.DefaultProcessor;
import org.apache.olingo.server.api.processor.ErrorProcessor;

public class SQLErrorProcessor extends DefaultProcessor implements ErrorProcessor {

	private ServletConfig config;

	public SQLErrorProcessor(ServletConfig config) {
		this.config = config;
	}

	@Override
	public void processError(ODataRequest request, ODataResponse response, ODataServerError serverError,
			ContentType responseFormat) {
		Throwable cause = serverError.getException();
		while(cause.getCause() != null){
			cause = cause.getCause();
		}
		if(config != null){
			config.getServletContext().log(serverError.getMessage(), cause);
		}else {
			cause.printStackTrace();
		}
		super.processError(request, response, serverError, responseFormat);

	}

}
