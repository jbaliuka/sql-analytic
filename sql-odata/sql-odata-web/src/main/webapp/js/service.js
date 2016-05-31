"use strict";
function Service($metadata){
	
	this.patch = function(uriInfo,data,callback){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
			if (xhttp.readyState == 4 && xhttp.status < 400) {				
				callback();								
			}else if (xhttp.readyState == 4 && xhttp.status >= 400){
				Alert.show(xhttp.responseText);
			}
		};		
		xhttp.open("PATCH", uriInfo.toServiceUri(), true);
		xhttp.setRequestHeader('Content-Type', 'application/json');
		xhttp.send(JSON.stringify(data));
	}
	
	this.post = function(uriInfo,data,callback){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
			if (xhttp.readyState == 4 && xhttp.status < 400) {				
				callback();								
			}else if (xhttp.readyState == 4 && xhttp.status >= 400){
				Alert.show(xhttp.responseText);
			}
		};		
		xhttp.open("POST", uriInfo.toServiceUri(), true);
		xhttp.setRequestHeader('Content-Type', 'application/json');
		xhttp.send(JSON.stringify(data));
	}
	
	this.get = function(uriInfo, processCallback){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
			if (xhttp.readyState == 4 && xhttp.status == 200) {
				var response = JSON.parse(xhttp.responseText);
				processCallback(response,$metadata);								
			}else if (xhttp.readyState == 4 && xhttp.status != 200){
				Alert.show(xhttp.responseText);
			}
		};
		var serviceUriInfo = uriInfo.toServiceUri().toUriInfo();
		serviceUriInfo.parameters.$format="JSON";
		if(serviceUriInfo.parameters.$select !== undefined && 
				serviceUriInfo.parameters.$select != "*"){
			var entityType = $metadata.resolveEntityType(serviceUriInfo);
			var select = serviceUriInfo.parameters.$select.split(",");
			for(var key in entityType.keys){
				if(select.indexOf(key) < 0){
					select.push(key);	
				}
			}
			serviceUriInfo.parameters.$select = select.join(",");
		}

		xhttp.open("GET", serviceUriInfo.toUri(), true);
		xhttp.send();
	}	
	this.batchDelete = function(uriInfo,requests,callBack){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
			if (xhttp.readyState == 4 && xhttp.status < 400) {				
				callBack();
				var msg = "";
				var lines = xhttp.responseText.split("\r\n");
				var status = /^HTTP\/1\.\d (\d{3}) (.*)$/i;
				for(var i in lines){
					var line = lines[i];
					var match = status.exec(line);
					if (match) {
						if( parseInt(match[1]) >= 400){
							msg += match[1] + " " + match[2] + "\n";
						}
					} 
				}
				if(msg.length > 0){
					Alert.show(msg);
				}
			}
		};		
		var boundary = guid();
		xhttp.open("POST", uriInfo.toServiceUri() + "/$batch", true);
		xhttp.setRequestHeader('Content-Type', 'multipart/mixed;boundary=' + boundary);
		xhttp.send(buildBatch(requests,boundary));		
	}	
	function guid() {
		function s4() {
			return Math.floor((1 + Math.random()) * 0x10000)
			.toString(16)
			.substring(1);
		}
		return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
		s4() + '-' + s4() + s4() + s4();
	}
	function buildBatch(requests,boundary){		 
		var body = [];
		body.push('--' + boundary);
		var changeSet =  guid();
		body.push('Content-type: multipart/mixed; boundary=' + changeSet, '','');
		requests.forEach( function(d,idx) {
			body.push('--' + changeSet);
			body.push('Content-Type: application/http', 'Content-Transfer-Encoding: binary','Content-ID: ' + (idx + 1));
			body.push('');
			body.push('DELETE ' + d + ' HTTP/1.1',
					'Accept: application/json;odata.metadata=minimal',
					'Content-Type: application/json;odata.metadata=minimal',
					'OData-MaxVersion: 4.0',
					'OData-Version: 4.0',
					'Content-ID: ' + (idx + 1));
			body.push('','');

		});
		body.push('--' + changeSet + '--', '');
		body.push('--' + boundary + '--', '');
		return body.join('\r\n');
	}
}	
