var Rembrandt = Rembrandt || {};

Rembrandt.Display = (function ($) {
	"use strict"
	$(function () {
	
		$(".link-button, .NE").live("mouseover",function() {	
			$(this).css("cursor","pointer"); 
		});

		$(".link-button, .NE").live("mouseout",function() {	
			$(this).css("cursor","default"); 
		});
	
		$(".NE").live("click",function(ev) {

			ev.stopPropagation();	
			// crazy, but it seems that NE still gets unintended events
			// if the click is meant for the tag_edit, do nothing.
			if (($(ev.target).is(".tag_edit"))) return
			// if the click is a A[HREF], from one menu, do nothing.
			if (($(ev.target).is("A"))) return
			if (Rembrandt.Tooltip.isOpen($(this)) ) {
				Rembrandt.Tooltip.hide($(this));
			} else {
				Rembrandt.Tooltip.show($(this));
			}
		});
		
		$("#edit-mode-on").live("click",function(ev) {
			ev.preventDefault()
			// hide the popup menu where I came from...
			$(this).parents("DIV:first").hide()
			var display = Rembrandt.Display.getFrom( $(this) )
			Rembrandt.Display.markSelectedMenu(display, $(this).closest("li"))
			var rrs_screen = $("#rrs-doc-display-screen",  display)
			if (rrs_screen.attr("status") != "edit") {
				_initializeEditMode(rrs_screen)
				Rembrandt.Display.updateFor(rrs_screen, "edit")	
			}	
		});
		
		$("#select-mode-on").live("click", function(ev) {
			ev.preventDefault()	
			// hide the popup menu where I came from...
			$(this).parents("DIV:first").hide()
			var display = Rembrandt.Display.getFrom( $(this) )
			Rembrandt.Display.markSelectedMenu(display, $(this).closest("li"))
			var rrs_screen = $("#rrs-doc-display-screen", display )
			if (rrs_screen.attr("status") != "select") {
				_initializeSelectMode(rrs_screen);
				Rembrandt.Display.updateFor(rrs_screen, "select");
			}
		});
	
		$("#view-mode-on").live("click",function(ev) {
			ev.preventDefault()
			// hide the popup menu where I came from...
			$(this).parents("DIV:first").hide()
			var display = Rembrandt.Display.getFrom( $(this) )
			Rembrandt.Display.markSelectedMenu(display, $(this).closest("li"))
			var rrs_screen = $("#rrs-doc-display-screen", display )
			Rembrandt.Display.updateFor(rrs_screen, "default")
		});
		
		$("#save_commit").live("click",function(ev) {
			ev.preventDefault();
			// do when someone clicks on a commit
			var display = Rembrandt.Display.getFrom( $(this) ) ,
				original_nes = Rembrandt.Display.getOriginalNEs(display),
			 	display_nes = Rembrandt.Display.retrieveNEs(display),
			 	doc = Rembrandt.Display.getDoc(display),
				doc_id = doc.doc_id,
			 	difference = Rembrandt.Display.diffNEs(display, original_nes, display_nes);
			
			SaveCommitModal(display, difference, doc_id); 
		});
		
		$(".commit-on").live("click",function(ev) {
			ev.preventDefault();
			// do when someone clicks on a commit
			var display = Rembrandt.Display.getFrom( $(this) ) ;
		});
	});
	
	var create = function (element, options) {
	   element.append(_generate(options))
	   element.find("#rrs-doc-display-menu div UL LI").hover(function() {
			$(this).children("DIV").show()
		}, function() { 
		   $(this).children("DIV").hide()
		});
	}, 
	
	_generate = function (options) {
		var data = {
			view 			 : i18n['view'][lang],
			mode 			 : i18n['mode'][lang],
			patch 			 : i18n['patch'][lang],
			commit 			 : i18n['commit'][lang],
			showalltooltips  : i18n['showalltooltips'][lang],
			hidealltooltips  : i18n['hidealltooltips'][lang],
			showallrelations : i18n['showallrelations'][lang],
			hideallrelations : i18n['hideallrelations'][lang],
			viewon			 : i18n['viewon'][lang],
			selecton		 : i18n['selecton'][lang],
			editon			 : i18n['editon'][lang]
		},
		menu = (!_.isUndefined(options) && !_.isUndefined(options.menu) ? options.menu : true),
		patch = (!_.isUndefined(options) && !_.isUndefined(options.patch) ? options.patch : true),
		template = "\
	<DIV ID='rrs-doc-display-header'>\
		<DIV ID='rrs-doc-display-status'></DIV>";
	
		if (menu) {
			template += "\
			<DIV ID='rrs-doc-display-menu'>\
		<DIV>\
			<UL>\
				<LI>{{view}}<DIV class='main-nav-submenu'>\
					<UL>\
						<LI><A ID='show-all-tooltips'  HREF='#'>{{showalltooltips}} </A></LI>\
						<LI><A ID='hide-all-tooltips'  HREF='#'>{{hidealltooltips}} </A></LI>\
						<LI><A ID='show-all-relations' HREF='#'>{{showallrelations}}</A></LI>\
						<LI><A ID='hide-all-relations' HREF='#'>{{hideallrelations}}</A></LI>\
					</UL>\
				</DIV></LI>\
				<LI>{{mode}}<DIV class='main-nav-submenu'>\
					<UL>\
						<LI><A ID='view-mode-on'   HREF='#'>{{viewon}}  </A></LI>\
						<LI><A ID='select-mode-on' HREF='#'>{{selecton}}</A></LI>\
						<LI><A ID='edit-mode-on'   HREF='#'>{{editon}}  </A></LI>\
					</UL>\
				</DIV></LI>";
		
		if (patch) {
			template += "\
				<LI>{{patch}}<DIV class='main-nav-submenu main-nav-submenu-patch'>\
					<UL></UL>\
				</DIV></LI>\
				<LI>{{commit}}<DIV class='main-nav-submenu main-nav-submenu-commit'>\
					<UL></UL>\
				</DIV></LI>";
		}	
		template += "\
			</UL>\
		</DIV>\
		<DIV id='main-nav-changed'></DIV>\
	</DIV>";
		}
		template += "\
	</DIV>\
	<DIV ID='rrs-doc-display-body'>\
		<DIV ID='rrs-doc-display-canvas'></DIV>\
		<DIV ID='rrs-doc-display-data'></DIV>\
		<DIV ID='rrs-doc-display-screen'>\
			<DIV ID='rrs-document-title'></DIV>\
			<DIV ID='rrs-document-body'></DIV>\
		</DIV>\
	</DIV>\
	<DIV ID='rrs-tooltips' style='display:none;'></DIV>";

		return Mustache.to_html(template, data);
	},

	SaveCommitModal = function (display, difference, doc_id) {
		
		var nes = []

		for (var i in difference) {
			var action = difference[i]["action"]
			var action_class = (action == "+" ? "add": (action == "-" ? "remove" : ""))
			var ne = difference[i]["ne"]["ne"]
			var ne_html = Rembrandt.Api.NE2HTML( 
				difference[i]["ne"]["ne"], 
				difference[i]["ne"]["sentence"],
				difference[i]["ne"]["term"]
			)
			nes.push({"ne":"<DIV class='ne_commit_wrapper "+action_class+"'>"+ne_html+"</DIV>"})
		};
		
		var data = {
			"l_NE_differences" 		: "Here is the differentes marked for commit",
			"nes"					: nes,
			"l_yesbutton"			: i18n["yes"][lang]+", "+i18n["save"][lang]+" "+i18n["commit"][lang],
			"l_nobutton"			: i18n["no"][lang]+", "+i18n["cancel"][lang]
		},

				template = "\
<div id='SaveCommitModal' class='rembrandt-modal' style='width:300px;'>\
	<div class='rembrandt-modal-escape'>{{l_pressescape}}</div>\
	<div style='text-align:center; padding:10px;overflow:scroll;min-height:200px;'>{{l_NE_differences}}:\
		<div id='NEs' style='display:inline; font-weight:bold;'>\
		{{#nes}}\
			{{{ne}}}\
		{{/nes}}\
		</div>\
	</div>\
	<div style='text-align:center;'>\
		<input type='button' id='YesButton' value='{{l_yesbutton}}'>\
		<input type='button' id='NoButton' value='{{l_nobutton}}'>\
	</div>\
</div>";
		
		$.modal(Mustache.to_html(template, data), {

			onShow: function (dialog) {

				dialog.data.find("#YesButton").click(function(ev) {
					ev.preventDefault();
					
					var data = {
						"cmm_commit" : difference,
						"api_key": Rembrandt.Util.getApiKey(),
						"lang"	: lang,
						"cmm_doc" : doc_id 
					}
					
					jQuery.ajax({ 
						type			: "POST", 
						url				: Rembrandt.urls.restlet_saskia_commit_url+"/save", 
						contentType		: "application/json",
						data			: JSON.stringify(data),
						beforeSubmit	: Rembrandt.Waiting.show(),
						success: function (response) {
							if (response["status"] == -1) {
								Rembrandt.Waiting.error(response)
							} else {
								Rembrandt.Waiting.hide({
									message :"Commit saved",
									when	: 5000
								})
								Rembrandt.Display.addCommitToMenu(display, response["message"])
								Rembrandt.Display.markAsSaved(display)
							}
						},
						error: function (response) {
							Rembrandt.Waiting.error(response)
						}
					});
					$.modal.close();
	 			});
				dialog.data.find("#NoButton").click(function(ev) {
					ev.preventDefault();
					$.modal.close();
	 			})
			},
			overlayCss:{backgroundColor: '#888', cursor: 'wait'}
		});
	},

	addDocumentContent = function (display, doctitle, docbody) {
		_addDocumentTitle (display, doctitle) 
		_addDocumentBody (display, docbody) 
	},
		
	_addDocumentTitle = function (display, doctitle) {
		display.find("#rrs-document-title").html(doctitle)
	},

	_addDocumentBody = function (display, docbody) {
		display.find("#rrs-document-body").html(docbody)
	},

	_clean = function (docdisplay) {
		$("#rrs-doc-display-status", docdisplay).html("Ready.")
		$("#rrs-doc-display-canvas", docdisplay).empty()
		$("#rrs-document-title", docdisplay).empty()
		$("#rrs-document-body", docdisplay).empty()
	},
	
	// change Rembrandt display to show...
	_changeScreenFor = function(display, newstate) {

		// backup initial color
		if (! display.attr("original-background-color")) {
			display.attr("original-background-color", 
		   display.css("background-color"))
		}
		if (! display.attr("original-border-color")) {
			display.attr("original-border-color", 
			display.css("border-color"))
		}
		switch(newstate) {
		case "relations":
			display.animate({ backgroundColor: "#aaaaaa"}, 500)
			display.css("border-color","#888888");
		break;

		case "edit":
			display.animate({ backgroundColor: "#ffe0e0"}, 500);
			display.css("border-color","#ff0000");
		break;

		case "select":
			display.animate({ backgroundColor: "#d8F5FF"}, 500);
			display.css("border-color","#7AC5CD");
		break;
		}
		display.attr("status",newstate)	
	},
	
	_unchangeScreenFor = function (display, currstate) {

		var change = true,
			canvas = display.siblings("#rrs-doc-display-canvas")
		switch(currstate) {
			case "relations":
			// if there's an active canvas, don't change.
				if  ($(".NEcanvas", canvas).length > 0) {change = false}
			break;
			case "edit":
				_destroyEditMode(display)
			break;
			case "select":
				_destroySelectMode(display)
			break;
		}
		if (change == true) {
			if (display.attr("original-background-color")) {
				display.animate({ 
		     backgroundColor: display.attr("original-background-color") })
				display.css('border-color', display.attr("original-border-color") )
				display.attr("status",'default')
			}
		}	
	},

	_initializeEditMode = function(display) {
		$(".NE", display).append("<DIV CLASS='tag_edit'>&Delta;</DIV>")
	},
	
	updateFor = function (display, newstate) {
		var currstate = display.attr('status')
		if (newstate == currstate) return;
		if ((newstate != "default") && (currstate == "default")) {
			_changeScreenFor(display, newstate)
		}
		if ((newstate == "default") && (currstate != "default")) {
			_unchangeScreenFor(display, currstate)
		}
		if ((newstate != "default") && (currstate != "default")) {
			_unchangeScreenFor(display, currstate)
			_changeScreenFor(display, newstate)
		}
	},

	markAsChanged = function(display) {
		display.find('#main-nav-changed').html("*")
		var save = display.find(".main-nav-submenu-commit ul li a#save_commit")
		if (save.size() == 0) {
			display.find(".main-nav-submenu-commit ul").prepend(
				"<LI><A ID='save_commit' HREF='#'>"+i18n["save"][lang]+"...</A></LI>"
			);
		}
	},

	markSelectedMenu = function(display, li) {
		li.siblings().removeClass("selected")
		li.addClass("selected")
	},
	
	markAsSaved = function(display) {
		display.find('#main-nav-changed').html("")
		var save = display.find(".main-nav-submenu-commit ul li a#save_commit").parent().remove()
	},
	
	hasChanged = function(display) {
		return display.find('#main-nav-changed').html() != ""
	},
	
	addCommitToMenu = function (display, commit) {
		var li = $("<LI><A CLASS='commit-on' HREF='#' COMMIT_ID='"+commit.cmm_id+"'>"+
		commit.cmm_id+": "+commit.cmm_date+"</A></LI>")
		li.data("commit", commit)
		display.find(".main-nav-submenu-commit ul").append(li)
	},
	
	wipe = function (display) {
		_clean(display)
		Rembrandt.Tooltip.hideAll($("#rrs-doc-display-screen", display)) 
		Rembrandt.Display.updateFor( $("#rrs-doc-display-screen", display), "default")
	},
	
	_initializeSelectMode = function (display) {
		 //delay helps prevent recursive selection. The menu hovers over selection, and menu click 
		// could trigger a recursive selection. Well, delay is better than ev.stopPropagation

		var stat = $("#rrs-doc-display-status", display.parents(".rrs-doc-display"))
		display.selectable({filter:'li[t]',  delay: 100 })

		display.bind("selectablestart", function() {
			stat.html("Drag to choose terms. Drag with Ctrl/Meta for multiple terms.")
		});
		display.bind("selectablestop", function() {
			stat.html("Ended selection.")
			Rembrandt.ContextMenu.addSelectionMenu( $(".ui-selected", display))
		});	
	},
	
	// leave edit mode
	_destroyEditMode = function(display) {
		// remove tag edits, you'll remove associated tooltip menus	
		$(".NE", display).each(function() {
			Rembrandt.DisplayNE.destroyEditButton($(this))
		});
	},
	
	// destroy a select mode
	_destroySelectMode = function (display) {
	// clear selected stuff
		$(".ui-selected", display).removeClass("ui-selected")
		display.selectable('destroy');
	},
	
	addOriginalNEs = function (display, nes) {
		display.find("#rrs-doc-display-data").data("nes", nes)
	},

	getOriginalNEs = function (display) {
		return display.find("#rrs-doc-display-data").data("nes")
	},
	
	addDoc = function (display, doc) {
		display.find("#rrs-doc-display-data").data("doc", doc)
	},

	getDoc = function (display) {
		return display.find("#rrs-doc-display-data").data("doc")
	},
		
	retrieveNEs = function (display) {
		var titleNEs = $("#rrs-document-title div.NE", display)
		var bodyNEs = $("#rrs-document-body div.NE", display)
		var doc = Rembrandt.Display.getDoc(display)

		var nes = []
		$.each(titleNEs, function() {
			nes.push( 
				_NEtoJSON( {
					ne: this, 
					section: "T",
					doc: doc.doc_id
				}) 
			)
		})
		
		$.each(bodyNEs, function() {
			nes.push(
				_NEtoJSON( {
					ne: this,
					section: "B",
					doc: doc.doc_id
				}) 
			)
		})
		return nes
	},
	
	_NEtoJSON = function(options) {
		var res = {
			doc: options.doc,
			ne: {
				ne_id: parseInt($(options.ne).attr("NE_ID")),
				ne_name: {
					nen_name: Rembrandt.DisplayNE.getTermsFrom($(options.ne))
				}
			},
			section: options.section,
			sentence: parseInt($(options.ne).attr("S")),
			term: parseInt($(options.ne).attr("T"))
		};
		
		if (!_.isUndefined($(options.ne).attr("CATEGORY")) && 
			$(options.ne).attr("CATEGORY") != "null") {
			res["ne"]["ne_category"] = {
				nec_category: "@"+$(options.ne).attr("CATEGORY")
			}
		}
		if (!_.isUndefined($(options.ne).attr("TYPE")) && 
			$(options.ne).attr("TYPE") != "null") {
			res["ne"]["ne_type"] = {
				net_type: "@"+$(options.ne).attr("TYPE")
			}
		}
		if (!_.isUndefined($(options.ne).attr("SUBTYPE")) && 
			$(options.ne).attr("SUBTYPE") != "null") {
			res["ne"]["ne_subtype"] = {
				nes_subtype: "@"+$(options.ne).attr("SUBTYPE")
			}
		}
		return res
	},
	
	diffNEs = function (display, original_nes, display_nes) {
		
		var original_nes_clone =  original_nes.slice(0),
		 	display_nes_clone  =  display_nes.slice(0),
			res = [] 
		
		$.each(original_nes_clone, function(ka,va) {
			$.each(display_nes_clone, function(kb,vb) {      
				if( va.doc == vb.doc && 
					va.ne.ne_id == vb.ne.ne_id && 
					va.section == vb.section && 
					va.sentence == vb.sentence && 
					va.term == vb.term) {
						// mark identical ones	
						va["common"] = true
						vb["common"] = true 
				}
			 });   
		});
		
		$.each(original_nes_clone, function(ka,va) {
			if (_.isUndefined(va["common"])) {
				res.push({
					"action": "-",
					"ne" : va
				})
			}
		});
		$.each(display_nes_clone, function(ka,va) {
			if (_.isUndefined(va["common"])) {
				res.push({
					"action": "+",
					"ne" : va
				})
			}
		});
		return res
	},
	
	getFrom = function (el) {
		return el.parents(".rrs-doc-display")
	},
	
	addNEs = function (display, nes) {
		display.find("#rrs-doc-display-data").data("nes", nes)
	},
	
	addPatches = function (display, patches) {
		display.find("#rrs-doc-display-data").data("patches", patches)
	},

	addCommits = function (display, commits) {
		display.find("#rrs-doc-display-data").data("commits", commits)
		$.each(commits, function() {
			Rembrandt.Display.addCommitToMenu(display, this)
		})
	};
	
	return {
		"create" 			: create,
		"getFrom"			: getFrom,
		"updateFor"			: updateFor,
		"wipe"				: wipe,
		"addDocumentContent": addDocumentContent,
		"addCommitToMenu"	: addCommitToMenu,
		"markAsChanged"		: markAsChanged,
		"markAsSaved"		: markAsSaved,
		"markSelectedMenu"	: markSelectedMenu,
		"hasChanged"		: hasChanged,
		"addOriginalNEs"	: addOriginalNEs,
		"getOriginalNEs"	: getOriginalNEs,
		"addDoc"			: addDoc,
		"getDoc"			: getDoc,
		"addPatches"		: addPatches,
		"addCommits"		: addCommits,
		"retrieveNEs"		: retrieveNEs,
		"diffNEs"			: diffNEs,
		"SaveCommitModal"	: SaveCommitModal
	}
	
}(jQuery));

