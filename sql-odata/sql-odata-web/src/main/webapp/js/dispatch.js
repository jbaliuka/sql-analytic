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
function navigationHandler(event) {
	var href = event.target.getAttribute('href');
	var uriInfo = new UriInfo(href);	     	
	dispatch($metadata,uriInfo);	
	var uiUri = uriInfo.toUIUri();
	history.pushState(uiUri, null,uiUri);
	return event.preventDefault();
}