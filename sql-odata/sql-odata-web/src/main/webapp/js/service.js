"use strict";

function Service($metadata){

this.$metadata = $metadata;

this.getEntitySet = function(eSetName,queryOptions, processCallback){
	
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			var response = JSON.parse(xhttp.responseText);
			processCallback(response,$metadata.getEntityType($metadata.entitySets[eSetName].entityType),queryOptions);
		}
	};
	var options = "";
	for(var option in queryOptions){
		if(option.startsWith("$")){
			options +=	"&" + option + "=" + queryOptions[option];
		}
	}
	var key = "";
	if(!(queryOptions.key === undefined)){
		var list = [];
		key = "(";
		for(var k in queryOptions.key){
			list.push(k + "=" + queryOptions.key[k]);
		}
		key += list.join();
		key += ")";
		options = "";
		for(var option in queryOptions){
			if(option.startsWith("$")){
				if(option != "$skip" && option != "$top"){
				  options +=	"&" + option + "=" + queryOptions[option];
				}
			}
		}
		
	}
	
	xhttp.open("GET", $metadata.url + "/" + eSetName + "{0}?$format=json".format(key) + options, true);
	xhttp.send();
	
}	
	
	
}