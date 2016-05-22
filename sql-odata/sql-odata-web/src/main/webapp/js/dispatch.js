"use strict";

function dispatch($metadata, uriInfo){
	
	if(uriInfo.pathInfo.length > 0){
		var l = uriInfo.pathInfo.length;
		if(uriInfo.pathInfo[l - 1].keys && Object.keys(uriInfo.pathInfo[l - 1].keys).length > 0 ){
		   processEntityRequest($metadata,uriInfo);	
		}else{
		   processEntitySetRequest($metadata,uriInfo);
		}
	}
	
}

function processEntitySetRequest($metadata, uriInfo){
	buildDataTable(uriInfo);
}

function processEntityRequest($metadata,uriInfo){
	buildEntityView(uriInfo);
}