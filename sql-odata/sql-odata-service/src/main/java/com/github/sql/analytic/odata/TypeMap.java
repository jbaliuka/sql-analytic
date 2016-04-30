package com.github.sql.analytic.odata;

import java.sql.Types;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;

public enum TypeMap {
	
	BIT(Types.BIT,EdmPrimitiveTypeKind.Byte),	
	TINYINT(Types.TINYINT,EdmPrimitiveTypeKind.Byte),
	SMALLINT(Types.SMALLINT,EdmPrimitiveTypeKind.Int16),
	INTEGER(Types.INTEGER,EdmPrimitiveTypeKind.Int32),
	BIGINT(Types.BIGINT,EdmPrimitiveTypeKind.Int64),
	FLOAT(Types.FLOAT,EdmPrimitiveTypeKind.Double),
	REAL(Types.REAL,EdmPrimitiveTypeKind.Double),
	DOUBLE(Types.DOUBLE,EdmPrimitiveTypeKind.Double),
	NUMERIC(Types.NUMERIC,EdmPrimitiveTypeKind.Decimal),
	DECIMAL(Types.DECIMAL,EdmPrimitiveTypeKind.Decimal),
	CHAR(Types.CHAR,EdmPrimitiveTypeKind.String),
	VARCHAR(Types.VARCHAR,EdmPrimitiveTypeKind.String),
	LONGVARCHAR(Types.LONGNVARCHAR,EdmPrimitiveTypeKind.String),
	DATE(Types.DATE,EdmPrimitiveTypeKind.Date),
	TIME(Types.TIME,EdmPrimitiveTypeKind.TimeOfDay),
	TIMESTAMP(Types.TIMESTAMP,EdmPrimitiveTypeKind.Date),
	BINARY(Types.BINARY,EdmPrimitiveTypeKind.Binary),
	VARBINARY(Types.VARBINARY,EdmPrimitiveTypeKind.Binary),
	LONGVARBINARY(Types.LONGVARBINARY,EdmPrimitiveTypeKind.Stream),
	NULL(Types.NULL,EdmPrimitiveTypeKind.Binary),
	OTHER(Types.OTHER,EdmPrimitiveTypeKind.Binary),
	JAVA_OBJECT(Types.JAVA_OBJECT,EdmPrimitiveTypeKind.Binary),
	DISTINCT(Types.DISTINCT,EdmPrimitiveTypeKind.Binary),
	STRUCT(Types.STRUCT,EdmPrimitiveTypeKind.Binary),
	ARRAY(Types.ARRAY,EdmPrimitiveTypeKind.Binary),
	BLOB(Types.BLOB,EdmPrimitiveTypeKind.String),
	CLOB(Types.CLOB,EdmPrimitiveTypeKind.String),
	REF(Types.REF,EdmPrimitiveTypeKind.Binary),
	DATALINK(Types.DATALINK,EdmPrimitiveTypeKind.Stream),
	BOOLEAN(Types.BOOLEAN,EdmPrimitiveTypeKind.Boolean),
	ROWID(Types.ROWID,EdmPrimitiveTypeKind.Binary),
	NCHAR(Types.NCHAR,EdmPrimitiveTypeKind.String),
	NVARCHAR(Types.NVARCHAR,EdmPrimitiveTypeKind.String),
	LONGNVARCHAR(Types.LONGNVARCHAR,EdmPrimitiveTypeKind.String),
	NCLOB(Types.NCLOB,EdmPrimitiveTypeKind.Stream),
	SQLXML(Types.SQLXML,EdmPrimitiveTypeKind.Stream),
//----- since java 8 ----
	REF_CURSOR(2012,EdmPrimitiveTypeKind.Stream),
	TIME_WITH_TIMEZONE(2013,EdmPrimitiveTypeKind.TimeOfDay),
	TIMESTAMP_WITH_TIMEZONE(2014,EdmPrimitiveTypeKind.Date);
//----------------------	
	private final int jdbcType;
	private EdmPrimitiveTypeKind oDataType;

	
	public EdmPrimitiveTypeKind getODataKind() {
		return oDataType;
	}
	private TypeMap(int jdbcType,EdmPrimitiveTypeKind odataType){
		this.jdbcType = jdbcType;
		this.oDataType = odataType;		
		
	}
	public int getJdbcType() {
		return jdbcType;
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
