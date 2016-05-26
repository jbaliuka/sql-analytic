package com.github.sql.analytic.odata.ser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.ODataContent;

import com.github.sql.analytic.odata.ResultSetIterator;

public class CSVContent implements ODataContent {
	
	private ResultSetIterator iterator;

	public CSVContent(ResultSetIterator iterator){
		this.iterator = iterator;
	}

	@Override
	public void write(WritableByteChannel channel) {
		try{
			StringBuilder line = new StringBuilder();
			while(iterator.hasNext()){
				Entity e = iterator.next();		
				line.setLength(0);
				List<Property> properties = e.getProperties();
				for(int i=0; i< properties.size(); i++){
					Property p = properties.get(i);
					Object value = p.getValue(); 
					if(value != null){
						line.append("\"");
						line.append(value.toString().replaceAll("\"", "\"\""));
						line.append("\"");
					}
					if( i < properties.size() - 1){
						line.append(",");	
					}
				}
				line.append("\r\n");
				channel.write(ByteBuffer.wrap(line.toString().getBytes("utf-8")));
						
			}	
		} catch (IOException e) {
			throw new ODataRuntimeException(e);			
		}finally{
			iterator.close();
		}

	}

	@Override
	public void write(OutputStream stream) {
		write(Channels.newChannel(stream));

	}

}
