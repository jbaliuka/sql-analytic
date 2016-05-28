"use strict";

function renderHtml(id,innerHtml){	
	var element = document.getElementById(id);
	element.innerHTML = innerHtml;
	var links = document.querySelectorAll( "#" + id +  " a.odataUri");
	for (var i = 0, l = links.length; i < l; i++) {
		links[i].addEventListener('click', navigationHandler, true);
	}	
}
function metadataCallback(metadata){
	var locationInfo = new  UriInfo(location.href);
	var list = "<ul>";
	for (var e in metadata.entitySets) {						
		var uriInfo = new UriInfo(locationInfo.scheme + "://" +
				locationInfo.server + ":" + locationInfo.port + "/" + 
				locationInfo.contextPath + "/" + e);
		uriInfo.parameters.$top = 20;
		uriInfo.parameters.$skip = 0;
		list += "<li><a class=\"odataUri\" href=\"{0}\">{1}</a></li>"
			.format(uriInfo.toUIUri(),e);
	}
	list += "</ul>";
	renderHtml("menu",list)
	dispatch(metadata,locationInfo);
}
function isSelected(uriInfo,propName){
	if(uriInfo.parameters === undefined){
		return true;
	}
	var selectOption = uriInfo.parameters.$select;
	if(selectOption === undefined || selectOption == "*"){
		return true;
	}else {
		return selectOption.split(",").indexOf(propName) != -1;
	}
}
function buildEntityView(uriInfo){
	$service.get(uriInfo, function(data,$metadata) {
		var	eSetName = uriInfo.pathInfo[0].name;
		var entityType = $metadata.resolveEntityType(uriInfo);
		var dataTable = "<div class=\"header\"><h2>{0}</h2></div><table>".format(uriInfo.getPath());
		for(var prop in entityType.properties){
			if(isSelected(uriInfo,prop)){
				dataTable += "<tr><td>{0}</td><td>{1}</td></tr>".format(prop,data[prop]);
			}
		}
		for(var nav in entityType.navProperties){
			var propTypeName = entityType.navProperties[nav].type;
			var navInfo = uriInfo.toUri().toUriInfo();
			navInfo.pathInfo.push({"name" : nav });
			navInfo.parameters.$top = 20;
			navInfo.parameters.$skip = 0;
			delete navInfo.parameters.$select;
			dataTable += "<tr><td>{0}</td><td><a class=\"odataUri\" href=\"{1}\">{2}</a></td></tr>".format(nav,navInfo.toUIUri(),propTypeName.split(".")[1]);
		}		
		dataTable += "</table>";
		renderHtml("dataTable",dataTable);
	});
}
function toggleSelectionTool() {
	document.getElementById("selectionTool").classList.toggle("show");
}
function selectionTool(uriInfo,entityType){	
	var filter = uriInfo.parameters.$filter || "";
	var csv = uriInfo.toServiceUri().toUriInfo();
	csv.parameters.$format = "text/csv";
	delete csv.parameters.$top;
	delete csv.parameters.$skip;
	var tool =
		"<div class=\"dropdown\">"+
		" <button class=\"button\" onclick=\"window.location.href='{0}';\">&#x21E9;</button>".format(csv.toUri()) +
		" <button class=\"button\" onclick=\"toggleSelectionTool()\">&#x25A5;</button> " +
		" <div id=\"selectionTool\" class=\"dropdown-content\"><ul>" ;
	for (var p in entityType.properties) {
		tool += "<li><input class=\"checkProp\" type=\"checkbox\" name=\"{0}\" {1}>{0}</li>"
			.format(p,isSelected(uriInfo,p) ? "checked" : "");
	}	
	tool += "<li><hr></li><li><button class=\"button\" onclick=\"handleSelectList()\">Ok</button>"+
	"<button class=\"button\" onclick=\"cancelSelectList()\">Cancel</button>"
	+ "</li></ul></div>" +
	"<input id=\"filter\" type=\"text\" value=\"{0}\" onkeypress=\"handleFilter(event)\"></div>".format(decodeURIComponent(filter));
	return tool;
}
function handleDelete(){
	var checkboxes = document.getElementsByClassName("deleteCheck");
	var requests = [];
	for(var j = 0; j < checkboxes.length; j++ ){
		if(checkboxes[j].checked){
			requests.push(checkboxes[j].name);	
		}
	}
	var uriInfo = new UriInfo(location.href).toBaseUri().toUriInfo();
	delete uriInfo.parameters.$format;
	$service.batchDelete(uriInfo,requests,function(response,$metadata){
		var currentUriInfo = new UriInfo(history.state);
		processEntitySetRequest($metadata,currentUriInfo);
	});
}

