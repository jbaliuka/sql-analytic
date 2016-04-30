package com.github.sql.analytic.odata.web;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataServiceDocumentRequest;
import org.apache.olingo.client.api.communication.request.retrieve.XMLMetadataRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientServiceDocument;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.api.http.HttpClientFactory;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.junit.Before;
import org.junit.Test;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.odata.testdata.Loader;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.test.TestUtil;

public class SQLODataServletTest {

	static final String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";		
	private static final String serviceRoot = "http://mock.server/SQLODataService.svc/";
	private DataSource datasource = new MockDataSource();	


	@Test
	public void test() throws ServletException, IOException {

		ODataClient client = setupMockClient();
		ODataServiceDocumentRequest serviceReq =
				client.getRetrieveRequestFactory().getServiceDocumentRequest(serviceRoot);
		ODataRetrieveResponse<ClientServiceDocument> serviceRes = serviceReq.execute();
		ClientServiceDocument serviceBody = serviceRes.getBody();		
		Collection<String> names = serviceBody.getEntitySetNames();
		assertFalse(names.isEmpty());	



		XMLMetadataRequest req =
				client.getRetrieveRequestFactory().getXMLMetadataRequest(serviceRoot);
		ODataRetrieveResponse<XMLMetadata> res = req.execute();

		XMLMetadata body = res.getBody();

		Map<String, CsdlSchema> shemas = body.getSchemaByNsOrAlias();
		assertTrue(shemas.size() == 2);
		assertTrue(shemas.containsKey("PUBLIC"));
		assertTrue(shemas.containsKey("INFORMATION_SCHEMA"));

		CsdlSchema publicSchema = shemas.get("PUBLIC");

		CsdlEntityType customers = publicSchema.getEntityType("CUSTOMERS");
		assertTrue(customers.getKey().size() == 1);

		CsdlEntityType joinTable = publicSchema.getEntityType("EMPLOYEE_PRIVILEGES");
		assertTrue(joinTable.getKey().size() == 2);
		assertTrue(joinTable.getNavigationProperties().size() == 2 );


	}



	@Before
	public void init() throws ServletException, IOException, SQLException{

		try(Connection connection = datasource.getConnection() ){				
			Loader.execute(connection);
		}

	}


	private ODataClient setupMockClient() {

		final SQLODataServlet servlet = new SQLODataServlet(){

			private static final long serialVersionUID = 1L;

			@Override
			protected List<CreatePolicy> getPolicy() {
				
				try {
					return Loader.getPolicyList();
				} catch (IOException | JSQLParserException e) {
					throw new AssertionError(e);
				}
			}

			@Override
			protected DataSource getDatasource() {
				
				return datasource;
			}
			
		};		
		
		ODataClient client = ODataClientFactory.getClient();
		client.getConfiguration().setDefaultPubFormat(ContentType.JSON);


		HttpClientFactory httpFactory = new HttpClientFactory() {

			@Override
			public HttpClient create(HttpMethod method, URI uri) {

				return new MockHttpClient(servlet);
			}

			@Override
			public void close(HttpClient httpClient) {			

			}
		};	
		client.getConfiguration().setHttpClientFactory(httpFactory);

		return client;
	}

}

class MockDataSource implements DataSource{

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		// 
		return  new org.h2.Driver().connect(SQLODataServletTest.url, new Properties());
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return null;
	}
	
}

