package com.github.sql.analytic.odata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlOnDelete;
import org.apache.olingo.commons.api.edm.provider.CsdlOnDeleteAction;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.ex.ODataException;

import com.github.sql.analytic.expression.NamedParameter;
import com.github.sql.analytic.statement.Cursor;
import com.github.sql.analytic.statement.create.table.ColumnDefinition;
import com.github.sql.analytic.transform.ExpressionTransform;
import com.github.sql.analytic.transform.StatementTransform;

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
	private Map<String,Cursor> cursors;


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
		Cursor cursor = cursors.get(entityTypeName.getName());
		if(cursor != null){
			for(ColumnDefinition def : cursor.getColumnDefinitions()){
				CsdlProperty property = new CsdlProperty()
						.setName(def.getColumnName())
						.setType(TypeMap.valueOf(def.getColDataType().getDataType().toUpperCase())
								.getODataKind().getFullQualifiedName());
				properties.add(property );
			}			
			return entityType;
		}
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

		CsdlEntitySet entitySet = null;
		try (ResultSet rs = metadata.getTables(null, null,entitySetName,null)){
			if(rs.next()){	
				entitySet = new CsdlEntitySet();
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
				schema.setFunctions(new ArrayList<CsdlFunction>());				
				schemaContainer.setFunctionImports(new ArrayList<CsdlFunctionImport>());
				List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();		 
				schema.setEntityTypes(entityTypes);
				
				for( String cursor : cursors.keySet()){
					List<CsdlFunction> f =  getFunctions(new FullQualifiedName(schema.getNamespace(), cursor));
					schema.getFunctions().addAll(f);	
					FullQualifiedName containerName = new FullQualifiedName("SQLODataService",schema.getNamespace());
					schemaContainer.getFunctionImports().add(getFunctionImport(containerName,cursor));
					FullQualifiedName cursorType = new FullQualifiedName(schema.getNamespace(),cursor);
					schema.getEntityTypes().add(getEntityType(cursorType));
				}
				
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

	

	@Override
	public List<CsdlFunction> getFunctions(FullQualifiedName functionName) throws ODataException {

		if(cursors == null || cursors.isEmpty()){
			return null;
		}
		Cursor cursor = cursors.get(functionName.getName());
		if(cursor == null){
			return null;
		}

		List<CsdlFunction> functions = new ArrayList<CsdlFunction>();
		List<CsdlParameter> parameters = new ArrayList<>();
		final List<NamedParameter> namedParams = new ArrayList<>();
		StatementTransform transform = new StatementTransform(){

			@Override
			protected ExpressionTransform createExpressionTransform() {				 
				return  new ExpressionTransform(this){

					@Override
					public void visit(NamedParameter namedParameter) {
						namedParams.add(namedParameter);
					}

				};
			}

		};

		transform.visit(cursor.getSelect());

		for( NamedParameter param : namedParams){
			CsdlParameter parameter = new CsdlParameter();		
			parameter.setName(param.getName());			
			parameter.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			parameters.add(parameter);
		}

		final CsdlReturnType returnType = new CsdlReturnType();
		returnType.setCollection(true);
		returnType.setType(new FullQualifiedName(functionName.getNamespace(),functionName.getName()));

		final CsdlFunction function = new CsdlFunction();
		function.setName(functionName.getName())
		.setParameters(parameters)
		.setReturnType(returnType);		
		functions.add(function);

		return functions;
	}

	@Override
	public CsdlFunctionImport getFunctionImport(FullQualifiedName entityContainer, String functionImportName)
			throws ODataException {

		return new CsdlFunctionImport()
				.setName(functionImportName)
				.setFunction(new FullQualifiedName(entityContainer.getName(),functionImportName))                
				.setIncludeInServiceDocument(true);
	}

	public Map<String,Cursor> getCursors() {
		return cursors;
	}

	public void setCursors(Map<String,Cursor> cursors) {
		this.cursors = cursors;
	}

}
