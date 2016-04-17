package com.github.sql.analytic.odata.web;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;

import com.github.sql.analytic.odata.SQLEdmProvider;
import com.github.sql.analytic.odata.SQLEntityCollectionProcessor;

public class SQLODataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		  OData odata = OData.newInstance();
	      ServiceMetadata edm = odata.createServiceMetadata(new SQLEdmProvider(), new ArrayList<EdmxReference>());
	      ODataHttpHandler handler = odata.createHandler(edm);
	      handler.register(new SQLEntityCollectionProcessor());
	      
	      handler.process(request, response);
	      
	}

}
