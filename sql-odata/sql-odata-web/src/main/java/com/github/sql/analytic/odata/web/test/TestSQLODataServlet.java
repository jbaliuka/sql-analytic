package com.github.sql.analytic.odata.web.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import com.github.sql.analytic.odata.web.SQLODataServlet;

public class TestSQLODataServlet extends SQLODataServlet {

	private static final long serialVersionUID = 1L;

	private static final String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

	private void loadDB(Connection connection) throws SQLException, IOException {

		try(InputStream in = getClass().getClassLoader().getResourceAsStream("mywind/northwind.sql")){
			BufferedReader reader = new BufferedReader( new InputStreamReader(in));
			String line;
			while((line = reader.readLine()) != null){
				connection.createStatement().execute(line);
			}
		}
		try(InputStream in = getClass().getClassLoader().getResourceAsStream("mywind/northwind-data.sql")){
			BufferedReader reader = new BufferedReader( new InputStreamReader(in));
			String line;
			while((line = reader.readLine()) != null){
				connection.createStatement().execute(line);
			}			
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {		
		super.init(config);

		setDatasource( new DataSource() {

			@Override
			public <T> T unwrap(Class<T> iface) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isWrapperFor(Class<?> iface) throws SQLException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void setLoginTimeout(int seconds) throws SQLException {
				// TODO Auto-generated method stub

			}

			@Override
			public void setLogWriter(PrintWriter out) throws SQLException {
				// TODO Auto-generated method stub

			}

			@Override
			public Logger getParentLogger() throws SQLFeatureNotSupportedException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getLoginTimeout() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public PrintWriter getLogWriter() throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Connection getConnection(String username, String password) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Connection getConnection() throws SQLException {

				return  new org.h2.Driver().connect(url, new Properties());
			}
		});
		try{
			try(Connection connection = getDatasource().getConnection()){
				loadDB(connection);
			} catch (IOException e) {
				throw new ServletException(e);
			} 
		}catch (SQLException e) {
			throw new ServletException(e);
		}

	}


}
