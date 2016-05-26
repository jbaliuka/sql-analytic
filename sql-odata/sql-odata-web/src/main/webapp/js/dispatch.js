"use strict";
{
	var baseUri = new UriInfo(location.href.toUriInfo().toBaseUri());
	window.$metadata = new ServiceMetadata(baseUri.toServiceUri(),metadataCallback);
};
function dispatch($metadata, uriInfo){
	if(uriInfo.pathInfo.length > 0){
		var l = uriInfo.pathInfo.length;
		if(uriInfo.pathInfo[l - 1].keys && 
				Object.keys(uriInfo.pathInfo[l - 1].keys).length > 0 ){
			processEntityRequest($metadata,uriInfo);	
		}else{
			processEntitySetRequest($metadata,uriInfo);
		}
	}	
}
function processEntitySetRequest($metadata, uriInfo){
	buildEntitySetView(uriInfo);
}
function processEntityRequest($metadata,uriInfo){
	buildEntityView(uriInfo);
}
window.addEventListener('popstate', function(event) {
	var uriInfo = new UriInfo(event.state);
	dispatch($metadata,uriInfo);
});

function pushStateHistory(uriInfo){
	var uiUri = uriInfo.toUIUri();
	history.pushState(uiUri, null,uiUri);
}
function navigationHandler(event) {
	var href = event.target.getAttribute('href');
	var uriInfo = new UriInfo(href);	     	
	dispatch($metadata,uriInfo);	
	pushStateHistory(uriInfo);
	return event.preventDefault();
}
function handleSelectList(event) {
	var dropdown = document.getElementsByClassName("dropdown-content")[0];
	var selected = [];	
	dropdown.classList.remove('show');
	var checkboxes = document.getElementsByClassName("checkProp");
	for(var j = 0; j < checkboxes.length; j++ ){
		if(checkboxes[j].checked){
			selected.push(checkboxes[j].name);	
		}
	}
	var uriInfo = new UriInfo(history.state || location.href);
	if(selected.length > 0){		
		uriInfo.parameters.$select = selected.join(",");		
	}
	dispatch($metadata,uriInfo);
	pushStateHistory(uriInfo);
}

function handleFilter(e){
	e = e || window.event;	
	if (e.keyCode == 13)
	{   
		var uriInfo = new UriInfo(history.state || location.href);
		var filter = document.getElementById("filter");
		if(filter && filter.value.trim().length > 0){
			uriInfo.parameters.$filter = filter.value.trim(); 
		}else {
			delete uriInfo.parameters.$filter;
		}    	
		dispatch($metadata,uriInfo)
		pushStateHistory(uriInfo);
		return false;
	}
	return true;
}

function cancelSelectList(event) {
	var dropdown = document.getElementsByClassName("dropdown-content")[0];
	var selected = [];	
	var uriInfo = new UriInfo(history.state || location.href);
	dropdown.classList.remove('show');
	var checkboxes = document.getElementsByClassName("checkProp");
	for(var j = 0; j < checkboxes.length; j++ ){
		checkboxes[j].checked = isSelected(uriInfo,checkboxes[j].name);
	}

}