Rembrandt.Tooltip = (function ($) {
	"use strict"

	$(function () {
		
		$("#show-all-tooltips").live("click", function(ev) {
			ev.preventDefault()
			// hide the popup menu where I came from...
			$(this).parents("DIV:first").hide()
			var rrs_screen = $("#rrs-doc-display-screen", Rembrandt.Display.getFrom($(this)))
			Rembrandt.Tooltip.showAll(rrs_screen);
		});
		
		$("#hide-all-tooltips").live("click", function(ev) {
			ev.preventDefault()
			// hide the popup menu where I came from...
			$(this).parents("DIV:first").hide()
			var rrs_screen = $("#rrs-doc-display-screen", Rembrandt.Display.getFrom($(this)))
			Rembrandt.Tooltip.hideAll(rrs_screen);
		});

	});
		
	var show = function (ne) {	
		if (!Rembrandt.Tooltip.has(ne) ) {
			var tooltip=$("#rrs-tooltips", Rembrandt.Display.getFrom($(this)))
			if (ne.attr("title") === undefined) {ne.attr("title", "")}
			tooltip.append(_getContent(ne))
			_add(ne);
		}
		ne.btOn();
	},

	has = function(ne) {
		return (ne.data('bt-box') != null) 
	},
	
	_add = function (ne) {
		ne.bt({
			trigger: 'none', 		// I'll handle the click events, to solve nested DIVs...
			// get this HTML to display - for a given NE, and a civen classification

			contentSelector: "$('#"+ Rembrandt.DisplayNE.getUniqueIDfor(ne,'tooltip')+"')", 
			// add an opacity value
			fill: ne.css('background-color').replace(/rgb/,"rgba").replace(/\)$/,",0.8)"),
			textzIndex:       399,                
	    	boxzIndex:        398, 
	    	wrapperzIndex:    397,
			clickAnywhereToClose: false,
		    strokeWidth: 3, strokeStyle: '#a0b7c4',
			cssStyles: {padding: '5px', display:'block'},
			positions: ['top'],
			shadow: true, shadowOffsetX: 5, shadowOffsetY: 5,
	    	shadowBlur: 8,	shadowColor: 'rgba(0,0,0,.6)', shadowOverlap: false,
			cornerRadius: 10, shrinkToFit: true, animate: true,
			noShadowOpts:     {strokeStyle: '#999'},
	  		spikeLength: 15, spikeGirth: 15
		});
	},
	
	refresh = function (ne) {
		hide(ne);
		show(ne);
	},

	showAll = function (display) {	
		$.eachCallback($(".NE", display), function() {
			if (!Rembrandt.Tooltip.isOpen($(this)) ) {
				Rembrandt.Tooltip.show($(this)) 
			}
		}, function(loopcount) {});
	},

	showMenu = function (menu) {
		menu.btOn();
	},
	
	hideAll = function (display) {
		$(".NE", display).each(function() {hide($(this))})
	},

	hide = function (ne) {
		ne.btOff();
	},
	
	destroy = function (ne, display) {
		$("#"+Rembrandt.DisplayNE.getUniqueIDfor(ne,'tooltip'), display).remove()
	},
	
	isOpen = function (ne) {
		return ne.hasClass("bt-active")
	},

	//create a display text to place in a tooltip
	_getContent = function (ne) {
		// build the baloon tooltip text

		var data = {
			"ne_id"					: ne.attr("id"),
			"tooltip_id"			: Rembrandt.DisplayNE.getUniqueIDfor(ne,'tooltip'),
			"ne_terms"				: Rembrandt.DisplayNE.getTermsFrom(ne),
			"ne_classifications"	: Rembrandt.DisplayNE.getClassificationsFrom(ne),
			"sentence"				: i18n['sentence'][lang],
			"s"						: ne.attr("S"),
			"term"					: i18n['term'][lang],
			"t"						: ne.attr("T"),
			"lang"					: lang,
			"showrelations"			: i18n['showrelations'][lang],
			"detailne"				: i18n['nedetails'][lang]
		};
		
		if (ne.attr("ENTITY_ID") && ne.attr("ENTITY_ID") != "null") {
			data["entity"] = {
				"DBpedia" 	: "DBpedia",
				"id" 		: ne.attr("ENTITY_ID"),
				"class" 	: ne.attr("ENTITY_CLASS"),
				"resource"	: ne.attr("ENTITY_RESOURCE") 
			}
		}
		
		var template = "\
<DIV CLASS='NEtooltip' STYLE='padding:3px' ID='{{tooltip_id}}'>\
	<P><B>{{ne_terms}}</B></P>\
	<P><I>{{ne_classifications}}</I></P>\
	<P>{{sentence}}: {{s}} {{term}}: {{t}}</P>\
	{{#entity}}\
		<P>Ent. ID: {{id}}</P>\
		<P>{{DBpedia}}: <A HREF='http://dbpedia.org/resource/{{resource}}'>{{resource}}</A></P>\
	{{/entity}}\
	<P><A class='link-button show-relations' neid='{{ne_id}}' lang='{{lang}}'>{{showrelations}}</A></P>\
	<P><DIV CLASS='Relations'></DIV></P>\
	<P><A CLASS='link-button DETAILNE' HREF='#' NE_ID='{{ne_id}}>{{detailne}}</A></P>\
</DIV>";
	
		return Mustache.to_html(template, data);
	}

	return {
		"_add"		: _add,
		"has"		: has,
		"hide"		: hide,
		"hideAll"	: hideAll,
		"refresh"	: refresh,
		"show"		: show,
		"showAll" 	: showAll,
		"showMenu" 	: showMenu,
		"destroy"	: destroy,
		"isOpen"	: isOpen
	}	
})(jQuery);

