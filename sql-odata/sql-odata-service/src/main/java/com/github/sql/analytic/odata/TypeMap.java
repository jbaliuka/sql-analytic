package com.github.sql.analytic.odata;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.sql.Date;
import java.sql.Time;
import java.sql.Types;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;

public enum TypeMap {
	
	BIT(Types.BIT,EdmPrimitiveTypeKind.Byte,Byte.TYPE),	
	TINYINT(Types.TINYINT,EdmPrimitiveTypeKind.Byte,Byte.TYPE),
	SMALLINT(Types.SMALLINT,EdmPrimitiveTypeKind.Int16,Short.TYPE),
	INTEGER(Types.INTEGER,EdmPrimitiveTypeKind.Int32,Integer.TYPE),
	INT(Types.INTEGER,EdmPrimitiveTypeKind.Int32,Integer.TYPE),
	BIGINT(Types.BIGINT,EdmPrimitiveTypeKind.Int64,Long.TYPE),
	FLOAT(Types.FLOAT,EdmPrimitiveTypeKind.Double,Double.TYPE),
	REAL(Types.REAL,EdmPrimitiveTypeKind.Double,Double.TYPE),
	DOUBLE(Types.DOUBLE,EdmPrimitiveTypeKind.Double,Double.TYPE),
	NUMERIC(Types.NUMERIC,EdmPrimitiveTypeKind.Decimal,BigDecimal.class),
	DECIMAL(Types.DECIMAL,EdmPrimitiveTypeKind.Decimal,BigDecimal.class),
	CHAR(Types.CHAR,EdmPrimitiveTypeKind.String,String.class),
	VARCHAR(Types.VARCHAR,EdmPrimitiveTypeKind.String,String.class),
	LONGVARCHAR(Types.LONGNVARCHAR,EdmPrimitiveTypeKind.String,String.class),
	DATE(Types.DATE,EdmPrimitiveTypeKind.Date,Date.class),
	TIME(Types.TIME,EdmPrimitiveTypeKind.TimeOfDay,Time.class),
	TIMESTAMP(Types.TIMESTAMP,EdmPrimitiveTypeKind.Date,Timestamp.class),
	BINARY(Types.BINARY,EdmPrimitiveTypeKind.Binary,byte[].class),
	VARBINARY(Types.VARBINARY,EdmPrimitiveTypeKind.Binary,byte[].class),
	LONGVARBINARY(Types.LONGVARBINARY,EdmPrimitiveTypeKind.Stream,InputStream.class),
	NULL(Types.NULL,EdmPrimitiveTypeKind.String,String.class),
	OTHER(Types.OTHER,EdmPrimitiveTypeKind.Binary,byte[].class),
	JAVA_OBJECT(Types.JAVA_OBJECT,EdmPrimitiveTypeKind.Binary,byte[].class),
	DISTINCT(Types.DISTINCT,EdmPrimitiveTypeKind.Binary,byte[].class),
	STRUCT(Types.STRUCT,EdmPrimitiveTypeKind.Binary,null),
	ARRAY(Types.ARRAY,EdmPrimitiveTypeKind.Binary,null),
	BLOB(Types.BLOB,EdmPrimitiveTypeKind.Stream,InputStream.class),
	CLOB(Types.CLOB,EdmPrimitiveTypeKind.String,null),
	REF(Types.REF,EdmPrimitiveTypeKind.Binary,null),
	DATALINK(Types.DATALINK,EdmPrimitiveTypeKind.Stream,null),
	BOOLEAN(Types.BOOLEAN,EdmPrimitiveTypeKind.Boolean,Boolean.TYPE),
	ROWID(Types.ROWID,EdmPrimitiveTypeKind.Binary,null),
	NCHAR(Types.NCHAR,EdmPrimitiveTypeKind.String,String.class),
	NVARCHAR(Types.NVARCHAR,EdmPrimitiveTypeKind.String,String.class),
	LONGNVARCHAR(Types.LONGNVARCHAR,EdmPrimitiveTypeKind.String,String.class),
	NCLOB(Types.NCLOB,EdmPrimitiveTypeKind.Stream,null),
	SQLXML(Types.SQLXML,EdmPrimitiveTypeKind.Stream,null),
//----- since java 8 ----
	REF_CURSOR(2012,EdmPrimitiveTypeKind.Stream,null),
	TIME_WITH_TIMEZONE(2013,EdmPrimitiveTypeKind.TimeOfDay,null),
	TIMESTAMP_WITH_TIMEZONE(2014,EdmPrimitiveTypeKind.Date,Timestamp.class);
//----------------------	
	private final int jdbcType;
	private EdmPrimitiveTypeKind oDataType;
	private Class<?> javaType;

	
	public EdmPrimitiveTypeKind getODataKind() {
		return oDataType;
	}
	private TypeMap(int jdbcType,EdmPrimitiveTypeKind odataType,Class<?> javaType){
		this.jdbcType = jdbcType;
		this.oDataType = odataType;		
		this.javaType = javaType;
		
	}
	public int getJdbcType() {
		return jdbcType;
	}
	
	public static Class<?> toODataType(EdmPrimitiveTypeKind type){
		for( TypeMap map : TypeMap.values()){
			if(type == map.oDataType && map.javaType != null){
				return map.javaType;
			}
		}
		return null;
	}
	
	public static EdmPrimitiveTypeKind toODataType(Class<?> cls){
		for( TypeMap map : TypeMap.values()){
			if(cls == map.javaType && map.oDataType != null){
				return map.oDataType;
			}
		}
		return null;
	}
	
	public static EdmPrimitiveTypeKind toODataType(int jdbc){
		for( TypeMap map : TypeMap.values()){
			if(jdbc == map.jdbcType){
				return map.oDataType;
			}
		}
		return null;
	}

}
