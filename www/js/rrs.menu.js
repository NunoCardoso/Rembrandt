// to handle generic UI interactions 

$(document).ready(function() {
	
	// Get the sub-menu hideable 
	$("#main-menu UL LI").hover(function(e) {
	  var div = $(this).children("DIV")
    div.hoverFlow(e.type, { 'height': 'show' }, 'fast');
  }, function(e) {
	  var div = $(this).children("DIV")
    div.hoverFlow(e.type, {'height': 'hide'}, 'fast');
  });

	$(".main-side-menu-section-header A").live("click", function(e) {
		e.preventDefault();
		var section = $(this).parent()
		if (!section.hasClass("main-side-menu-section-body-collapsed")) {
			section.siblings().slideUp('fast').hide();
			section.addClass('main-side-menu-section-body-collapsed')
		} else {
			section.siblings().slideDown('fast').show();
			section.removeClass('main-side-menu-section-body-collapsed')
		}
	});
		
	// back button os main-slidable-divs 
	$('A.GO_BACK_IOS_ISH_BUTTON').live("click", function(ev, ui) {
		ev.preventDefault();	
		removeLastBreadcrumbleElement()
		slideRightToLeftWith($("#"+$(this).attr("target")))	
	})	
	
	// back button os main-slidable-divs 
	$('A.GO_FORWARD_IOS_ISH_BUTTON').live("click", function(ev, ui) {
		ev.preventDefault();	
		var newdiv = $("#"+$(this).attr("target"))
		addBreadcrumbleElement(newdiv.attr('title'), newdiv.attr('id')) 
		slideLeftToRightWith(newdiv)	
	})	
		
	// when a breadcrumble link is clicked, let's refresh main-body and update breadrcrumbles 
	$('A.main-breadcrumbles-link').live("click", function(ev, ui) {
		ev.preventDefault();
		var divtoshow = $("#"+$(this).attr('target'))
		var breadcrumble_element = $(this).parents("DIV.main-breadcrumbles-element")
		// How many elements there are?
		var alldivs = $("DIV.main-breadcrumbles-element").toArray()
		
		// now, let's eliminate right divs ans slide, if we are going back in breadcrumbles
		for (var i = alldivs.length - 1; i> breadcrumble_element.index(); i--) {
			var breadcrumbletoremove = $(alldivs[i])
				// if ) want also to delete targeted divs...  
				//var divtoremove = $("#"+breadcrumbletoremove.attr('target'))
				// divtoremove.remove()			
			breadcrumbletoremove.remove()
		}	
		reformatBreadcrumbles()
		slideRightToLeftWith(divtoshow) // breadcrumble links always go right to left				
	})
})

/**** ADD SLIDABLE DIV ***/

function showSlidableDIV(options) {		
			
	var divtohide = $("DIV.main-slidable-div:visible")
	var divtoshow = $("#"+options.target)
		
	if (divtoshow.size() > 0) {
		if (options.slide == 'vertical') {
			slideDownWith(divtoshow)	
		} else {		
	   	slideLeftToRightWith(divtoshow)
		}
		// the sliding funciton takes care already of the side menu! :)			
	} else {	
		
		// if no ajax, invoke div creator with no response
		if (isUndefined(options.ajax) || options.ajax == false) {
			
			var divgenerator = options.divcreator
			// make sure the DIV has an id and a title, for the breadcrumble
			divtoshow = divgenerator(null, options.su, options.role, options.divcreatoroptions)			
			
			addSlidableDIV(divtohide, divtoshow, options)
			
		} else {
					
			jQuery.ajax({type:'POST', url:options.restlet_url,
			data:options.postdata, beforeSubmit: waitMessageBeforeSubmit(lang),
			success: function(response)  {		
				if (response['status'] == -1) {
					errorMessageWaitingDiv(lang, response['message'])
				} else {								
					hideWaitingDiv()
					
					var su = false
					var pubkey = response['usr_pub_key']
				
					// this is where I refresh the su validation...
					if (!isUndefined(pubkey)) {
						$("#main-body").attr('USR_PUB_KEY',pubkey)
						su = validateSu(pubkey)
					}
				
					var divgenerator = options.divcreator
					// make sure the DIV has an id and a title, for the breadcrumble
					divtoshow = divgenerator(response['message'], su, options.role, options.divcreatoroptions)
					
					addSlidableDIV(divtohide, divtoshow, options)			
				
				}	 
		  },									
		  error: function(response) {errorMessageWaitingDiv(lang, response)}
		})
	}
	}
}