Rembrandt.Relation = (function ($) {
	"use strict"
	
	$(function () {
	
		$("#show-relations").live("click",function(ev) {
	
			ev.preventDefault()
			// hide the popup menu where I came from...
			$(this).parents("DIV:first").hide()
			// get the NE id from the 'neid' attribute of the clicked link
			var ne = $("#"+$(this).attr("neid"));
			var rrs_screen = $("#rrs-doc-display-screen", Rembrandt.Display.getFrom($(this)))
			// if anchor action was 'show relations'

			if ( $(this).html() == i18n['showrelations'][lang] ) {
				//	print relations
				Rembrandt.Relation.show(ne, rrs_screen);
				// change display to improve the visibility of the lines		
				Rembrandt.Display.updateFor(rrs_screen, "relations");
			// the click is to close relations
			} else if ($(this).html() == i18n['hiderelations'][lang] ) {
				//hide Relations
				Rembrandt.Relation.hide(ne, rrs_screen);
				//restore it
				Rembrandt.Display.updateFor(rrs_screen, "default");
			}	
		});

		$("#show-all-relations").live("click", function(ev) {
			ev.preventDefault()
			// hide the popup menu where I came from...
			$(this).parents("DIV:first").hide()
			var rrs_screen = $("#rrs-doc-display-screen", Rembrandt.Display.getFrom($(this)))
			Rembrandt.Relation.showAll(rrs_screen)
			Rembrandt.Display.updateFor(rrs_screen, "relations");
		});
		
		
		$("#hide-all-relations").live("click", function(ev) {
			ev.preventDefault()
			// hide the popup menu where I came from...
			$(this).parents("DIV:first").hide()
			var rrs_screen = $("#rrs-doc-display-screen", Rembrandt.Display.getFrom($(this)))
			Rembrandt.Relation.hideAll(rrs_screen)
			Rembrandt.Display.updateFor(rrs_screen, "default");
		});

		
	});
	
	var _hasCanvas = function (ne, display) {
		return $("#"+Rembrandt.DisplayNE.getUniqueIDfor(ne,'canvas'), 
		display.siblings("#rrs-doc-display-canvas")).length
	},
	
	hideAll = function(display) {
		$(".NE", display).each(function() {
			if (_hasCanvas($(this), display)) {
				Rembrandt.Relation.hide($(this), display);
			}
		});
	}, 
	
	showAll = function (display) {	
		$(".NE", display).each(function() {
			if (!_hasCanvas($(this), display)) {
				Rembrandt.Relation.show($(this));
			}
		});
	},
	
	hide = function (ne, display_) {

		var display
		if (display_ === undefined) display = ne.parents('#rrs-doc-display-screen')
		else display = display_

		// change tooltip info
		var tooltip_id = "#"+Rembrandt.DisplayNE.getUniqueIDfor(ne,'tooltip')
		$(tooltip_id+" A[neid]").html(i18n['showrelations'][lang])
		$(tooltip_id+" .Relations").empty()

		// remove canvas 
		$("#"+Rembrandt.DisplayNE.getUniqueIDfor(ne,'canvas')).remove()

		// this ne will get the original colour
		ne.animate({ color: ne.attr('original-color')}, 500)

		// refresh the balloon tip
		if (Rembrandt.Tooltip.isOpen(ne)) {
			Rembrandt.Tooltip.refresh(ne)
		}	
	},
	
	// search sibling NEs for relations, update RI and RT info
	addIndirectInfo = function (display, ne) {
		var id = ne.attr('id')
		var indirectRels = new Array();
		var indirectReltypes = new Array();

		Rembrandt.DisplayNE.getSiblings(ne).each(function() {
			var otherne = $(this)
			var otherid = otherne.attr("id")
			if (otherid != id) {
				// look this NE relations also...
				var otherDirectRels = otherne.attr("RI");
				var otherDirectReltypes = otherne.attr("RT");
				if (!(otherDirectRels === undefined)) otherDirectRels = otherDirectRels.split(";");
				if (!(otherDirectReltypes === undefined)) otherDirectReltypes = otherDirectReltypes.split(";");

				// if there's a mention
				if  (!(otherDirectRels === undefined) && (otherDirectRels.indexOf(id)) > -1 )  {
					// and it's not already in the bag, add it
					if (indirectRels.indexOf(otherne.attr('id')) == -1) {
						indirectRels.push(otherne.attr('id'))
						indirectReltypes.push(otherDirectReltypes[ otherDirectRels.indexOf(id) ] )
					}
				}
			}
		});
		//Indirect Relation ID
		if (indirectRels.length >0) {ne.attr("IRI", indirectRels.join(";"))}
		if (indirectReltypes.length >0) {ne.attr("IRT", indirectReltypes.join(";"))}
	},
	
	show = function (ne, display_) {

		var display
		if (display_ === undefined) display = ne.parents('#rrs-doc-display-screen')
		else display = display_

		// change tooltip info
		var tooltip_id = "#"+Rembrandt.DisplayNE.getUniqueIDfor(ne,'tooltip')
		$(tooltip_id+" A[neid]").html(i18n['hiderelations'][lang])




		var canvas = display.siblings('#rrs-doc-display-canvas')
		// let's add a new canvas in the display div
		var canvasdiv = $(document.createElement("DIV")).addClass("canvas").addClass("NEcanvas")
		canvasdiv.attr("id",Rembrandt.DisplayNE.getUniqueIDfor(ne, 'canvas'))
		// Let's stretch it to the output area
		canvasdiv.css('width',display.css('width'))
		canvasdiv.css('height',display.css('height'))
	//	canvasdiv.css('opacity','1')
		// it has to be attached now, to get element positions. I can remove it later, it there's nothing to draw
		canvas.append(canvasdiv)	

		// initial line computations	
		var initialtop = (ne.offset().top) - (canvasdiv.offset().top)
		var initialleft = (ne.offset().left) - (canvasdiv.offset().left)
		var offsetleft = 3;
		var offsettop = 5;

		// print direct ones.
		var directRels = ne.attr("RI");
		var directReltypes = ne.attr("RT");
		if (!(directRels === undefined)) directRels = directRels.split(";");
		if (!(directReltypes === undefined)) directReltypes = directReltypes.split(";");
		if (!(directRels === undefined)) { 
			for(i in directRels) {
				var otherne = $("#"+directRels[i])
				var top = ((otherne.offset().top) - (ne.offset().top))
				var left = ((otherne.offset().left) - (ne.offset().left))

				// draw line
				canvasdiv.drawLine(initialleft + offsetleft, initialtop + offsettop, 
					initialleft + left + offsetleft, initialtop + top + offsettop, 
					  {color: ne.css('background-color'), stroke: 4});
				// update tooltip
				$(tooltip_id+" .Relations").append(
					"<P>&rArr; "+directReltypes[i]+": "+($("#"+directRels[i]).text()+"</P>"))
				// flag it: there are relations shown/drawn here
			}
		}

		// print indirect ones. addIndirectRelationInfo function was already called. 

		var indirectRels = ne.attr("IRI");
		var indirectReltypes = ne.attr("IRT");
		if (!(indirectRels === undefined)) indirectRels = indirectRels.split(";");
		if (!(indirectReltypes === undefined)) indirectReltypes = indirectReltypes.split(";");

		if (!(indirectRels === undefined)) { 
			for(i in indirectRels) {
				var otherne = $("#"+indirectRels[i])

				var top = ((otherne.offset().top)- (ne.offset().top))
				var left = ((otherne.offset().left)- (ne.offset().left))

				// draw line
				canvasdiv.drawLine(initialleft + offsetleft, initialtop + offsettop, 
					initialleft + left + offsetleft, initialtop + top + offsettop, 
					  {color: ne.css('background-color'), stroke: 4});
					// update tooltip
				$(tooltip_id+" .Relations").append(
					"<P>&lArr; "+indirectReltypes[i]+": "+($("#"+indirectRels[i]).text()+"</P>"))
				// flag it: there are relations shown/drawn here
			}
		}

		// if there's no relations for this NE
		if ( ! $(tooltip_id+" .Relations").html() ) 
			$(tooltip_id+" .Relations").append(i18n['norelations'][lang]) 

		// now, the .Relations must have a true or a false
		// add a face red... we've clicked it for relations 
		if (! ne.attr("original-color")) {	ne.attr("original-color", ne.css("color")) }
		ne.animate({ color: "#ff0000"}, 500)

		// we should call a redraw on the baloon to accomodate the new info.
		if (Rembrandt.Tooltip.isOpen(ne)) {Rembrandt.Tooltip.refresh(ne)}
	};

	return {
		"hideAll" : hideAll,
		"hide"		: hide,
		"show"		: show,
		"showAll"	: showAll,
		"addIndirectInfo":addIndirectInfo
	}
}(jQuery));

