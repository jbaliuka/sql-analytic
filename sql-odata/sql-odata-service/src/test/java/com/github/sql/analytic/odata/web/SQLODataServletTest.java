package com.github.sql.analytic.odata.web;

import static org.mockito.Mockito.stub;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SQLODataServletTest {


	private SQLODataServlet servlet;
	@Mock
	private ServletConfig config;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private ServletOutputStream servletOutputStream;

	@Before
	public void init() throws ServletException, IOException{
		MockitoAnnotations.initMocks(this);		
		servlet = new SQLODataServlet();
		servlet.init(config);
		stub(response.getOutputStream()).toReturn(servletOutputStream);


	}

	@Test
	public void test() throws ServletException, IOException {
		servlet.service(request, response);
	}

}
