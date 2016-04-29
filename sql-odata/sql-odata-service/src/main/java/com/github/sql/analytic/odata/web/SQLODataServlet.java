package com.github.sql.analytic.odata.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class SQLODataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DataSource datasource;
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			try(Connection connection = datasource.getConnection()){
				try{	

					Connection session = createWrapper(connection,request);
					SQLOdataHandler handler = new SQLOdataHandler(session);
					handler.process(request, response);
					connection.commit();

				} catch (SQLException e) {
					connection.rollback();
					throw new ServletException(e);
				}
			}
		}catch(SQLException e){
			throw new ServletException(e);
		}

	}


	protected Connection createWrapper(Connection connection, HttpServletRequest request) {		
		return connection;
	}


	public DataSource getDatasource() {
		return datasource;
	}


	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

}
