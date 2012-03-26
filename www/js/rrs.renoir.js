
$().ready(function() {
	
		// set the autocomplete, if the page loads with it on */
	if ($("#rrs-search-suggestion").attr("checked")) {
			$("#q").autocomplete(Rembrandt.urls.restlet_suggestion_url, {
			minChars: 2, dataType: "json", multiple: true,
			mustMatch: false, autoFill: false, matchContains: false,
			formatItem: formatItem, formatMatch: formatMatch,
			multipleSeparator: " ",	formatResult: formatResult,
			parse: parser
		});
	}
	
	/** Decides what to do where an autocomplete lookup returns **/ 
	$("#q").result(findValueCallback)

	  /** submitting the query. Adds additional parameters, puts query in GET, 
	      calls again te same page, relies on updateDisplay() to make the query*/	
	$("A#rrs-search-submit-button").click(function(e) {
		e.preventDefault();
		
		// important to sanitize the next page / query //
		var form = $("#rrs-search-form")
		var text = jQuery.trim(form.find("#q").val())
		
		// hard-code q as a GET variable, too
		form.attr("action", "http://"+window.location.hostname+
		window.location.pathname+setQueryVariable({"q":""+urlencode(text), "do":"search"}) )
		
		var tags = getTags($("#rrs-search-tags"));
		
		//debug("tags:"+tags)
		// Note: you MUST fill all important 
		// insert tag info... collection and user info  can be captured later

		$("#rrs-search-form").append("<INPUT TYPE='HIDDEN' NAME='t' VALUE='"+$.toJSON(tags)+"'>")
		$("#rrs-search-form").append("<INPUT TYPE='HIDDEN' NAME='as_qe' VALUE='"+$("select#as_qe option:selected").val()+"'>")
		$("#rrs-search-form").append("<INPUT TYPE='HIDDEN' NAME='as_model' VALUE='"+$("select#as_model option:selected").val()+"'>")
		$("#rrs-search-form").append("<INPUT TYPE='HIDDEN' NAME='as_maps' VALUE='"+$("#as_maps").attr("checked")+"'>")	
		$("#rrs-search-form").append("<INPUT TYPE='HIDDEN' NAME='as_stem' VALUE='"+$("#as_stem").attr("checked")+"'>")	
		$("#rrs-search-form").append("<INPUT TYPE='HIDDEN' NAME='as_partialscores' VALUE='"+$("#as_partialscores").attr("checked")+"'>")	
		form.trigger("submit")
	}); 
	
	/** shows and hides advanced info **/
	$("#rrs-search-advanced-link").live("click", function(e) {
		e.preventDefault(); /** avoids a kick in the end of the execution */  
		var arrows = $("#rrs-search-advanced-arrows").html()
		//alert(arrows)
		if ($("#rrs-search-advanced-div").css("display") == "none") {
			$("#rrs-search-advanced-div").show("fast")
			$("#rrs-search-advanced-arrows").html("<<")
		} else {
			$("#rrs-search-advanced-div").hide("fast")
			$("#rrs-search-advanced-arrows").html(">>")
		}
	})
	
	/** Removes a tag from lag list **/
	$(".tag_remove").live("click", function() {
		$(this).parent().animate({"width":0, "opacity": 0, "margin":0, "padding":0}, 
		{duration:500, complete:function() {$(this).remove();} 
		});
	});
		
	/** chech if suggestions are on **/
	$("#rrs-search-suggestion").live("click", function(ev, ui) {
		var checked = $(this).attr("checked")
		$.cookie("search_suggestion",checked)
		if (!checked) {
			$("#q").unautocomplete()
		} else {
			$("#q").autocomplete(restlet_suggestion_url, {
			minChars: 2, dataType: "json", multiple: true,
			mustMatch: false, autoFill: false, matchContains: false,
			formatItem: formatItem, formatMatch: formatMatch,
			multipleSeparator: " ",	formatResult: formatResult,
			parse: parser
		});
		}
	})
	
	/** Makes tags dragable **/
	$("#rrs-search-tags").sortable({
		axis: 'x', cursor:'hand', 
		placeholder:'tag_placeholder',
		forcePlaceholderSize: true
	});
})

