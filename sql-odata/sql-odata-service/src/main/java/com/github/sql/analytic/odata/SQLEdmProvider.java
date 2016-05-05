package com.github.sql.analytic.odata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlOnDelete;
import org.apache.olingo.commons.api.edm.provider.CsdlOnDeleteAction;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.ex.ODataException;

public class SQLEdmProvider extends CsdlAbstractEdmProvider {

	public static final String TABLE_SCHEM = "TABLE_SCHEM";
	public static final String TABLE_NAME = "TABLE_NAME";
	public static final String DATA_TYPE = "DATA_TYPE";
	public static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";
	public static final String COLUMN_SIZE = "COLUMN_SIZE";
	public static final String COLUMN_DEF = "COLUMN_DEF";
	public static final String NULLABLE = "NULLABLE";
	public static final String COLUMN_NAME = "COLUMN_NAME";
	public static final String CONTAINER_NAME = "Container";
	public static final String PKTABLE_SCHEM = "PKTABLE_SCHEM"; 
	public static final String PKTABLE_NAME = "PKTABLE_NAME"; 
	public static final String FKTABLE_SCHEM = "FKTABLE_SCHEM"; 
	public static final String FKTABLE_NAME = "FKTABLE_NAME"; 
	public static final String KEY_SEQ = "KEY_SEQ";
	public static final String DELETE_RULE = "DELETE_RULE";
	public static final String FK_NAME = "FK_NAME";
	public static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";
	public static final String FKCOLUMN_NAME = "FKCOLUMN_NAME"; 

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
				setName(rs.getString(FK_NAME)).
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
				setPartner(rs.getString(FK_NAME)).
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

	@Override
	public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) throws ODataException {

		CsdlEntitySet entitySet = new CsdlEntitySet();
		try (ResultSet rs = metadata.getTables(null, null,entitySetName,null)){
			if(rs.next()){	
				FullQualifiedName entityTypeName = new FullQualifiedName(rs.getString(TABLE_SCHEM),
						rs.getString(TABLE_NAME));										
				entitySet.setType(entityTypeName).setName(entityTypeName.getName());
				addBindings(entityTypeName, entitySet);
			}	

		} catch (SQLException e) {
			throw new ODataException(e);
		} 
		return entitySet;

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
						CsdlEntitySet entitySet = new CsdlEntitySet();
						entitySets.add( entitySet.
								setType(entityTypeName).
								setName( entityTypeName.getName() )
								);
						addBindings(entityTypeName, entitySet);
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

	private void addBindings(FullQualifiedName entityTypeName, CsdlEntitySet entitySet)
			throws SQLException {
		List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();
		entitySet.setNavigationPropertyBindings(navPropBindingList);		
		try( ResultSet rs = metadata.getImportedKeys(null, null, entityTypeName.getName()) ){						
			while(rs.next()){		
				if(rs.getInt(KEY_SEQ) == 1){
					CsdlNavigationPropertyBinding navPropBinding = new CsdlNavigationPropertyBinding();
					navPropBinding.setPath(rs.getString(FK_NAME)); 
					navPropBinding.setTarget(rs.getString(PKTABLE_NAME)); 
					navPropBindingList.add(navPropBinding);						
				}
			}
		}
		try( ResultSet rs = metadata.getExportedKeys(null, null, entityTypeName.getName()) ){						
			while(rs.next()){		
				if(rs.getInt(KEY_SEQ) == 1){
					CsdlNavigationPropertyBinding navPropBinding = new CsdlNavigationPropertyBinding();
					navPropBinding.setPath(rs.getString(FK_NAME)); 
					navPropBinding.setTarget(rs.getString(FKTABLE_NAME)); 
					navPropBindingList.add(navPropBinding);						
				}
			}
		}
	}

	@Override
	public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) throws ODataException {
		CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
		entityContainerInfo.setContainerName( container );      
		return entityContainerInfo;
	}
}
