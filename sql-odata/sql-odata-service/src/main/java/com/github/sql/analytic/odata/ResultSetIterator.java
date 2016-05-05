package com.github.sql.analytic.odata;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityIterator;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataContentWriteErrorCallback;
import org.apache.olingo.server.api.ODataContentWriteErrorContext;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;

import com.github.sql.analytic.session.SQLSession;

final class ResultSetIterator extends EntityIterator implements ODataContentWriteErrorCallback{

	private final ResultSet rs;
	private final Set<String> projection;
	private EdmEntityType type;
	private ExpandOption expandOption;
	private SQLSession session;
	
	ResultSetIterator(SQLSession session,ResultSet rs, EdmEntityType type, ExpandOption expandOption) throws SQLException {
		this.session = session;
		this.expandOption = expandOption;
		this.rs = rs;
		this.projection = new HashSet<>();		
		this.type = type;
		ResultSetMetaData md = rs.getMetaData();
		for(int i = 0; i < md.getColumnCount(); i++ ){
			projection.add(md.getColumnName(i + 1));
		}
	}

	@Override
	public Entity next() {
		try{
			Entity entity = EntityData.createEntity(type,projection,rs);
			if(expandOption != null){
				new ExpandCommad(expandOption, type, entity).expand(session);
			}
			return entity;
		} catch (SQLException | IOException | ODataApplicationException e) {
			throw new ODataRuntimeException(e);
		}	
	}

	@Override
	public boolean hasNext() {				
		try {
			if( rs.next() ){
				return true;
			}else {
				close();
				return false;
			}
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	public void close()  {
		try{			
			if(!rs.isClosed()){
				rs.close();
			}				
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void handleError(ODataContentWriteErrorContext context, WritableByteChannel channel) {
		close();
	}
	public ResultSet getResultSet() {
		return rs;
	}

	public Set<String> getProjection() {
		return projection;
	}


}