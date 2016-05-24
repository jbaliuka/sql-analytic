"use strict";
function ServiceMetadata(url, readyCallback){
	var $metadata = this;	
	$metadata.url = url;	
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			buildMetadata(xhttp);
		}
	};
	xhttp.open("GET", $metadata.url + "/$metadata", true);
	xhttp.send();
	function buildMetadata(xml) {
		var xmlDoc = xml.responseXML;
		buildSchemas(xmlDoc);
		buildEntitySets(xmlDoc);
		window.$service = new Service($metadata);
		readyCallback($metadata);
	}
	function buildSchemas(xmlDoc){
		var schemaNodes = xmlDoc.getElementsByTagName("Schema");
		$metadata.schemas = new Object();
		for( var i = 0; i < schemaNodes.length; i++){
			var namespace = schemaNodes[i].attributes["Namespace"].value;
			var schema = new Schema(namespace);
			$metadata.schemas[namespace] = schema;
			schema.entityTypes = new Object();
			for( var j = 0; j < schemaNodes[i].childNodes.length; j++ ){
				var childNode = schemaNodes[i].childNodes[j];
				buildEntityType(schema,childNode);
			}
		}
	}
	function buildEntityType(schema,childNode){
		if(childNode.localName == "EntityType"){
			var typeName = childNode.attributes["Name"].value; 
			var entityType = new EntityType(typeName);
			schema.entityTypes[typeName] = entityType;
			entityType.properties = new Object();
			for(var k = 0; k < childNode.childNodes.length; k++ ){
				var propertyNode = childNode.childNodes[k];	
				if( propertyNode.localName == "Property"){
					var propertyName = propertyNode.attributes["Name"].value;  
					entityType.properties[propertyName] = new Property(propertyName,
							propertyNode.attributes["Type"].value);
				}else if (propertyNode.localName == "Key"){
					buildTypeKeys(entityType,propertyNode.childNodes);
				}else if ( propertyNode.localName == "NavigationProperty"){
					buildNavigationProperty(entityType,propertyNode);
				}
			}
		}
	}	
	function buildNavigationProperty(entityType,propertyNode){		
		var propertyName = propertyNode.attributes["Name"].value;
		var propertyType = propertyNode.attributes["Type"].value;
		entityType.navProperties = {};
		if(propertyType.startsWith("Collection")){
			propertyType = propertyType.split("(")[1].split(")")[0];
			entityType.navProperties[propertyName] = new Property(propertyName,propertyType);
		}
	}	
	function buildTypeKeys(entityType,keys){
		entityType.keys = {};
		for(var i = 0; i < keys.length; i++ ){
			var name = keys[i].attributes["Name"].value;
			entityType.keys[name] = name;	
		}
	}
	function buildEntitySets(xmlDoc){
		var entitySetNodes = xmlDoc.getElementsByTagName("EntitySet");
		$metadata.entitySets = new Object();
		for( var i = 0; i < entitySetNodes.length; i++){
			var attributes = entitySetNodes[i].attributes;
			var name = attributes["Name"].value;
			var typeName = attributes["EntityType"].value;	    	
			var entitySet = new EntitySet($metadata.url + "/" + name, name, typeName);	
			$metadata.entitySets[name] = entitySet;
		}
	}
	$metadata.getEntityType = function (fullName){
		var split = fullName.split(".");    	
		return $metadata.schemas[split[0]].entityTypes[split[1]];
	}
	$metadata.resolveEntityType = function (uriInfo){
		var eSet = $metadata.entitySets[uriInfo.pathInfo[0].name];
		var entityType = $metadata.getEntityType(eSet.entityType);
		for(var i = 1; i < uriInfo.pathInfo.length; i++){
			var nav = entityType.navProperties[uriInfo.pathInfo[i].name];
			entityType = $metadata.getEntityType(nav.type);	
		}
		return entityType;
	}	
	function EntitySet(path,name,type){
		this.path = path;
		this.name = name;
		this.entityType = type;	
	}
	function EntityType(name){	
		this.name = name;	
	}
	function Schema(namespace){	
		this.namespace = namespace;		
	}
	function Property(name,type){
		this.name = name;
		this.type = type;
	}
}