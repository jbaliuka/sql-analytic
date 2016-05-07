package com.github.sql.analytic.odata.web;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;

import com.github.sql.analytic.odata.SQLEdmProvider;
import com.github.sql.analytic.odata.SQLEntityCollectionProcessor;
import com.github.sql.analytic.odata.SQLEntityProcessor;
import com.github.sql.analytic.session.SQLSession;
import com.github.sql.analytic.statement.Cursor;
import com.github.sql.analytic.statement.Variable;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.transform.policy.SessionContext;

public class SQLOdataHandler {

	private static final String PUBLIC = "PUBLIC";
	private Connection connection;
	private List<CreatePolicy> policy;
	private ServletConfig config;
	private Map<String,Cursor> cursors;
	private Map<String, Variable> variables;
	private String schema;

	public SQLOdataHandler(ServletConfig config,Connection connection,List<CreatePolicy> policy, Map<String, Cursor> cursors, Map<String, Variable> variables){
		this.connection = connection;
		this.policy = policy;
		this.config = config;
		this.cursors = cursors;
		this.variables = variables;
	}


	public void process(final HttpServletRequest request, HttpServletResponse response) throws SQLException{

		SessionContext context = createContext(request);										
		SQLSession session = createSession(context);					
		OData odata = OData.newInstance();
		SQLEdmProvider edmProvider = new SQLEdmProvider(session.getMetaData());
		edmProvider.setCursors(cursors);
		edmProvider.setVariables(variables);
		ServiceMetadata edm = odata.createServiceMetadata(edmProvider, new ArrayList<EdmxReference>());
		ODataHttpHandler handler = odata.createHandler(edm);
		SQLEntityCollectionProcessor processor = new SQLEntityCollectionProcessor(session);
		processor.setCursors(cursors);
		handler.register(processor);	      
		handler.register(new SQLEntityProcessor(session));					
		handler.process(request, response);	

	}

	protected SQLSession createSession(SessionContext context) {
		return new SQLSession(context , connection, policy);
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
				if(schema != null){
					return schema;
				}
				try {
					schema = connection.getSchema();
				} catch (Throwable e) {
					schema = PUBLIC;
				} 
				return schema == null ? PUBLIC : schema;
			}
		};
	}

}
