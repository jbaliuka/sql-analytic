package com.github.sql.analytic.session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

public class SQLCommand implements Statement{

	private SQLSession session;	
	private PreparedStatement statement;
	private int maxFieldSize = -1;
	private int maxRows = -1;
	private int escapeProcessing =  -1;
	private int queryTimeout = -1;
	private String cursorName;
	private int fetchDirection = -1;
	private int fetchSize = -1;	
	private int resultSetHoldability = -1;
	private int poolable = -1;
	private int closeOnCompletion = -1;
	private int resultSetConcurrency;
	private int resultSetType = -1;
	

	public SQLSession getSession() {
		return session;
	}
	public SQLCommand(SQLSession session) {
		super();
		this.session = session;		
	}
	public SQLCommand(SQLSession session, int resultSetType, int resultSetConcurrency) {
		this.session = session;		
		this.resultSetType = resultSetType;
		this.resultSetConcurrency = resultSetConcurrency;
		
	}
	public SQLCommand(SQLSession session, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
		this.session = session;		
		this.resultSetType = resultSetType;
		this.resultSetConcurrency = resultSetConcurrency;
		this.resultSetHoldability = resultSetHoldability;
	}
	
	private void setOptions() throws SQLException{
		
		if(maxFieldSize >= 0 ){
			statement.setMaxFieldSize(maxFieldSize);
		}
		if(maxRows >= 0){
			statement.setMaxRows(maxRows);
		}
		if (escapeProcessing >=0  ){
			statement.setEscapeProcessing(escapeProcessing == 1);
		}		
		if(queryTimeout >= 0){
			statement.setQueryTimeout(queryTimeout);
		}
		if(cursorName != null){
			statement.setCursorName(cursorName);
		}
		if(fetchDirection >= 0){
			statement.setFetchDirection(fetchDirection);
		}
		if (fetchSize >= 0 ){
			statement.setFetchSize(fetchSize);
		}
		if (poolable >= 0){
			statement.setPoolable(poolable == 1);
		}
		if (closeOnCompletion == 1){
			statement.closeOnCompletion();
		}
		
		
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return statement == null ? null : statement.unwrap(iface);
	}
	public ResultSet executeQuery(String sql) throws SQLException {
		statement = session.prepareStatement(sql);
		setOptions();
		return statement.executeQuery();
	}
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return statement == null ? false : statement.isWrapperFor(iface);
	}
	public int executeUpdate(String sql) throws SQLException {
		statement = session.prepareStatement(sql);
		setOptions();
		return statement.executeUpdate();
	}
	public void close() throws SQLException {
		if(statement != null){
			statement.close();
		}
	}
	public int getMaxFieldSize() throws SQLException {
		return  maxFieldSize;
	}
	public void setMaxFieldSize(int max) throws SQLException {
		maxFieldSize = max;
	}
	public int getMaxRows() throws SQLException {
		return maxRows;
	}
	public void setMaxRows(int max) throws SQLException {
		maxRows = max;
	}
	public void setEscapeProcessing(boolean enable) throws SQLException {
		escapeProcessing = enable ? 1 : 0; 
	}
	public int getQueryTimeout() throws SQLException {
		return queryTimeout;
	}
	public void setQueryTimeout(int seconds) throws SQLException {
		queryTimeout = seconds;
	}
	public void cancel() throws SQLException {
		if(statement != null){
			statement.cancel();
		}
	}
	public SQLWarning getWarnings() throws SQLException {
		return statement == null ? null : statement.getWarnings();
	}
	public void clearWarnings() throws SQLException {
		if(statement != null){
			statement.clearWarnings();
		}
	}
	public void setCursorName(String name) throws SQLException {
		cursorName = name;
	}
	public boolean execute(String sql) throws SQLException {
		statement = session.prepareStatement(sql);
		return statement.execute();
	}
	public ResultSet getResultSet() throws SQLException {
		return statement == null ? null : statement.getResultSet();
	}
	public int getUpdateCount() throws SQLException {
		return statement == null ? -1 : statement.getUpdateCount();
	}
	public boolean getMoreResults() throws SQLException {
		return statement == null ? false : statement.getMoreResults();
	}
	public void setFetchDirection(int direction) throws SQLException {
		fetchDirection = direction;
	}
	public int getFetchDirection() throws SQLException {
		return fetchDirection;
	}
	public void setFetchSize(int rows) throws SQLException {
		fetchSize = rows;
	}
	public int getFetchSize() throws SQLException {
		return fetchSize;
	}
	public int getResultSetConcurrency() throws SQLException {
		return resultSetConcurrency;
	}
	public int getResultSetType() throws SQLException {
		return statement.getResultSetType();
	}
	public void addBatch(String sql) throws SQLException {
		statement = session.prepareStatement(sql);
		statement.addBatch();
	}
	public void clearBatch() throws SQLException {
		if(statement != null){
			statement.clearBatch();
		}
	}
	public int[] executeBatch() throws SQLException {
		if(statement != null){
			return statement.executeBatch();
		}else {
			return new int[]{};
		}
	}
	public Connection getConnection() throws SQLException {
		return session;
	}
	public boolean getMoreResults(int current) throws SQLException {
		if(statement != null){
			return statement.getMoreResults(current);
		}else {
			return false;
		}
	}
	public ResultSet getGeneratedKeys() throws SQLException {
		if(statement != null){
			return statement.getGeneratedKeys();
		}else {
			return null;
		}
	}
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		statement = session.prepareStatement(sql, autoGeneratedKeys);
		return statement.executeUpdate();
	}
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		statement = session.prepareStatement(sql, columnIndexes);
		return statement.executeUpdate();
	}
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		statement = session.prepareStatement(sql,columnNames);
		return statement.executeUpdate();
	}
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		statement = session.prepareStatement(sql,autoGeneratedKeys);
		return statement.execute();
	}
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		statement = session.prepareStatement(sql,columnIndexes);
		return statement.execute();
	}
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		statement = session.prepareStatement(sql,columnNames);
		return statement.execute();
	}
	public int getResultSetHoldability() throws SQLException {
		return resultSetHoldability;
	}
	public boolean isClosed() throws SQLException {
		return statement == null ? true : statement.isClosed();
	}
	public void setPoolable(boolean poolable) throws SQLException {
		this.poolable = poolable ? 1 : 0;
	}
	public boolean isPoolable() throws SQLException {
		return poolable == 1;
	}
	public void closeOnCompletion() throws SQLException {
		closeOnCompletion = 1;
		
	}
	public boolean isCloseOnCompletion() throws SQLException {
		return closeOnCompletion == 1;
	}

}