function addSlidableDIV(divtohide, divtoshow, options) {

	if (options.slide == 'horizontal') {
		addSlidableDivHeaderTo(divtoshow, divtohide, null);
		addForwardButtonTargeting(divtohide, divtoshow);	
	} else {
		// copy slidable div header from the doc to hide
		copySlidableDivHeaderFromTo(divtohide, divtoshow)
		replaceForwardButtonsTargetingThisToThis(divtohide, divtoshow)
	}
								
	// add the new div
	divtoshow.appendTo($("#main-body"))
				
	// activate newdiv stuff, now that we have it on DOM
	$('TABLE.tablesorter', divtoshow).tablesorter()
 	updateEditInPlace(divtoshow);

	// add submenu to side menu
	if (!isUndefined(options.sidemenu)) {
		var sidemenu = addSubmeuOnSideMenu(options.sidemenu, options.role)	
		// show it
		configureSubmenu(sidemenu, options.role, options.sidemenuoptions)
		if (options.slide == 'horizontal') {
			// up/down slides already make this check, this is for 'new' sidemenus 
			reviewSideMenuMakeActiveFor(sidemenu, divtoshow.attr('id'))
		}
		showSubmeuOnSideMenu(sidemenu)
	}
										
	// add breadcrumble (label, target)
	if (options.slide == 'vertical') {
		substituteLastBreadcrumbleElement(divtoshow.attr('title'), divtoshow.attr('id'))
		slideDownWith(divtoshow)	
	} else {		
		addBreadcrumbleElement(divtoshow.attr('title'), divtoshow.attr('id'))
		slideLeftToRightWith(divtoshow)
	}					
}

/**** ADD MANAGE & CONFIGURE SIDE MENUS *****/

