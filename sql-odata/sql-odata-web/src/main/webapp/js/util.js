if (!String.prototype.format) {
	String.prototype.format = function() {
		var args = arguments;
		var result = this.replace(/{(\d+)}/g, function(match, number) { 
			return typeof args[number] != 'undefined'
				? args[number]
			: match
			;
		});
		return result; 
	};
}

function UriInfo(uri){

	init(this);
	uriInfo = this;

	function init(uriInfo){		
		uriInfo.parameters = {};
		var uriFragments = uri.split("?");	
		if(uriFragments.length == 2){
			var queryFragments = uriFragments[1].split("&");
			for(var i = 0; i < queryFragments.length; i++){
				var paramFragments = queryFragments[i].split("=");
				uriInfo.parameters[paramFragments[0]] = paramFragments[1]; 
			}
		}
		var schemeFragments = uriFragments[0].split(":"); 
		uriInfo.scheme = schemeFragments[0];
		var path;
		if(schemeFragments.length == 3){
			uriInfo.server = schemeFragments[1].slice(2);
			var pathFragments = schemeFragments[2].split("/");
			uriInfo.port = pathFragments[0];
			path = pathFragments.slice(1);
		}else if (schemeFragments.length == 2){
			var pathFragments = schemeFragments[1].split("/");
			uriInfo.server = pathFragments[2];
			path =  pathFragments.slice(3);
		}
		uriInfo.pathInfo = [];
		var start = 0;
		if(path[0] == "ui"){
			uriInfo.contextPath = "";
			start = 1;
			uriInfo.servletPath = "ui";
		}else {
			uriInfo.contextPath = path[0];
			start = 2;
		}
		for(var i = start; i < path.length; i++){
			var pathFragments = path[i].split("(");
			var keys = [];
			uriInfo.pathInfo.push({"name": pathFragments[0],"keys" : keys });			
			if(pathFragments.length == 2){
				var keysFragments = pathFragments[1].split(")");
				keysFragments = keysFragments[0].split(",");
				for(var j = 0; j < keysFragments.length; j++){
					var key = keysFragments[j].split("=");
					keys.push({"name": key[0],"value":key[1]});
				}
			}
		}

	}

	this.toUri = function(){
		var uri = uriInfo.scheme + "://" + uriInfo.server + (uriInfo.port === undefined ? "/" : ":" + uriInfo.port);
		var servletPath = uriInfo.servletPath === undefined ? "" : "/" + uriInfo.servletPath;
		if(uriInfo.contextPath == ""){
			uri += servletPath;
	     }else {
	    	 uri += "/" + uriInfo.contextPath +  servletPath;
	     }
		for(var i = 0; i < uriInfo.pathInfo.length; i++){
			uri += "/" + uriInfo.pathInfo[i].name;
			var keys = [];
			var uriKeys = uriInfo.pathInfo[i].keys;
			for(var j = 0; j < uriKeys.length; j++){
				keys.push(uriKeys[j].name + "=" + uriKeys[j].value);
			}
			if(keys.length > 0){
				uri += "(" + keys.join() + ")"; 	
			}
		}
		var params = [];
		for(var p in uriInfo.parameters){
			params.push(p + "=" + uriInfo.parameters[p]);
		}
		if(params.length > 0){
			uri += "?" + params.join("&");
		}
		return uri;
	}

}

