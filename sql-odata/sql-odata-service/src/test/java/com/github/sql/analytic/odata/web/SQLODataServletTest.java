package com.github.sql.analytic.odata.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.batch.BatchManager;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataChangeset;
import org.apache.olingo.client.api.communication.request.cud.CUDRequestFactory;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataServiceDocumentRequest;
import org.apache.olingo.client.api.communication.request.retrieve.XMLMetadataRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientServiceDocument;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.api.http.HttpClientFactory;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.domain.ClientEntityImpl;
import org.apache.olingo.client.core.domain.ClientPrimitiveValueImpl;
import org.apache.olingo.client.core.domain.ClientPropertyImpl;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.odata.testdata.Loader;
import com.github.sql.analytic.statement.Cursor;
import com.github.sql.analytic.statement.policy.CreatePolicy;

public class SQLODataServletTest {

	static final String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";		
	private static final String serviceRoot = "http://mock.server/SQLODataService.svc/";
	private DataSource datasource = new MockDataSource();	
	private ODataClient client;


	@Test
	public void testMetadataReq() {
		XMLMetadataRequest req =
				client.getRetrieveRequestFactory().getXMLMetadataRequest(serviceRoot);
		ODataRetrieveResponse<XMLMetadata> res = req.execute();

		XMLMetadata body = res.getBody();

		Map<String, CsdlSchema> shemas = body.getSchemaByNsOrAlias();
		assertTrue(shemas.size() == 1);
		assertTrue(shemas.containsKey("PUBLIC"));
		
		CsdlSchema publicSchema = shemas.get("PUBLIC");

		CsdlEntityType customers = publicSchema.getEntityType("CUSTOMERS");
		assertTrue(customers.getKey().size() == 1);

		CsdlEntityType joinTable = publicSchema.getEntityType("EMPLOYEE_PRIVILEGES");
		assertTrue(joinTable.getKey().size() == 2);
		assertTrue(joinTable.getNavigationProperties().size() == 2 );
	}

	@Test
	public void testEntityReq() throws URISyntaxException {
		URI uri = new URI(serviceRoot + "CUSTOMERS(3)");
		ODataEntityRequest<ClientEntity> req =
				client.getRetrieveRequestFactory().getEntityRequest(uri);
		ODataRetrieveResponse<ClientEntity> res = req.execute();
		ClientEntity entity = res.getBody();		
		ClientProperty id = entity.getProperty("ID");
		assertEquals(3,id.getValue().asPrimitive().toValue());

	}
	
	@Test
	public void testExpandEntityReq() throws URISyntaxException {
		URI uri = new URI(serviceRoot + "CUSTOMERS(3)?$expand=FK_ORDERS_CUSTOMERS");
		ODataEntityRequest<ClientEntity> req =
				client.getRetrieveRequestFactory().getEntityRequest(uri);
		ODataRetrieveResponse<ClientEntity> res = req.execute();
		ClientEntity entity = res.getBody();		
		ClientProperty expanded = entity.getProperty("FK_ORDERS_CUSTOMERS");
		assertTrue(expanded.getCollectionValue().size() > 1);

	}
	
	@Test
	public void testNavEntityReq() throws URISyntaxException {
		
		URI uri = new URI(serviceRoot + "CUSTOMERS(3)/FK_ORDERS_CUSTOMERS?$format=xml");		
		ODataEntitySetRequest<ClientEntitySet> req =
				client.getRetrieveRequestFactory().getEntitySetRequest(uri);
		ODataRetrieveResponse<ClientEntitySet> res = req.execute();
		ClientEntitySet entity = res.getBody();		
		assertFalse( entity.getEntities().isEmpty() );

	}
	
	@Test
	public void testBatch() throws URISyntaxException, IOException, InterruptedException, ExecutionException, TimeoutException{
		
		ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(serviceRoot);
		BatchManager manager = request.payloadManager();
		ODataChangeset changeset = manager.addChangeset();		
		ODataBatchableRequest delete  = client.getCUDRequestFactory().getDeleteRequest(new URI(serviceRoot + "ORDER_DETAILS(30)"));
		changeset.addRequest(delete);	
		delete  =  client.getCUDRequestFactory().getDeleteRequest(new URI(serviceRoot + "ORDER_DETAILS(31)"));
		changeset.addRequest(delete);	
				
		try(InputStream resp = manager.getResponse().getRawResponse()){
			//TODO: mock up  PipedOutputStream (it is random)  
		   assertTrue(true);	   
		}
		
	}
	