function addSubmeuOnSideMenu(object, role) {
	// add an item on side menu
	var sidemenu = $('#main-side-menu-section-'+object)
	if (sidemenu.size() > 0) {return sidemenu}
	switch(object) {
		case "searchresult": 
			$('#main-side-menu').append("<DIV ID='main-side-menu-section-searchresult'"+ 
			"	CLASS='main-side-menu-section' style='display:none'>\n" +
			"<DIV CLASS='main-side-menu-section-header'><A HREF='#'>"+i18n['results'][lang]+"</A>\n"+ 
			"</DIV>\n<DIV CLASS='main-side-menu-section-body'>\n"+
			"<DIV CLASS='main-side-menu-section-body-element'>\n"+
			"<A CLASS='SEARCHRESULT_SHOW slide-vertically-link' HREF='#' TARGET='' ROLE='"+role+"'>"+
				i18n['search-result-list'][lang]+"</A>"+
			"</DIV>\n"+
			"<DIV CLASS='main-side-menu-section-body-element'>\n"+
			"<A CLASS='SEARCHRESULT_EXPLANATION slide-vertically-link' HREF='#' TARGET='' ROLE='"+role+"'>"+
			 i18n['explanation'][lang]+"</A>"+
			"</DIV>\n</DIV>\n</DIV>\n");
		break;
		
		case "rdoc": 
				$('#main-side-menu').append("<DIV ID='main-side-menu-section-rdoc'"+ 
			"	CLASS='main-side-menu-section' style='display:none'>\n" +
			"<DIV CLASS='main-side-menu-section-header'><A HREF='#'>"+i18n['document'][lang]+"</A>\n"+ 
			"</DIV>\n<DIV CLASS='main-side-menu-section-body'>\n"+
			"<DIV CLASS='main-side-menu-section-body-element'>\n"+
			"<A CLASS='RDOC_SHOW slide-vertically-link' HREF='#' TARGET='' ROLE='"+role+"'>"+
					i18n['content'][lang]+"</A>"+
			"</DIV>\n"+
			"<DIV CLASS='main-side-menu-section-body-element'>\n"+
			"<A CLASS='RDOC_METADATA slide-vertically-link' HREF='#' TARGET='' ROLE='"+role+"'>"+
				i18n['metadata'][lang]+"</A>"+
			"</DIV>\n</DIV>\n</DIV>\n");
		break;
		
		case "collection":	

				$("#main-side-menu").append("<DIV CLASS='main-side-menu-section' "+
		"ID='main-side-menu-section-collection' style='display:none'>\n" + 
		" <DIV CLASS='main-side-menu-section-header'><A HREF='#'></A></DIV>"+
				"<DIV CLASS='main-side-menu-section-body'> <DIV CLASS='main-side-menu-section-body-element'>"+
				"<A HREF='#' CLASS='COLLECTION_SHOW slide-vertically-link' TARGET='' ROLE='"+role+"'>"+
					i18n["collection-show"][lang]+"</A></DIV>"+
				"<DIV CLASS='main-side-menu-section-body'> <DIV CLASS='main-side-menu-section-body-element'>"+
				"<A HREF='#' CLASS='COLLECTION_SDOC_LIST slide-vertically-link' TARGET='' ROLE='"+role+"'>"+
					i18n["sdocs"][lang]+"</A></DIV>"+
				"<DIV CLASS='main-side-menu-section-body'> <DIV CLASS='main-side-menu-section-body-element'>"+
				"<A HREF='#' CLASS='COLLECTION_RDOC_LIST slide-vertically-link' TARGET='' ROLE='"+role+"'>"+
					i18n["rdocs"][lang]+"</A></DIV>"+
				( (role == "admin" || role == "col-admin") ? "<DIV CLASS='main-side-menu-section-body'> <DIV CLASS='main-side-menu-section-body-element'>"+
				"<A HREF='#' CLASS='COLLECTION_TASK_LIST slide-vertically-link' TARGET='' ROLE='"+role+"'>"+
					i18n["tasks"][lang]+"</A></DIV>" : "")+
				"</DIV></DIV>")
		break;
	}
		
 	sidemenu = $('#main-side-menu-section-'+object)
	return sidemenu
} 

function configureSubmenu(submenu, role, options) {

	// RESULT_LIST
	if (submenu.attr('id') == "main-side-menu-section-searchresult") {

		var links = $("DIV.main-side-menu-section-body-element A", submenu)
		links.attr("TITLE",options.title);
		links.attr("ROLE",role);
		var id = options['id']
		$("DIV.main-side-menu-section-body-element A.SEARCHRESULT_SHOW", submenu).attr("TARGET","rrs-searchresult-show-"+id);
		$("DIV.main-side-menu-section-body-element A.SEARCHRESULT_EXPLANATION", submenu).attr("TARGET","rrs-searchresult-explanation-"+id);
		
	
	}	// RDOC
	else if (submenu.attr('id') == "main-side-menu-section-rdoc") {

		var links = $("DIV.main-side-menu-section-body-element A", submenu)
		var id = options['id']
		links.attr("ID",id);
		links.attr("DOC_ORIGINAL_ID",options.doc_original_id);
		links.attr("TITLE",options.title);
		links.attr("ROLE",role);
	
		$("DIV.main-side-menu-section-body-element A.RDOC_SHOW", submenu).attr("TARGET","rrs-rdoc-show-"+id);
		$("DIV.main-side-menu-section-body-element A.RDOC_METADATA", submenu).attr("TARGET","rrs-rdoc-metadata-"+id);
		
	// COLLECTION	
	
	} else if (submenu.attr('id') == "main-side-menu-section-collection") {
	
		// sub-menus clicked ask for a submenu refresh, but there is no need to change section header
		if (options.col_name) $("DIV.main-side-menu-section-header A",submenu).html(shortenTitle(options.col_name))
		
		var links = $("DIV.main-side-menu-section-body-element A", submenu)
		var id = options['id']
		links.attr("ID", id);
		if (options.col_name) links.attr("TITLE", options.col_name);
		links.attr("ROLE",role);
		$("DIV.main-side-menu-section-body-element A.COLLECTION_SHOW", submenu).attr("TARGET","rrs-collection-show-"+id);
		$("DIV.main-side-menu-section-body-element A.COLLECTION_SDOC_LIST", submenu).attr("TARGET","rrs-collection-sdoc-list-"+id);
		$("DIV.main-side-menu-section-body-element A.COLLECTION_RDOC_LIST", submenu).attr("TARGET","rrs-collection-rdoc-list-"+id);
		if (role == "admin" || role == "col-admin") {
			$("DIV.main-side-menu-section-body-element A.COLLECTION_TASK_LIST", submenu).attr("TARGET","rrs-collection-task-list-"+id);
		}
	}
}

