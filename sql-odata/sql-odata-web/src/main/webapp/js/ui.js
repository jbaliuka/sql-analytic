window.addEventListener('popstate', function(event) {	   	
	var uriInfo = new UriInfo(event.state);				      	
	buildDataTable(uriInfo);		
});

function eSetHandler(event) {
	var href = event.target.getAttribute('href');			      	
	var uriInfo = new UriInfo(href);	     	
	buildDataTable(uriInfo);
	history.pushState(href, null, uriInfo.toUIUri());
	return event.preventDefault();
}

function metadataCallback(metadata) {

	var locationInfo = new  UriInfo(location.href);
	var list = "<ul>";
	for (var e in metadata.entitySets) {						
		var uriInfo = new UriInfo(locationInfo.scheme + "://" +
				locationInfo.server + ":" + locationInfo.port + "/" + 
				locationInfo.contextPath + "/" + e);		
				
		uriInfo.parameters.$top = 20;
		uriInfo.parameters.$skip = 0;
		list += "<li><a class=\"entitySet\" href=\"{0}\">{1}</a></li>"
			.format(uriInfo.toServiceUri(),e);
	}
	list += "</ul>";									
	var menu = document.getElementById("menu");
	menu.innerHTML = list;
	var entitySet = document.querySelectorAll("a.entitySet");					 
	for (var i = 0, l = entitySet.length; i < l; i++) {
		entitySet[i].addEventListener('click', eSetHandler, true);
	}
	
}

function buildDataTable(uriInfo) {			
	$service.getEntitySet(uriInfo, function(data, entityType,eSetName) {
		var entities;
		if(data.value === undefined){					
			entities = new Array(data);
		}else {
			entities = data.value;
		}
		var dataTable = "<div class=\"header\"><h2>{0}</h2></div><table><thead><tr>".format(eSetName);
		var colCount = 0;
		for (p in entityType.properties) {
			dataTable += "<th>{0}</th>".format(p);
			colCount++;
		}
		dataTable += "</thead></tr><tbody>";
		for (var i in entities) {
			var row = entities[i];
			var key = {};
			for(var k in entityType.keys){
				key[entityType.keys[k]] = row[entityType.keys[k]];  
			}

			if (i % 2 == 1) {
				dataTable += "<tr >";
			} else {
				dataTable += "<tr class=\"alt\">";
			}
			for (col in entityType.properties) {
				dataTable += "<td>{0}</td>".format(row[col]);
			}
			dataTable += "</tr>";
		}
		for(var j = 1; j < uriInfo.parameters.$top - i; j++ ){
			dataTable += "<tr><td colspan=\"{0}\">&nbsp;</td></tr>".format(entityType.properties.lenght);
		}
		dataTable += "</tbody>";
		var previus = new UriInfo(uriInfo.toUri());

		var skip = parseInt(uriInfo.parameters.$skip) - parseInt(uriInfo.parameters.$top);
		var previus = new UriInfo(uriInfo.toUri());
		previus.parameters.$skip = skip < 0 ? 0 : skip;

		var next = new UriInfo(uriInfo.toUri());
		if(++i == parseInt(next.parameters.$top)){
			next.parameters.$skip = parseInt(uriInfo.parameters.$skip) + parseInt(uriInfo.parameters.$top);
		}		
		dataTable += document.getElementById("dataTableFoot").
		innerHTML.format(colCount,previus.toUri(),next.toUri());				
		dataTable += "</table>";
		document.getElementById("dataTable").innerHTML = dataTable;
		document.getElementById("previus").addEventListener('click', eSetHandler, true);
		document.getElementById("next").addEventListener('click', eSetHandler, true);;

	});
}


