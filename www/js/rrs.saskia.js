var Saskia = (function ($) {
	"use strict"

    var display = function() {
		var query = $.trim(Rembrandt.Util.getQueryVariable("q"));
		var queryterms; if (query) queryterms = query.split(/\s+/);

		var tags = null
		lang = $("HTML").attr("lang")	// must be here!
		var limit = Rembrandt.Util.getQueryVariable("l"); if (_.isUndefined(limit)) limit = 10;
		var offset = Rembrandt.Util.getQueryVariable("o"); if (_.isUndefined(offset)) offset = 0;
		var userid = Rembrandt.Util.getUserId()		
		var user = Rembrandt.Util.getUser()
		var api_key = Rembrandt.Util.getApiKey()
	
		var pubkey = $("A.USER_ADMIN").attr("USR_PUB_KEY")
		var su = Rembrandt.Util.validateSu(pubkey)
		var role = (su ? "admin" : "saskia")
	
		var main_body = $("#main-body")
		// not really slidable, but it must be included for hide/show
		// add this breadcrumble header, with or without query
		addBreadcrumbleHeader($("#rrs-homepage-collection").attr('title'), 'rrs-homepage-collection')			

		// let's query out stuff
		
		jQuery.ajax({type:'POST', url:Rembrandt.urls.restlet_saskia_collection_url,
			contentType:"application/x-www-form-urlencoded",
			data: "do=list-all&l="+limit+"&o="+offset+"&lg="+lang+"&api_key="+api_key,
			beforeSubmit: Rembrandt.Waiting.show(),
			success: function(response)  {
				if (response['status'] == -1 ) {
					Rembrandt.Waiting.error(response)
				} else {
					Rembrandt.Waiting.hide()
					$("#rrs-homepage-collection").append(
						Saskia.generateCollectionMainPageDIV(response['message'], su, role, {}))			
				}
			},
			error: function(response) {Rembrandt.Waiting.error(response)}
		})
	},

    generateCollectionMainPageDIV = function(response, su, role, options) {

		var ownablecollections = new Array();

		var cols = response['result']
		var perms = response['perms']

		// we need this, as pager will crop from here (normally, pageDIV creaters generate a main-slidable-div to encompass the pageable)
		var s = "<DIV>"

		s += "<DIV class='rrs-pageable'>"
	
		// pager
		s += createPagerNavigation({
			"context":"collection",
			"contexts":"collections", 
			"response":response,
			"role":"saskia", // just to compute servlet, anything but admin
			"action":"list-all", // ask pagers to list-all, not list by default
			"render":"generateCollectionMainPageDIV", // set this same fucntion as the div render (it defaults to generate-context-listDIV)
			"allowedSearchableFields":{
				"col_id":i18n['id'][lang], 
				"col_name":i18n['name'][lang], 
			} 
		})

		var max_number_of_collections_owned = response["max_number_collections_owned"]
		var collections_owned = response["collections_owned"]

		s += "<TABLE ID='rrs-collections-main-table' CLASS='tablesorter' BORDER=0>"
		s += "<THEAD><TR><TH>"+i18n['name'][lang]+"</TH><TH>"+i18n['owns'][lang]+"?</TH>";
		s += "<TH>"+i18n['canread'][lang]+"?</TH><TH>"+i18n['canwrite'][lang]+"?</TH><TH>";
		s += i18n['canadmin'][lang]+"?</TH></TR></THEAD>";
		s += "<TBODY>"; 
	
		for (i in cols) {
		
			var id = cols[i]['col_id']
			var role = (su ? "admin" : (perms[i]['uoc_can_admin'] ? "col_admin" : 
		 	(perms[i]['uoc_can_write'] ? "col_writer" : (perms[i]['uoc_can_read'] ? "col_reader" : ""  ) )))
		
			s += "<TR><TD><A HREF='#' CLASS='COLLECTION_SHOW' ID='"+id+"' ROLE='"+role+"' "+
		 	"TITLE='"+cols[i]['col_name']+"' TARGET='rrs-collection-show-"+id+"'>"+
			cols[i]['col_name']+"</A></TD><TD>"+
			Rembrandt.Util.printYesOrNo(perms[i]['uoc_own'])+"</TD><TD>"+
			Rembrandt.Util.printYesOrNo(perms[i]['uoc_can_read'])+"</TD><TD>"+
			Rembrandt.Util.printYesOrNo(perms[i]['uoc_can_write'])+"</TD><TD>"+
			Rembrandt.Util.printYesOrNo(perms[i]['uoc_can_admin'])+"</TD></TR>"
		
			if (perms[i]['uoc_own'] == true) ownablecollections.push(id)
		}	
		s += "</TBODY></TABLE>"
		s += "<SCRIPT>$('#rrs-collections-main-table').tablesorter(); </SCRIPT>"
		s += "Total: "+response['total']+" collections."
	
		var collections_owned = ownablecollections.length
	
		if (collections_owned < max_number_of_collections_owned) {
			
			// add side menu if not exists 
			if ($("#main-side-menu-section-collection-create").size() ==0) {
				$("#main-side-menu").append("<DIV CLASS='main-side-menu-section' "+
				" ID='main-side-menu-section-collection-create'> "+
				"<DIV CLASS='main-side-menu-section-body'>"+
				"<DIV CLASS='main-side-menu-section-body-element'><A HREF='#' CLASS=\"COLLECTION_CREATE\" ROLE='"+role+"'>"+
				i18n["createnew"][lang]+" "+i18n["collection"][lang]+"...</A></DIV></DIV>")
			}
			if (lang == "pt") {	
				s += "<P>Tem "+collections_owned+" colecções que lhe pertencem, num máximo de "+max_number_of_collections_owned+". "
				s += "Pode <A HREF='#' CLASS=\"COLLECTION_CREATE\" ROLE='"+role+"'>criar mais uma colecção</A>.</P>"
			} else {
				s += "<P>You have "+collections_owned+" collections owned, in a maximum of "+max_number_of_collections_owned+". "
				s += "You can <A HREF='#' CLASS=\"COLLECTION_CREATE\" ROLE='"+role+"'>create a new collection</A>.</P>"
			}
		}
		s += "</DIV></DIV>"
		return $(s)	
	};

	return {
		"display":display,
		"generateCollectionMainPageDIV" : generateCollectionMainPageDIV
	}
})(jQuery);