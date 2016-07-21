package com.github.sql.analytic.session;

import java.io.StringReader;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.parser.CCJSqlParserManager;
import com.github.sql.analytic.statement.SQLStatement;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.transform.policy.Policy;
import com.github.sql.analytic.transform.policy.SessionContext;

public class SQLSession implements Connection{

	protected Connection connection;	
	private SessionContext context;
	private List<CreatePolicy> policy;
	private Map<String, DeparsedSQL> sqlCache = createCache();
	private Policy transform;


	public SQLSession(SessionContext context, Connection connection, List<CreatePolicy> policy) {
		super();
		this.connection = connection;		
		this.policy = policy;
		this.context = context;
		this.transform = createPolicy();
	}


	protected Map<String, DeparsedSQL> createCache() {
		return new HashMap<>();
	}

	public void enablePolicy(String name){
		transform.enablePolicy(name);
	}

	public void disablePolicy(String name){
		transform.disablePolicy(name);
	}

	public DeparsedSQL transform(String sql) throws SQLException{

		DeparsedSQL cached = sqlCache.get(sql);
		if(cached != null){
			return cached;
		}

		CCJSqlParserManager parserManager = new CCJSqlParserManager();			

		try {

			com.github.sql.analytic.statement.SQLStatement stmt = parserManager.transform(new StringReader(sql), transform);
			StringBuffer buffer = new StringBuffer();
			ParamsDeparser deparser = createDeparser(buffer);
			stmt.accept(deparser);			
			cached = deparser.getDeparsedSQL();			
			sqlCache.put(sql, cached);

			return cached;

		} catch (JSQLParserException e) {
			throw new SQLException(e);
		}		
	}

	public PreparedStatement create(SQLStatement stmt,Map<String,Object> statementParams)throws SQLException{

		stmt = transform.trasform(stmt);
		StringBuffer buffer = new StringBuffer();
		ParamsDeparser deparser = createDeparser(buffer);
		stmt.accept(deparser);			
		DeparsedSQL deparsed = deparser.getDeparsedSQL();	
		return new SQLPreparedCommand(this, statementParams,
				connection.prepareStatement(deparsed.getSql()),deparsed);
	}

	protected Policy createPolicy() {		
		return  new Policy(policy, context);
	}

	public ParamsDeparser createDeparser(StringBuffer buffer) {
		return new ParamsDeparser(buffer);
	} 


	public List<CreatePolicy> getPolicy() {
		return policy;
	}

	public SessionContext getContext() {
		return context;
	}


	public <T> T unwrap(Class<T> iface) throws SQLException {
		return connection.unwrap(iface);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return connection.isWrapperFor(iface);
	}

	public Statement createStatement() throws SQLException {
		return new SQLCommand(this);
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		DeparsedSQL deparsed = transform(sql);
		return new SQLPreparedCommand(this,connection.prepareStatement(deparsed.getSql()),deparsed);
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		DeparsedSQL deparsed = transform(sql);
		return new SQLCallableCommand(this, connection.prepareCall(deparsed.getSql()),deparsed);
	}

	public String nativeSQL(String sql) throws SQLException {
		return transform(sql).getSql();
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		connection.setAutoCommit(autoCommit);
	}

	public boolean getAutoCommit() throws SQLException {
		return connection.getAutoCommit();
	}

	public void commit() throws SQLException {
		connection.commit();
	}

	public void rollback() throws SQLException {
		connection.rollback();
	}

	public void close() throws SQLException {
		connection.close();
	}

	public boolean isClosed() throws SQLException {
		return connection.isClosed();
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return new PolicyAwareMetadata(this, connection.getMetaData());
	}

	public void setReadOnly(boolean readOnly) throws SQLException {
		connection.setReadOnly(readOnly);
	}

	public boolean isReadOnly() throws SQLException {
		return connection.isReadOnly();
	}

	public void setCatalog(String catalog) throws SQLException {
		connection.setCatalog(catalog);
	}

	public String getCatalog() throws SQLException {
		return connection.getCatalog();
	}

	public void setTransactionIsolation(int level) throws SQLException {
		connection.setTransactionIsolation(level);
	}

	public int getTransactionIsolation() throws SQLException {
		return connection.getTransactionIsolation();
	}

	public SQLWarning getWarnings() throws SQLException {
		return connection.getWarnings();
	}

	public void clearWarnings() throws SQLException {
		connection.clearWarnings();
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {		
		return  new SQLCommand(this,resultSetType,resultSetConcurrency);

	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		DeparsedSQL deparsed = transform(sql);		
		return  new SQLPreparedCommand(this, 
				connection.prepareStatement(deparsed.getSql(), resultSetType, resultSetConcurrency),deparsed);
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		DeparsedSQL deparsed = transform(sql);
		return new SQLCallableCommand(this, connection.prepareCall(deparsed.getSql(), 
				resultSetType, resultSetConcurrency),deparsed);
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return connection.getTypeMap();
	}

	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		connection.setTypeMap(map);
	}

	public void setHoldability(int holdability) throws SQLException {
		connection.setHoldability(holdability);
	}

	public int getHoldability() throws SQLException {
		return connection.getHoldability();
	}

	public Savepoint setSavepoint() throws SQLException {
		return connection.setSavepoint();
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		return connection.setSavepoint(name);
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		connection.rollback(savepoint);
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		connection.releaseSavepoint(savepoint);
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {		
		return new SQLCommand(this,resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		DeparsedSQL deparsed = transform(sql);
		return new SQLPreparedCommand(this,connection.prepareStatement(deparsed.getSql(),
				resultSetType, resultSetConcurrency, resultSetHoldability),deparsed);
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		DeparsedSQL deparsed = transform(sql);
		return new SQLCallableCommand(this,connection.prepareCall(deparsed.getSql(),
				resultSetType, resultSetConcurrency, resultSetHoldability),deparsed);
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		DeparsedSQL deparsed = transform(sql);
		return new SQLPreparedCommand(this,connection.prepareStatement(deparsed.getSql(), autoGeneratedKeys),deparsed);
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		DeparsedSQL deparsed = transform(sql);
		return  new SQLPreparedCommand(this, connection.prepareStatement(deparsed.getSql(), columnIndexes),deparsed);
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		DeparsedSQL deparsed = transform(sql);
		return  new SQLPreparedCommand(this,connection.prepareStatement(deparsed.getSql(), columnNames),deparsed);
	}

	public Clob createClob() throws SQLException {
		return connection.createClob();
	}

	public Blob createBlob() throws SQLException {
		return connection.createBlob();
	}

	public NClob createNClob() throws SQLException {
		return connection.createNClob();
	}

	public SQLXML createSQLXML() throws SQLException {
		return connection.createSQLXML();
	}

	public boolean isValid(int timeout) throws SQLException {
		return connection.isValid(timeout);
	}

	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		connection.setClientInfo(name, value);
	}

	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		connection.setClientInfo(properties);
	}

	public String getClientInfo(String name) throws SQLException {
		return connection.getClientInfo(name);
	}

	public Properties getClientInfo() throws SQLException {
		return connection.getClientInfo();
	}

	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return connection.createArrayOf(typeName, elements);
	}

	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return connection.createStruct(typeName, attributes);
	}

	public void setSchema(String schema) throws SQLException {
		connection.setSchema(schema);
	}

	public String getSchema() throws SQLException {
		return connection.getSchema();
	}

	public void abort(Executor executor) throws SQLException {
		connection.abort(executor);
	}

	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		connection.setNetworkTimeout(executor, milliseconds);
	}

	public int getNetworkTimeout() throws SQLException {
		return connection.getNetworkTimeout();
	}

}
