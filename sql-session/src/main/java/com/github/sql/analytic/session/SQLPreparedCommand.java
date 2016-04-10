package com.github.sql.analytic.session;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

public class SQLPreparedCommand extends SQLCommand implements PreparedStatement{

	private PreparedStatement preparedStatement;	
	private DeparsedSQL deparsed;

	public SQLPreparedCommand(SQLSession session, PreparedStatement statement,DeparsedSQL deparsed) throws SQLException {
		super(session);
		this.preparedStatement = statement;
		this.deparsed = deparsed;
		
	}

	protected void setSessionParams() throws SQLException {
		for( ParamNamePosition param: deparsed.getSessionParams()){
			Object value = getSession().getContext().getParameters().get(param.getName());
			if(value != null){
				preparedStatement.setObject(param.getPosition() + 1, value);
			}else {
				preparedStatement.setNull(param.getPosition() + 1, Types.CHAR);	
			}
		}
	}

	private int position(int original){
		return deparsed.getPositionalParams().get(original - 1) + 1;
	}

	public ResultSet executeQuery() throws SQLException {
		setSessionParams();
		return preparedStatement.executeQuery();
	}

	public int executeUpdate() throws SQLException {
		setSessionParams();
		return preparedStatement.executeUpdate();
	}

	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		preparedStatement.setNull(position(parameterIndex), sqlType);
	}

	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		preparedStatement.setBoolean(position(parameterIndex), x);
	}

	public void setByte(int parameterIndex, byte x) throws SQLException {
		preparedStatement.setByte(position(parameterIndex), x);
	}

	public void setShort(int parameterIndex, short x) throws SQLException {
		preparedStatement.setShort(position(parameterIndex), x);
	}

	public void setInt(int parameterIndex, int x) throws SQLException {
		preparedStatement.setInt(position(parameterIndex), x);
	}

	public void setLong(int parameterIndex, long x) throws SQLException {
		preparedStatement.setLong(position(parameterIndex), x);
	}

	public void setFloat(int parameterIndex, float x) throws SQLException {
		preparedStatement.setFloat(position(parameterIndex), x);
	}

	public void setDouble(int parameterIndex, double x) throws SQLException {
		preparedStatement.setDouble(position(parameterIndex), x);
	}

	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		preparedStatement.setBigDecimal(position(parameterIndex), x);
	}

	public void setString(int parameterIndex, String x) throws SQLException {
		preparedStatement.setString(position(parameterIndex), x);
	}

	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		preparedStatement.setBytes(position(parameterIndex), x);
	}

	public void setDate(int parameterIndex, Date x) throws SQLException {
		preparedStatement.setDate(position(parameterIndex), x);
	}

	public void setTime(int parameterIndex, Time x) throws SQLException {
		preparedStatement.setTime(position(parameterIndex), x);
	}

	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		preparedStatement.setTimestamp(position(parameterIndex), x);
	}

	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		preparedStatement.setAsciiStream(position(parameterIndex), x, length);
	}

	@SuppressWarnings("deprecation")
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		preparedStatement.setUnicodeStream(position(parameterIndex), x, length);
	}

	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		preparedStatement.setBinaryStream(position(parameterIndex), x, length);
	}

	public void clearParameters() throws SQLException {
		preparedStatement.clearParameters();
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		preparedStatement.setObject(position(parameterIndex), x, targetSqlType);
	}

	public void setObject(int parameterIndex, Object x) throws SQLException {
		preparedStatement.setObject(position(parameterIndex), x);
	}

	public boolean execute() throws SQLException {
		setSessionParams();
		return preparedStatement.execute();
	}

	public void addBatch() throws SQLException {
		setSessionParams();
		preparedStatement.addBatch();
	}

	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		preparedStatement.setCharacterStream(position(parameterIndex), reader, length);
	}


	public void setRef(int parameterIndex, Ref x) throws SQLException {
		preparedStatement.setRef(position(parameterIndex), x);
	}

	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		preparedStatement.setBlob(position(parameterIndex), x);
	}

	public void setClob(int parameterIndex, Clob x) throws SQLException {
		preparedStatement.setClob(position(parameterIndex), x);
	}

	public void setArray(int parameterIndex, Array x) throws SQLException {
		preparedStatement.setArray(position(parameterIndex), x);
	}


	public ResultSetMetaData getMetaData() throws SQLException {
		return preparedStatement.getMetaData();
	}

	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		preparedStatement.setDate(position(parameterIndex), x, cal);
	}

	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		preparedStatement.setTime(position(parameterIndex), x, cal);
	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		preparedStatement.setTimestamp(position(parameterIndex), x, cal);
	}

	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		setSessionParams();
		return preparedStatement.executeUpdate(sql, autoGeneratedKeys);
	}

	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		preparedStatement.setNull(position(parameterIndex), sqlType, typeName);
	}

	public void setURL(int parameterIndex, URL x) throws SQLException {
		preparedStatement.setURL(position(parameterIndex), x);
	}

	public ParameterMetaData getParameterMetaData() throws SQLException {
		return preparedStatement.getParameterMetaData();
	}

	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		preparedStatement.setRowId(parameterIndex, x);
	}

	public void setNString(int parameterIndex, String value) throws SQLException {
		preparedStatement.setNString(position(parameterIndex), value);
	}

	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		preparedStatement.setNCharacterStream(position(parameterIndex), value, length);
	}

	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		preparedStatement.setNClob(position(parameterIndex), value);
	}

	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		preparedStatement.setClob(position(parameterIndex), reader, length);
	}

	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		preparedStatement.setBlob(position(parameterIndex), inputStream, length);
	}

	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		preparedStatement.setNClob(position(parameterIndex), reader, length);
	}

	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		preparedStatement.setSQLXML(position(parameterIndex), xmlObject);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		preparedStatement.setObject(position(parameterIndex), x, targetSqlType, scaleOrLength);
	}

	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		preparedStatement.setAsciiStream(position(parameterIndex), x, length);
	}

	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		preparedStatement.setBinaryStream(position(parameterIndex), x, length);
	}


	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		preparedStatement.setCharacterStream(position(parameterIndex), reader, length);
	}

	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		preparedStatement.setAsciiStream(position(parameterIndex), x);
	}

	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		preparedStatement.setBinaryStream(position(parameterIndex), x);
	}

	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		preparedStatement.setCharacterStream(position(parameterIndex), reader);
	}

	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		preparedStatement.setNCharacterStream(position(parameterIndex), value);
	}

	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		preparedStatement.setClob(position(parameterIndex), reader);
	}

	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		preparedStatement.setBlob(position(parameterIndex), inputStream);
	}

	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		preparedStatement.setNClob(position(parameterIndex), reader);
	}


}
