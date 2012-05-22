$().ready(function() {
	
	// click on side menu to send result page div up front
	// note that this show request comes from a link, not from a query
	$('A.SEARCHRESULT_SHOW').live("click", function(ev, ui) {
		
		ev.preventDefault();
		var a_clicked = $(this)
		var col_id = a_clicked.attr("ID")
		var api_key = Rembrandt.Util.getApiKey()
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : a_clicked.text())
			
		showSlidableDIV({
			"title": title,
			"target":a_clicked.attr("TARGET"),
			"role":a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":false
		})		
	});
	
		// click on side menu to send result page div up front 
	$('A.SEARCHRESULT_EXPLANATION').live("click", function(ev, ui) {
		ev.preventDefault()

		var a_clicked = $(this)
		var col_id = a_clicked.attr("ID")
		var api_key = Rembrandt.Util.getApiKey()
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : a_clicked.text())
			
		showSlidableDIV({
			"title": title,
			"target":a_clicked.attr("TARGET"),
			"role":a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":false
		})
	})
	
		
	// click for more search results on same tab	
	$('A.SEARCHRESULT_PAGER').live("click", function(ev, ui) {
		
		ev.preventDefault();
		var offset = $(this).attr('OFFSET')
		var limit = $(this).attr('LIMIT')
		var divtoupdate = $(this).parents('DIV.main-slidable-div')
		var role =  $(this).attr('ROLE')	

		var qe = $('#as_qe option:selected').val() // sets RENOIR QE
		var model = $('#as_model option:selected').val() // sets RENOIR Model
		var maps = $('#as_maps').attr('checked') // tells RENOIR to return coordinate stuff
		var feedback = $('#as_feedback').attr('checked') // ask for feedback info

		// href is in form of offset=$nexthit&limit=$steps
		var tags = null;
		var query = $.trim(Rembrandt.Util.getQueryVariable("q"));
		var queryterms; if (query) queryterms = query.split(/\s+/);
		var lang = $("HTML").attr("lang")
		var user = Rembrandt.Util.getUser()
		var collection_id = Rembrandt.Util.getCollectionId()
		var api_key = Rembrandt.Util.getApiKey()
		
		jQuery.ajax({type:'POST', url:Rembrandt.urls.restlet_renoir_search_url+"?q="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(query)),
			contentType:"application/x-www-form-urlencoded", dataType:'json',
			data: "u="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(user))+
			(tags ? "&t="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8($.toJSON(tags))) : "")+
			(qe ? "&qe="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(qe)) : "") + 
			(model ? "&model="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(model)) : "") + 
			(maps ? "&maps="+maps : "") + 
			"&ci="+collection_id+"&lg="+lang+
			"&api_key="+api_key+"&o="+offset+"&l="+limit,
			beforeSubmit:  waitMessageBeforeSubmit(lang),
			success: function(response)  {		
				if (response['status'] == -1) {errorMessageWaitingDiv(lang, response)}
				else {
					hideWaitingDiv()
				
					var su = false
					var pubkey = response['usr_pub_key']
				
					if (!_.isUndefined(pubkey)) {
						$("#main-body").attr('USR_PUB_KEY',pubkey)
						su = Rembrandt.Util.validateSu(pubkey)
					}
				
					
					var newdiv = generateSearchResultShowDIV(response, su, role, {"maps":maps})
					$("DIV.rrs-pageable", divtoupdate).html( $("DIV.rrs-pageable", newdiv).html() )
									
					processMap(response, $('#rrs-searchresult-map', divtoupdate))
				}
			}, 
			error: function(response) {errorMessageWaitingDiv(lang, response)}
		})
	})
})

