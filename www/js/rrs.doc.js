var Rembrandt = Rembrandt || {};

Rembrandt.Doc = (function ($) {
	"use strict";

	$(function () {

		$('A.DOC_CREATE').live("click", function (ev, ui) {
			ev.preventDefault();
			var a_clicked = $(this),
				api_key = Rembrandt.Util.getApiKey(),
				title = a_clicked.attr("title") || a_clicked.text(),
				col_id = parseInt(a_clicked.attr('id'));
		
			showSlidableDIV({
				"title"			: title,
				"target"		: a_clicked.attr("TARGET"),
				"role"			: a_clicked.attr('ROLE'),
				"slide"			: getSlideOrientationFromLink(a_clicked),
				"ajax"			: false,
				"restlet_url"	: null,
				"postdata"		: null,
				"divRender"		: generateDocCreateDIV, 
				"divRenderOptions":{'col_id':col_id},
				"sidemenu"		: null, 
				"sidemenuoptions":{}
			});
		});

		$('A.DOC_DELETE').live("click", function(ev, ui) {
			ev.preventDefault();
			modalDocDelete($(this));
			_refreshBody();
		});
		
		// click to show a doc into a new/existing tab
		$('A.DOC_SHOW').live("click", function(ev, ui) {

			ev.preventDefault();
			var a_clicked = $(this),
				doc_id = parseInt(a_clicked.attr("ID")),
				doc_original_id = a_clicked.attr("DOC_ORIGINAL_ID"),
				api_key = Rembrandt.Util.getApiKey(),
				title = a_clicked.attr("title") || doc_original_id;

			showSlidableDIV({
				"title"			: title,
				"target"		: a_clicked.attr("TARGET"),
				"role"			: a_clicked.attr('ROLE'),
				"slide"			: getSlideOrientationFromLink(a_clicked),
				"ajax"			: true,
				"restlet_url"	: Rembrandt.Util.getServletEngineFromRole(a_clicked.attr('ROLE'), "doc")+"/show",
				"data"			:{"doc_id":doc_id, "lg":lang, "api_key":api_key},
				"divRender"		: generateDocShowDIV, 
				"divRenderOptions":{},
				"sidemenu"		: "doc", 
				"sidemenuoptions": {'id':doc_id}
			});
		});

		$('A.DOC_METADATA').live("click", function(ev, ui) {

			ev.preventDefault();
			var a_clicked = $(this),
			 	doc_id = parseInt(a_clicked.attr("ID")),
				doc_original_id = a_clicked.attr("DOC_ORIGINAL_ID"),
				api_key = Rembrandt.Util.getApiKey(),
				title = a_clicked.attr("title") || doc_original_id,
				targeted_div_title = "rembrandt-detaildoc-"+doc_id;
				
			showSlidableDIV({
				"title"			: title,
				"target"		: a_clicked.attr("TARGET"),
				"role"			: a_clicked.attr('ROLE'),
				"slide"			: getSlideOrientationFromLink(a_clicked),
				"ajax"			: true,
				"restlet_url"	: Rembrandt.Util.getServletEngineFromRole(a_clicked.attr('ROLE'), "doc")+"/metadata",
				"data"			: {"doc_id":doc_id, "lg":lang, "api_key":api_key},
				"divRender"		: generateDocMetadataDIV, 
				"divRenderOptions":{},
				"sidemenu"		: "doc", 
				"sidemenuoptions": {'id':doc_id},
				'callback': function() {
					if (coordinates) {
						// each unique map 
						createGoogleMap($("#"+targeted_div_title+" #stats-map"), coordinates, []);
					}
				}
			});
		});
	});	

	var generateDocMetadataDIV = function (doc, su, role, options) {

		var context="doc",
			canadmin = (su || role.toLowerCase() == "col-admin"),
			data = {
				"context": "doc",
				"doc_id" : doc["doc_id"],
				"title"  : Rembrandt.Util.shortenTitle(doc["doc_original_id"]),
				"doc_content": doc["doc_content"]
			},
			template = "<DIV ID='rrs-{{context}}-metadata-{{doc_id}}' CLASS='main-slidable-div' \
			TITLE='{{title}}' STYLE='display:none;overflow:auto;'>\
				{{{doc_content}}}\
			</DIV>",
			div = Mustache.to_html(template, data);
			
		return $(div);
	},

	_refreshBody = function() {
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-collection-doc-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
	},
	
	generateDocShowDIV = function (response, su, role, options) {

		var context="doc",
			doc = response["doc"],
			nes = response["nes"],
			patches = response["patches"],
			commits = response["commits"],
			doc_id = doc["doc_id"],
			doc_original_id = doc["doc_original_id"],
			doc_content = doc["doc_content"],
			canadmin = (su || role.toLowerCase() == "col-admin"),
			display = $("<DIV ID='rrs-"+context+"-show-"+doc_id+"' CLASS='main-slidable-div rrs-doc-display' "+
		" TITLE='"+Rembrandt.Util.shortenTitle(doc_original_id)+"' STYLE='display:none;overflow:auto;'></DIV>"),
			html = Rembrandt.Api.Rembrandt2HTML(doc_content, nes);

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
			'context'			: context,
			'id'				: parseInt(button.attr("ID")),
			'info'				: button.attr('TITLE'),
			'servlet_url'		: Rembrandt.Util.getServletEngineFromRole(Rembrandt.Util.getRole(button), context)+"/delete",
			'data' : {
				"id"		: parseInt(button.attr("ID")),
				"lg"		: lang,
				"api_key"	: Rembrandt.Util.getApiKey()
			},
			'success_message'	: i18n['doc_deleted'][lang]
		})	
	};

	return {
		"generateDocMetadataDIV"	: generateDocMetadataDIV,
		"generateDocShowDIV" 		: generateDocShowDIV,
		"modalDocDelete"			: modalDocDelete
	}
	
}(jQuery));
