"use strict";
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
if (!String.prototype.toUriInfo) {
	String.prototype.toUriInfo = function() {		
		return new UriInfo(this); 
	};
}
function UriInfo(uri){

	this.parameters = {};
	var uriHash = uri.split("#");
	if(uriHash.length == 2){
		this.hash = uriHash[1];
	}
	var uriFragments = uriHash[0].split("?");	
	if(uriFragments.length == 2){
		var queryFragments = uriFragments[1].split("&");
		for(var i = 0; i < queryFragments.length; i++){
			var paramFragments = queryFragments[i].split("=");
			this.parameters[paramFragments[0]] = paramFragments[1]; 
		}
	}
	var schemeFragments = uriFragments[0].split(":"); 
	this.scheme = schemeFragments[0];
	var path;
	if(schemeFragments.length == 3){
		this.server = schemeFragments[1].slice(2);
		var pathFragments = schemeFragments[2].split("/");
		this.port = pathFragments[0];
		path = pathFragments.slice(1);
	}else if (schemeFragments.length == 2){
		var pathFragments = schemeFragments[1].split("/");
		this.server = pathFragments[2];
		path =  pathFragments.slice(3);
	}
	this.pathInfo = [];
	var start = 0;
	if(path[0] == "ui" || path[0].endsWith(".svc") ){
		this.contextPath = "";
		start = 1;
		this.servletPath = path[0];
	}else if(path.length > 1 && (path[1] == "ui" || path[1].endsWith(".svc") )) {
		this.contextPath = path[0];
		this.servletPath = path[1];
		start = 2;
	}else {
		this.contextPath = path[0];
		start = 1;
	}
	for(var i = start; i < path.length; i++){
		var pathFragments = path[i].split("(");
		if(pathFragments[0] == ""){
			continue;
		}
		var keys = {};
		this.pathInfo.push({"name": pathFragments[0],"keys" : keys });
		if(pathFragments.length == 2){
			var keysFragments = pathFragments[1].split(")");
			keysFragments = keysFragments[0].split(",");
			for(var j = 0; j < keysFragments.length; j++){
				var key = keysFragments[j].split("=");
				keys[key[0]] = key[1];
			}
		}
	}	
	this.toUIUri = function(){
		var newUri = new UriInfo(this.toUri());
		newUri.servletPath = "ui";
		return newUri.toUri();
	}
	
	this.toServiceUri = function(){
		var newUri = new UriInfo(this.toUri());
		newUri.servletPath = "SQLODataService.svc";
		return newUri.toUri();
	}
	this.toBaseUri = function(){
		var uri = this.scheme + "://" + this.server + (this.port === undefined ? "/" : ":" + this.port) + "/";
		if(this.contextPath === undefined ){
			return uri; 
		}else{
			return uri + this.contextPath;
		}	
		
	}
	this.getPath = function(){
		var uri = "";
		for(var i = 0; i < this.pathInfo.length; i++){
			uri += "/" + this.pathInfo[i].name;
			var keys = [];
			var uriKeys = this.pathInfo[i].keys;
			for(var k in uriKeys ){
				keys.push(k + "=" + uriKeys[k]);
			}
			if(keys.length > 0){
				uri += "(" + keys.join() + ")"; 	
			}
		}
		return uri;
	}
	this.toEntityUri = function(){
		var newUri = new UriInfo(this.toUri());
		delete newUri.parameters.$top;
		delete newUri.parameters.$skip;		
		delete newUri.parameters.$filter;
		delete newUri.parameters.$orderby;
		delete newUri.parameters.$filter;
		return newUri;
	}
	this.toUri = function(){
		var uri = this.scheme + "://" + this.server + (this.port === undefined ? "/" : ":" + this.port);
		var servletPath = this.servletPath === undefined ? "" : "/" + this.servletPath;
		if(this.contextPath == ""){
			uri += servletPath;
		}else {
			uri += "/" + this.contextPath +  servletPath;
		}
		uri += this.getPath();
		var params = [];
		for(var p in this.parameters){
			params.push(p + "=" + this.parameters[p]);
		}
		if(params.length > 0){
			uri += "?" + params.join("&");
		}
		return uri + (this.hash === undefined ? "" : "#" + this.hash);
	}
	
	this.isSelected = function (propName){
		if(this.parameters === undefined){
			return true;
		}
		var selectOption = this.parameters.$select;
		if(selectOption === undefined || selectOption == "*"){
			return true;
		}else {
			return selectOption.split(",").indexOf(propName) != -1;
		}
	}

}

