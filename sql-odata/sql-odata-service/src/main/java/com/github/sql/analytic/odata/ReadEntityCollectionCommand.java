package com.github.sql.analytic.odata;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;

import com.github.sql.analytic.expression.BinaryExpression;
import com.github.sql.analytic.expression.NamedParameter;
import com.github.sql.analytic.expression.SQLExpression;
import com.github.sql.analytic.expression.operators.conditional.AndExpression;
import com.github.sql.analytic.expression.operators.relational.EqualsTo;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.session.SQLSession;
import com.github.sql.analytic.statement.select.FromItem;
import com.github.sql.analytic.statement.select.Join;
import com.github.sql.analytic.statement.select.Select;
import static com.github.sql.analytic.session.PolicyAwareMetadata.*;

public class ReadEntityCollectionCommand extends ReadCommand{

	private UriInfoResource uriInfo;	
	private int index = 0;
	private EdmEntityType entityType;
	

	public ReadEntityCollectionCommand(UriInfoResource uriInfo) {		
		this.uriInfo = uriInfo;		
	}

	@Override
	public ResultSetIterator execute(SQLSession connection) throws 	ODataApplicationException{

		try{
			processUriResource(connection);
			processUriInfo(uriInfo, getEntityType(),  getCurrentAlias());
			PreparedStatement statement = connection.create(new Select().setSelectBody(getSelect()), getStatementParams() );			
			ResultSet rs = statement.executeQuery();			
			statement.closeOnCompletion();
			return new ResultSetIterator(connection, rs, getEntityType(),uriInfo.getExpandOption());

		} catch (SQLException | EdmPrimitiveTypeException e) {
			throw internalError(e);
		}
	}

	private String getCurrentAlias() {
		return getEntityType().getName() + "_" + index;
	}

	private void processUriResource(SQLSession connection) throws ODataApplicationException, EdmPrimitiveTypeException {

		for(UriResource segment : uriInfo.getUriResourceParts() ){
			index++;
			if(segment instanceof UriResourceNavigation){				
				UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) segment;
				EdmNavigationProperty edmNavigationProperty = uriResourceNavigation.getProperty();
				Join join = appendFrom(edmNavigationProperty.getType());
				appendOn(connection,join,edmNavigationProperty.getName(),entityType,edmNavigationProperty.getType());
				appendWhere(uriResourceNavigation.getKeyPredicates());
				entityType = edmNavigationProperty.getType();
			}else if (segment instanceof UriResourceEntitySet){				
				UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) segment;
				entityType = uriResourceEntitySet.getEntitySet().getEntityType();			
				appendFrom(entityType);
				appendWhere(uriResourceEntitySet.getKeyPredicates());			
			}else {
				throw new ODataApplicationException(segment.toString(), HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), 
						Locale.ROOT);
			}			
		}
	}


	private void appendOn(SQLSession connection,Join join, String fKname, EdmEntityType leftType, EdmEntityType rightType) throws ODataApplicationException {

		boolean found = false;
		try(ResultSet rs = connection.getMetaData().getImportedKeys(null, rightType.getNamespace(), rightType.getName())){			
			while(rs.next()){
				if(fKname.equalsIgnoreCase(rs.getString(FK_NAME))){
					found = true;
					setOn(join, rs,index - 1,index);					
				}
			}
			if(!found){
				try(ResultSet right = connection.getMetaData().getExportedKeys(null, rightType.getNamespace(), rightType.getName())){					
					while(right.next()){
						if(fKname.equalsIgnoreCase(right.getString(FK_NAME))){
							found = true;
							setOn(join, right,index,index - 1 );					
						}
					}
				}
			}

		} catch (SQLException e) {
			throw internalError(e);
		}		
	}

	private void setOn(Join join, ResultSet rs, int pkIndex,int fkIndex) throws SQLException {

		Table pkTable = new Table(null, rs.getString(PKTABLE_NAME)+ "_" + pkIndex);
		Column pfCol = new Column(pkTable,rs.getString(PKCOLUMN_NAME));

		Table fkTable = new Table(null, rs.getString(FKTABLE_NAME) + "_" + fkIndex);
		Column fkCol = new Column(fkTable,rs.getString(FKCOLUMN_NAME));

		BinaryExpression eq = new EqualsTo().setLeftExpression(pfCol).setRightExpression(fkCol);

		if(join.getOnExpression() == null ){
			join.setOnExpression(eq);
		}else {
			join.setOnExpression(new AndExpression(join.getOnExpression(),eq));
		}
	}

	private Join appendFrom(EdmEntityType entity) {

		FromItem from = getSelect().getFromItem();
		String name = entity.getName();
		Table table = new Table(getEntityType().getNamespace(),name);
		table.setAlias(name + "_" + index);
		if(from == null){
			getSelect().setFromItem(table);
			return null;
		}else {
			Join join = new Join().setRightItem(table);			
			if(getSelect().getJoins() == null){
				getSelect().setJoins(new ArrayList<Join>());
			}
			getSelect().getJoins().add(join);
			return join;
		}

	}

	private void appendWhere( List<UriParameter> list) throws EdmPrimitiveTypeException {

		SQLExpression expression = null;
		for( UriParameter key :  list){
			expression = appendKey( expression, key );
		}
		if(expression != null){
			SQLExpression where = getSelect().getWhere();
			if(where == null){
				where = expression;
			}else {
				where = new AndExpression(expression, where);
			}
			getSelect().setWhere(where);
		}
	}

	private SQLExpression appendKey(SQLExpression expression, UriParameter key) throws EdmPrimitiveTypeException {

		EdmEntityType entityType =  getEntityType();
		String name = entityType.getName() + "_" + index;

		EdmProperty keyProperty = (EdmProperty)entityType.getProperty(key.getName());	
		Object value = getValue(key, keyProperty);
		getStatementParams().put(name, value);

		Column column = new Column().
				setColumnName(key.getName()).
				setTable(new Table().setName(name));

		BinaryExpression eq = new EqualsTo().
				setLeftExpression(column).
				setRightExpression(new NamedParameter(name));

		if(expression == null){							
			expression = eq;
		}else {
			expression = new AndExpression(expression, eq);
		}
		return expression;
	}

	private Object getValue(UriParameter key, EdmProperty keyProperty) throws EdmPrimitiveTypeException {

		EdmPrimitiveType type = (EdmPrimitiveType) keyProperty.getType();
		Object value = type.valueOfString(key.getText(), 
				keyProperty.isNullable(),
				keyProperty.getMaxLength(),
				keyProperty.getPrecision(), 
				keyProperty.getScale(), true, type.getDefaultType());

		return value;
	}

	public EdmEntityType getEntityType() {		
		return entityType;
	}

	



}