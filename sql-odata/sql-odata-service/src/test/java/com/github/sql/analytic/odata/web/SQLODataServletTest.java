package com.github.sql.analytic.odata.web;

import static org.mockito.Mockito.stub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataServiceDocumentRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientServiceDocument;
import org.apache.olingo.client.api.http.HttpClientFactory;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SQLODataServletTest {

	private static final String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

	private SQLODataServlet servlet;
	@Mock
	private ServletConfig config;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private ServletOutputStream servletOutputStream;
	private Connection connection;
	@Mock
	private DataSource datasource;
	private ODataClient client;
	
	private HttpClientFactory httpFactory = new HttpClientFactory() {
		
		@Override
		public HttpClient create(HttpMethod method, URI uri) {
			
			return httpClient;
		}
		
		@Override
		public void close(HttpClient httpClient) {			
			
		}
	};	

	private HttpClient httpClient; 


	@Before
	public void init() throws ServletException, IOException, SQLException{
		MockitoAnnotations.initMocks(this);	
		connection = new org.h2.Driver().connect(url, new Properties());
		try(InputStream in = getClass().getClassLoader().getResourceAsStream("mywind/northwind.sql")){
			BufferedReader reader = new BufferedReader( new InputStreamReader(in));
			connection.createStatement().execute(reader.readLine());			
		}
		try(InputStream in = getClass().getClassLoader().getResourceAsStream("mywind/northwind-data.sql")){
			BufferedReader reader = new BufferedReader( new InputStreamReader(in));
			connection.createStatement().execute(reader.readLine());			
		}
		stub(datasource.getConnection()).toReturn(connection);
		servlet = new SQLODataServlet();
		servlet.init(config);
		servlet.setDatasource(datasource);
		stub(response.getOutputStream()).toReturn(servletOutputStream);

		client =  ODataClientFactory.getClient();		
		client.getConfiguration().setHttpClientFactory(httpFactory);
		client.getConfiguration().setDefaultPubFormat(ContentType.JSON);
		httpClient = new MockHttpClient(servlet);

	}

	@Test
	public void test() throws ServletException, IOException {

		String serviceRoot = "http://localhost/SQLODataService.svc/";
		ODataServiceDocumentRequest req =
		client.getRetrieveRequestFactory().getServiceDocumentRequest(serviceRoot);
		ODataRetrieveResponse<ClientServiceDocument> res = req.execute();

		ClientServiceDocument body = res.getBody();
		
		Collection<String> names = body.getEntitySetNames();
		Assert.assertFalse(names.isEmpty());
		
		
	}


	@After
	public void tearDown() throws Exception {
		connection.close();
	}


}