// check if link comes from side menu, so I know if link slide is left-right or up-down 

function getSlideOrientationFromLink(link) {
	return (link.hasClass('slide-vertically-link') ? 'vertical' : 'horizontal')
}

function reviewSideMenuMakeActiveFor(submenu, visibledivid) {
 
   submenu.find("DIV.main-side-menu-section-body-element").each(function(index, item) {

		if ($(item).find("A").attr("target") == visibledivid) {
			$(item).addClass("main-side-menu-section-body-element-active")			
			$(item).find("A").addClass("disabled")			
		} else {
			$(item).removeClass("main-side-menu-section-body-element-active")						
			$(item).find("A").removeClass("disabled")			
		}		
	})	
}

function hideSubmeuOnSideMenu(submenutohide) {
	if(submenutohide.is(':visible')) submenutohide.slideUp("fast").hide()
}

function showSubmeuOnSideMenu(submenutoshow) {
	if (submenutoshow.is(':hidden')) submenutoshow.slideDown("fast").show()
}

/**** SLIDES *****/

function slideUpWith(newdiv) {
	// the current one is...
	var divtohide = $("#main-body DIV.main-slidable-div:visible")
	if (divtohide.attr('id') != newdiv.attr('id')) {
	
		divtohide.hide("slide",{direction: 'up'})
		newdiv.show()
		callbackVerticalSlide(divtohide, newdiv) // callback function for menu reposition
	}
}

function slideDownWith(newdiv) {
	// the current one is...
	var divtohide = $("#main-body DIV.main-slidable-div:visible")
	if (divtohide.attr('id') != newdiv.attr('id')) {
	
		divtohide.hide("slide",{direction: 'down'})
		newdiv.show()
		callbackVerticalSlide(divtohide, newdiv) // callback function for menu reposition
	}
}

function slideLeftToRightWith(newdiv) {
	// the current one is...
	var divtohide = $("#main-body DIV.main-slidable-div:visible")
	if (divtohide.attr('id') != newdiv.attr('id')) {
	
		//	'#'+divtohide.attr('id')+",#"+newdiv.attr('id')).slideToggle();
		
	//  $('#'+divtohide.attr('id')+",#"+newdiv.attr('id')).slideToggle();
		divtohide.hide("slide",{direction: 'left'})
		newdiv.show("slide",{direction: 'right'})
		callbackHorizontalSlide(divtohide, newdiv) // callback function for menu reposition
	}
}

