//$().ready(function() {
function makeGraphs(panel) { 

	$('.stats-graph-ne-per-doc-table', panel).visualize({
		type:'bar', width:300, height:150,
		appendTitle:true, barGroupMargin:3
	});
	
	$('.stats-graph-ne-dist-table', panel).visualize({
		type:'pie', width:220, height:190,
		appendTitle:false, pieMargin:3
	});
	
	$('.stats-graph-time-of-ne-table', panel).visualize({
		type:'area', width:550, height:150,
		appendTitle:true, barGroupMargin:3
	});
//	refreshGraphs(panel) 
}

function refreshGraphs(panel) { 
$('#stats-graph-ne-per-doc-table', panel).trigger('visualizeRefresh');
$('#stats-graph-ne-dist-table', panel).trigger('visualizeRefresh');
$('#stats-graph-time-of-ne-table', panel).trigger('visualizeRefresh');
}


/*
function listStats(response, lang, collection_id) {
	var r = response['message'] // HTML
	var res = "<DIV ID=\"stats\">\n";
	res += "<DIV>"
	var cache = response['cache_dates'] 
	for (j in cache) { res += "<P>"+j+": "+cache[j]+"</P>" }
	res += "<P><A HREF='#' COLLECTIONID='"+collection_id+"' CLASS='REFRESH_STATS_PAGE adminbutton'>"
	res += i18n['refresh'][lang]+"</A></P>"
	res += r
	res += "</DIV>\n";
	return res
}
*/  
