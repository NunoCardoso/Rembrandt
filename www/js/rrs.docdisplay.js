var Rembrandt = Rembrandt || {};

Rembrandt.Display = (function ($) {
	"use strict"
	$(function () {

		$(".SelectionMenu A").live("click", function(ev) {
			ev.stopPropagation();
	 		var action = $(this).attr("href")// #changene, #deleteall
			var terms = $(".ui-selected") 
			if (action == "#createalt") {}
			if (action == "#createne") {createNEModal(terms)}
			if (action == "#deleteall") {confirmDeletionAllModal(terms)}
		});
	
		$("#edit-mode-on").live("click",function(ev) {
			ev.preventDefault()
			// hide the popup menu where I came from...
			$(this).parents("DIV:first").hide()
		
			// add remove DIVs for each NE
			var rrs_screen = $("#rrs-doc-display-screen", $(this).parents(".rrs-doc-display"))
			if (rrs_screen.attr("status") != "edit") {
				initializeEditMode(rrs_screen)
				updateDocDisplayScreenFor(rrs_screen, "edit")	
			}	
		});
	
		$("#hide-all-relations").live("click", function(ev) {
			ev.preventDefault()
			// hide the popup menu where I came from...
			$(this).parents("DIV:first").hide()
			var rrs_screen = $("#rrs-doc-display-screen", $(this).parents(".rrs-doc-display"))
			hideAllRelations(rrs_screen)
			updateDocDisplayScreenFor(rrs_screen, "default");
		});
		
		$("#hide-all-tooltips").live("click", function(ev) {
			ev.preventDefault()
			// hide the popup menu where I came from...
			$(this).parents("DIV:first").hide()
			var rrs_screen = $("#rrs-doc-display-screen", $(this).parents(".rrs-doc-display"))
			Rembrandt.Tooltip.hideAll(rrs_screen);
		});

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
			if (Rembrandt.Tooltip.isOpen($(this)) ) {Rembrandt.Tooltip.hide($(this));}
			else {Rembrandt.Tooltip.show($(this));}
		});

		$("#show-relations").live("click",function(ev) {
		
			ev.preventDefault()
			// hide the popup menu where I came from...
			$(this).parents("DIV:first").hide()
			// get the NE id from the 'neid' attribute of the clicked link
			var ne = $("#"+$(this).attr("neid"));
			var rrs_screen = $("#rrs-doc-display-screen", $(this).parents(".rrs-doc-display"))
			// if anchor action was 'show relations'

			if ( $(this).html() == i18n['showrelations'][lang] ) {
			//	print relations
				showRelationsFor(ne, rrs_screen);
			// change display to improve the visibility of the lines		
				updateDocDisplayScreenFor(rrs_screen, "relations");
				// the click is to close relations
			} else if ($(this).html() == i18n['hiderelations'][lang] ) {
				//hide Relations
				hideRelationsFor(ne, rrs_screen);
				//restore it
				updateDocDisplayScreenFor(rrs_screen, "default");
			}	
		});

		$("#show-all-relations").live("click", function(ev) {
			ev.preventDefault()
			// hide the popup menu where I came from...
			$(this).parents("DIV:first").hide()
			var rrs_screen = $("#rrs-doc-display-screen", $(this).parents(".rrs-doc-display"))
			showAllRelations(rrs_screen)
			updateDocDisplayScreenFor(rrs_screen, "relations");
		});

		$("#show-all-tooltips").live("click", function(ev) {
			ev.preventDefault()
			// hide the popup menu where I came from...
			$(this).parents("DIV:first").hide()
			var rrs_screen = $("#rrs-doc-display-screen", $(this).parents(".rrs-doc-display"))
			Rembrandt.Tooltip.showAll(rrs_screen);
		});
	
		$("#select-mode-on").live("click", function(ev) {
			ev.preventDefault()	
			// hide the popup menu where I came from...
			$(this).parents("DIV:first").hide()
			var rrs_screen = $("#rrs-doc-display-screen", $(this).parents(".rrs-doc-display"))
			if (rrs_screen.attr("status") != "select") {
				initializeSelectMode(rrs_screen);
				updateDocDisplayScreenFor(rrs_screen, "select");
			}
		});

		$(".tag_edit").live("click", function(ev) {
			ev.stopPropagation();
			if ( !Rembrandt.Tooltip.isOpen($(this)) ) {
				Rembrandt.ContextMenu.addEditMenu($(this))
			} else {
				Rembrandt.Tooltip.hide($(this))
			}
		});
	
		$(".EditMenu A").live("click", function(ev) {
			ev.stopPropagation();
			// get through parents ( menu ->  tooltip -> tag_edit -> first NE found)
			// note that there is more than one NE (for nested ones), so go for the first. 
			// look out, don't mess up their children.	
		 	var action = $(this).attr("href")// #change of #delete
			var ne = $(this).parents(".NE:first") 
			if (action == "#change") {  changeNEclassModal(ne)}
			if (action == "#delete") {	confirmDeletionModal(ne) }
		
		});
	
		$("#view-mode-on").live("click",function(ev) {
			ev.preventDefault()
			// hide the popup menu where I came from...
			$(this).parents("DIV:first").hide()
			var rrs_screen = $("#rrs-doc-display-screen", $(this).parents(".rrs-doc-display"))
			updateDocDisplayScreenFor(rrs_screen, "default")
		});
	});
	
	var appendDocDisplayTo = function (element) {
	   element.append(generateDocDisplay())
	   element.find("#rrs-doc-display-menu div UL LI").hover(function() {
			$(this).children("DIV").show()
		}, function() { 
		   $(this).children("DIV").hide()
		});
	}, 
	
	generateDocDisplay = function () {
		var data = {
			view 			 : i18n['view'][lang],
			mode 			 : i18n['mode'][lang],
			showalltooltips  : i18n['showalltooltips'][lang],
			hidealltooltips  : i18n['hidealltooltips'][lang],
			showallrelations : i18n['showallrelations'][lang],
			hideallrelations : i18n['hideallrelations'][lang],
			viewon			 : i18n['viewon'][lang],
			selecton		 : i18n['selecton'][lang],
			editon			 : i18n['editon'][lang]
		},
		
		template = "\
<DIV ID='rrs-doc-display-header'>\
	<DIV ID='rrs-doc-display-status'></DIV>\
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
						<LI><A ID='view-mode-on' HREF='#'>{{viewon}}</A></LI>\
						<LI><A ID='select-mode-on' HREF='#'>{{selecton}}</A></LI>\
						<LI><A ID='edit-mode-on' HREF='#'>{{editon}}</A></LI>\
					</UL>\
				</DIV></LI>\
			</UL>\
		</DIV>\
	</DIV>\
</DIV>\
<DIV ID='rrs-doc-display-body'>\
	<DIV ID='rrs-doc-display-canvas'></DIV>\
	<DIV ID='rrs-doc-display-screen'>\
		<DIV ID='rrs-document-title'></DIV>\
		<DIV ID='rrs-document-body'></DIV>\
	</DIV>\
</DIV>\
<DIV ID='rrs-tooltips' style='display:none;'></DIV>";

		return Mustache.to_html(template, data);
	},
	
	addDocumentTitleToDocDisplay = function (display, doctitle) {
		display.find("#rrs-document-title").html(doctitle)
	},

	addDocumentBodyToDocDisplay = function (display, docbody) {
		display.find("#rrs-document-body").html(docbody)
	},

	cleanDocDisplay = function (docdisplay) {
		$("#rrs-doc-display-status", docdisplay).html("Ready.")
		$("#rrs-doc-display-canvas", docdisplay).empty()
		$("#rrs-document-title", docdisplay).empty()
		$("#rrs-document-body", docdisplay).empty()
	},
	
	// search sibling NEs for relations, update RI and RT info
	addIndirectRelationInfo = function (ne) {
		var id = ne.attr('id')
		var indirectRels = new Array();
		var indirectReltypes = new Array();

		getSiblingNEs(ne).each(function() {
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
	
	showRelationsFor = function (ne, display_) {

		var display
		if (display_ === undefined) display = ne.parents('#rrs-doc-display-screen')
		else display = display_

		// change tooltip info
		var tooltip_id = "#"+getUniqueIDfor(ne,'tooltip')
		$(tooltip_id+" A[neid]").html(i18n['hiderelations'][lang])


		var canvas = display.siblings('#rrs-doc-display-canvas')
		// let's add a new canvas in the display div
		var canvasdiv = $(document.createElement("DIV")).addClass("canvas").addClass("NEcanvas")
		canvasdiv.attr("id",getUniqueIDfor(ne, 'canvas'))
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
	},

	showAllRelations = function (display) {	
		$(".NE", display).each(function() {
			if (!hasRelationsCanvas($(this), display)) {
				showRelationsFor($(this));
			}
		});
	},
	
	undoDocDisplayScreenFor = function (display, currstate) {

		var change = true,
			canvas = display.siblings("#rrs-doc-display-canvas")
		switch(currstate) {
			case "relations":
			// if there's an active canvas, don't change.
				if  ($(".NEcanvas", canvas).length > 0) {change = false}
			break;
			case "edit":
			 destroyEditMode(display)
			break;
			case "select":
			 destroySelectMode(display)
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

	updateDocDisplayScreenFor = function (display, newstate) {
		var currstate = display.attr('status')
		if (newstate == currstate) return;
		if ((newstate != "default") && (currstate == "default")) {
			doDocDisplayScreenFor(display, newstate)
		}
		if ((newstate == "default") && (currstate != "default")) {
			undoDocDisplayScreenFor(display, currstate)
		}
		if ((newstate != "default") && (currstate != "default")) {
			undoDocDisplayScreenFor(display, currstate)
			doDocDisplayScreenFor(display, newstate)
		}
	},

	wipeDisplay = function (display) {
		cleanDocDisplay(display)
		Rembrandt.Tooltip.hideAll($("#rrs-doc-display-screen", display)) // clean ophan tooltips
		// if someone re-submitted while the display was on a special mode... well, restore it
		updateDocDisplayScreenFor( $("#rrs-doc-display-screen", display), "default")
			//display results div
	},
	
	initializeSelectMode = function (display) {
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
	};
	
	return {
		"appendDocDisplayTo" : appendDocDisplayTo,
		"generateDocDisplay" : generateDocDisplay,
		"addDocumentTitleToDocDisplay" : addDocumentTitleToDocDisplay,
		"addDocumentBodyToDocDisplay"	: addDocumentBodyToDocDisplay,
		"addIndirectRelationInfo"		: addIndirectRelationInfo,
		"cleanDocDisplay"			: cleanDocDisplay,
		"showRelationsFor"			: showRelationsFor,
		"undoDocDisplayScreenFor"	: 	undoDocDisplayScreenFor,
		"updateDocDisplayScreenFor" : updateDocDisplayScreenFor,
		"wipeDisplay"					: wipeDisplay,
		"showAllRelations"				: showAllRelations,
		"initializeSelectMode"		: initializeSelectMode
	}
}(jQuery));

Rembrandt.Tooltip = (function ($) {
	"use strict"

	var show = function (ne) {	
		if (!has(ne) ) {
			var tooltip=$("#rrs-tooltips", ne.parents(".rrs-doc-display"))
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

			contentSelector: "$('#"+ getUniqueIDfor(ne,'tooltip')+"')", 
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
		$("#"+getUniqueIDfor(ne,'tooltip'), display).remove()
	},
	
	isOpen = function (ne) {
		return ne.hasClass("bt-active")
	},

	//create a display text to place in a tooltip
	_getContent = function (ne) {
		// build the baloon tooltip text
		var id = ne.attr("id");
		var entity = getTermsFromNE(ne) // ne.text() is also good, goes to children DIV - good in nested NEs
		var tooltip = "<DIV CLASS='NEtooltip' STYLE='padding:3px' ID='"+getUniqueIDfor(ne,'tooltip')+"'>"
		tooltip += "<P><B>"+entity+"</B></P>"
		tooltip += "<P><I>"+getClassificationsFromNE(ne)+"</I></P>"
		tooltip += "<P>"+i18n['sentence'][lang]+": "+ne.attr("S")+" "+i18n['term'][lang]+": "+ne.attr("T")+"</P>"

		/* add grounded info */			

		if (ne.attr("DB")) {
			var db = "<P>DBpedia: "
			var linksDB = ne.attr("DB").split(";")
			for (i in linksDB) {
				db += "<A HREF=\"http://dbpedia.org/resource/"+linksDB[i]+"\">"+linksDB[i]+"</A> "
			}
			db += "</P>"
			tooltip += db
		}

		/* add relations*/		
		var a = "<A CLASS='link-button show-relations' neid='"+id+"' lang='"+lang+"'>";
		a += i18n['showrelations'][lang]+"</A>"
		tooltip += "<P>"+a+"</P><P><DIV CLASS='Relations'></DIV></P>"
		// if this is a part of saskia whole tabs, let's also add details for ne
		if ($("#saskia-content").length > 0) {
			tooltip += "<P><A CLASS='link-button DETAILNE' HREF='#' TITLE='"+entity+"' "
			tooltip += "S='"+ne.attr("S")+"' T='"+ne.attr("T")+"' C1='"+ne.attr("C1")+"' "
			tooltip += (ne.attr("C2") ? "C2='"+ne.attr("C2")+"' ":"")
			tooltip += (ne.attr("C3") ? "C3='"+ne.attr("C3")+"' ":"")
			tooltip += ">"+i18n['nedetails'][lang]+"</A></P>"
		}
		tooltip += "</DIV>"	
		return tooltip 	
	};
	
	return {
		"_add"	: _add,
		"has"	: has,
		"hide"	: hide,
		"hideAll" : hideAll,
		"refresh" : refresh,
		"show"	: show,
		"showAll" : showAll,
		"showMenu" : showMenu,
		"destroy"	: destroy,
		"isOpen"   : isOpen
	}	
})(jQuery);

Rembrandt.ContextMenu = (function ($) {
	"use strict"
 	
	var addEditMenu = function (ne_tag_edit) {
		if (!has(ne_tag_edit)) {
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
		if (has(term)) {Rembrandt.Tooltip.showMenu(term)}
		else {
			if (term.attr("title") === undefined) {term.attr("title","")}
		
			term.bt(
			"<ul class='SelectionMenu NEmenu'>"+
	    	"<li><a href='#createalt'>"+i18n["createalt"][lang]+"...</a></li>"+
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
	"addSelectionMenu" 	: addSelectionMenu
	}
})(jQuery);

//modal window for NE change 
function changeNEclassModal(ne) {
	$.modal("<div id='modalChangeNEclassDialog' class='rembrandt-modal' style='height:180px;width:350px;'>"+
      "<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
	  "<div style='text-align:center; padding:10px;'>"+i18n['pickclassforNE'][lang]+" '"+
		"<div id='NEterms' style='display:inline; font-weight:bold;'></div>'.</div> "+
	  "<div style='text-align:left; padding:3px;'>"+i18n['oldclass'][lang]+": </div>"+
	  "<div id='NEclass' style='text-align:left; padding:3px;display:inline; font-weight:italic;'></div>"+
	  "<div style='text-align:left; padding:3px;'>"+i18n['newclass'][lang]+": </div>"+
	  "<div style='text-align:left; padding:3px;'>"+
		"<form class='selectNEclassForm'>"+
			"<select size=1 id='selectNEclass' onChange='fillNEtypes($(this));'></select>"+
			"<select size=1 id='selectNEtype' onChange='fillNEsubtypes($(this));' disabled></select>"+
			"<select size=1 id='selectNEsubtype' disabled></select>"+
		"<BR><BR>	"+
			"<div style='text-align:center;'><input type='button' id='YesButton' value='"+i18n["yes"][lang]+", "+i18n["change"][lang]+"'>"+
			"<input type='button' id='NoButton' value='"+i18n["no"][lang]+", "+i18n["cancel"][lang]+"'></div></form>"+
      "</div>"+
	"</div>", {
	onShow: function (dialog) {
		// get all terms (ie, li entities with t attribute) to show
		dialog.data.find("#NEterms").append(getTermsFromNE(ne));	
		dialog.data.find("#NEclass").append(getClassificationsFromNE(ne));		
		// now, fill selects with info
		fillNEclasses(dialog.data.find("#selectNEclass"))
		// filling types and subtypes are done automatically, don't worry.

		// Yes button clicked - let's change categories
		dialog.data.find("#YesButton").click(function(ev) {
			ev.preventDefault();
			// remove tooltip
			destroyTooltip(ne)

			ne.removeClass(ne.attr("C1"))
			ne.removeClass(ne.attr("C2"))
			ne.removeClass(ne.attr("C3"))
			
			//change C1/C2/C3 properties
			var selectedClass = dialog.data.find("#selectNEclass option:selected").text();
			if (selectedClass == "--") {selectedClass = i18n['unknownClass'][lang];}
			ne.attr("C1",selectedClass)

			var selectedType = dialog.data.find("#selectNEtype option:selected").text();
			if (selectedType === undefined || selectedType == "--") {ne.removeAttr("C2")}
			else {ne.attr("C2",selectedType)}
			
			var selectedSubtype = dialog.data.find("#selectNEsubtype option:selected").text();
			if (selectedSubtype === undefined || selectedSubtype == "--") {ne.removeAttr("C3")}
			else {ne.attr("C3",selectedSubtype)}
			
			ne.addClass(ne.attr("C1")).addClass(ne.attr("C2")).addClass(ne.attr("C3"))	
			ne.addClass("NE")

			// create/add new tooltip, compute relations, bla bla
			setupNE(ne) 		
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

//modal window to delete one NE
function confirmDeletionModal(ne) {
	$.modal("<div id='modalConfirmNEDelectionDialog' class='rembrandt-modal' style='height:100px;width:300px;'>"+
      "<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
	  "<div style='text-align:center; padding:10px;'>"+i18n['areyousuredeleteNE'][lang]+"'"+
		"<div id='NEterms' style='display:inline; font-weight:bold;'></div>', "+i18n['withclass'][lang]+" "+
		"<div id='NEclass' style='display:inline; font-weight:italic;'></div> ?</div>"+
	  "<div style='text-align:center;'"+
		"<form><input type='button' id='YesButton' value='"+i18n["yes"][lang]+', '+i18n["delete"][lang]+"'>"+
			"<input type='button' id='NoButton' value='"+i18n["no"][lang]+", "+i18n["keep"][lang]+"'>"+
		"</form>"+
      "</div>"+
	"</div>",{
	onShow: function (dialog) {
		// get all terms (ie, li entities with t attribute) to show
		dialog.data.find("#NEterms").append(getTermsFromNE(ne));	
		dialog.data.find("#NEclass").append(getClassificationsFromNE(ne));	
			// Yes button clicked
		dialog.data.find("#YesButton").click(function(ev) {
			ev.preventDefault();
			$.modal.close();
			// first, remove the NE tag_edit 
			destroyNE(ne)
 		});
		dialog.data.find("#NoButton").click(function(ev) {
			ev.preventDefault();
			$.modal.close();
 		})
	},
	overlayCss:{backgroundColor: '#888', cursor: 'wait'}
//	containerCss: {height:parseInt($("#modalConfirmNEDelectionDialog").css('height'))+6, 
//		width:parseInt($("#modalConfirmNEDelectionDialog").css('width'))+6	}
	});
}    

//modal window to delete multiple NE
function confirmDeletionAllModal(selected_terms) {
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
//			containerCss: {height:parseInt($("#modalConfirmNEDelectionAllButNoNEFoundDialog").css('height'))+6, 
//				width:parseInt($("#modalConfirmNEDelectionAllButNoNEFoundDialog").css('width'))+6
//			}
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
					destroyNE($(this)) 
				});
				$.modal.close();

 			});
			dialog.data.find("#NoButton").click(function(ev) {
				ev.preventDefault();
				$.modal.close();
 			})
		},
		overlayCss:{backgroundColor: '#888', cursor: 'wait'}
//		containerCss: {height:parseInt($("#modalConfirmNEDelectionAllDialog").css('height'))+6, 
//			width:parseInt($("#modalConfirmNEDelectionAllDialog").css('width'))+6
//			}
		});
	}	
}

//modal window to create a NE
function createNEModal(selected_terms) {
	// convert jQuery to array, to better handle them
	if (selected_terms.length == 0) return
	var differentSentences = haveDifferentSentences(selected_terms)
	var differentTerms = haveUncontiguousTerms(selected_terms)

	var requiresAlt = haveNEoverlap(selected_terms)
	
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
//			containerCss: {height:parseInt($("#modalConfirmNEDelectionAllButNoNEFoundDialog").css('height'))+6, 
//				width:parseInt($("#modalConfirmNEDelectionAllButNoNEFoundDialog").css('width'))+6
//			}
		});
		
	// Ok, let's create
	} else {	
		$.modal("<div id='modalCreateNE' class='rembrandt-modal' style='height:150px;width:380px;'>"+
      "<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
	  "<div style='text-align:center; padding:10px;'>"+i18n['createNEwithclass'][lang]+":</div>"+
	  "<div style='text-align:left; padding:3px;'>"+
		"<form class='selectNEclassForm'>"+
			"<select size=1 id='selectNEclass' onChange='fillNEtypes($(this));'></select>"+
			"<select size=1 id='selectNEtype' onChange='fillNEsubtypes($(this));' disabled></select>"+
			"<select size=1 id='selectNEsubtype' disabled></select>"+
		"<BR><BR>	"+
			"<div style='text-align:center;'><input type='button' id='YesButton' value='"+i18n["yes"][lang]+", "+i18n["createnew"][lang]+"'>"+
			"<input type='button' id='NoButton' value='"+i18n["no"][lang]+", "+i18n["cancel"][lang]+"'></div></form>"+
      "</div>"+
	"</div>	", {
			onShow: function modalShow(dialog) {
					
				fillNEclasses(dialog.data.find("#selectNEclass"))
					// filling types and subtypes are done automatically, don't worry.
					
				dialog.data.find("#YesButton").click(function(ev) {
					ev.preventDefault();
					
					/* magic happens here */
					var nextindex = getNextNEIndex($(".NE", selected_terms.parents("#rrs-doc-display-screen")))
					// wrap terms with the NE
					selected_terms.wrap("<DIV CLASS='NE' ID='"+nextindex+"'></DIV>")
					ne = selected_terms.parent("#"+nextindex)

					
					
					/* Add S/T */
					ne.attr("S",ne.parents("UL:first").attr("S"))
					ne.attr("T",ne.children("LI:first").attr("T"))

					//change C1/C2/C3 properties
					var selectedClass = dialog.data.find("#selectNEclass option:selected").text();
					if (selectedClass == "--") {selectedClass = i18n['unknownClass'][lang];}
					ne.attr("C1",selectedClass)

					var selectedType = dialog.data.find("#selectNEtype option:selected").text();
					if (selectedType === undefined || selectedType == "--") {ne.removeAttr("C2")}
					else {ne.attr("C2",selectedType)}
			
					var selectedSubtype = dialog.data.find("#selectNEsubtype option:selected").text();
					if (selectedSubtype === undefined || selectedSubtype == "--") {ne.removeAttr("C3")}
					else {ne.attr("C3",selectedSubtype)}
			
					ne.addClass(ne.attr("C1")).addClass(ne.attr("C2")).addClass(ne.attr("C3"))	
					ne.addClass("NE")
			// create/add new tooltip, compute relations, bla bla
					setupNE(ne) 		
					$.modal.close();
 				});

				dialog.data.find("#NoButton").click(function(ev) {
					ev.preventDefault();
					$.modal.close();
 				})
			},
			overlayCss:{backgroundColor: '#888', cursor: 'wait'}
		//	containerCss: {height:(parseInt($("#modalCreateNE").css('height'))+6), // 6 to fit border, 6 for margin 
		//	width:(parseInt($("#modalCreateNE").css('width'))+6) }
		} );
	}
}


// destroy a tooltip
function destroyCanvas(ne, display) {
	$("#"+getUniqueIDfor(ne,'canvas'), display).remove()
}

// leave edit mode
function destroyEditMode(display) {
	// remove tag edits, you'll remove associated tooltip menus	
	$(".NE", display).each(function() {
		destroyEditButton($(this))
	});
}

// destroy an edit button for a ne
function destroyEditButton(ne) {
	var tag_edit = $(".tag_edit", ne)
	if (tag_edit.hasClass("bt-active") ) {
		tag_edit.btOff();
	}
	tag_edit.hide("slow").remove();
}

// destroy a NE
function destroyNE(ne) {	
	// remove tag edit menu, hide / remove tooltip div
	destroyEditButton(ne)
	if (Rembrandt.Tooltip.has(ne)) {
		Rembrandt.Tooltip.hide(ne)
	 	Rembrandt.Tooltip.destroy(ne)
	}
	//  replace contents
    var children = ne.contents()
	ne.replaceWith(children);
}


// destroy a select mode
function destroySelectMode(display) {
// clear selected stuff
	$(".ui-selected", display).removeClass("ui-selected")
	display.selectable('destroy');	
}

// change Rembrandt display to show...
function doDocDisplayScreenFor(display, newstate) {

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
}	


function fillNEclasses(select) {
	select.empty()
	select.append("<OPTION VALUE=\"null\" DEFAULT>--</OPTION>")
	for (var i in i18n['class'][lang]) {
		select.append("<OPTION>"+ i+"</OPTION>")
	}
}	
	
function fillNEtypes(selectclass) {

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
}
	
function fillNEsubtypes(selecttype) {
	
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
}

function getClassificationsFromNE(ne) {
	return (ne.attr("C1")+" "+ne.attr("C2")+" "+ne.attr("C3")).replace(/undefined/g,"")
}

function getNextNEIndex(nes) {
	var index = 0
	nes.each(function() {
		var neid = $(this).attr("id")
		if (parseInt(neid) > index) index = parseInt(neid) 
	});
	return (index+1)
} 

function getTermsFromNE(ne) {
	var res = ""
	$("li[t]", ne).each(function() {
		res += $(this).text()+" "
	})
	return jQuery.trim(res)
}

function getSiblingNEs(ne) {
	return $(".NE", ne.parents("#rrs-doc-display-screen"))
}

function getUniqueIDfor(ne, prefix) {
	if (prefix === undefined) {prefix=""} else {prefix = prefix+"-"}
	var tailhash = hex_md5( (ne.attr('C1')+""+ne.attr('C2')+""+ne.attr('C3')).replace(
		/ /g,'').replace(/undefined/g,'') ).substring(0,4)
	return (prefix+ne.attr('id')+"-"+tailhash)
}

function hasRelationsCanvas(ne, display) {
	return $("#"+getUniqueIDfor(ne,'canvas'), display.siblings("#rrs-doc-display-canvas")).length
}

function haveDifferentSentences(terms) {
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
}

// returns true if terms do not belong to a EM or do exacly match a NE boundary, false otherwise.
function haveNEoverlap(terms) {

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
			if (($(NEs[i]).find("li[t]").size()) == terms.size()) {	match = "false"  }
		}
	}
	return match
}

