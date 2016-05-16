package com.github.sql.analytic.odata.web.test;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestUiServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private ServletConfig config;
	
	@Override
	public void init(ServletConfig config) throws ServletException {		
		super.init(config);
		this.config = config;
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "/";
		if(request.getRequestURI().endsWith(".js")){
			path = "/js/" + new LinkedList<String>(Arrays.asList(request.getRequestURI().split("/"))).getLast();
		}
		if(request.getRequestURI().endsWith(".css")){
			path = "/css/" + new LinkedList<String>(Arrays.asList(request.getRequestURI().split("/"))).getLast();
		}
		
		
		config.getServletContext().getRequestDispatcher(path).forward(request, response);
	}
	

}
