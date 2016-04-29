package com.github.sql.analytic.odata.web;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;

import com.github.sql.analytic.odata.SQLEdmProvider;
import com.github.sql.analytic.odata.SQLEntityCollectionProcessor;

public class SQLOdataHandler {
	
	private Connection connection;

	public SQLOdataHandler(Connection connection){
		this.connection = connection;
	}
	
	public void process(HttpServletRequest request, HttpServletResponse response) throws SQLException{
					
					OData odata = OData.newInstance();
					ServiceMetadata edm = odata.createServiceMetadata(new SQLEdmProvider(connection.getMetaData()), new ArrayList<EdmxReference>());
					ODataHttpHandler handler = odata.createHandler(edm);
					handler.register(new SQLEntityCollectionProcessor());	      
					handler.process(request, response);		

		
	}

}