function haveUncontiguousTerms(terms) {
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
}

function hideAllRelations(display) {
	$(".NE", display).each(function() {
		if (hasRelationsCanvas($(this), display)) {
			hideRelationsFor($(this), display);
		}
	});
}


function hideRelationsFor(ne, display_) {
	
	var display
	if (display_ === undefined) display = ne.parents('#rrs-doc-display-screen')
	else display = display_

	// change tooltip info
	var tooltip_id = "#"+getUniqueIDfor(ne,'tooltip')
	$(tooltip_id+" A[neid]").html(i18n['showrelations'][lang])
	$(tooltip_id+" .Relations").empty()

	// remove canvas 
	$("#"+getUniqueIDfor(ne,'canvas')).remove()

	// this ne will get the original colour
	ne.animate({ color: ne.attr('original-color')}, 500)

	// refresh the balloon tip
	if (Rembrandt.Tooltip.isOpen(ne)) {Rembrandt.Tooltip.refresh(ne)}	
}


function initializeEditMode(display) {
	$(".NE", display).append("<DIV CLASS='tag_edit'>&Delta;</DIV>")
}

function setupNE(ne) {	
	Rembrandt.Display.addIndirectRelationInfo(ne);
}

function setupNEs(display) {
	$.eachCallback($(".NE", display), function() {
		setupNE($(this))	
	}, function(loopcount) {});
}




