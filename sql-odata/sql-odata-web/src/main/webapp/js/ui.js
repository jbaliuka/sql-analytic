"use strict";

window.addEventListener('popstate', function(event) {	   	
	var uriInfo = new UriInfo(event.state);				      	
	dispatch($metadata,uriInfo);		
});

function eSetHandler(event) {
	var href = event.target.getAttribute('href');			      	
	var uriInfo = new UriInfo(href);	     	
	dispatch($metadata,uriInfo);
	history.pushState(href, null, uriInfo.toUIUri());
	return event.preventDefault();
}

function metadataCallback(metadata) {

	var locationInfo = new  UriInfo(location.href);
	var list = "<ul>";
	for (var e in metadata.entitySets) {						
		var uriInfo = new UriInfo(locationInfo.scheme + "://" +
				locationInfo.server + ":" + locationInfo.port + "/" + 
				locationInfo.contextPath + "/" + e);		

		uriInfo.parameters.$top = 20;
		uriInfo.parameters.$skip = 0;
		list += "<li><a class=\"entitySet\" href=\"{0}\">{1}</a></li>"
			.format(uriInfo.toServiceUri(),e);
	}
	list += "</ul>";									
	var menu = document.getElementById("menu");
	menu.innerHTML = list;
	var entitySet = document.querySelectorAll("a.entitySet");					 
	for (var i = 0, l = entitySet.length; i < l; i++) {
		entitySet[i].addEventListener('click', eSetHandler, true);
	}

	dispatch(metadata,locationInfo);
}

function buildEntityView(uriInfo){
	$service.get(uriInfo, function(data,$metadata) {		
		var	eSetName = uriInfo.pathInfo[uriInfo.pathInfo.length - 1].name;
		var entityType =  $metadata.getEntityType($metadata.entitySets[eSetName].entityType);		
		var dataTable = "<div class=\"header\"><h2>{0}</h2></div><table>".format(entityType.name);
		for(var prop in entityType.properties){
			dataTable += "<tr><td>{0}</td><td>{1}</td></tr>".format(prop,data[prop]);
		}
		dataTable += "</table>";
		document.getElementById("dataTable").innerHTML = dataTable;
	});
}

function buildDataTable(uriInfo) {			
	$service.get(uriInfo, function(data,$metadata) {
		var	eSetName = uriInfo.pathInfo[uriInfo.pathInfo.length - 1].name;
		var entityType = $metadata.getEntityType($metadata.entitySets[eSetName].entityType);
		var entities;
		if(data.value === undefined){					
			entities = new Array(data);
		}else {
			entities = data.value;
		}
		var dataTable = "<div class=\"header\"><h2>{0}</h2></div><table><thead><tr>".format(eSetName);
		var colCount = 0;
		for (var k in entityType.keys) {		
			dataTable += "<th>{0}</th>".format(k);
			colCount++;			
		}
		for (var p in entityType.properties) {
			if(entityType.keys[p] === undefined){
				dataTable += "<th>{0}</th>".format(p);
				colCount++;
			}	
		}
		dataTable += "</thead></tr><tbody>";
		for (var i in entities) {
			var row = entities[i];
			if (i % 2 == 1) {
				dataTable += "<tr >";
			} else {
				dataTable += "<tr class=\"alt\">";
			}
			var entityKey = {};			
			var entityUri = uriInfo.toServiceUri().toUriInfo();
			for (var k in entityType.keys) {
				entityKey[k] = row[k];	 	
			}	
			entityUri.pathInfo[entityUri.pathInfo.length - 1].keys = entityKey; 
			delete entityUri.parameters.$top;
			delete entityUri.parameters.$skip;
			for (var k in entityType.keys) {				
				dataTable += "<td><a class=\"key\" href=\"{0}\">{1}</a></td>".format(entityUri.toServiceUri(),row[k]);							
			}
			for (var col in entityType.properties) {
				if(entityType.keys[col] === undefined){
					dataTable += "<td>{0}</td>".format(row[col]);
				}
			}
			dataTable += "</tr>";
		}
		for(var j = 1; j < uriInfo.parameters.$top - i; j++ ){
			dataTable += "<tr><td colspan=\"{0}\">&nbsp;</td></tr>".format(entityType.properties.lenght);
		}
		dataTable += "</tbody>";
		var previus = new UriInfo(uriInfo.toUri());

		var skip = parseInt(uriInfo.parameters.$skip) - parseInt(uriInfo.parameters.$top);
		var previus = new UriInfo(uriInfo.toUri());
		previus.parameters.$skip = skip < 0 ? 0 : skip;

		var next = new UriInfo(uriInfo.toUri());
		if(++i == parseInt(next.parameters.$top)){
			next.parameters.$skip = parseInt(uriInfo.parameters.$skip) + parseInt(uriInfo.parameters.$top);
		}		
		dataTable += document.getElementById("dataTableFoot").
		innerHTML.format(colCount,previus.toUri(),next.toUri());				
		dataTable += "</table>";
		document.getElementById("dataTable").innerHTML = dataTable;
		document.getElementById("previus").addEventListener('click', eSetHandler, true);
		document.getElementById("next").addEventListener('click', eSetHandler, true);
				
		var entityLinks = document.querySelectorAll("a.key");					 
		for (var i = 0, l = entityLinks.length; i < l; i++) {
			entityLinks[i].addEventListener('click', eSetHandler, true);
		}

	});
}


