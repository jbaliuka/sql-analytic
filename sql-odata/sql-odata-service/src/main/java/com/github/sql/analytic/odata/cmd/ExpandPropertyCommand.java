package com.github.sql.analytic.odata.cmd;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;

import com.github.sql.analytic.expression.NamedParameter;
import com.github.sql.analytic.expression.SQLExpression;
import com.github.sql.analytic.expression.operators.conditional.AndExpression;
import com.github.sql.analytic.expression.operators.relational.EqualsTo;
import com.github.sql.analytic.odata.ResultSetIterator;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;
import com.github.sql.analytic.session.SQLSession;
import com.github.sql.analytic.statement.select.Select;
import static com.github.sql.analytic.session.PolicyAwareMetadata.*;

public class ExpandPropertyCommand extends ReadCommand {

	private final EdmNavigationProperty property;
	private final ExpandItem expandItem;
	
	private Map<String, Object> statementParams = new HashMap<String, Object>();
	private Entity entity;

	public ExpandPropertyCommand(Entity entity, EdmNavigationProperty property, ExpandItem expandItem) {
		this.property = property;
		this.expandItem = expandItem;		
		this.entity = entity;
	}

	@Override
	public ResultSetIterator execute(SQLSession connection) throws ODataApplicationException {

		try{
			String name = property.getType().getName();
			Table table = new Table(property.getType().getNamespace(),name);
			table.setAlias(name);
			getSelect().setFromItem(table);
			
			DatabaseMetaData md = connection.getMetaData();
			if(property.isCollection()){
					try (ResultSet rs = md.getImportedKeys(null, property.getType().getNamespace(), name)){
						while(rs.next()){
							if(property.getName().equals(rs.getString(FK_NAME))){
								String parentKey = rs.getString(PKCOLUMN_NAME);
								Property keyProp = entity.getProperty(parentKey);								
								String childKey = rs.getString(FKCOLUMN_NAME);
								appendKeyFilter(name,childKey,keyProp.getValue());
							}
						}
					}
				
			}else {
				try (ResultSet rs = md.getExportedKeys(null, property.getType().getNamespace(), name)){
					while(rs.next()){
						if(property.getName().equals(rs.getString(FK_NAME))){
							String parentKey = rs.getString(FKCOLUMN_NAME);
							Property keyProp = entity.getProperty(parentKey);								
							String childKey = rs.getString(PKCOLUMN_NAME);
							appendKeyFilter(name,childKey,keyProp.getValue());
						}
					}
				}
			}

			processUriInfo(expandItem.getResourcePath(), property.getType(),  name);

			PreparedStatement statement = connection.create(new Select().setSelectBody(getSelect()), statementParams  );			
			ResultSet rs = statement.executeQuery();			
			statement.closeOnCompletion();

			return new ResultSetIterator(connection, rs, property.getType(),expandItem.getExpandOption());

		} catch (SQLException  e) {
			throw internalError(e);
		}
	}

	private void appendKeyFilter(String name, String childKey, Object value) {
		
		SQLExpression where = getSelect().getWhere();
		String paramName = name + "_" + childKey;
		statementParams.put(paramName, value);
		SQLExpression filter = new EqualsTo().
				setLeftExpression(new Column().setTable( new Table().setName(name)).setColumnName(childKey) ).
				setRightExpression( new NamedParameter(paramName));
		if(where == null){
			 getSelect().setWhere(filter);
		}else {
			 getSelect().setWhere( new AndExpression().setLeftExpression(where).setRightExpression(filter));
		}
	}
}