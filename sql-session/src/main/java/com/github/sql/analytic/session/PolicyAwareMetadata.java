package com.github.sql.analytic.session;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.transform.policy.Policy;

public class PolicyAwareMetadata implements DatabaseMetaData{
	
	public static final String FKTABLE_NAME = "FKTABLE_NAME";
	public static final String PKTABLE_NAME = "PKTABLE_NAME";
	public static final String COLUMN_NAME = "COLUMN_NAME";
	public static final String TABLE_SCHEM = "TABLE_SCHEM";
	public static final String TABLE_NAME = "TABLE_NAME";
	public static final String DATA_TYPE = "DATA_TYPE";
	public static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";
	public static final String COLUMN_SIZE = "COLUMN_SIZE";
	public static final String COLUMN_DEF = "COLUMN_DEF";
	public static final String NULLABLE = "NULLABLE";	
	public static final String CONTAINER_NAME = "Container";
	public static final String PKTABLE_SCHEM = "PKTABLE_SCHEM"; 
	public static final String FKTABLE_SCHEM = "FKTABLE_SCHEM";	 
	public static final String KEY_SEQ = "KEY_SEQ";
	public static final String DELETE_RULE = "DELETE_RULE";
	public static final String FK_NAME = "FK_NAME";
	public static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";
	public static final String FKCOLUMN_NAME = "FKCOLUMN_NAME"; 


	class EmptyResultSet extends SQLResultSet{

		public EmptyResultSet() {
			super(null, null);			
		}
		@Override
		public boolean next() throws SQLException {
			return false;
		}
		
	}
	class ColumnResultSet extends SQLResultSet {
		private String colFieldName;
		private String tabFieldName;
		private ColumnResultSet(ResultSet rs,String colFieldName,String tabFieldName) {
			super(null, rs);
			this.colFieldName = colFieldName;
			this.tabFieldName = tabFieldName;
		}
		@Override
		public boolean next() throws SQLException {
			while(super.next()){
				String table = getString(tabFieldName);
				String col = getString(colFieldName);
				for(CreatePolicy pol : policy.getPolicyList()){
					if(table.equalsIgnoreCase(pol.getTable().getName())){
						if(pol.getColumns() == null){
							return true;
						}else {
							for(Column c : pol.getColumns() ){
								if(c.getColumnName().equalsIgnoreCase(col)){
									return true;
								}
							}
						}
					}
				}
			}
			return false;
		}
	}

	class TableResultSet extends SQLResultSet {
		private String fieldName;
		private TableResultSet(ResultSet rs,String fieldName) {
			super(null, rs);
			this.fieldName = fieldName;
		}

		@Override
		public boolean next() throws SQLException {
			while(super.next()){
				String table = getString(fieldName);				
				if(policy.hasPolicy(new Table(null,table))){
					return true;
				}
			}
			return false;
		}
	}

	  class SchemasResultSet extends SQLResultSet {
		private SchemasResultSet(ResultSet schemas) {
			super(null, schemas);			
		}
		@Override
		public boolean next() throws SQLException {				
			while(super.next()){
				String schema = getString(TABLE_SCHEM);
				if(schema.equalsIgnoreCase(session.getContext().getDefaultSchema())){
					return true;
				}else {
					for( CreatePolicy policy : session.getPolicy()){
						if(schema.equalsIgnoreCase(policy.getTable().getSchemaName())){
							return true;
						}
					}						
				}
				
			}
			return false;
		}
	}

	private SQLSession session;
    private Policy policy;
    private DatabaseMetaData metadata;
    
