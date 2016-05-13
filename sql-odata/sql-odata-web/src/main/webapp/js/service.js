"use strict";

function Service($metadata){

this.$metadata = $metadata;

this.getEntitySet = function(eSetName, processCallback){
	
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			var response = JSON.parse(xhttp.responseText);
			processCallback(response,$metadata.getEntityType($metadata.entitySets[eSetName].entityType));
		}
	};
	xhttp.open("GET", $metadata.url + "/" + eSetName + "?$format=json", true);
	xhttp.send();
	
}	
	
	
}