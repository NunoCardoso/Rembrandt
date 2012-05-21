var Renoir = (function ($) {
	"use strict"
	$(function () {
		
		if ($("#rrs-search-suggestion").attr("checked")) {
			$("#q").autocomplete(Rembrandt.urls.restlet_suggestion_url, {
				minChars: 2, 
				dataType: "json", 
				multiple: true,
				mustMatch: false, 
				autoFill: false, 
				matchContains: false,
				formatItem: _formatItem, 
				formatMatch: _formatMatch,
				multipleSeparator: " ",	
				formatResult: _formatResult,
				parse: _parser
			});
		}
	
		/** Decides what to do where an autocomplete lookup returns **/ 
		$("#q").result(_findValueCallback)

		/** submitting the query. Adds additional parameters, puts query in GET, 
	      calls again te same page, relies on updateDisplay() to make the query*/	
		$("A#rrs-search-submit-button").click(function(e) {
			e.preventDefault();
		
			// important to sanitize the next page / query //
			var form = $("#rrs-search-form")
			var text = jQuery.trim(form.find("#q").val())
			Renoir.display(text)
		}); 
		
		/** submitting pre-existing queries */	
		$("A#rrs-search-submit-button-2").click(function(e) {
			e.preventDefault();
			// important to sanitize the next page / query //
			var form = $("#rrs-search-form")
			var val = $("#query option:selected").val()
			Renoir.fetchQrelsForQueryID(val)
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
					minChars: 2, 
					dataType: "json", 
					multiple: true,
					mustMatch: false, 
					autoFill: false, 
					matchContains: false,
					formatItem: _formatItem, 
					formatMatch: _formatMatch,
					multipleSeparator: " ",	
					formatResult: _formatResult,
					parse: _parser
				});
			}
		})
	
		/** Makes tags dragable **/
		$("#rrs-search-tags").sortable({
			axis: 'x', cursor:'hand', 
			placeholder:'tag_placeholder',
			forcePlaceholderSize: true
		});
	});

	var display = function(_query) {
	
		// get query
		var query
		if (_.isUndefined(_query)) {
			query = $.trim(Rembrandt.Util.getQueryVariable("q"));
		} else {
			query = _query;
		}
		var queryterms; if (query) queryterms = query.split(/\s+/);

		// get tags
		var tags = Renoir.getTags($("#rrs-search-tags"));//

		// get advanced search parameter
		var qe = $('#as_qe option:selected').val() // sets RENOIR QE
		var model = $('#as_model option:selected').val() // sets RENOIR Model
		var stem = $('#as_stem option:selected').attr('checked') // sets RENOIR Model
		var maps = $('#as_maps').attr('checked') // tells RENOIR to return coordinate stuff
	
		// other vars	
		var lang = $('HTML').attr('lang')	
		var limit = Rembrandt.Util.getQueryVariable("l");
		var offset = Rembrandt.Util.getQueryVariable("o");
		var user = Rembrandt.Util.getUser()
		var collection = Rembrandt.Util.getCollection()
		var collection_id = Rembrandt.Util.getCollectionId()
		var api_key= Rembrandt.Util.getApiKey();

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
			
			jQuery.ajax({type:'POST', 
				url:Rembrandt.urls.restlet_renoir_search_url+
					"?q="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(query)),
				contentType:"application/x-www-form-urlencoded",
				data: "u="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(user))+
			 	(tags ? "&t="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8($.toJSON(tags))) : "")+
			 	(qe ? "&qe="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(qe)) : "") + 
			 	(model ? "&model="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(model)) : "") + 
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
				
						if (!_.isUndefined(pubkey)) {
							$("#main-body").attr('USR_PUB_KEY',pubkey)
							su = Rembrandt.Util.validateSu(pubkey)
						}
				
						divtoshow = generateSearchResultShowDIV(response,  su, 'saskia', {"id":id, "maps":maps, "query":query})
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
	},

 	bindAutoCompleteOnNESearch = function(neform) {
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
	},

    _parser = function(data) {
		var parsed = []
		var rows = data['answer']
		for (var i=0; i < rows.length; i++) {
			var row = $.trim(rows[i]);
			if (row) {
				row = row.split("|");
				parsed[parsed.length] = {
					data: row,
					value: row[0],
					result: _formatResult(row)
				};
			}
		}
		return parsed;
	},
	
	fillQueryCollecions = function(select_) {
		if (select_.find("option").size() > 1) return
		jQuery.ajax({type:'GET', 
			url:Rembrandt.urls.restlet_renoir_query_collection_url,
			dataType:'json',
		 	beforeSubmit: waitMessageBeforeSubmit(lang), 
			success: function(response) {
				if (response['status'] == -1) {
					errorMessageWaitingDiv(lang, response) //['message'])
				} else {
					hideWaitingDiv()
					select_.append("<option value=''>--</option>")
					var message = response["message"]
					for (var i in message) {
						select_.append("<option value='"+message[i]["qcl_id"]+"'>"+message[i]["qcl_name"]+"</option>")
					}
				}
			}, 
			error: function(response) {
				errorMessageWaitingDiv(lang, response)
			}
		})
	},
	
	fillQueries = function(select_, val) {
		if (select_.find("option").size() > 1) return
		jQuery.ajax({type:'GET', 
			url:Rembrandt.urls.restlet_renoir_queries_url+"?q="+val,
			dataType:'json',
		 	beforeSubmit: waitMessageBeforeSubmit(lang), 
			success: function(response) {
				if (response['status'] == -1) {
					errorMessageWaitingDiv(lang, response) //['message'])
				} else {
					hideWaitingDiv()
					var message = response["message"]
					for (var i in message) {
						var text = message[i]["que_query"]
						text = text.replace(/\]/g," ").replace(/\[/g,"").trim()
						select_.append("<option value='"+message[i]["que_id"]+"'>"+text+"</option>")
					}
				}
			}, 
			error: function(response) {
				errorMessageWaitingDiv(lang, response)
			}
		})
	},
	
	fetchQrelsForQueryID = function (queryid, query) {

		var lang = $('HTML').attr('lang')	
		var limit = Rembrandt.Util.getQueryVariable("l");
		var offset = Rembrandt.Util.getQueryVariable("o");
		var user = Rembrandt.Util.getUser()
		var api_key= Rembrandt.Util.getApiKey();
		var maps = $('#as_maps').attr('checked') // tells RENOIR to return coordinate stuff

		var main_body = $("#main-body")
		// not really slidable, but it must be included for hide/show
		main_body.html("<DIV ID='rrs-homepage-search' CLASS='main-slidable-div' TITLE='"+
		i18n['search'][lang]+"'></DIV>")	
		// add this breadcrumble header, with or without query
		addBreadcrumbleHeader($("#rrs-homepage-search").attr('title'), 'rrs-homepage-search')
		
		if (!_.isUndefined(queryid)) {
	
			var divtohide = $("DIV.main-slidable-div:visible")
			var target = "rrs-searchresult-show-"+queryid
			var divtoshow = $("#"+target)
			var slide = "horizontal"
			
			jQuery.ajax({type:'POST', 
				url:Rembrandt.urls.restlet_renoir_searchwithqrel_url+
					"?q="+queryid,
				contentType:"application/x-www-form-urlencoded",
				data: "u="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(user))+
				"&lg="+lang+
				(limit ? "&l="+limit : "") + 
				(offset ? "&o="+offset : "" )+
				(maps ? "&maps="+maps : "") + 
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
				
						if (!_.isUndefined(pubkey)) {
							$("#main-body").attr('USR_PUB_KEY',pubkey)
							su = Rembrandt.Util.validateSu(pubkey)
						}
				
						divtoshow = generateSearchResultShowDIV(response,  su, 'saskia', {"id":queryid, "maps":maps, "query":query})
					
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
						
						// add submenu to side menu
						var sidemenu = addSubmeuOnSideMenu("searchresult", "saskia")
						configureSubmenu(sidemenu, "saskia", {"id":queryid})
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
	},
	
	_formatItem = function(row) {
		// row0: name row1: type row2:NE-desc row3: ground info 
	 	return "["+row[1]+"] "+row[0] + (row[2] != 'null' ? " <I>"+row[2]+"</I>" : "");
	},

	_formatResult = function(row) {
	 	return row[0]
	},

	_formatResult2 = function(row) {
	 	return row[3]+":"+row[0]
	},

	_formatMatch = function(row) {
		return row[0] + " | " + row[1]+" | " + row[2]+" | " + row[3];
	},

	_findValueCallback = function (event, data, formatted) {
		/** This function adds and deletes tags. data is an array [sug_name, sug_type]*/
			Renoir.addTag(new Renoir.Tag(data[0], data[1], data[2], data[3]))
	},
	
	addTag = function(tag) {
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
	},
	
	getTags = function(element) {
		var tags = new Array();
		$(".tag", element).each(function() {
			var value = $(this).attr("value");
			var cla = $(this).attr("class")
			var cl
			if (cla) cl = cla.split(" ")
			for (var i in cl) {
				if (cl[i].match("^tag_type_")) 
				    tags.push(new Renoir.Tag(value, cl[i].substring(9),$(this).attr("desc"), $(this).attr("ground") )); 
			}
		});
		return tags
	},
	
	Tag = function (name, type, desc, ground) {
		this.name = name
		this.type = type
		if (!_.isUndefined(desc)) this.desc = desc
		if (!_.isUndefined(ground)) this.ground = ground
	
	};

	return {
		"bindAutoCompleteOnNESearch":bindAutoCompleteOnNESearch,
		"display":display,
		"Tag":Tag,
		"addTag":addTag,
		"getTags":getTags,
		"fillQueryCollecions": fillQueryCollecions,
		"fillQueries": fillQueries,
		"fetchQrelsForQueryID":fetchQrelsForQueryID
	}
})(jQuery);