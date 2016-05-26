package com.github.sql.analytic.odata.ser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.ODataContent;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;

import com.github.sql.analytic.odata.ResultSetIterator;

public class CSVContent implements ODataContent {
	
	private ResultSetIterator iterator;
	private EntityCollectionSerializerOptions options;

	public CSVContent(EntityCollectionSerializerOptions opts, ResultSetIterator iterator){
		this.iterator = iterator;
		this.options = opts;
	}

	@Override
	public void write(WritableByteChannel channel) {
		try{
			
			List<String> propertyNames = new ArrayList<String>();
			if(options.getSelect() == null ||
					options.getSelect().getSelectItems().isEmpty() || 
					options.getSelect().getSelectItems().get(0).isStar()){
				propertyNames.addAll(iterator.getProjection());	
			}else {
				for(SelectItem item : options.getSelect().getSelectItems()){
					propertyNames.add(item.getResourcePath().getUriResourceParts().get(0).getSegmentValue());
				}
			}
			StringBuilder line = new StringBuilder();			
			for(int i = 0; i< propertyNames.size(); i++){
				String name = propertyNames.get(i);
				line.append("\"");
				line.append(name);
				line.append("\"");
				if( i < propertyNames.size() - 1){
					line.append(",");	
				}
			}
			line.append("\r\n");
			channel.write(ByteBuffer.wrap(line.toString().getBytes("utf-8")));			
			while(iterator.hasNext()){
				Entity e = iterator.next();		
				line.setLength(0);				
				for(int i = 0; i< propertyNames.size(); i++){
					Property p = e.getProperty(propertyNames.get(i));
					Object value = p.getValue(); 
					if(value != null){
						line.append("\"");
						line.append(value.toString().replaceAll("\"", "\"\""));
						line.append("\"");
					}
					if( i < propertyNames.size() - 1){
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