/** this function performs a search when there is a query variable */
function displayBodyOfRenoir() {
	
	// get query
	var query = $.trim(getQueryVariable("q"));
	var queryterms; if (query) queryterms = query.split(/\s+/);

	// get tags
	var tags = getTags($("#rrs-search-tags"));//

	// get advanced search parameter
	var qe = $('#as_qe option:selected').val() // sets RENOIR QE
	var model = $('#as_model option:selected').val() // sets RENOIR Model
	var stem = $('#as_stem option:selected').attr('checked') // sets RENOIR Model
	var maps = $('#as_maps').attr('checked') // tells RENOIR to return coordinate stuff
	
	// other vars	
	var lang = $('HTML').attr('lang')	
	var limit = getQueryVariable("l");
	var offset = getQueryVariable("o");
	var user = getUser()
	var collection = getCollection()
	var collection_id = getCollectionID()
	var api_key= getAPIKey();

	var main_body = $("#main-body")
	// not really slidable, but it must be included for hide/show
	main_body.html("<DIV ID='rrs-homepage-search' CLASS='main-slidable-div' TITLE='"+
		i18n['search'][lang]+"'></DIV>")	
	// add this breadcrumble header, with or without query
	addBreadcrumbleHeader($("#rrs-homepage-search").attr('title'), 'rrs-homepage-search')			
		
	if (query) {
	
		// this is always a kick-in ledftToRight, as it is triggered by a query.
		
		var divtohide = $("DIV.main-slidable-div:visible")
		var id = hex_md5(query+tags)
		var target = "rrs-searchresult-show-"+id
		var divtoshow = $("#"+target)
		var slide = "horizontal"
			
		jQuery.ajax({type:'POST', url:restlet_renoir_search_url+"?q="+urlencode(encode_utf8(query)),
		    contentType:"application/x-www-form-urlencoded",
			 data: "u="+urlencode(encode_utf8(user))+
			 (tags ? "&t="+urlencode(encode_utf8($.toJSON(tags))) : "")+
			 (qe ? "&qe="+urlencode(encode_utf8(qe)) : "") + 
			 (model ? "&model="+urlencode(encode_utf8(model)) : "") + 
			 (maps ? "&maps="+maps : "") + 
			 "&lg="+lang+"&ci="+collection_id+
			 (limit ? "&l="+limit : "") + (offset ? "&o="+offset : "" )+
			 "&api_key="+api_key,
			 dataType:'json',
			 beforeSubmit: waitMessageBeforeSubmit(lang), 
			
			success: function(response)  {		
				if (response['status'] == -1) {
					errorMessageWaitingDiv(lang, response) //['message'])
				} else {								
					hideWaitingDiv()
					
					var su = false
					var pubkey = response['usr_pub_key']
				
					if (!isUndefined(pubkey)) {
						$("#main-body").attr('USR_PUB_KEY',pubkey)
						su = validateSu(pubkey)
					}
				
					divtoshow = generateSearchResultShowDIV(response,  su, 'saskia', {"id":id, "maps":maps, "query":query,})
					var divtoshow2 = generateSearchResultExplanationDIV(response,  su, 'saskia', {"id":id})
					
					if (slide == 'horizontal') {
						addSlidableDivHeaderTo(divtoshow, divtohide, null);
						addForwardButtonTargeting(divtohide, divtoshow);	
					} else {
						// copy slidable div header from the doc to hide
						copySlidableDivHeaderFromTo(divtohide, divtoshow)
						replaceForwardButtonsTargetingThisToThis(divtohide, divtoshow)
					}
								
					// add the new divs
					divtoshow.appendTo($("#main-body"))
					divtoshow2.appendTo($("#main-body"))	
					
												
					// add submenu to side menu
					var sidemenu = addSubmeuOnSideMenu("searchresult", "saskia")			
					configureSubmenu(sidemenu, "saskia", {"id":id})
					if (slide == 'horizontal') {
						// up/down slides already make this check, this is for 'new' sidemenus 
						reviewSideMenuMakeActiveFor(sidemenu, divtoshow.attr('id'))
					}
					showSubmeuOnSideMenu(sidemenu)
				
				// add breadcrumble (label, target)
					if (slide == 'vertical') {
						substituteLastBreadcrumbleElement(divtoshow.attr('title'), divtoshow.attr('id'))
						slideDownWith(divtoshow)	
					} else {		
						addBreadcrumbleElement(divtoshow.attr('title'), divtoshow.attr('id'))
						slideLeftToRightWith(divtoshow)
					}	
					
					// now process maps
					if (maps) processMap(response, $('#rrs-searchresult-map', divtoshow))
				}	
		  	},			
			error: function(response) {errorMessageWaitingDiv(lang, response)}
		})	
	}
}

		
function bindAutoCompleteOnNESearch(neform) {
		// ne search on stats.. it has to be a live bind
	neform.autocomplete(restlet_suggestion_url, {	
		extraParams: {'t':'ne','lg':lang},
		minChars: 2,
		dataType: "json",
		multiple: false,
		mustMatch: true, 
		autoFill: false,   
		matchContains: false,
		formatItem: formatItem, 
		formatMatch: formatMatch,
		formatResult: formatResult2, 
		parse: parser
	});
	
	// show new tab with the ne
	neform.result(function(value, data){ 
		var  ne_id = data[3]
		var  nename = data[0] 
		var tabs = $('#rrs-tabs'); // tabs or... stats!
		var collection =$("A.collection", $("#rrs-collections")).attr("COLLECTION") 
	  	detailNEinTab(tabs, ne_id, nename, null, null, null, null, null, collection) 
	
	})
}


