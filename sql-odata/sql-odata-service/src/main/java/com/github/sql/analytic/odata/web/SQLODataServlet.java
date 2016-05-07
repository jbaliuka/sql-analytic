package com.github.sql.analytic.odata.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.github.sql.analytic.statement.Cursor;
import com.github.sql.analytic.statement.Variable;
import com.github.sql.analytic.statement.policy.CreatePolicy;

public abstract class SQLODataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletConfig config;
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			try(Connection connection = getDatasource().getConnection()){
				try{
					
					SQLOdataHandler handler = new SQLOdataHandler(config,connection, getPolicy(),getCursors(),getVariables());
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
	@Override
	public void init(ServletConfig config) throws ServletException {		
		super.init(config);
		this.config = config;
	}

	abstract protected List<CreatePolicy> getPolicy();

	abstract protected DataSource getDatasource();
	abstract protected Map<String, Cursor> getCursors();
	abstract protected Map<String, Variable> getVariables();



}