function slideRightToLeftWith(newdiv) {
	// the current one is...
	var divtohide = $("#main-body DIV.main-slidable-div:visible")

	if (divtohide.attr('id') != newdiv.attr('id')) {
	//	  $('#'+divtohide.attr('id')+",#"+newdiv.attr('id')).slideToggle();
		divtohide.hide("slide",{direction: 'right'})
		newdiv.show("slide",{direction: 'left'})
		callbackHorizontalSlide(divtohide, newdiv) // callback function for menu reposition
	}
}

// function to call when showing a slidable page vertically -> that is, a side menu must be updated 
function callbackHorizontalSlide(divtohide, divtoshow) {
		
	var divtohideitem = divtohide.attr("id").match(/rrs-([^-]+)-/)[1]
	var divtoshowitem = divtoshow.attr("id").match(/rrs-([^-]+)-/)[1]
		
	// iterate through all side menus, check if its main object (rdoc, collection, etc) 
	// matches any of the divs	
	$("DIV.main-side-menu-section").each(function(index, item) {
		var id = $(item).attr("id"),
		   section = (!isUndefined(id) ? id.match(/main-side-menu-section-([^-]+)$/) : undefined)
		if (!isUndefined(section) && section.length >= 2) {
			var sectionitem = section[1]
			if (sectionitem == divtohideitem) {
				hideSubmeuOnSideMenu($(item))
			}
			if (sectionitem == divtoshowitem) {
				showSubmeuOnSideMenu($(item))
			}
		}
	})
}

// function to call when showing a slidable page horizontally -> that is, a side menu must be hidden/showed  
function callbackVerticalSlide(divtohide, newdiv) {
	
	//if (newdiv.attr("id").match("rrs-search-*")) { 
	//	reviewSideMenuMakeActiveFor($("#main-side-menu-section-search-results"), newdiv.attr("id"))
	// etc... 
	
	// so, let's collect the main object (rdoc, collection, etc) and use it. 
	// march returns array, 0 is the whole pattern, 1 is the () pattern
	var item = newdiv.attr("id").match(/rrs-([^-]+)-/)[1]
	reviewSideMenuMakeActiveFor($("#main-side-menu-section-"+item), newdiv.attr("id"))
}

/****** BUTTONS *******/

function addSlidableDivHeaderTo(div, divforbackbutton, divforforwardbutton) {
	div.prepend("<DIV CLASS='main-slidable-div-header'>"+
		"<DIV CLASS='main-slidable-div-header-left'></DIV>"+
		"<DIV CLASS='main-slidable-div-header-right'></DIV>"+
		"</DIV>")
	if (!isUndefined(divforbackbutton)) {
		addBackButtonTargeting(div, divforbackbutton)		
	}
	if (!isUndefined(divforforwardbutton)) {
		addForwardButtonTargeting(div, divforforwardbutton)		
	}
}

function copySlidableDivHeaderFromTo(divtohide, div) {
	divtohide.find("DIV.main-slidable-div-header").clone().prependTo(div)	
}

function addBackButtonTargeting(containerdiv, divforbackbutton) {
	containerdiv.find("DIV.main-slidable-div-header-left").html(generateBackButton(divforbackbutton))
}

function addForwardButtonTargeting(containerdiv, divforforwardbutton) {
	containerdiv.find("DIV.main-slidable-div-header-right").html(generateForwardButton(divforforwardbutton))	
}

function replaceForwardButtonsTargetingThisToThis(divneedle, divnewtarget) {
 var buttons = $('A.GO_FORWARD_IOS_ISH_BUTTON[TARGET='+divneedle.attr('id')+']')
 buttons.attr('TARGET', divnewtarget.attr('TARGET'))
 buttons.find("SPAN").html(divnewtarget.attr('TITLE'))
}

/** Generates HTML for a back button */
function generateBackButton(div) {
	return "<A HREF='#' CLASS='GO_BACK_IOS_ISH_BUTTON' TARGET='"+div.attr('id')+"'>"+
	"<SPAN>"+div.attr('title')+"</SPAN></A>";
}			

