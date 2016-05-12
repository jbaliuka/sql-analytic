"use strict";

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

function SQLODataService(url, readyCallback){	
	
	SQLODataService = this;	
	SQLODataService.url = url;	
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
	    if (xhttp.readyState == 4 && xhttp.status == 200) {
	   	 metadata(xhttp);
	    }
	};
	xhttp.open("GET", SQLODataService.url + "/$metadata", true);
	xhttp.send();
	
	function metadata(xml) {
	    var xmlDoc = xml.responseXML;
	    var schemaNodes = xmlDoc.getElementsByTagName("Schema");
	    SQLODataService.schemas = new Object();
	   	for( var i = 0; i < schemaNodes.length; i++){
	   	  var namespace = schemaNodes[i].attributes["Namespace"].value;	
	   	  var schema = new Schema(namespace);
	   	  SQLODataService.schemas[namespace] = schema;
	   	  schema.entityTypes = new Object();
	   	  for( var j = 0; j < schemaNodes[i].childNodes.length; j++ ){
	   		 var childNode = schemaNodes[i].childNodes[j];
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
	   			 }
	   			}
	   		 } 
	   	  }
	   	}
	    var entitySetNodes = xmlDoc.getElementsByTagName("EntitySet");
	    SQLODataService.entitySets = new Object();
	    for( var i = 0; i < entitySetNodes.length; i++){
	    	var attributes = entitySetNodes[i].attributes;
	    	var name = attributes["Name"].value;
	    	var typeName = attributes["EntityType"].value;	    	
	    	var entitySet = new EntitySet(url + "/" + name, name, typeName);	
	    	SQLODataService.entitySets[name] = entitySet;
	    }
	     
	   	readyCallback(SQLODataService);
	   
	}
	
	function getEntityType(fullName){
    	var split = fullName.split(".");    	
    	return SQLODataService.schemas[split[0]].entityTypes[split[1]];	    	
    }
	
}


