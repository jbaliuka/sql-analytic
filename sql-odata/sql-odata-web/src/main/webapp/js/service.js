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
	this.batchDelete = function(uriInfo,requests,callBack){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
			if (xhttp.readyState == 4 ) {				
				callBack();								
			}
		};		
		delete uriInfo.parameters.$format;
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