Rembrandt.ContextMenu = (function ($) {
	"use strict"
	
	$(function () {
	
		$(".EditMenu A").live("click", function(ev) {
			ev.preventDefault();
			var display = Rembrandt.Display.getFrom( $(this) ) ,
			 	action = $(this).attr("href"),
			 	ne = $(this).parents(".NE:first") 
//			ev.stopPropagation();
			if (action == "#change") {  Rembrandt.DisplayNE.changeNEclassModal(display, ne) }
			if (action == "#delete") {	Rembrandt.DisplayNE.confirmDeletionModal(display, ne) }
		});
	
		$(".SelectionMenu A").live("click", function(ev) {
			ev.preventDefault();
			var display = Rembrandt.Display.getFrom( $(this) ) ,
			 	action = $(this).attr("href"),
			 	terms = $(".ui-selected") 
			
//			ev.stopPropagation();
//			if (action == "#createalt") {}
			if (action == "#createne") {Rembrandt.DisplayNE.createNEModal(display, terms)}
			if (action == "#deleteall") {Rembrandt.DisplayNE.confirmDeletionAllModal(display, terms)}
		});
		
		$(".tag_edit").live("click", function(ev) {
			ev.preventDefault();
			ev.stopPropagation();
			if ( !Rembrandt.Tooltip.isOpen($(this)) ) {
				Rembrandt.ContextMenu.addEditMenu($(this))
			} else {
				Rembrandt.Tooltip.hide($(this))
			}
		});
		
	});
	
	var addEditMenu = function (ne_tag_edit) {
		if (!Rembrandt.Tooltip.has(ne_tag_edit)) {
			if (ne_tag_edit.attr("title") === undefined) {ne_tag_edit.attr("title","")}
			ne_tag_edit.bt(
				"<ul class='EditMenu NEmenu'>"+
			    	"<li><a href='#change'>"+i18n["change"][lang]+"...</a></li>"+
			    	"<li><a href='#delete'>"+i18n["delete"][lang]+"...</a></li>"+
			   	"</ul>", {
					trigger: 'none', 
					// get this HTML to display - for a given NE, and a civen classification
					contentSelector: "$('.EditMenu')", 
					fill: 'rgba(239, 250, 252, 1)', //#effafc
					clickAnywhereToClose: true,
					closeWhenOthersOpen: true,
					textzIndex:       499,                  // z-index for the text
					boxzIndex:        498,                  // z-index for the "talk" box (should always be less than textzIndex)
					wrapperzIndex:    497,
					strokeWidth: 1, 
					strokeStyle: '#a0b7c4',
					cssStyles: {padding: '5px', display:'block'},
					positions: ['top'],
					shadow: false,
					cornerRadius:0 , shrinkToFit: true, animate: true,
					spikeLength: 1, spikeGirth: 1
			});
		}
		if (!Rembrandt.Tooltip.isOpen(ne_tag_edit)) {
			Rembrandt.Tooltip.showMenu(ne_tag_edit);
		}
	},

	addSelectionMenu = function(selected_terms) {

		//let's index the tooltip to the last term
		if (selected_terms.length == 0) return;	
		var term = selected_terms.eq(selected_terms.length-1)
		if (Rembrandt.Tooltip.has(term)) {Rembrandt.Tooltip.showMenu(term)}
		else {
			if (term.attr("title") === undefined) {term.attr("title","")}
		
			term.bt(
			"<ul class='SelectionMenu NEmenu'>"+
	    	//"<li><a href='#createalt'>"+i18n["createalt"][lang]+"...</a></li>"+
	    	"<li><a href='#createne'>"+i18n["createne"][lang]+"...</a></li>"+
	    	"<li><a href='#deleteall'>"+i18n["deleteall"][lang]+"...</a></li>"+
	    	"</ul>",{
			trigger: 'none', 
			// get this HTML to display - for a given NE, and a civen classification
			contentSelector: "$('.SelectionMenu')", 
		    closeWhenOthersOpen: true,
			fill: 'rgba(239, 250, 252, 1)', //#effafc
	   		textzIndex:       599,  
	    	boxzIndex:        598,  
	    	wrapperzIndex:    597,
			clickAnywhereToClose: true,
		    strokeWidth: 1, strokeStyle: '#a0b7c4',
			cssStyles: {padding: '5px', display:'block'},
			positions: ['top'],
			shadow: false,
			cornerRadius:0 , shrinkToFit: true, animate: true,
	  		spikeLength: 1, spikeGirth: 1,
			postHide: function(){
				 $(selected_terms).removeClass("ui-selected")
			} 
			});
			Rembrandt.Tooltip.showMenu(term);
		}
	};

	return {
		"addEditMenu"		: addEditMenu,
		"addSelectionMenu" 	: addSelectionMenu,
	}
})(jQuery);