function parser(data) {
	var parsed = []
	var rows = data['answer']
	for (var i=0; i < rows.length; i++) {
		var row = $.trim(rows[i]);
		if (row) {
			row = row.split("|");
			parsed[parsed.length] = {
				data: row,
				value: row[0],
				result: formatResult(row)
			};
		}
	}
	return parsed;
}
		
function formatItem(row) {
	// row0: name row1: type row2:NE-desc row3: ground info 
	 return "["+row[1]+"] "+row[0] + (row[2] != 'null' ? " <I>"+row[2]+"</I>" : "");
}

function formatResult(row) {
	 return row[0]
}

function formatResult2(row) {
	 return row[3]+":"+row[0]
}

function formatMatch(row) {
	return row[0] + " | " + row[1]+" | " + row[2]+" | " + row[3];
}

function addTag(tag) {
	var unique = true;
	var index = 0;	

	$("#rrs-search-tags .tag").each(function(i, n) { // i has to stay here
	// detect duplicates, check value property + type
	  if ($(n).attr("value") == tag.name && $(n).hasClass(tag.type)) unique=false
	// Get DIV index from its id
	  var currIndex = parseInt($(n).attr("id").split("_")[1]);
	  if (currIndex > index) {index = currIndex;}
	});
	
	// if it's unique, add a DIV
	if (unique) { 	
		var id = "tag_"+(++index)
		var removelink = "<A CLASS='tag_remove'>&times;</A>"
		var div = "<DIV ID='"+id+"' CLASS='tag tag_type_"+tag.type+"' VALUE='"+tag.name+"' "+
			"DESC='"+(tag.desc ? tag.desc : "")+"' GROUND='"+(tag.ground ? tag.ground : "")+"'>"
		div += tag.name+" "+removelink+"</DIV>"
	 	$(div).appendTo("#rrs-search-tags").hide().fadeIn("500").show()
	}
}
	
function getTags(element) {
	var tags = new Array();		
	$(".tag", element).each(function() {
		var value = $(this).attr("value");
		var cl = $(this).attr("className").split(" ")
		for (i in cl) {
			if (cl[i].match("^tag_type_")) 
			    tags.push(new Tag(value, cl[i].substring(9),$(this).attr("desc"), $(this).attr("ground") )); 
		}
	});
//	debug("tags on getTags:"+tags)
	return tags
}	

function findValueCallback(event, data, formatted) {
//	debug("findValueCallback called")
//	debug(event)
//	debug(data)		
	/** This function adds and deletes tags. data is an array [sug_name, sug_type]*/
		addTag(new Tag(data[0], data[1], data[2], data[3]))
};
		
function Tag(name, type, desc, ground) {
	this.name = name
	this.type = type
	if (!isUndefined(desc)) this.desc = desc
	if (!isUndefined(ground)) this.ground = ground
}


