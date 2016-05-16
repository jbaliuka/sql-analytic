package com.github.sql.analytic.odata;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;

public class EntityData {
	
	public static boolean inSelection(SelectOption selectOption, String name) {
		if(selectOption == null || selectOption.getSelectItems().isEmpty()){
			return true;
		}
		for(SelectItem item : selectOption.getSelectItems()){
		  if(item.isStar()){
			  return true;
		  }
		  List<UriResource> parts = item.getResourcePath().getUriResourceParts();
		  if(parts.get(parts.size() - 1).getSegmentValue().equals(name)  ){
			  return true;
		  }
		}
		return false;
	}

	public static Entity createEntity(EdmEntityType edmEntityType, Set<String> projection, ResultSet rs) throws SQLException, IOException {

		
		Entity entity = new Entity();
		entity.setType(edmEntityType.getFullQualifiedName().toString());

		for( String name : edmEntityType.getPropertyNames()){	
			if(projection == null || projection.contains(name.toUpperCase())){
				EdmElement prop = edmEntityType.getProperty(name);
				entity.addProperty( new Property(null,prop.getName(),ValueType.PRIMITIVE,toPrimitive(rs, prop)));
			}
		}
		for( EdmKeyPropertyRef ref : edmEntityType.getKeyPropertyRefs()){
			if(projection == null || projection.contains(ref.getName())){
				entity.addProperty( new Property(null,ref.getName(),ValueType.PRIMITIVE,rs.getObject(ref.getName())));
			}
		}		
		entity.setId(createId(edmEntityType.getName(),rs,edmEntityType));
		return entity;
	}

	private static Object toPrimitive(ResultSet rs, EdmElement prop) throws SQLException, IOException {
		Object value = rs.getObject(prop.getName());
		if(value instanceof Blob){
			Blob blob = (Blob) value;
			try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
				try(InputStream is = blob.getBinaryStream()){
					int b;
					while( ( b = is.read()) > 0){
						out.write(b);
					}
				}
				value = DatatypeConverter.printBase64Binary(out.toByteArray());
			}
		}else if (value instanceof Clob) {
			Clob clob = (Clob) value;
			StringBuilder builder = new StringBuilder();
			try(BufferedReader reader = new BufferedReader( clob.getCharacterStream())){
				String line;
				while((line = reader.readLine()) != null){
					builder.append(line);
				}
			}
			value = builder.toString();
		}
		return value;
	}

	private static URI createId(String name, ResultSet rs, EdmEntityType edmEntityType) throws SQLException {
		try {
			List<EdmKeyPropertyRef> key = edmEntityType.getKeyPropertyRefs();
			StringBuilder builder = new StringBuilder(name).append("(");
			for(int i = 0; i< key.size(); i++ ){
				builder.append(key.get(i).getName());
				builder.append("=");
				builder.append(rs.getObject(key.get(i).getName()));				
				if( i < key.size() - 1 ){
					builder.append(",");
				}
			}
			return new URI(URLEncoder.encode(builder.append(")").toString(),"UTF-8"));
		} catch (URISyntaxException | UnsupportedEncodingException e) {
			throw new ODataRuntimeException("Unable to create id for entity: " + name, e);
		}
	}

	public static StringBuilder buildSelect(EdmEntityType edmEntityType) {
		StringBuilder sql = new StringBuilder("SELECT ");
		for(int i = 0; i < edmEntityType.getPropertyNames().size(); i++ ){
			sql.append(edmEntityType.getPropertyNames().get(i));
			if( i < edmEntityType.getPropertyNames().size() - 1){
				sql.append(",");
			}
		}
		sql.append(" FROM ").append(edmEntityType.getFullQualifiedName());
		return sql;
	}

	public static void buildWhere(List<UriParameter> keyPredicates, StringBuilder sql) {
		sql.append(" WHERE ");
		for(int i = 0; i < keyPredicates.size(); i++){
			UriParameter key = keyPredicates.get(i);
			sql.append(key.getName());
			sql.append("=?");
			if( i < keyPredicates.size() - 1 ){
				sql.append(" AND ");
			}	    	
		}
	}
	
	


}