	@Test
	public void testCreateEntityReq() throws URISyntaxException {
		URI uri = new URI(serviceRoot + "CUSTOMERS");
		CUDRequestFactory req = client.getCUDRequestFactory();				
		FullQualifiedName name = new FullQualifiedName("PUBLIC","CUSTOMERS");
		ClientEntityImpl entity = new ClientEntityImpl(name);
		ClientPrimitiveValue value = new ClientPrimitiveValueImpl.BuilderImpl().buildInt32(666);
		entity.getProperties().add( new ClientPropertyImpl("ID", value ) );
		
		ODataEntityCreateResponse<ClientEntityImpl> res = req.getEntityCreateRequest(uri, entity).execute();
		ClientEntity newEntity = res.getBody();		
		ClientProperty id = newEntity.getProperty("ID");
		assertEquals(666,id.getValue().asPrimitive().toValue());

	}
	
	@Test
	public void testPatchEntityReq() throws URISyntaxException {
		
		URI uri = new URI(serviceRoot + "CUSTOMERS(1)");
		CUDRequestFactory req = client.getCUDRequestFactory();				
		FullQualifiedName name = new FullQualifiedName("PUBLIC","CUSTOMERS");
		ClientEntityImpl entity = new ClientEntityImpl(name);
		ClientPrimitiveValue value = new ClientPrimitiveValueImpl.BuilderImpl().buildInt32(1);
		entity.getProperties().add( new ClientPropertyImpl("ID", value ) );
		 value = new ClientPrimitiveValueImpl.BuilderImpl().buildString("TEST");
		entity.getProperties().add( new ClientPropertyImpl("COMPANY", value ) );		
		
		ODataEntityUpdateRequest<ClientEntityImpl> res = req.getEntityUpdateRequest(uri,UpdateType.PATCH, entity);
		res.execute();
		
		res = req.getEntityUpdateRequest(uri,UpdateType.PATCH, entity);
		res.execute();
	}
	
	@Test
	public void testDeleteEntityReq() throws URISyntaxException {
		URI uri = new URI(serviceRoot + "STRINGS(2)");
		CUDRequestFactory req = client.getCUDRequestFactory();
		ODataDeleteRequest res = req.getDeleteRequest(uri);
		res.execute();
	}

	@Test
	public void testEntitySetReq() throws URISyntaxException {
		
		URI uri = new URI(serviceRoot + "CUSTOMERS");
		ODataEntitySetRequest<ClientEntitySet> req =
				client.getRetrieveRequestFactory().getEntitySetRequest(uri);
		ODataRetrieveResponse<ClientEntitySet> res = req.execute();
		ClientEntitySet entity = res.getBody();		
		assertFalse( entity.getEntities().isEmpty() );

	}
	
	@Test
	public void testFunctionReq() throws URISyntaxException {
		
		URI uri = new URI(serviceRoot + "myCustomers(date=2010-02-26)");
		
		ODataEntitySetRequest<ClientEntitySet> req =
				client.getRetrieveRequestFactory().getEntitySetRequest(uri);
		ODataRetrieveResponse<ClientEntitySet> res = req.execute();
		ClientEntitySet entity = res.getBody();		
		assertFalse( entity.getEntities().isEmpty() );

	}
	
	@Test
	public void testTopReq() throws URISyntaxException {
		
		URI uri = new URI(serviceRoot + "CUSTOMERS?$top=1");
		ODataEntitySetRequest<ClientEntitySet> req =
				client.getRetrieveRequestFactory().getEntitySetRequest(uri);
		ODataRetrieveResponse<ClientEntitySet> res = req.execute();
		ClientEntitySet entity = res.getBody();		
		assertTrue( entity.getEntities().size() == 1 );

	}
	
	@Test
	public void testFilterReq() throws URISyntaxException {
		
		URI uri = new URI(serviceRoot + "CUSTOMERS?$filter=contains(LAST_NAME,%27Andersen%27)");
		ODataEntitySetRequest<ClientEntitySet> req =
				client.getRetrieveRequestFactory().getEntitySetRequest(uri);		
		ODataRetrieveResponse<ClientEntitySet> res = req.execute();
		ClientEntitySet entity = res.getBody();		
		assertTrue( entity.getEntities().size() == 1 );

	}

	@Test
	public void testServiceReq() {
		ODataServiceDocumentRequest serviceReq =
				client.getRetrieveRequestFactory().getServiceDocumentRequest(serviceRoot);
		ODataRetrieveResponse<ClientServiceDocument> serviceRes = serviceReq.execute();
		ClientServiceDocument serviceBody = serviceRes.getBody();		
		Collection<String> names = serviceBody.getEntitySetNames();
		assertFalse(names.isEmpty());
	}



	@Before
	public void init() throws ServletException, IOException, SQLException{
		try(Connection connection = datasource.getConnection() ){				
			Loader.execute(connection);
		} catch (SQLException | IOException e) {
			throw new AssertionError(e);
		}
		client = setupMockClient();
	}

	@After
	public void drop(){
		try(Connection connection = datasource.getConnection() ){				
			Loader.drop(connection);
		} catch (SQLException | IOException e) {
			throw new AssertionError(e);
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

			@Override
			protected Map<String, Cursor> getCursors() {				
				try {
					return Loader.getCursors();
				} catch (IOException | JSQLParserException e) {
					throw new AssertionError(e);
				}
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

