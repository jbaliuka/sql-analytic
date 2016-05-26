package com.github.sql.analytic.odata.ser;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.serializer.CustomContentTypeSupport;
import org.apache.olingo.server.api.serializer.RepresentationType;

public class CustomContentTypes implements CustomContentTypeSupport{

	@Override
	public List<ContentType> modifySupportedContentTypes(List<ContentType> defaultContentTypes,
			RepresentationType type) {
		if(type == RepresentationType.COLLECTION_ENTITY){
			List<ContentType> newTypes = new ArrayList<ContentType>(defaultContentTypes);
			newTypes.add(ContentType.create("text/csv"));
			return newTypes;
		}
		return defaultContentTypes;
	}

}
