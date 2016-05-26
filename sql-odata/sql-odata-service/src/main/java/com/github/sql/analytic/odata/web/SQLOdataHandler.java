package com.github.sql.analytic.odata.web;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;

import com.github.sql.analytic.odata.FunctionCommand;
import com.github.sql.analytic.odata.SQLEdmProvider;
import com.github.sql.analytic.odata.SQLEntityCollectionProcessor;
import com.github.sql.analytic.odata.SQLEntityProcessor;
import com.github.sql.analytic.odata.SQLPrimitiveProcessor;
import com.github.sql.analytic.odata.ser.CustomContentTypes;
import com.github.sql.analytic.session.SQLDialect;
import com.github.sql.analytic.session.SQLSession;
import com.github.sql.analytic.statement.Cursor;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.transform.policy.SessionContext;

public class SQLOdataHandler {
	
	private Connection connection;
	private List<CreatePolicy> policy;
	private ServletConfig config;
	private Map<String,Cursor> cursors;
	private SQLDialect dialect;
	private SQLSession session;
	private Map<String,FunctionCommand> functions = new HashMap<>();

	public SQLOdataHandler(ServletConfig config,Connection connection,List<CreatePolicy> policy, Map<String, Cursor> cursors){
		this.connection = connection;
		this.policy = policy;
		this.config = config;
		this.cursors = cursors;
		loadDialect(connection);
	}

	public void register(FunctionCommand function ){
		functions.put(function.getName(),function);
	}

	private void loadDialect(Connection connection) {
		try{
		for(SQLDialect dialect :  ServiceLoader.load(SQLDialect.class)){			
				if(dialect.acceptsURL(connection.getMetaData().getURL())){
					this.setDialect(dialect);
					break;
				}			
		}
		if(getDialect() == null){
			throw new ODataRuntimeException("Unable to find dialect for " + connection.getMetaData().getURL());
		}
		} catch (SQLException e) {
			throw new ODataRuntimeException(e);
		}
	}


	public void process(final HttpServletRequest request, HttpServletResponse response) throws SQLException{

		SessionContext context = createContext(request);										
		session = createSession(context);					
		OData odata = OData.newInstance();
		SQLEdmProvider edmProvider = new SQLEdmProvider(session.getMetaData());
		edmProvider.setCursors(cursors);
		edmProvider.setFunctions(functions);
		ServiceMetadata edm = odata.createServiceMetadata(edmProvider, new ArrayList<EdmxReference>());
		ODataHttpHandler handler = odata.createHandler(edm);
		SQLEntityCollectionProcessor processor = new SQLEntityCollectionProcessor(session);
		processor.setCursors(cursors);
		handler.register(processor);	      
		handler.register(new SQLEntityProcessor(session));
		handler.register(new SQLPrimitiveProcessor(session,functions));
		handler.register(new CustomContentTypes());
		handler.process(request, response);	

	}

	protected SQLSession createSession(SessionContext context) {
		try {
			return getDialect().geSQLSession(context , connection, policy);
		} catch (SQLException e) {
			throw new ODataRuntimeException(e);
		}
	}

	protected SessionContext createContext(final HttpServletRequest request) {
		return new SessionContext() {

			@Override
			public boolean isUserInRole(String role) {							
				return request.isUserInRole(role);
			}

			@Override
			public  Object getParameter(String name) {

				Object value = request.getAttribute(name);
				if(value == null && request.getSession(false) != null){
					value =  request.getSession(false).getAttribute(name);								
				}
				if(value == null && config != null ){
					value = config.getServletContext().getAttribute(name);
				}

				return value;
			}

			@Override
			public String getCurrentUser() {							
				return request.getUserPrincipal() == null ? null : request.getUserPrincipal().getName();
			}

			@Override
			public String getDefaultSchema() {				
					try {
						return session.getSchema();
					} catch (SQLException e) {
						return null;
					}
				 
				
			}
		};
	}


	public SQLDialect getDialect() {
		return dialect;
	}


	public void setDialect(SQLDialect dialect) {
		this.dialect = dialect;
	}

}