    public PolicyAwareMetadata(SQLSession session,DatabaseMetaData metadata){
		this.session = session;
		this.metadata = metadata;
		policy = session.createPolicy();
	}

	
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return metadata.unwrap(iface);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return metadata.isWrapperFor(iface);
	}

	public boolean allProceduresAreCallable() throws SQLException {
		return metadata.allProceduresAreCallable();
	}

	public boolean allTablesAreSelectable() throws SQLException {
		return metadata.allTablesAreSelectable();
	}

	public String getURL() throws SQLException {
		return metadata.getURL();
	}

	public String getUserName() throws SQLException {
		return metadata.getUserName();
	}

	public boolean isReadOnly() throws SQLException {
		return metadata.isReadOnly();
	}

	public boolean nullsAreSortedHigh() throws SQLException {
		return metadata.nullsAreSortedHigh();
	}

	public boolean nullsAreSortedLow() throws SQLException {
		return metadata.nullsAreSortedLow();
	}

	public boolean nullsAreSortedAtStart() throws SQLException {
		return metadata.nullsAreSortedAtStart();
	}

	public boolean nullsAreSortedAtEnd() throws SQLException {
		return metadata.nullsAreSortedAtEnd();
	}

	public String getDatabaseProductName() throws SQLException {
		return metadata.getDatabaseProductName();
	}

	public String getDatabaseProductVersion() throws SQLException {
		return metadata.getDatabaseProductVersion();
	}

	public String getDriverName() throws SQLException {
		return metadata.getDriverName();
	}

	public String getDriverVersion() throws SQLException {
		return metadata.getDriverVersion();
	}

	public int getDriverMajorVersion() {
		return metadata.getDriverMajorVersion();
	}

	public int getDriverMinorVersion() {
		return metadata.getDriverMinorVersion();
	}

	public boolean usesLocalFiles() throws SQLException {
		return metadata.usesLocalFiles();
	}

	public boolean usesLocalFilePerTable() throws SQLException {
		return metadata.usesLocalFilePerTable();
	}

	public boolean supportsMixedCaseIdentifiers() throws SQLException {
		return metadata.supportsMixedCaseIdentifiers();
	}

	public boolean storesUpperCaseIdentifiers() throws SQLException {
		return metadata.storesUpperCaseIdentifiers();
	}

	public boolean storesLowerCaseIdentifiers() throws SQLException {
		return metadata.storesLowerCaseIdentifiers();
	}

	public boolean storesMixedCaseIdentifiers() throws SQLException {
		return metadata.storesMixedCaseIdentifiers();
	}

	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		return metadata.supportsMixedCaseQuotedIdentifiers();
	}

	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		return metadata.storesUpperCaseQuotedIdentifiers();
	}

	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		return metadata.storesLowerCaseQuotedIdentifiers();
	}

	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		return metadata.storesMixedCaseQuotedIdentifiers();
	}

	public String getIdentifierQuoteString() throws SQLException {
		return metadata.getIdentifierQuoteString();
	}

	public String getSQLKeywords() throws SQLException {
		return metadata.getSQLKeywords();
	}

	public String getNumericFunctions() throws SQLException {
		return metadata.getNumericFunctions();
	}

	public String getStringFunctions() throws SQLException {
		return metadata.getStringFunctions();
	}

	public String getSystemFunctions() throws SQLException {
		return metadata.getSystemFunctions();
	}

	public String getTimeDateFunctions() throws SQLException {
		return metadata.getTimeDateFunctions();
	}

	public String getSearchStringEscape() throws SQLException {
		return metadata.getSearchStringEscape();
	}

	public String getExtraNameCharacters() throws SQLException {
		return metadata.getExtraNameCharacters();
	}

	public boolean supportsAlterTableWithAddColumn() throws SQLException {
		return metadata.supportsAlterTableWithAddColumn();
	}

	public boolean supportsAlterTableWithDropColumn() throws SQLException {
		return metadata.supportsAlterTableWithDropColumn();
	}

	public boolean supportsColumnAliasing() throws SQLException {
		return metadata.supportsColumnAliasing();
	}

	public boolean nullPlusNonNullIsNull() throws SQLException {
		return metadata.nullPlusNonNullIsNull();
	}

	public boolean supportsConvert() throws SQLException {
		return metadata.supportsConvert();
	}

	public boolean supportsConvert(int fromType, int toType) throws SQLException {
		return metadata.supportsConvert(fromType, toType);
	}

	public boolean supportsTableCorrelationNames() throws SQLException {
		return metadata.supportsTableCorrelationNames();
	}

	public boolean supportsDifferentTableCorrelationNames() throws SQLException {
		return metadata.supportsDifferentTableCorrelationNames();
	}

	public boolean supportsExpressionsInOrderBy() throws SQLException {
		return metadata.supportsExpressionsInOrderBy();
	}

	public boolean supportsOrderByUnrelated() throws SQLException {
		return metadata.supportsOrderByUnrelated();
	}

	public boolean supportsGroupBy() throws SQLException {
		return metadata.supportsGroupBy();
	}

	public boolean supportsGroupByUnrelated() throws SQLException {
		return metadata.supportsGroupByUnrelated();
	}

	public boolean supportsGroupByBeyondSelect() throws SQLException {
		return metadata.supportsGroupByBeyondSelect();
	}

	public boolean supportsLikeEscapeClause() throws SQLException {
		return metadata.supportsLikeEscapeClause();
	}

	public boolean supportsMultipleResultSets() throws SQLException {
		return metadata.supportsMultipleResultSets();
	}

	public boolean supportsMultipleTransactions() throws SQLException {
		return metadata.supportsMultipleTransactions();
	}

	public boolean supportsNonNullableColumns() throws SQLException {
		return metadata.supportsNonNullableColumns();
	}

	public boolean supportsMinimumSQLGrammar() throws SQLException {
		return metadata.supportsMinimumSQLGrammar();
	}

	public boolean supportsCoreSQLGrammar() throws SQLException {
		return metadata.supportsCoreSQLGrammar();
	}

	public boolean supportsExtendedSQLGrammar() throws SQLException {
		return metadata.supportsExtendedSQLGrammar();
	}

	public boolean supportsANSI92EntryLevelSQL() throws SQLException {
		return metadata.supportsANSI92EntryLevelSQL();
	}

	public boolean supportsANSI92IntermediateSQL() throws SQLException {
		return metadata.supportsANSI92IntermediateSQL();
	}

	public boolean supportsANSI92FullSQL() throws SQLException {
		return metadata.supportsANSI92FullSQL();
	}

	public boolean supportsIntegrityEnhancementFacility() throws SQLException {
		return metadata.supportsIntegrityEnhancementFacility();
	}

	public boolean supportsOuterJoins() throws SQLException {
		return metadata.supportsOuterJoins();
	}

	public boolean supportsFullOuterJoins() throws SQLException {
		return metadata.supportsFullOuterJoins();
	}

	public boolean supportsLimitedOuterJoins() throws SQLException {
		return metadata.supportsLimitedOuterJoins();
	}

	public String getSchemaTerm() throws SQLException {
		return metadata.getSchemaTerm();
	}

	public String getProcedureTerm() throws SQLException {
		return metadata.getProcedureTerm();
	}

	public String getCatalogTerm() throws SQLException {
		return metadata.getCatalogTerm();
	}

	public boolean isCatalogAtStart() throws SQLException {
		return metadata.isCatalogAtStart();
	}

	public String getCatalogSeparator() throws SQLException {
		return metadata.getCatalogSeparator();
	}

	public boolean supportsSchemasInDataManipulation() throws SQLException {
		return metadata.supportsSchemasInDataManipulation();
	}

	public boolean supportsSchemasInProcedureCalls() throws SQLException {
		return metadata.supportsSchemasInProcedureCalls();
	}

	public boolean supportsSchemasInTableDefinitions() throws SQLException {
		return metadata.supportsSchemasInTableDefinitions();
	}

	public boolean supportsSchemasInIndexDefinitions() throws SQLException {
		return metadata.supportsSchemasInIndexDefinitions();
	}

	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		return metadata.supportsSchemasInPrivilegeDefinitions();
	}

	public boolean supportsCatalogsInDataManipulation() throws SQLException {
		return metadata.supportsCatalogsInDataManipulation();
	}

	public boolean supportsCatalogsInProcedureCalls() throws SQLException {
		return metadata.supportsCatalogsInProcedureCalls();
	}

	public boolean supportsCatalogsInTableDefinitions() throws SQLException {
		return metadata.supportsCatalogsInTableDefinitions();
	}

	public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		return metadata.supportsCatalogsInIndexDefinitions();
	}

	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		return metadata.supportsCatalogsInPrivilegeDefinitions();
	}

	public boolean supportsPositionedDelete() throws SQLException {
		return metadata.supportsPositionedDelete();
	}

	public boolean supportsPositionedUpdate() throws SQLException {
		return metadata.supportsPositionedUpdate();
	}

	public boolean supportsSelectForUpdate() throws SQLException {
		return metadata.supportsSelectForUpdate();
	}

	public boolean supportsStoredProcedures() throws SQLException {
		return metadata.supportsStoredProcedures();
	}

	public boolean supportsSubqueriesInComparisons() throws SQLException {
		return metadata.supportsSubqueriesInComparisons();
	}

	public boolean supportsSubqueriesInExists() throws SQLException {
		return metadata.supportsSubqueriesInExists();
	}

	public boolean supportsSubqueriesInIns() throws SQLException {
		return metadata.supportsSubqueriesInIns();
	}

	public boolean supportsSubqueriesInQuantifieds() throws SQLException {
		return metadata.supportsSubqueriesInQuantifieds();
	}

	public boolean supportsCorrelatedSubqueries() throws SQLException {
		return metadata.supportsCorrelatedSubqueries();
	}

	public boolean supportsUnion() throws SQLException {
		return metadata.supportsUnion();
	}

	public boolean supportsUnionAll() throws SQLException {
		return metadata.supportsUnionAll();
	}

	public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		return metadata.supportsOpenCursorsAcrossCommit();
	}

	public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		return metadata.supportsOpenCursorsAcrossRollback();
	}

	public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		return metadata.supportsOpenStatementsAcrossCommit();
	}

	public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		return metadata.supportsOpenStatementsAcrossRollback();
	}

	public int getMaxBinaryLiteralLength() throws SQLException {
		return metadata.getMaxBinaryLiteralLength();
	}

	public int getMaxCharLiteralLength() throws SQLException {
		return metadata.getMaxCharLiteralLength();
	}

	public int getMaxColumnNameLength() throws SQLException {
		return metadata.getMaxColumnNameLength();
	}

	public int getMaxColumnsInGroupBy() throws SQLException {
		return metadata.getMaxColumnsInGroupBy();
	}

	public int getMaxColumnsInIndex() throws SQLException {
		return metadata.getMaxColumnsInIndex();
	}

	public int getMaxColumnsInOrderBy() throws SQLException {
		return metadata.getMaxColumnsInOrderBy();
	}

	public int getMaxColumnsInSelect() throws SQLException {
		return metadata.getMaxColumnsInSelect();
	}

	public int getMaxColumnsInTable() throws SQLException {
		return metadata.getMaxColumnsInTable();
	}

	public int getMaxConnections() throws SQLException {
		return metadata.getMaxConnections();
	}

	public int getMaxCursorNameLength() throws SQLException {
		return metadata.getMaxCursorNameLength();
	}

	public int getMaxIndexLength() throws SQLException {
		return metadata.getMaxIndexLength();
	}

	public int getMaxSchemaNameLength() throws SQLException {
		return metadata.getMaxSchemaNameLength();
	}

	public int getMaxProcedureNameLength() throws SQLException {
		return metadata.getMaxProcedureNameLength();
	}

	public int getMaxCatalogNameLength() throws SQLException {
		return metadata.getMaxCatalogNameLength();
	}

	public int getMaxRowSize() throws SQLException {
		return metadata.getMaxRowSize();
	}

	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		return metadata.doesMaxRowSizeIncludeBlobs();
	}

	public int getMaxStatementLength() throws SQLException {
		return metadata.getMaxStatementLength();
	}

	public int getMaxStatements() throws SQLException {
		return metadata.getMaxStatements();
	}

	public int getMaxTableNameLength() throws SQLException {
		return metadata.getMaxTableNameLength();
	}

	public int getMaxTablesInSelect() throws SQLException {
		return metadata.getMaxTablesInSelect();
	}

	public int getMaxUserNameLength() throws SQLException {
		return metadata.getMaxUserNameLength();
	}

	public int getDefaultTransactionIsolation() throws SQLException {
		return metadata.getDefaultTransactionIsolation();
	}

	public boolean supportsTransactions() throws SQLException {
		return metadata.supportsTransactions();
	}

	public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
		return metadata.supportsTransactionIsolationLevel(level);
	}

	public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
		return metadata.supportsDataDefinitionAndDataManipulationTransactions();
	}

	public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
		return metadata.supportsDataManipulationTransactionsOnly();
	}

	public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		return metadata.dataDefinitionCausesTransactionCommit();
	}

	public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		return metadata.dataDefinitionIgnoredInTransactions();
	}

	public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern)
			throws SQLException {
		return metadata.getProcedures(catalog, schemaPattern, procedureNamePattern);
	}

	public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern,
			String columnNamePattern) throws SQLException {
		return metadata.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern);
	}

	public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
			throws SQLException {
		ResultSet tables = metadata.getTables(catalog, schemaPattern, tableNamePattern, types);  
		return new TableResultSet(tables,TABLE_NAME);
	}

	public ResultSet getSchemas() throws SQLException {		
		return new SchemasResultSet(metadata.getSchemas());
	}

	public ResultSet getCatalogs() throws SQLException {
		return metadata.getCatalogs();
	}

	public ResultSet getTableTypes() throws SQLException {
		return metadata.getTableTypes();
	}

	public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
			throws SQLException {
		ResultSet columns = metadata.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
		return new ColumnResultSet(columns,COLUMN_NAME,TABLE_NAME);
	}

	public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern)
			throws SQLException {
		if(!policy.hasPolicy(new Table(schema,table))){
			return new EmptyResultSet();
		}
		return new ColumnResultSet(metadata.getColumnPrivileges(catalog, schema, table, columnNamePattern),COLUMN_NAME,TABLE_NAME);
	}

	public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern)
			throws SQLException {
		return new TableResultSet(metadata.getTablePrivileges(catalog, schemaPattern, tableNamePattern),TABLE_NAME);
	}

	public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable)
			throws SQLException {
		if(!policy.hasPolicy(new Table(schema,table))){
			return new EmptyResultSet();
		}
		return metadata.getBestRowIdentifier(catalog, schema, table, scope, nullable);
	}

	public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
		if(!policy.hasPolicy(new Table(schema,table))){
			return new EmptyResultSet();
		}
		return metadata.getVersionColumns(catalog, schema, table);
	}

	public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
		if(!policy.hasPolicy(new Table(schema,table))){
			return new EmptyResultSet();
		}
		return metadata.getPrimaryKeys(catalog, schema, table);
	}

	public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
		if(!policy.hasPolicy(new Table(schema,table))){
			return new EmptyResultSet();
		}
		return new TableResultSet(metadata.getImportedKeys(catalog, schema, table),PKTABLE_NAME);
	}

	public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
		if(!policy.hasPolicy(new Table(schema,table))){
			return new EmptyResultSet();
		}
		return new TableResultSet(metadata.getExportedKeys(catalog, schema, table),FKTABLE_NAME);
	}

	public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable,
			String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
		
		if(!policy.hasPolicy(new Table(parentSchema,parentTable))){
			return new EmptyResultSet();
		}
		if(!policy.hasPolicy(new Table(foreignSchema,foreignTable))){
			return new EmptyResultSet();
		}
		return metadata.getCrossReference(parentCatalog, parentSchema, parentTable, 
				foreignCatalog, foreignSchema,foreignTable);
	}

	public ResultSet getTypeInfo() throws SQLException {
		return metadata.getTypeInfo();
	}

	public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate)
			throws SQLException {
		if(!policy.hasPolicy(new Table(schema,table))){
			return new EmptyResultSet();
		}
		return metadata.getIndexInfo(catalog, schema, table, unique, approximate);
	}

	public boolean supportsResultSetType(int type) throws SQLException {
		return metadata.supportsResultSetType(type);
	}

	public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
		return metadata.supportsResultSetConcurrency(type, concurrency);
	}

	public boolean ownUpdatesAreVisible(int type) throws SQLException {
		return metadata.ownUpdatesAreVisible(type);
	}

	public boolean ownDeletesAreVisible(int type) throws SQLException {
		return metadata.ownDeletesAreVisible(type);
	}

	public boolean ownInsertsAreVisible(int type) throws SQLException {
		return metadata.ownInsertsAreVisible(type);
	}

	public boolean othersUpdatesAreVisible(int type) throws SQLException {
		return metadata.othersUpdatesAreVisible(type);
	}

	public boolean othersDeletesAreVisible(int type) throws SQLException {
		return metadata.othersDeletesAreVisible(type);
	}

	public boolean othersInsertsAreVisible(int type) throws SQLException {
		return metadata.othersInsertsAreVisible(type);
	}

	public boolean updatesAreDetected(int type) throws SQLException {
		return metadata.updatesAreDetected(type);
	}

	public boolean deletesAreDetected(int type) throws SQLException {
		return metadata.deletesAreDetected(type);
	}

	public boolean insertsAreDetected(int type) throws SQLException {
		return metadata.insertsAreDetected(type);
	}

	public boolean supportsBatchUpdates() throws SQLException {
		return metadata.supportsBatchUpdates();
	}

	public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types)
			throws SQLException {
		return metadata.getUDTs(catalog, schemaPattern, typeNamePattern, types);
	}

	public Connection getConnection() throws SQLException {
		return session;
	}

	public boolean supportsSavepoints() throws SQLException {
		return metadata.supportsSavepoints();
	}

	public boolean supportsNamedParameters() throws SQLException {
		return metadata.supportsNamedParameters();
	}

	public boolean supportsMultipleOpenResults() throws SQLException {
		return metadata.supportsMultipleOpenResults();
	}

	public boolean supportsGetGeneratedKeys() throws SQLException {
		return metadata.supportsGetGeneratedKeys();
	}

	public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
		return metadata.getSuperTypes(catalog, schemaPattern, typeNamePattern);
	}

	public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
		return new TableResultSet(metadata.getSuperTables(catalog, schemaPattern, tableNamePattern),TABLE_NAME);
	}

	public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern,
			String attributeNamePattern) throws SQLException {
		return metadata.getAttributes(catalog, schemaPattern, typeNamePattern, attributeNamePattern);
	}

	public boolean supportsResultSetHoldability(int holdability) throws SQLException {
		return metadata.supportsResultSetHoldability(holdability);
	}

	public int getResultSetHoldability() throws SQLException {
		return metadata.getResultSetHoldability();
	}

	public int getDatabaseMajorVersion() throws SQLException {
		return metadata.getDatabaseMajorVersion();
	}

	public int getDatabaseMinorVersion() throws SQLException {
		return metadata.getDatabaseMinorVersion();
	}

	public int getJDBCMajorVersion() throws SQLException {
		return metadata.getJDBCMajorVersion();
	}

	public int getJDBCMinorVersion() throws SQLException {
		return metadata.getJDBCMinorVersion();
	}

	public int getSQLStateType() throws SQLException {
		return metadata.getSQLStateType();
	}

	public boolean locatorsUpdateCopy() throws SQLException {
		return metadata.locatorsUpdateCopy();
	}

	public boolean supportsStatementPooling() throws SQLException {
		return metadata.supportsStatementPooling();
	}

	public RowIdLifetime getRowIdLifetime() throws SQLException {
		return metadata.getRowIdLifetime();
	}

	public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
		return new SchemasResultSet(metadata.getSchemas(catalog, schemaPattern));
	}

	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
		return metadata.supportsStoredFunctionsUsingCallSyntax();
	}

	public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
		return metadata.autoCommitFailureClosesAllResultSets();
	}

	public ResultSet getClientInfoProperties() throws SQLException {
		return metadata.getClientInfoProperties();
	}

	public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern)
			throws SQLException {
		return metadata.getFunctions(catalog, schemaPattern, functionNamePattern);
	}

	public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern,
			String columnNamePattern) throws SQLException {
		return metadata.getFunctionColumns(catalog, schemaPattern, functionNamePattern, columnNamePattern);
	}

	public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern,
			String columnNamePattern) throws SQLException {
		return metadata.getPseudoColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
	}

	public boolean generatedKeyAlwaysReturned() throws SQLException {
		return metadata.generatedKeyAlwaysReturned();
	}

}
