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
		uriInfo.pathInfo = {};
		for(var i = 0; i < path.length; i++){
			var pathFragments = path[i].split("(");
			var keys = {};
			uriInfo.pathInfo[pathFragments[0]] = keys;			
			if(pathFragments.length == 2){
				var keysFragments = pathFragments[1].split(")");
				keysFragments = keysFragments[0].split(",");
				for(var j = 0; j < keysFragments.length; j++){
					var key = keysFragments[j].split("=");
					keys[key[0]] = key[1];
				}
			}
		}

	}
	
	this.toUri = function(){
		var uri = uriInfo.scheme + "://" + uriInfo.server + (uriInfo.port === undefined ? "/" : ":" + uriInfo.port + "/");
		for(var item in uriInfo.pathInfo){
			uri += item;
			var keys = [];
			var pathInfo = uriInfo.pathInfo[item];
			for(var key in pathInfo){
				keys.push(key + "=" + pathInfo[key]);
			}
			if(keys.length > 0){
				uri += "(" + keys.join() + ")/"; 	
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


