package com.github.sql.analytic.odata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlOnDelete;
import org.apache.olingo.commons.api.edm.provider.CsdlOnDeleteAction;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.ex.ODataException;

public class SQLEdmProvider extends CsdlAbstractEdmProvider {

	private static final String TABLE_SCHEM = "TABLE_SCHEM";
	private static final String TABLE_NAME = "TABLE_NAME";
	private static final String DATA_TYPE = "DATA_TYPE";
	private static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";
	private static final String COLUMN_SIZE = "COLUMN_SIZE";
	private static final String COLUMN_DEF = "COLUMN_DEF";
	private static final String NULLABLE = "NULLABLE";
	private static final String COLUMN_NAME = "COLUMN_NAME";
	private static final String CONTAINER_NAME = "Container";
	private static final String PKTABLE_SCHEM = "PKTABLE_SCHEM"; 
	private static final String PKTABLE_NAME = "PKTABLE_NAME"; 
	private static final String FKTABLE_SCHEM = "FKTABLE_SCHEM"; 
	private static final String FKTABLE_NAME = "FKTABLE_NAME"; 
	private static final String	KEY_SEQ = "KEY_SEQ";  
	private static final String UPDATE_RULE = "UPDATE_RULE"; 			
	private static final String DELETE_RULE = "DELETE_RULE";
	private static final String	FK_NAME = "FK_NAME"; 




	private static FullQualifiedName container = new FullQualifiedName("SQLODataService",CONTAINER_NAME);

	private DatabaseMetaData metadata;


	public SQLEdmProvider(DatabaseMetaData metadata) {
		this.metadata = metadata;		
	}

	@Override
	public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {

		CsdlEntityType entityType = new CsdlEntityType().setName(entityTypeName.getName()); 
		List<CsdlProperty> properties = new ArrayList<>();
		List<CsdlPropertyRef> key = new ArrayList<>();
		List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();


		String schema = entityTypeName.getNamespace();
		try {
			try(ResultSet rs = metadata.getColumns(null, schema, entityTypeName.getName(), "%")){			
				while(rs.next()){					
					properties.add(createProperty(rs));	
				}
			}

			try( ResultSet rs = metadata.getPrimaryKeys(null, schema, entityTypeName.getName()) ){
				while(rs.next()){					
					key.add(createKeyProperty(rs));
				}
			}

			try( ResultSet rs = metadata.getImportedKeys(null, schema, entityTypeName.getName()) ){
				while(rs.next()){		
					if(rs.getInt(KEY_SEQ) == 1){
						navPropList.add(createImporteNavProperty(rs));
					}
				}
			}
			
			try( ResultSet rs = metadata.getExportedKeys(null,schema, entityTypeName.getName()) ){
				while(rs.next()){		
					if(rs.getInt(KEY_SEQ) == 1){
						navPropList.add(createExportedNavProperty(rs));
					}
				}
			}

		} catch (SQLException e) {
			throw new ODataException(e);
		}

		return entityType.setProperties(properties).setKey(key).setNavigationProperties(navPropList);
	}

	private CsdlNavigationProperty createExportedNavProperty(ResultSet rs) throws SQLException {
		return new CsdlNavigationProperty().
				setName(rs.getString(FKTABLE_NAME)).
				setType(new FullQualifiedName(rs.getString(FKTABLE_SCHEM), rs.getString(FKTABLE_NAME))).
				setCollection(true).
				setPartner(rs.getString(FK_NAME)).
				setOnDelete(new CsdlOnDelete().setAction(onDeleteAction(rs.getInt(DELETE_RULE))));

	}

	private CsdlNavigationProperty createImporteNavProperty(ResultSet rs) throws SQLException {
		return new CsdlNavigationProperty().
				setName(rs.getString(FK_NAME)).
				setType(new FullQualifiedName(rs.getString(PKTABLE_SCHEM), rs.getString(PKTABLE_NAME))).
				setCollection(false).
				setPartner(rs.getString(FKTABLE_NAME)).
				setOnDelete(new CsdlOnDelete().setAction(onDeleteAction(rs.getInt(DELETE_RULE))));
				

	}

	private CsdlOnDeleteAction onDeleteAction(int action) {
		switch (action) {
		case DatabaseMetaData.importedKeyCascade:
			return CsdlOnDeleteAction.Cascade;
		case DatabaseMetaData.importedKeySetDefault:
			return CsdlOnDeleteAction.SetDefault;
		case DatabaseMetaData.importedKeySetNull:
			return CsdlOnDeleteAction.SetNull;
		default:
			return CsdlOnDeleteAction.None;
		}
	}

	protected CsdlPropertyRef createKeyProperty(ResultSet rs) throws SQLException {
		return new CsdlPropertyRef().setName(rs.getString(COLUMN_NAME));
	}

	protected CsdlProperty createProperty(ResultSet rs) throws SQLException {
		return new CsdlProperty().
				setName(rs.getString(COLUMN_NAME)).
				setNullable(rs.getInt(NULLABLE) == DatabaseMetaData.columnNullable ).
				setDefaultValue(rs.getString(COLUMN_DEF)).
				setMaxLength(rs.getInt(COLUMN_SIZE)).
				setPrecision(rs.getInt(COLUMN_SIZE)).
				setScale(rs.getInt(DECIMAL_DIGITS)).
				setType(TypeMap.toODataType(rs.getInt(DATA_TYPE)).getFullQualifiedName());

	}
	
	@Override
	public CsdlEntityContainer getEntityContainer() throws ODataException {
		
		CsdlEntityContainer root = new CsdlEntityContainer();
		List<CsdlEntitySet> entitySets = new ArrayList<>();
		root.setEntitySets(entitySets);
		
		for(CsdlSchema schema : getSchemas()){
			for(CsdlEntitySet set : schema.getEntityContainer().getEntitySets()){
				entitySets.add(set);
			}
		}
		
		return root;
	}
	
	public List<CsdlSchema> getSchemas() throws ODataException {

		List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
		
		try(ResultSet schemasRs = metadata.getSchemas()){
			
			while(schemasRs.next()){
				
				CsdlEntityContainer schemaContainer = new CsdlEntityContainer();
				List<CsdlEntitySet> entitySets = new ArrayList<>();
				schemaContainer.setEntitySets(entitySets);
				
				CsdlSchema schema = new CsdlSchema();
				schema.setNamespace(schemasRs.getString(TABLE_SCHEM));		  
				List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();		 
				schema.setEntityTypes(entityTypes);
				try (ResultSet rs = metadata.getTables(null, schema.getNamespace(),"%",null)){
					while(rs.next()){	
						FullQualifiedName entityTypeName = new FullQualifiedName(schema.getNamespace(),
								rs.getString(TABLE_NAME));
						CsdlEntityType entityType = getEntityType(entityTypeName);
						
						entitySets.add( new CsdlEntitySet().
								setType(entityTypeName).
								setName( entityTypeName.getNamespace() + "_"  + entityTypeName.getName() )
								);
						entityTypes.add(entityType);							
					}				
				} 
				schema.setEntityContainer(schemaContainer.setName(schema.getNamespace()));
				schemas.add(schema);
			}
		} catch (SQLException sqle) {
			throw new ODataException(sqle);
		}



		return schemas;
	}





	@Override
	public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) throws ODataException {
		CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
		entityContainerInfo.setContainerName( container );      
		return entityContainerInfo;
	}




}