/** Generates HTML for a forward button */
function generateForwardButton(div) {
	return "<A HREF='#' CLASS='GO_FORWARD_IOS_ISH_BUTTON' TARGET='"+div.attr('id')+"'>"+
	"<SPAN>"+div.attr('title')+"</SPAN></A>";
}			

/****** GENERIC MODALS ********/

function genericDeleteModal(options) {

	$.modal("<div id='modal"+options.context+"Delete' class='rembrandt-modal'>"+
      "<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
 		"<div style='text-align:center; padding:10px;'>"+i18n['delete'][lang]+" "+i18n[options.context][lang]+"</div>"+
	   "<div style='text-align:left; padding:3px;'>"+i18n['ays'][lang]+" "+i18n['user'][lang]+"?"+
	   "<div id='info'></div>"+
		"<div id='rrs-waiting-div' style='text-align:center;margin-bottom:5px'>"+
		"<div class='rrs-waiting-div-message'></div></div> "+ 
		"<div id='buttons' style='text-align:center;'>"+
		"<input type='button' id='YesButton' value='"+i18n["yes"][lang]+", "+i18n["delete"][lang]+"'>"+
		"<input type='button' id='NoButton' value='"+i18n["no"][lang]+", "+i18n["cancel"][lang]+"'>"+
		"</div></form></div></div>", {

		onShow: function modalShow(dialog) {
			// fill out table
			dialog.data.find("#info").html(options.info)
			dialog.data.find("#YesButton").click(function(ev) {
			
			jQuery.ajax( {
				type:"POST", url:options.servlet_url,	
				contentType:"application/x-www-form-urlencoded",
				data: options.postdata,
				beforeSubmit: waitMessageBeforeSubmit(lang),
				success: function(response) {
					if (response['status'] == -1) {
						errorMessageWaitingDiv(lang, response['message'])
						dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
						dialog.data.find("#buttons").show()	
					} else if (response['status'] == 0)  {
						showCustomMessageWaitingDiv(options.success_message)
						dialog.data.find("#YesButton").hide()
						dialog.data.find("#NoButton").attr("value",i18n["OK"][lang])
					}
				},
				error:function(response) {
					errorMessageWaitingDiv(lang, response['message'])			
				}
			})
		});
		dialog.data.find("#NoButton").click(function(ev) {
			ev.preventDefault();
			$.modal.close();
		});
	},
	overlayCss:{backgroundColor: '#888', cursor: 'wait'}
	});
}
	
/****** BREADCRUMBLE *******/

function addBreadcrumbleHeader(label, target) {
	$("#main-breadcrumbles").html("<DIV CLASS='main-breadcrumbles-element main-breadcrumbles-header-element'>"+ 
	"<A HREF='#' CLASS='main-breadcrumbles-link' TARGET='"+target+"'>"+label+"</A></DIV>")
	reformatBreadcrumbles()
}

function addBreadcrumbleElement(label, target) {
	$("#main-breadcrumbles").append("<DIV CLASS='main-breadcrumbles-element'>"+ 
	"<A HREF='#' CLASS='main-breadcrumbles-link' TARGET='"+target+"'>"+label+"</A></DIV>")
	reformatBreadcrumbles()
}

function removeLastBreadcrumbleElement() {
	$("#main-breadcrumbles").find("DIV.main-breadcrumbles-element:last").remove()
	reformatBreadcrumbles()
}

function substituteLastBreadcrumbleElement(label, target) {
	$("#main-breadcrumbles").find("DIV.main-breadcrumbles-element:last").remove()
	addBreadcrumbleElement(label, target) 
}

// this function ensures that last element has a.disabled, the remaining ones are active
function reformatBreadcrumbles() {
	var size = $("DIV.main-breadcrumbles-element").size()
	$("DIV.main-breadcrumbles-element").each(function(index, item) {
		if (index < size-1) {
			$(item).find("A").removeClass("disabled")
		} else {
			$(item).find("A").addClass("disabled")		
		}
	})
}
