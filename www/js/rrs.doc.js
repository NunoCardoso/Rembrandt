Rembrandt = Rembrandt || {};

Rembrandt.Doc = (function ($) {
	"use strict"

	$(function () {

		$('A.DOC_CREATE').live("click", function(ev, ui) {
			ev.preventDefault();
			var a_clicked = $(this)
			var api_key = Rembrandt.Util.getApiKey()
			var title = (a_clicked.attr("title") ? a_clicked.attr("title") : a_clicked.text())
			var col_id = a_clicked.attr('id')	
		
			showSlidableDIV({
				"title": title,
				"target":a_clicked.attr("TARGET"),
				"role":a_clicked.attr('ROLE'),
				"slide": getSlideOrientationFromLink(a_clicked),
				"ajax":false,
				"restlet_url":null,
				"postdata":null,
				"divRender":generateDocCreateDIV, 
				"divRenderOptions":{'col_id':col_id},
				"sidemenu":null, 
				"sidemenuoptions":{}
			})				
		});

		$('A.DOC_DELETE').live("click", function(ev, ui) {
			ev.preventDefault();
			modalDocDelete($(this))
			// let's refresh the content. How? triggering the admin-pager link if the 
			// visible div is for a collection list or collection main page
			var divshown = $('DIV.main-slidable-div:visible')
	 
			if (divshown.attr('id') == 'rrs-collection-doc-list') {
				divshown.find("A.MANAGE_PAGER").trigger('click')
			}
		});
		
		// click to show a doc into a new/existing tab
		$('A.DOC_SHOW').live("click", function(ev, ui) {

			ev.preventDefault();
			var a_clicked = $(this)
			var doc_id = a_clicked.attr("ID")
			var doc_original_id = a_clicked.attr("DOC_ORIGINAL_ID")
			var api_key = Rembrandt.Util.getApiKey()
			var title = (a_clicked.attr("title") ? a_clicked.attr("title") : doc_original_id)
		 	
			showSlidableDIV({
				"title": title,
				"target":a_clicked.attr("TARGET"),
				"role":a_clicked.attr('ROLE'),
				"slide": getSlideOrientationFromLink(a_clicked),
				"ajax":true,
				"restlet_url":Rembrandt.Util.getServletEngineFromRole(a_clicked.attr('ROLE'), "doc"),
				"postdata":"do=show&doc_id="+doc_id+"&lg="+lang+"&api_key="+api_key,
				"divRender":generateDocShowDIV, 
				"divRenderOptions":{},
				"sidemenu":"doc", 
				"sidemenuoptions":{'id':doc_id}
			})
		});

		$('A.DOC_METADATA').live("click", function(ev, ui) {

			ev.preventDefault();
			var a_clicked = $(this)
			var doc_id = a_clicked.attr("ID")
			var doc_original_id = a_clicked.attr("DOC_ORIGINAL_ID")
			var api_key = Rembrandt.Util.getApiKey()
			var title = (a_clicked.attr("title") ? a_clicked.attr("title") : doc_original_id)
			var targeted_div_title = "rembrandt-detaildoc-"+doc_id
			showSlidableDIV({
				"title": title,
				"target":a_clicked.attr("TARGET"),
				"role":a_clicked.attr('ROLE'),
				"slide": getSlideOrientationFromLink(a_clicked),
				"ajax":true,
				"restlet_url":Rembrandt.Util.getServletEngineFromRole(a_clicked.attr('ROLE'), "doc"),
				"postdata":"do=metadata&doc_id="+doc_id+"&lg="+lang+"&api_key="+api_key,
				"divRender":generateDocMetadataDIV, 
				"divRenderOptions":{},
				"sidemenu":"doc", 
				"sidemenuoptions":{'id':doc_id},
				'callback': function() {
					if (coordinates) {
						// each unique map 
						createGoogleMap($("#"+targeted_div_title+" #stats-map"), coordinates, []);
					}
				}
			})
		});
	});	

	// TODO
	//function generateDocCreateDIV(response, su, role, options) {

	var generateDocMetadataDIV = function (doc, su, role, options) {

		var context="doc"
		var doc_id = doc["doc_id"]
		var doc_original_id = doc["doc_original_id"]

		var canadmin = (su || role.toLowerCase() == "col-admin")
		var newdiv = $("<DIV ID='rrs-"+context+"-metadata-"+doc_id+"' CLASS='main-slidable-div' "+
		" TITLE='"+Rembrandt.Util.shortenTitle(doc_original_id)+"' STYLE='display:none;overflow:auto;'></DIV>")

		newdiv.append(doc["doc_content"])
		return newdiv						
	},

	generateDocShowDIV = function (response, su, role, options) {

		var context="doc"
		var doc = response["doc"]
		var nes = response["nes"]
		var patches = response["patches"]
		var commits = response["commits"]
		
		var doc_id = doc["doc_id"]
		var doc_original_id = doc["doc_original_id"]
		var doc_content = doc["doc_content"]

		var canadmin = (su || role.toLowerCase() == "col-admin")

		// newdiv
		var display = $("<DIV ID='rrs-"+context+"-show-"+doc_id+"' CLASS='main-slidable-div rrs-doc-display' "+
		" TITLE='"+Rembrandt.Util.shortenTitle(doc_original_id)+"' STYLE='display:none;overflow:auto;'></DIV>")

		var html = Rembrandt.Api.Rembrandt2HTML(doc_content, nes)

		Rembrandt.Display.create(display)
		Rembrandt.Display.addDoc(display, doc)
		Rembrandt.Display.addDocumentContent(display, html["title"], html["body"])
		Rembrandt.Display.addOriginalNEs(display, nes)
		Rembrandt.Display.addPatches(display, patches)
		Rembrandt.Display.addCommits(display, commits)

		return display
	},
	
	modalDocDelete = function (button) {

		var context = "doc"

		Rembrandt.Modal.genericDeleteModel({
			'context':context,
			'id': button.attr("ID"),
			'info':button.attr('TITLE'),
			'servlet_url': Rembrandt.Util.getServletEngineFromRole(Rembrandt.Util.getRole(button), context),
			'postdata' : "do=delete&id="+button.attr("ID")+"&lg="+lang+"&api_key="+Rembrandt.Util.getApiKey(),
			'success_message' : i18n['doc_deleted'][lang]
		})	
	};

	return {
		"generateDocMetadataDIV"	: generateDocMetadataDIV,
		"generateDocShowDIV" 		: generateDocShowDIV,
		"modalDocDelete"			: modalDocDelete
	}
	
}(jQuery));
