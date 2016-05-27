"use strict";

function Service($metadata){

	this.get = function(uriInfo, processCallback){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
			if (xhttp.readyState == 4 && xhttp.status == 200) {
				var response = JSON.parse(xhttp.responseText);
				processCallback(response,$metadata);								
			}
		};
		uriInfo.parameters.$format="JSON";
		xhttp.open("GET", uriInfo.toServiceUri(), true);
		xhttp.send();
	}
}	