function buildEntitySetView(uriInfo) {	
	$service.get(uriInfo, function(data,$metadata) {
		var entityType = $metadata.resolveEntityType(uriInfo);
		var entities = data.value;		
		var dataTable = "<div class=\"header\"><h2>{1}</h2>{0}</div><table><thead><tr>"
			.format(selectionTool(uriInfo,entityType),uriInfo.getPath());
		dataTable += "<th><button class=\"button\" onclick=\"handleDelete()\">&#x2717;</button></th>";
		var colCount = 0;
		var sort = "<div class=\"sortIcon\"><span class=\"up\"><a class=\"odataUri\" href=\"{0}\">&#x25B2;</a></span>"+
		"<span class=\"down\"><a class=\"odataUri\" href=\"{1}\">&#x25BC;</a></span><div>";		
		for (var k in entityType.keys) {
			if(isSelected(uriInfo,k)){
				var sortUriInfoAsc = uriInfo.toUIUri().toUriInfo();
				var sortUriInfoDesc = uriInfo.toUIUri().toUriInfo();
				sortUriInfoAsc.parameters.$orderby = k + " asc";
				sortUriInfoDesc.parameters.$orderby = k + " desc";				
				dataTable += "<th>{0}{1}</th>".format(k.split("_").join("<br/>"),
						sort.format(sortUriInfoAsc.toUri(),sortUriInfoDesc.toUri()));
				colCount++;		
			}
		}
		for (var p in entityType.properties) {
			if(entityType.keys[p] === undefined && isSelected(uriInfo,p)){
				var sortUriInfoAsc = uriInfo.toUIUri().toUriInfo();
				var sortUriInfoDesc = uriInfo.toUIUri().toUriInfo();
				sortUriInfoAsc.parameters.$orderby = p + " asc";
				sortUriInfoDesc.parameters.$orderby = p + " desc";
				dataTable += "<th>{0}{1}</th>".format(p.split("_").join("<br/>"),
						sort.format(sortUriInfoAsc.toUri(),sortUriInfoDesc.toUri()));
				colCount++;
			}	
		}
		dataTable += "</thead></tr><tbody>";
		for (var i in entities) {
			var row = entities[i];
			if (i % 2 == 1) {
				dataTable += "<tr >";
			} else {
				dataTable += "<tr class=\"alt\">";
			}
			var entityKey = {};			
			var entityUri = uriInfo.toEntityUri().toServiceUri().toUriInfo();
			for (var k in entityType.keys) {				
				entityKey[k] = row[k];				
			}	
			entityUri.pathInfo = [{"name" : entityType.name }];
			entityUri.pathInfo[0].keys = entityKey;
			var deleteUri = entityUri.toUri().toUriInfo();
			delete deleteUri.parameters.$format;
			dataTable += "<td><input class=\"deleteCheck\" type=\"checkbox\" name=\"{0}\" ></td>".format(deleteUri.toServiceUri());
			
			for (var k in entityType.keys) {	
				if(isSelected(uriInfo,k)){					
					dataTable += "<td><a class=\"odataUri\" href=\"{0}\">{1}</a></td>".format(entityUri.toUIUri(),row[k]);
				}
			}
			for (var col in entityType.properties) {
				if(entityType.keys[col] === undefined && isSelected(uriInfo,col)){
					var property = entityType.properties[col];
					dataTable += "<td class=\""+ property.type.split(".")[1] +"\">{0}</td>".format(row[col]);
				}
			}
			dataTable += "</tr>";
		}
		for(var j = 1; j < uriInfo.parameters.$top - i; j++ ){
			dataTable += "<tr><td colspan=\"{0}\">&nbsp;</td></tr>".format(colCount);
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
		var tfoot = "<tfoot>" +
		"<tr>" +
		"	<td colspan=\"{0}\">" +
		"		<div id=\"paging\">" +
		"			<ul>" +
		"				<li><a class=\"odataUri button\" id=\"previus\" href=\"{1}\" >Previous</a></li>" +
		"				<li><a class=\"odataUri button\" id=\"next\" href=\"{2}\">Next</a></li>" +
		"			</ul>" +
		"		</div>" +
		"</tr>" +
		"</tfoot>"; 
		dataTable += tfoot.format(colCount,previus.toUri(),next.toUri());;
		dataTable += "</table>";
		renderHtml("dataTable",dataTable);

	});
}