function generateSearchResultShowDIV(searchresult, su, role, options) {

	var context = "searchresult"
	var canadmin = (su || role.toLowerCase() == "col-admin")
	
	var id = options['id']
	
	// newdiv
	var newdiv = $("<DIV ID='rrs-"+context+"-show-"+id+"' CLASS='main-slidable-div' "+
	" TITLE='"+i18n['results'][lang]+"' STYLE='display:none;overflow:auto;'></DIV>")
			
	// pageable div
	var pageablediv = $("<DIV CLASS='rrs-pageable'></DIV>") 

	// status div - must be included in pageable-div
	var statusdiv = $("<DIV ID='rrs-searchresult-status'>" +
		  i18n["results"][lang]+" <DIV ID='STARTHIT' CLASS='HIT'></DIV> - "+
		 "<DIV ID='NEXTHIT' CLASS='HIT'></DIV> "+i18n["of"][lang]+" "+
		 "<DIV ID='TOTALHIT' CLASS='HIT'></DIV> "+i18n["for"][lang]+" <B>"+options.query+"</B>.</DIV>")
	
	pageablediv.append(statusdiv)
	
	// filling statusdiv	
	var starthit = searchresult['nr_first_result']; // offset result
	var stephit = searchresult['nr_results_shown']; // this is the number of results displayed. Max: 10
	var steps = 10;
	var totalhits = searchresult['total']; // total number of hits.
	var prevhit = ((starthit-steps) < 0 ? 0 : (starthit-steps));
	var nexthit = ((starthit+steps) > totalhits ? totalhits : (starthit+steps));

	var currentpage = parseInt((starthit+steps) / steps);
	var totalpages =  parseInt((totalhits+steps) / steps);
	var maxpagestoshow = (totalpages > 10 ? 10 : totalpages);

	$("#STARTHIT", statusdiv).html(searchresult['nr_first_result']+1)
	$("#NEXTHIT", statusdiv).html(searchresult['nr_last_result'])
	$("#TOTALHIT", statusdiv).html(searchresult['total'])
			
	// let's add map placeholder
	if (options.maps) pageablediv.append("<DIV ID='rrs-searchresult-map'></DIV>")
	
	// now let's add snippets
	var res = searchresult['result']
	var ii = 0
	for( i in res) {
		ii++
		var s = ""
		var title = res[i]['title'];
		var abstract = res[i]['abstract']
		
		// for qrel searches
		var qrel_class = ""
		if (!_.isUndefined(res[i]['qrel'])) {
			if (res[i]['qrel'] == 1) {
				qrel_class = "relevant_document"
			}else if (res[i]['qrel'] == 0) {
				qrel_class = "irrelevant_document"
			}
		}
		
		s += "<DIV CLASS='rrs-searchresult-snippet "+qrel_class+"' ID='rrs-searchresult-snippet-pos"+ii+"'>";
		
		// if we're going for maps, place an icon here.//
		if (options.maps) s += "<A NAME='pos"+ii+"'><IMG style='float:left;margin:4px;' SRC='img/maps/iconr"+ii+".png'>"
		
		// snippet title
		s += "<DIV class='rrs-searchresult-snippet-title'><A CLASS='DOC_SHOW'ID='"+res[i]['doc_id']+"' "		
		s += " DOC_ORIGINAL_ID='"+res[i]['doc_original_id']+"' TITLE='"+title+"' ROLE='"+role+"' HREF='#'> ";
		if (title == "") {s += res[i]['doc_original_id']} else {s += title}
		s += "</A></DIV>\n";
		s += "<DIV class='rrs-searchresult-snippet-abstract'>"
		if (abstract == "") {s += i18n['no-abstract-available'][lang]} else {s += abstract}
		s += "</DIV>\n"; // &#8230; = reticências
		s += "<DIV CLASS='rrs-searchresult-snippet-footer'>"+(parseInt(res[i]['size']/1024.0))+"KB - ";
		// get only the date, not the time - split by the '\s'
		s += res[i]['date'].split(" ")[0];
		// details on doc
		s += " | <A CLASS='DOC_SHOW' ID='"+res[i]['doc_id']+"' DOC_ORIGINAL_ID='"+res[i]['doc_original_id'];
		s += "' TITLE='"+title+"' ROLE='"+role+"' HREF='#'> ";
		s += i18n["show"][lang] + "</A>\n";
		
		s += " | <A CLASS='DOC_METADATA' ID='"+res[i]['doc_id']+"' DOC_ORIGINAL_ID='"+res[i]['doc_original_id'];
		s += "' TITLE='"+title+"' ROLE='"+role+"' HREF='#'> ";
		s += i18n["detail"][lang]+"</A>\n";
		// info on doc
		
		s += "</DIV>\n"; // snippet-footer
		s += "</DIV>\n"; // snippet
		pageablediv.append(s)
    }

// start pager

   var p = "<DIV ALIGN='CENTER'>";
   if (starthit != 0) { // if offset is set...
  	  p += "<A CLASS='SEARCHRESULT_PAGER' HREF='#' OFFSET='"+prevhit+"' LIMIT='"+steps+"' ROLE='"+role+"'>";
	  p += i18n['prev'][lang]+'</A> ';
   }  
   for (i=1; i <= maxpagestoshow; i++) {
		if (i != currentpage) {
			targetedoffset = (i-1)*steps;
			p += "<A CLASS='SEARCHRESULT_PAGER' HREF='#' OFFSET='"+targetedoffset+"' LIMIT='"+steps+"' ROLE='"+role+"'>";
		}
		if (maxpagestoshow > 1) {p += i;}
		if (i != currentpage) {p += "</A>";}
		p += " ";
   }
   	if (nexthit != totalhits) {
		p += " <A CLASS='SEARCHRESULT_PAGER' HREF='#' OFFSET='"+nexthit+"' LIMIT='"+steps+"' ROLE='"+role+"'> ";
		p += i18n['next'][lang]+"</A>";
	}
	p += "</DIV>\n";
	
	pageablediv.append(p)
	newdiv.append(pageablediv)
   return newdiv
	
}

