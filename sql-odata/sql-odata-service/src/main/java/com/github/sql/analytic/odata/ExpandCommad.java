package com.github.sql.analytic.odata;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;

import com.github.sql.analytic.session.SQLSession;

public class ExpandCommad {

	private ExpandOption expandOption;
	private EdmEntityType edmEntityType;
	private Entity entity;

	public ExpandCommad(ExpandOption expandOption,EdmEntityType edmEntityType ,Entity entity ){

		this.expandOption = expandOption;
		this.edmEntityType = edmEntityType;
		this.entity = entity;		

	}


	public void expand(SQLSession session) throws ODataApplicationException{

		for(ExpandItem expandItem : expandOption.getExpandItems()){
			expandItem(session,expandItem);
		}

	}


	private void expandItem(SQLSession session, ExpandItem expandItem) throws ODataApplicationException {
		if(expandItem.isStar()){
				
			for(String nav : edmEntityType.getNavigationPropertyNames()){
				expandProperty(session,expandItem, edmEntityType.getNavigationProperty(nav));
				
			}
		}else {
			
			for(UriResource uriResource : expandItem.getResourcePath().getUriResourceParts()){			  
				if(uriResource instanceof UriResourceNavigation) {
					UriResourceNavigation navigation = (UriResourceNavigation) uriResource;
					expandProperty(session,expandItem, navigation.getProperty() );					
				}
			}
		}

	}


	private void expandProperty(SQLSession session, ExpandItem expandItem, EdmNavigationProperty property) throws ODataApplicationException {

		EntityCollection collection = readEntitySet(expandItem,property, session);
		if(!collection.getEntities().isEmpty()){
			String name = property.getName();		  
			Link link = new Link();
			link.setTitle(name);
			link.setRel(Constants.NS_NAVIGATION_LINK_REL);
			if(property.isCollection()){
				link.setInlineEntitySet(collection);
			}else{
				link.setInlineEntity(collection.getEntities().get(0));
			}
			entity.getNavigationLinks().add(link);
		}

	}


	private EntityCollection readEntitySet(final ExpandItem expandItem, final EdmNavigationProperty property, SQLSession session) throws ODataApplicationException {
		
		ReadCommand read = new ExpandPropertyCommand(entity, property, expandItem);
		EntityCollection collection = new EntityCollection();
		for( ResultSetIterator  iterator = read.execute(session);iterator.hasNext(); ){
			collection.getEntities().add(iterator.next());
		}
		return collection;
	}


}
