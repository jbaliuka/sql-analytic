package com.github.sql.analytic.odata.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public final class MockHttpClient implements HttpClient {
	
	
	
	private SQLODataServlet servlet;

	public MockHttpClient(SQLODataServlet servlet) {
		this.servlet = servlet;	
	}

	@Override
	public HttpParams getParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClientConnectionManager getConnectionManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpResponse execute(HttpUriRequest clientRequest) throws IOException, ClientProtocolException {
		
		ProtocolVersion version = new ProtocolVersion("http", 1, 0);
		StatusLine status = new BasicStatusLine(version , 200, "OK");
		HttpResponse clientResponse = new BasicHttpResponse(status);
		try {
			
			HttpServletRequest request = new MockHttpServletRequest(clientRequest);
			
			MockHtppServletResponse response = new MockHtppServletResponse(clientResponse);
			
			servlet.service(request, response);
			
			clientResponse.setEntity(response.getEntity());
			
		} catch (ServletException e) {
			throw new ClientProtocolException(e);
		}
		
		return clientResponse;
	}

	@Override
	public HttpResponse execute(HttpUriRequest request, HttpContext context)
			throws IOException, ClientProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context)
			throws IOException, ClientProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler)
			throws IOException, ClientProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context)
			throws IOException, ClientProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler)
			throws IOException, ClientProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler,
			HttpContext context) throws IOException, ClientProtocolException {
		// TODO Auto-generated method stub
		return null;
	}
}