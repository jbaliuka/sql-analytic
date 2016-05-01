package com.github.sql.analytic.odata.web;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;

import com.github.sql.analytic.odata.SQLEdmProvider;
import com.github.sql.analytic.odata.SQLEntityCollectionProcessor;
import com.github.sql.analytic.odata.SQLEntityProcessor;
import com.github.sql.analytic.session.SQLSession;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.transform.policy.SessionContext;

public class SQLOdataHandler {
	
	private Connection connection;
	private List<CreatePolicy> policy;

	public SQLOdataHandler(Connection connection,List<CreatePolicy> policy){
		this.connection = connection;
		this.policy = policy;
	}
	
	public void process(final HttpServletRequest request, HttpServletResponse response) throws SQLException{
		             
		            SessionContext context = createContext(request);
										
					SQLSession session = createSession(context);
					OData odata = OData.newInstance();
					ServiceMetadata edm = odata.createServiceMetadata(new SQLEdmProvider(session.getMetaData()), new ArrayList<EdmxReference>());
					ODataHttpHandler handler = odata.createHandler(edm);
					handler.register(new SQLEntityCollectionProcessor(session));	      
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
				if(value == null && request.getServletContext() != null ){
					value = request.getServletContext().getAttribute(name);
				}
				
				return value;
			}
			
			@Override
			public String getCurrentUser() {							
				return request.getUserPrincipal() == null ? null : request.getUserPrincipal().getName();
			}
		};
	}

}