function processMap(res, mapdiv) {
		var points = new Array();
		var plines = new Array();
		var ii = 0
		if (res["result"]) { 
		for( i in res["result"]) {
			var result =  res["result"][i]
			ii++
			if (result["coordinates"]) {
				var coordinates = result["coordinates"]
				for (j in coordinates) {
					// let's mark it with the position
					coordinates[j]["Position"] = ii	
					points.push(coordinates[j]) // in a {'Latitude':xxx, 'Longitude':xxx} format
				}		
			} 
			if (result["polylines"]) {
				var polylines = result["polylines"]
				if (!_.isUndefined(polylines)) {
					for (j in polylines) {
						// let's mark it with the position
						polylines[j]["Position"] = ii	
						plines.push(polylines[j]) // in a {'Latitude':xxx, 'Longitude':xxx} format
					}
				}
			}
		}	
		}
		createGoogleMap(mapdiv, points, plines)
	
}

function generateSearchResultExplanationDIV(searchresult, su, role, options) {

	var context = "searchresult"
	var canadmin = (su || role.toLowerCase() == "col-admin")
	
	var id = options.id
	
	// newdiv
	var newdiv = $("<DIV ID='rrs-"+context+"-explanation-"+id+"' CLASS='main-slidable-div' "+
	" TITLE='"+i18n['results'][lang]+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	var s = "<DIV CLASS='rrs-pageable'>" 
	
	var explanation = searchresult['explanation']	
	
	if (explanation) {
		
		if (explanation['feedback']) text += explanation['feedback']
		if (explanation['tags']) {
			s += "<DIV ID='rrs-searchresult-explanation-tags' style='display:table;'>"
			var tags = explanation['tags']	
			for(var i in tags){
				s += "<DIV ID='tag_"+(i+1)+"' CLASS='tag tag_type_"+tags[i]['type']+"'" ;
				s += " value='"+tags[i]['name']+"'>"+tags[i]['name']+" ";
				s += "<A CLASS='tag_remove'>&times;</A></DIV>";
			}
		}
	}
	s += "</DIV>"		
	newdiv.html(s)
	return newdiv
}