Rembrandt.DisplayNE = (function ($) {
	"use strict"

	var changeNEclassModal = function (display, ne) {
		
		var data = {
			"l_pressescape" 		: i18n['pressescape'][lang],
			"l_pickclassforNE"		: i18n['pickclassforNE'][lang],
			"ne_terms"				: Rembrandt.DisplayNE.getTermsFrom(ne),
			"ne_classifications"	: Rembrandt.DisplayNE.getClassificationsFrom(ne),
			"l_oldclass"			: i18n['oldclass'][lang],
			"l_newclass"			: i18n['newclass'][lang],
			"l_yesbutton"			: i18n["yes"][lang]+", "+i18n["change"][lang],
			"l_nobutton"			: i18n["no"][lang]+", "+i18n["cancel"][lang]
		},
		
		template = "\
<div id='modalChangeNEclassDialog' class='rembrandt-modal' style='height:180px;width:350px;'>\
	<div class='rembrandt-modal-escape'>{{l_pressescape}}</div>\
	<div style='text-align:center; padding:10px;'>{{l_pickclassforNE}} '\
		<div id='NEterms' style='display:inline; font-weight:bold;'>{{ne_terms}}</div>'.\
	</div>\
	<div style='text-align:left; padding:3px;'>{{l_oldclass}}: </div>\
	<div id='NEclass' style='text-align:left; padding:3px;display:inline; font-weight:italic;'>{{ne_classifications}}</div>\
	<div style='text-align:left; padding:3px;'>{{l_newclass}}: </div>\
	<div style='text-align:left; padding:3px;'>\
		<form class='selectNEclassForm'>\
			<select size=1 id='selectNEclass' onChange='Rembrandt.DisplayNE.fillNEtypes($(this));'></select>\
			<select size=1 id='selectNEtype' onChange='Rembrandt.DisplayNE.fillNEsubtypes($(this));' disabled></select>\
			<select size=1 id='selectNEsubtype' disabled></select>\
		<BR><BR>\
			<div style='text-align:center;'>\
				<input type='button' id='YesButton' value='{{l_yesbutton}}'>\
				<input type='button' id='NoButton' value='{{l_nobutton}}'>\
			</div>\
		</form>\
	</div>\
</div>";
		
	$.modal(Mustache.to_html(template, data), {
		onShow: function (dialog) {

			// now, fill selects with info
			Rembrandt.DisplayNE.fillNEclasses(dialog.data.find("#selectNEclass"))
			// filling types and subtypes are done automatically, don't worry.

			// Yes button clicked - let's change categories
			dialog.data.find("#YesButton").click(function(ev) {
				ev.preventDefault();
				Rembrandt.Tooltip.destroy(ne)
				ne.removeClass(ne.attr("CATEGORY"))
				ne.removeClass(ne.attr("TYPE"))
				ne.removeClass(ne.attr("SUBTYPE"))

				var selectedClass = dialog.data.find("#selectNEclass option:selected").text();
				if (selectedClass == "--") {selectedClass = i18n['unknownClass'][lang];}
				ne.attr("CATEGORY",selectedClass)

				var selectedType = dialog.data.find("#selectNEtype option:selected").text();
				if (selectedType === undefined || selectedType == "--") {ne.removeAttr("TYPE")}
				else {ne.attr("TYPE",selectedType)}
			
				var selectedSubtype = dialog.data.find("#selectNEsubtype option:selected").text();
				if (selectedSubtype === undefined || selectedSubtype == "--") {ne.removeAttr("SUBTYPE")}
				else {ne.attr("SUBTYPE",selectedSubtype)}
			
				ne.addClass(ne.attr("CATEGORY")).addClass(ne.attr("TYPE")).addClass(ne.attr("SUBTYPE"))	
				ne.addClass("NE")

				Rembrandt.DisplayNE.setupNE(display, ne)
				Rembrandt.Display.markAsChanged(display)
				$.modal.close();
 			});

			dialog.data.find("#NoButton").click(function(ev) {
				ev.preventDefault();
				$.modal.close();
 			})
		},
			overlayCss:{backgroundColor: '#888', cursor: 'wait'}
		});
	},

//modal window to delete one NE
	confirmDeletionModal = function (display, ne) {
		
		var data = {
			"l_pressescape" 		: i18n['pressescape'][lang],
			"l_areyousuredeleteNE"	: i18n['areyousuredeleteNE'][lang],
			"ne_terms"				: Rembrandt.DisplayNE.getTermsFrom(ne),
			"ne_classifications"	: Rembrandt.DisplayNE.getClassificationsFrom(ne),
			"l_withclass"			: i18n['withclass'][lang],
			"l_yesbutton"			: i18n["yes"][lang]+", "+i18n["delete"][lang],
			"l_nobutton"			: i18n["no"][lang]+", "+i18n["keep"][lang]
		},
		
		template = "\
<div id='modalConfirmNEDelectionDialog' class='rembrandt-modal' style='height:100px;width:300px;'>\
	<div class='rembrandt-modal-escape'>{{l_pressescape}}</div>\
	<div style='text-align:center; padding:10px;'>{{l_areyousuredeleteNE}} '\
		<div id='NEterms' style='display:inline; font-weight:bold;'>{{ne_terms}}</div>\
	', {{l_with_class}} \
		<div id='NEclass' style='display:inline; font-weight:italic;'>{{ne_classifications}}</div>\
	?</div>\
	<div style='text-align:center;'>\
		<input type='button' id='YesButton' value='{{l_yesbutton}}'>\
		<input type='button' id='NoButton' value='{{l_nobutton}}'>\
	</div>\
</div>";

		$.modal(Mustache.to_html(template, data), {
			onShow: function (dialog) {
				dialog.data.find("#YesButton").click(function(ev) {
					Rembrandt.DisplayNE.destroy(ne)
					Rembrandt.Display.markAsChanged( display )
					ev.preventDefault();
					$.modal.close();
				});
				dialog.data.find("#NoButton").click(function(ev) {
					ev.preventDefault();
					$.modal.close();
				})
			},
			overlayCss:{backgroundColor: '#888', cursor: 'wait'}
		});
	},    

//modal window to delete multiple NE
    confirmDeletionAllModal = function(display, selected_terms) {

		//first, get all NEs from the selected terms
		var nes = selected_terms.parents(".NE")
		// no NEs to erase - use different modal
		if (nes.length == 0) {
			$.modal("<div id='modalConfirmNEDelectionAllButNoNEFoundDialog' class='rembrandt-modal' style='height:100px;width:300px;'>"+
			"<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
			"<div style='text-align:center; padding:10px;'>"+i18n['noNEfound'][lang]+"</div>"+
			"<div style='text-align:center;'"+
			"<form><input type='button' id='NoButton' value='&nbsp;"+i18n["OK"][lang]+"&nbsp;'>"+
		"</form>"+
      "</div>"+
	"</div>", {
			onShow: function modalShow(dialog) {
				dialog.data.find("#NoButton").click(function(ev) {
					ev.preventDefault();
					$.modal.close();
 				})
			},
			overlayCss:{backgroundColor: '#888', cursor: 'wait'}
		});
		
// no NEs to erase - use different modal

	} else {
		
	$.modal( "<div id='modalConfirmNEDelectionAllDialog' class='rembrandt-modal' style='height:150px;width:300px;'>"+
      "<div class='rembrandt-modal-escape'></div>"+
	  "<div id='NEmessage' style='text-align:center; padding:15px;width:280px;'></div>"+
	  "<div style='text-align:center;'"+
		"<form><input type='button' id='YesButton' value='"+i18n["yes"][lang]+", "+i18n["deleteall"][lang]+"'>"+
			"<input type='button' id='NoButton' value='"+i18n["no"][lang]+", "+i18n["cancel"][lang]+"'>"+
		"</form>"+
      "</div>"+
	"</div>", {
		onShow: function modalShow(dialog) {
		// get all terms (ie, li entities with t attribute) to show
		
			dialog.data.find("#NEmessage").append(i18n["areyousuredeleteAllNE"][lang]);	
			nes.each(function()  {
				// clone, or else the original NE goes modal!
				var ne = $(this).clone()
				// put the terms.
				ne.html(getTermsFromNE($(this)))

				dialog.data.find("#NEmessage").append(ne);
				dialog.data.find("#NEmessage").append(" "); // to allow DIV break
				 
			});
			
			// Yes button clicked
			dialog.data.find("#YesButton").click(function(ev) {
				ev.preventDefault();
				nes.each(function()  {	
					Rembrandt.DisplayNE.destroy($(this)) 
				});
				Rembrandt.Display.markAsChanged( display )
				
				$.modal.close();

 			});
			dialog.data.find("#NoButton").click(function(ev) {
				ev.preventDefault();
				$.modal.close();
 			})
		},
		overlayCss:{backgroundColor: '#888', cursor: 'wait'}

		});
	}	
	},

//modal window to create a NE
 	createNEModal = function(display, selected_terms) {
	// convert jQuery to array, to better handle them
	if (selected_terms.length == 0) return
	var differentSentences = Rembrandt.DisplayNE.haveDifferentSentences(selected_terms)
	var differentTerms = Rembrandt.DisplayNE.haveUncontiguousTerms(selected_terms)

	var requiresAlt = Rembrandt.DisplayNE.haveNEoverlap(selected_terms)
	
	if (differentSentences || differentTerms) {
		// nops, no create NE for you...
		var msg = ""
		if (differentTerms) msg = i18n["selectionTermError"][lang]
		if (differentSentences) msg = i18n["selectionSentenceError"][lang]
		
		$.modal("<div id='modalCreateNEbutNoGoodTerms' class='rembrandt-modal' style='height:100px;width:300px;'>"+
      "<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
	  "<div style='text-align:center; padding:10px;'>"+i18n['noNEcreationBecause'][lang]+" "+ 
		"<div id='NEmessage' style='display:inline;'></div></div>"+
	  "<div style='text-align:center;'"+
		"<form><input type='button' id='NoButton' value='&nbsp;"+i18n["OK"][lang]+"&nbsp;'>"+
		"</form>"+
      "</div>"+
	"</div>", {
				onShow: function modalShow(dialog) {

					
				dialog.data.find("#NEmessage").append(msg)	
				dialog.data.find("#NoButton").click(function(ev) {
					ev.preventDefault();
					$.modal.close();
 				})
			},
			overlayCss:{backgroundColor: '#888', cursor: 'wait'}

		});
		
	// Ok, let's create
	} else {	
		$.modal("<div id='modalCreateNE' class='rembrandt-modal' style='height:150px;width:380px;'>"+
      "<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
	  "<div style='text-align:center; padding:10px;'>"+i18n['createNEwithclass'][lang]+":</div>"+
	  "<div style='text-align:left; padding:3px;'>"+
		"<form class='selectNEclassForm'>"+
			"<select size=1 id='selectNEclass' onChange='Rembrandt.DisplayNE.fillNEtypes($(this));'></select>"+
			"<select size=1 id='selectNEtype' onChange='Rembrandt.DisplayNE.fillNEsubtypes($(this));' disabled></select>"+
			"<select size=1 id='selectNEsubtype' disabled></select>"+
		"<BR><BR>"+i18n["entity"][lang]+"* : <INPUT TYPE='TEXT' ID='entity' SIZE=20><BR><BR>"+
			"<div style='text-align:center;'><input type='button' id='YesButton' value='"+i18n["yes"][lang]+", "+i18n["createnew"][lang]+"'>"+
			"<input type='button' id='NoButton' value='"+i18n["no"][lang]+", "+i18n["cancel"][lang]+"'></div></form>"+
      "</div>"+
	"</div>	", {
			onShow: function modalShow(dialog) {
					
				Rembrandt.DisplayNE.fillNEclasses(dialog.data.find("#selectNEclass"))
				
				dialog.data.find("#entity").autocomplete(restlet_dbosuggestion_url, {
					minChars:3, 
					dataType: "json", 
					mustMatch: false, 
					autoFill: false, 
					matchContains: false,
					multipleSeparator:"",
					extraParams:{"t" : "entity"},
					parse: theParse, 
					formatItem: theFormatItem,
					formatMatch: theFormatMatch,
					formatResult: theFormatResult
				})
				dialog.data.find("#entity").result(theResult)
				
				dialog.data.find("#YesButton").click(function(ev) {
					ev.preventDefault();
					
					/* magic happens here */
					var nextindex = getNextNEIndex($(".NE", selected_terms.parents("#rrs-doc-display-screen")))
					// wrap terms with the NE
					selected_terms.wrapAll("<DIV CLASS='NE' ID='"+nextindex+"'></DIV>")
					var ne = selected_terms.parent("#"+nextindex)

					/* Add S/T */
					ne.attr("S",ne.parents("UL:first").attr("S"))
					ne.attr("T",ne.children("LI:first").attr("T"))

					//change C1/C2/C3 properties
					var selectedClass = dialog.data.find("#selectNEclass option:selected").text();
					if (selectedClass == "--") {selectedClass = i18n['unknownClass'][lang];}
					ne.attr("CATEGORY",selectedClass)

					var selectedType = dialog.data.find("#selectNEtype option:selected").text();
					if (selectedType === undefined || selectedType == "--") {ne.removeAttr("TYPE")}
					else {ne.attr("TYPE",selectedType)}
			
					var selectedSubtype = dialog.data.find("#selectNEsubtype option:selected").text();
					if (selectedSubtype === undefined || selectedSubtype == "--") {ne.removeAttr("SUBTYPE")}
					else {ne.attr("SUBTYPE",selectedSubtype)}
			
					ne.addClass(ne.attr("CATEGORY")).addClass(ne.attr("TYPE")).addClass(ne.attr("SUBTYPE"))	
					ne.addClass("NE")

					Rembrandt.Display.setupNE(display, ne) 
					Rembrandt.Display.markAsChanged( display )
					
					$.modal.close();
 				});

				dialog.data.find("#NoButton").click(function(ev) {
					ev.preventDefault();
					$.modal.close();
 				})
			},
			overlayCss:{backgroundColor: '#888', cursor: 'wait'}
		} );
	}
	},

	// destroy a NE
	destroy = function (ne) {	
		// remove tag edit menu, hide / remove tooltip div
		Rembrandt.DisplayNE.destroyEditButton(ne)
		if (Rembrandt.Tooltip.has(ne)) {
			Rembrandt.Tooltip.hide(ne)
		 	Rembrandt.Tooltip.destroy(ne)
		}
		//  replace contents
	    var children = ne.contents()
		ne.replaceWith(children);
	},

    fillNEclasses = function(select) {
		select.empty()
		select.append("<OPTION VALUE=\"null\" DEFAULT>--</OPTION>")
		for (var i in i18n['class'][lang]) {
			select.append("<OPTION>"+ i+"</OPTION>")
		}
	},

	fillNEtypes = function(selectclass) {

		var selectsubtype = selectclass.siblings("#selectNEsubtype")
		var selecttype = selectclass.siblings("#selectNEtype")
		var classname = selectclass.find(":selected").text()
	
		selecttype.find("option").remove() // clean options
		selectsubtype.attr("disabled",true)
		selectsubtype.find("option").remove() // clean options

		if (classname != "--") {
			selecttype.attr("disabled",false)
			selecttype.append("<OPTION VALUE=\"null\" DEFAULT>--</OPTION>")
			for (var i in i18n['class'][lang][classname]) {
				selecttype.append("<OPTION>"+ i+"</OPTION>")
			}
		}
	},
	
	fillNEsubtypes = function(selecttype) {
	
		var selectsubtype = selecttype.siblings("#selectNEsubtype")
		var classname = selecttype.siblings("#selectNEclass").find(":selected").text()
		var typename = selecttype.find(":selected").text()
	
		selectsubtype.find("option").remove() // clean options
	
		if (typename != "--") {	
			selectsubtype.attr("disabled",false)
			selectsubtype.append("<OPTION VALUE=\"null\" DEFAULT>--</OPTION>")
			for (var i in i18n['class'][lang][classname][typename]) {
				selectsubtype.append("<OPTION>"+ i+"</OPTION>")
			}
		}
	},

	getTermsFrom = function (ne) {
		var res = ""
		$("li[t]", ne).each(function() {
			res += $(this).text()+" "
		})
		return jQuery.trim(res)
	},
	
	getClassificationsFrom = function (ne) {
		return (ne.attr("CATEGORY")+" "+ne.attr("TYPE")+" "+ne.attr("SUBTYPE")).replace(/undefined/g,"")
	},

 	getNextNEIndex = function(nes) {
		var index = 0
		nes.each(function() {
			var neid = $(this).attr("id")
			if (parseInt(neid) > index) index = parseInt(neid) 
		});
		return (index+1)
	}, 

	getSiblings = function(ne) {
		return $(".NE", ne.parents("#rrs-doc-display-screen"))
	},

	getUniqueIDfor = function(ne, prefix) {
		if (prefix === undefined) {prefix=""} else {prefix = prefix+"-"}
		var tailhash = hex_md5( (ne.attr('CATEGORY')+""+ne.attr('TYPE')+""+
			ne.attr('SUBTYPE')+""+ne.attr('ENTITY_ID')).replace(/ /g,'')
			.replace(/undefined/g,'') ).substring(0,4)
		return (prefix+ne.attr('id')+"-"+tailhash)
	},

 	haveDifferentSentences = function (terms) {
		// test for different sentences
		if (terms.length == 1) { return false}
		var currentTerm = terms.get(0)
		var currentSentenceIndex = parseInt($(currentTerm).parents("UL:first").attr("S"))
	
		for (i = 1; i<terms.length; i++) {
			if ( (parseInt($(terms.get(i)).parents("UL:first").attr("S") ) ) != (currentSentenceIndex) ) {
				return true
			}
		}
		return false
	},

// returns true if terms do not belong to a EM or do exacly match a NE boundary, false otherwise.
 	haveNEoverlap = function (terms) {

		var NEs = new Array();
		terms.each(function() { 
			$(this).parents(".NE:first").each(function() {
				NEs.push($(this))
			});
		});
		var match = "true"

		if (NEs.length == 0) {
			match = "false" // no NEs - clean text
		} else {
			for(i in NEs)  {
				if (($(NEs[i]).find("li[t]").size()) == terms.size()) {match = "false"}
			}
		}
		return match
	},

    haveUncontiguousTerms = function (terms) {
		if (terms.length == 1) { return false}
		var currentTerm = terms.get(0)
		var currentTermIndex = parseInt($(currentTerm).attr("T"))

		for (i = 1; i<terms.length; i++) {
			if ( (parseInt( $(terms.get(i)).attr("T") )) != (currentTermIndex + 1) ) {
				return true
			}
			currentTermIndex++
		}
		return false
	},

	// destroy an edit button for a ne
 	destroyEditButton = function(ne) {
		var tag_edit = $(".tag_edit", ne)
		if (tag_edit.hasClass("bt-active") ) {
			tag_edit.btOff();
		}
		tag_edit.hide("slow").remove();
	},

	setupNE = function(display, ne) {	
		Rembrandt.Relation.addIndirectInfo(display, ne);
	},

	setupNEs = function(display) {
		$.eachCallback($(".NE", display), function() {
				setupNE($(this), display)	
		}, function(loopcount) {});
	};

	return {
		"changeNEclassModal":changeNEclassModal,
		"confirmDeletionModal" : confirmDeletionModal,
		"confirmDeletionAllModal" : confirmDeletionAllModal,
		"createNEModal" : createNEModal,
		
		"destroy" :destroy,
		
		"fillNEclasses":fillNEclasses,
		"fillNEtypes":fillNEtypes,
		"fillNEsubtypes":fillNEsubtypes,
		
		"getTermsFrom" : getTermsFrom,
		"getClassificationsFrom": getClassificationsFrom,
		"getNextNEIndex" : getNextNEIndex,
		"getSiblings" : getSiblings,
		"getUniqueIDfor" : getUniqueIDfor,
		
		"setupNE"	: setupNE,
		"setupNEs"	: setupNEs,
		
		"destroyEditButton" : destroyEditButton,
		"haveDifferentSentences": haveDifferentSentences,
		"haveUncontiguousTerms" : haveUncontiguousTerms,
		"haveNEoverlap" : haveNEoverlap
	}
})(jQuery);
