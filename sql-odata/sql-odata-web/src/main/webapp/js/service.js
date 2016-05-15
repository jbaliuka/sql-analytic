"use strict";

function Service($metadata){
	
	this.getEntitySet = function(uriInfo, processCallback){
		var	eSetName = uriInfo.pathInfo[0].name;
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
			if (xhttp.readyState == 4 && xhttp.status == 200) {
				var response = JSON.parse(xhttp.responseText);
				processCallback(response,$metadata.getEntityType($metadata.entitySets[eSetName].entityType),eSetName);
			}
		};	

		xhttp.open("GET", uriInfo.toUri(), true);
		xhttp.send();	
	}	

}