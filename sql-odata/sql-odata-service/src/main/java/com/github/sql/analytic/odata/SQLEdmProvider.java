package com.github.sql.analytic.odata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.ex.ODataException;

public class SQLEdmProvider extends CsdlAbstractEdmProvider {

	private static final String TABLE_NAME = "TABLE_NAME";
	private static final String DATA_TYPE = "DATA_TYPE";
	private static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";
	private static final String COLUMN_SIZE = "COLUMN_SIZE";
	private static final String COLUMN_DEF = "COLUMN_DEF";
	private static final String NULLABLE = "NULLABLE";
	private static final String COLUMN_NAME = "COLUMN_NAME";
	private static final String CONTAINER_NAME = "Tables";
	private DatabaseMetaData metadata;
	private Map<String, String> schemaMap;

	public SQLEdmProvider(DatabaseMetaData metadata,Map<String,String> schemaMap) {
		this.metadata = metadata;
		this.schemaMap = schemaMap;
	}

	@Override
	public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {

		CsdlEntityType entityType = new CsdlEntityType().setName(entityTypeName.getName()); 
		List<CsdlProperty> properties = new ArrayList<>();
		List<CsdlPropertyRef> key = new ArrayList<>();

		String schema = schemaMap.get(entityTypeName.getNamespace());

		try {
			ResultSet rs = metadata.getColumns(null, schema, entityTypeName.getName(), "%");
			try{
				while(rs.next()){					
					properties.add(createProperty(rs));	
				}
			}finally{
				rs.close();
			}

		} catch (SQLException e) {
			throw new ODataException(e);
		}

		try {
			ResultSet rs = metadata.getPrimaryKeys(null, schema, entityTypeName.getName());
			try{
				while(rs.next()){					
					key.add(createKeyProperty(rs));	
				}
			}finally{
				rs.close();
			}

		} catch (SQLException e) {
			throw new ODataException(e);
		}


		return entityType.setProperties(properties).setKey(key);
	}

	protected CsdlPropertyRef createKeyProperty(ResultSet rs) throws SQLException {
		return new CsdlPropertyRef().setName(rs.getString(COLUMN_NAME));
	}

	protected CsdlProperty createProperty(ResultSet rs) throws SQLException {
		return new CsdlProperty().
				setName(rs.getString(COLUMN_NAME)).
				setNullable(rs.getInt(NULLABLE) == DatabaseMetaData.columnNullable ).
				setDefaultValue(rs.getString(COLUMN_DEF)).
				setMaxLength(rs.getInt(rs.getInt(COLUMN_SIZE))).
				setPrecision(rs.getInt(COLUMN_SIZE)).
				setScale(rs.getInt(DECIMAL_DIGITS)).
				setType(TypeMap.toODataType(rs.getInt(DATA_TYPE)).getFullQualifiedName());

	}

	public List<CsdlSchema> getSchemas() throws ODataException {


		List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();

		for(Entry<String, String> entry : schemaMap.entrySet()){

			CsdlSchema schema = new CsdlSchema();
			schema.setNamespace(entry.getKey());		  
			List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();		 
			schema.setEntityTypes(entityTypes);

			try {
				ResultSet rs = metadata.getTables(null, entry.getValue(),"%",null);
				try{
					while(rs.next()){	
						entityTypes.add(getEntityType(new FullQualifiedName(schema.getNamespace(),
								rs.getString(TABLE_NAME))));							
					}
				}finally{
					rs.close();
				}

			} catch (SQLException e) {
				throw new ODataException(e);
			}

			schema.setEntityContainer(getEntityContainer());
			schemas.add(schema);
		}



		return schemas;
	}


	@Override
	public CsdlEntityContainer getEntityContainer() throws ODataException {

		FullQualifiedName container = new FullQualifiedName("OData.SQL",CONTAINER_NAME);

		List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
		for(Entry<String, String> entry : schemaMap.entrySet()){
			
			try {
				ResultSet rs = metadata.getTables(null, entry.getValue(),"%",null);
				try{
					while(rs.next()){						
						
						entitySets.add(getEntitySet(container , rs.getString(TABLE_NAME) + "Set"));
													
					}
				}finally{
					rs.close();
				}

			} catch (SQLException e) {
				throw new ODataException(e);
			}
			
			
		}

		CsdlEntityContainer entityContainer = new CsdlEntityContainer();
		entityContainer.setName(CONTAINER_NAME);		
		entityContainer.setEntitySets(entitySets);

		return entityContainer;



	}

	@Override
	public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) throws ODataException {
		CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
        entityContainerInfo.setContainerName(entityContainerName);      
        return entityContainerInfo;
	}

	public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) {

		CsdlEntitySet entitySet = new CsdlEntitySet();
		entitySet.setName(entitySetName);
		entitySet.setType(new FullQualifiedName(entityContainer.getNamespace(),entitySetName.substring(0, entitySetName.length() - 3)) );

		return entitySet;
		
	}


}
