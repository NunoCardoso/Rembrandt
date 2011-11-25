
$().ready(function() {
		// pagination auto-submissions
	$('FORM#page').live("submit", function(ev, ui) {
		ev.preventDefault()
		var a = $(this).parents("DIV.ui-tabs-panel:first").find("A.MANAGE_PAGER.main")
		a.trigger('click')
		return false;
	})

		// pagination event from any tab - will request a refresh on the tabs div with new table DONE
	$('A.MANAGE_PAGER').live('click', function(ev, ui) {
		
		ev.preventDefault()
		var a_clicked = $(this)
		
		var pagerdiv = a_clicked.parents("DIV.rrs-admin-pager") // the pagination div
		var context = pagerdiv.attr("CONTEXT") // context - the entity type being changed

		var action = a_clicked.attr("DO") 
		if (isUndefined(action)) action = "list"
		
		var collection_id = pagerdiv.attr("COLID") // collection id, if found
		var limit = pagerdiv.find("#limit option:selected").val() // limit
		// offset eith comes from an arrow's link attr, or from the box
		var offset = (a_clicked.attr("OFFSET") != null ? a_clicked.attr("OFFSET") : 
			((parseInt(pagerdiv.find("INPUT:text#page").val())-1)*limit) ) 
		var selected = pagerdiv.find("#admin-filter-column-name option:selected").val() // selected
		var column = (selected ? selected: "") // column
		var value = pagerdiv.find("#admin-filter-column-value").val() // value to filter
		var role = pagerdiv.attr("ROLE") 		
		var api_key = getAPIKey()
	   
		// for customized list render functions
		var render = pagerdiv.attr("RENDER")
		
		jQuery.ajax({type:'POST', url:getServletEngineFromRole(role,context),
		contentType:"application/x-www-form-urlencoded", dataType:'json',
		data: "do="+action+"&l="+limit+"&o="+offset+"&lg="+lang+
		(isUndefined(collection_id) ? "" : "&ci="+collection_id)+ // collection_id is undefined if we are paging collections!
		"&c="+column+"&v="+urlencode(encode_utf8(value))+"&api_key="+api_key, 
		beforeSubmit: waitMessageBeforeSubmit(lang),
		
		success: function(response)  {
			if (response['status'] == -1) {errorMessageWaitingDiv(lang, response)}
			else {
				hideWaitingDiv()
				
				var su = false
				var pubkey = response['usr_pub_key']
				
				if (!isUndefined(pubkey)) {
					$("#main-body").attr('USR_PUB_KEY',pubkey)
					su = validateSu(pubkey)
				}
				
				divtoupdate = pagerdiv.parents('DIV.main-slidable-div')
				var functiontocall = (!isUndefined(render) ?  eval(render) : 
					eval("generate"+ UpperCaseFirstLetter(context)+"ListDIV") )
				
				var newdiv = functiontocall(response['message'],su, role, {})
				$("DIV.rrs-pageable", divtoupdate).html( $("DIV.rrs-pageable", newdiv).html() )
								
				$('TABLE.tablesorter', divtoupdate).tablesorter()
 				updateEditInPlace(divtoupdate);
			}
		},
		error: function(response) {errorMessageWaitingDiv(lang, response)}
		})
	})
	
	// a "check all" checkbox that checks or unchecks all checkboxes 
	$(".main-checkbox").live("click", function(ev, ui) {
		if ($(this).attr("checked")) {
			$(this).parents("TABLE:first").find(".sec-checkbox").attr("checked",true)
			$(this).parents("TABLE:first").find(".main-checkbox").attr("checked",true)
		} else {
			$(this).parents("TABLE:first").find(".sec-checkbox").attr("checked",false)
			$(this).parents("TABLE:first").find(".main-checkbox").attr("checked",false)
		}
	})	 
})

/** create a pagination navigator DIV */
function createPagerNavigation(options) {
	
	var limit = options.response['limit'] // max amount of itens per page
	var page = options.response['page'] // real amount of itens per page
	var offset = options.response['offset']
	var total = options.response['total']
	var column = options.response['column']
	var value = options.response['value']
	var collection_id = options.response['col_id'] // only relevant for sdoc, rdoc, etc
	
	var firstpage = (total > 0 ? 1 : 0)
	
	var currpage = Math.ceil((parseInt(offset)+parseInt(page))/limit)
	var lastpage = Math.ceil(total/limit)
	var prevpage = (currpage-1 < firstpage?  firstpage : currpage-1)
	var nextpage = (currpage+1 > lastpage ? lastpage : currpage+1)

	var firstoffset = 0
	var prevoffset = (prevpage-1)*limit
	var nextoffset = (nextpage-1)*limit
	var lastoffset = (lastpage-1)*limit
	
	var action = options.action	
	if (isUndefined(action)) action = "list"
	var render = options.render	
	
	// pager
	// vars: l(limit) in some A.MANAGE_PAGER
	//       o(offset) in some MANAGE_PAGER
	//       do(context) in the DIV.rrs-admin-pager
	//		   ci(collection_id), in the DIV.rrs-admin-pager (if required)
	//       c(column) in the SELECT.admin-filter-column-name
	// 		v(value) in the admin-filter-column-value 
	//       lg(lang) & api_key 
	
	var t = "<DIV CLASS='rrs-admin-pager' CONTEXT='"+options.context+"' COLID='"+collection_id+"' "	
	t += "ROLE='"+options.role+"' "+(!isUndefined(render) ? "RENDER='"+render+"'" : "")+">"
	t+="<DIV>"+total+" "+i18n[options.contexts][lang]
	t += ". "+i18n['showing'][lang]+" "+i18n['page'][lang]+" "+currpage+" "+i18n['of'][lang]+ " "+lastpage+", ";
	t += "<SELECT ID='limit' SIZE=1>"
	t += "<OPTION VALUE='10'"+(limit == 10 ? " SELECTED" : "")+">10</OPTION>"
	t += "<OPTION VALUE='20'"+(limit == 20 ? " SELECTED" : "")+">20</OPTION>"
	t += "<OPTION VALUE='50'"+(limit == 50 ? " SELECTED" : "")+">50</OPTION>"
	t += "</SELECT> "
	t += i18n[options.contexts][lang]+" "+i18n["perpage"][lang]+". </DIV>"
	 
	t += "<DIV CLASS='rrs-admin-arrows'>"
	// pager arrows
	if (currpage == firstpage) {
		t += "<IMG SRC='img/arrow-first.png' style='vertical-align: middle; opacity:0.4'> "
	} else { 
		t += "<A HREF='#' DO='"+action+"' CLASS='MANAGE_PAGER' OFFSET='"+firstoffset+"'>"
		t += "<IMG style='vertical-align: middle;' SRC='img/arrow-first.png'></A> "
	}
	if (currpage == prevpage) {
		t += "<IMG SRC='img/arrow-left.png' style='vertical-align: middle; opacity:0.4'> "
	} else { 
		t += "<A HREF='#' DO='"+action+"' CLASS='MANAGE_PAGER' OFFSET='"+prevoffset+"'>"
		t += "<IMG style='vertical-align: middle;' SRC='img/arrow-left.png'></A> "
	}
	t += "<FORM ID='page' style='display:inline;'><INPUT STYLE='display:inline;' TYPE='TEXT' ID='page' SIZE=3 VALUE='"+currpage+"'></FORM> "
	if (currpage == nextpage) {
		t += "<IMG SRC='img/arrow-right.png' style='vertical-align: middle; opacity:0.4'> "
	} else { 
		t += "<A HREF='#' DO='"+action+"' CLASS='MANAGE_PAGER' OFFSET='"+nextoffset+"'>"
		t += "<IMG style='vertical-align: middle;' SRC='img/arrow-right.png'></A> "
	}
	if (currpage == lastpage) {
		t += "<IMG SRC='img/arrow-last.png' style='vertical-align: middle; opacity:0.4'> "
	} else { 
		t += "<A HREF='#' DO='"+action+"' CLASS='MANAGE_PAGER' OFFSET='"+lastoffset+"'>"
		t += "<IMG style='vertical-align: middle;' SRC='img/arrow-last.png'></A> "
	}
	t += "</DIV>"
	
	// searchable selection
	t += "<DIV>"+i18n['filterby'][lang]+" <SELECT ID='admin-filter-column-name' SIZE=1><OPTION VALUE='' DEFAULT></OPTION>"
	for (i in options.allowedSearchableFields) { 
		var defcolumn = ""
		if (!(column === undefined) && i == column) defcolumn=" SELECTED" 
		t+= "<OPTION VALUE='"+i+"'"+defcolumn+">"+options.allowedSearchableFields[i]+"</OPTION>"
	}
	t += "</SELECT>, "+i18n['having'][lang]+" <INPUT TYPE='TEXT' ID='admin-filter-column-value' ";
	t += " SIZE=10 VALUE='"+( (value === undefined || value == null) ? "" : value)+"'> "
	
	t += "<A HREF='#' CLASS='MANAGE_PAGER main-button' DO='"+action+"'><SPAN>"+i18n['go'][lang]+"</SPAN></A></DIV>"
	t += "</DIV>"
	return t
}

function updateEditInPlace(div) {
	
	var api_key = getAPIKey();
	
	$(".editinplace", div).each(function() {
		var self = $(this)
		var url = eval("restlet_admin_"+self.attr("CONTEXT")+"_url")
		
		var params = ""
		// for user_on_collection, there's two additional parameters: 
		params += "lg="+lang
		params += "&do=update"
		params += "&c="+self.attr("col")
		if (self.attr("id")) params += "&id="+self.attr("id")
		if (self.attr("colid")) params += "&ci="+self.attr("colid")
		if (self.attr("usrid")) params += "&ui="+self.attr("usrid")
		params += '&api_key=' + api_key
		
	 	if (self.hasClass("autocompletetextfield")) {
		
			self.editInPlace({
			field_type:"autocompletetextfield",
			url:url,
			dataType:"json",
			bg_out:	"white",
			update_value:"v",
			default_text:i18n['default_text'][lang],
			show_buttons:true,
			save_button:"<INPUT TYPE='SUBMIT' CLASS='inplace_save' VALUE='"+i18n['save'][lang]+"'></INPUT>",
			cancel_button:"<INPUT TYPE='SUBMIT' CLASS='inplace_cancel' VALUE='"+i18n['cancel'][lang]+"'></INPUT>",
			saving_text:i18n['saving'][lang],
			value_required:true,
			params:params,
		/*	newvalue: function(new_html) { 			
				return urlencode(encode_utf8(new_html)) 
			}, */ 
			success:function(response, el, original_html) { 
				
				if (response["status"] > 0) {
					// I get a full updated object, so let's display the field I want.
					// if it's a saskia object (that is, it has a DIV.saskia_object_tag instead of text),
					// check the COL2 attr, and recreate the DIV.
					
					debug(el)
					
					var answer = response["message"][el.attr('COL')]
					var sobj = el.attr('COL2')
					if (isUndefined(sobj)) {
						el.html(answer)
					} else {
						el.html("<DIV CLASS='saskia_object_tag'>"+answer[sobj]+"</DIV>")
					}
		
				} else {
					el.html(errormessage(lang, response['message'])).pause(1000).hide("fast")
					el.html(original_html).show("fast")
				}
			}, 
			error: function(response, el, original_html) {
				errorMessageWaitingDiv(lang, response['message'])
				el.html(original_html).show("fast")
			}, 		
			callback: function(orig_id, new_html, original_html) {
				if (self.hasClass("group")) {
					// if it's a group. let's replace with checked stuff
					var selves = new Array();
					var colname = self.attr("COL")
					var tr = self.parents("TABLE").find("TR").filter(function() {
					if ($(this).find("INPUT:checkbox").attr("checked")) {return true} else {return false}
					})
					tr.find("DIV[COL="+colname+"]").each(function() {
						$(this).trigger("click")
						$(this).children("form").children(".inplace_field").val(new_html);
						$(this).children("form").children(".inplace_save").trigger('click');
					})
					return false
				} else {return true}
			}
		})
		} else if (self.hasClass("textfield")) {
			self.editInPlace({
			url:url,
			dataType:"json",
			update_value:"v",
			default_text:i18n['default_text'][lang],
			show_buttons:true,
			save_button:"<INPUT TYPE='SUBMIT' CLASS='inplace_save' VALUE='"+i18n['save'][lang]+"'></INPUT>",
			cancel_button:"<INPUT TYPE='SUBMIT' CLASS='inplace_cancel' VALUE='"+i18n['cancel'][lang]+"'></INPUT>",
			saving_text:i18n['saving'][lang],
			value_required:true,
			params:params,
			newvalue: function(new_html) { return urlencode(encode_utf8(new_html)) },
			success:function(response, el, original_html) { 
				if (response["status"] > 0) {
					// I get a full updated object, so let's display the field I want. 
					// How do I know? The field is given by 'COL' attr from original element
					el.html(response["message"][el.attr('COL')])
				} else {
					errorMessageWaitingDiv(lang, response['message'])
					el.html(original_html).show("fast")
				}
			}, 
			error: function(response, el, original_html) {
				errorMessageWaitingDiv(lang, response['message'])
				el.html(original_html).show("fast")
			}, 		
			callback: function(orig_id, new_html, original_html) {
				if (self.hasClass("group")) {
					// if it's a group. let's replace with checked stuff
					var selves = new Array();
					var colname = self.attr("COL")
					var tr = self.parents("TABLE").find("TR").filter(function() {
					if ($(this).find("INPUT:checkbox").attr("checked")) {return true} else {return false}
					})
					tr.find("DIV[COL="+colname+"]").each(function() {
						$(this).trigger("click")
						$(this).children("form").children(".inplace_field").val(new_html);
						$(this).children("form").children(".inplace_save").trigger('click');
					})
					return false
				} else {return true}
			}
		})
		} else if (self.hasClass("textarea")) {	
			self.editInPlace({
			url:url,
			field_type:"textarea",
			dataType:"json",
			update_value:"v",
			default_text:i18n['default_text'][lang],
			show_buttons:true,
			save_button:"<INPUT TYPE='SUBMIT' CLASS='inplace_save' VALUE='"+i18n['save'][lang]+"'></INPUT>",
			cancel_button:"<INPUT TYPE='SUBMIT' CLASS='inplace_cancel' VALUE='"+i18n['cancel'][lang]+"'></INPUT>",
			saving_text:i18n['saving'][lang],
			value_required:true,
			params:params,
			newvalue: function(new_html) { return urlencode(encode_utf8(new_html)) },
			success:function(response, el, original_html) { 
				if (response["status"] > 0) {
					// I get a full updated object, so let's display the field I want. 
					// How do I know? The field is given by 'COL' attr from original element
					el.html(response["message"][el.attr('COL')])
				} else {
					errorMessageWaitingDiv(lang, response['message'])
					el.html(original_html).show("fast")
				}
			}, 
			error: function(response, el, original_html) {
				errorMessageWaitingDiv(lang, response['message'])
				el.html(original_html).show("fast")
			},		
			callback: function(orig_id, new_html, original_html) {
				if (self.hasClass("group")) {
					// if it's a group. let's replace with checked stuff
					var selves = new Array();
					var colname = self.attr("COL")
					var tr = self.parents("TABLE").find("TR").filter(function() {
					if ($(this).find("INPUT:checkbox").attr("checked")) {return true} else {return false}
					})
					tr.find("DIV[COL="+colname+"]").each(function() {
						$(this).trigger("click")
						$(this).children("form").children(".inplace_field").val(new_html);
						$(this).children("form").children(".inplace_save").trigger('click');
					})
					return false
				} else {return true}
			}
		})
		} else if (self.hasClass("selectfield")) {
		
		// can be 01 , lang, proc, sync, edit
		var select_options =""
		if (self.hasClass("01")) {
			select_options=i18n['yes'][lang]+":true, "+i18n['no'][lang]+":false"
		}
		if (self.hasClass("lang")) {
			var it = new Array();
			for(i in selectLang) {it[it.length] = selectLang[i]+":"+i}
			select_options = it.join(", ")
		}
		if (self.hasClass("proc")) {
			var it = new Array();
			for(i in selectProc) {it[it.length] = selectProc[i]+":"+i}
			select_options = it.join(", ")
		}
		if (self.hasClass("sync")) {
			var it = new Array();
			for(i in selectSync) {it[it.length] = selectSync[i]+":"+i}
			select_options = it.join(", ")
		}
		if (self.hasClass("edit")) {
			var it = new Array();
			for(i in selectEdit) {it[it.length] = selectEdit[i]+":"+i}
			select_options = it.join(", ")
		}
			
		self.editInPlace({
			field_type:"select",
			select_options:select_options,
			url:eval("restlet_admin_"+self.attr("CONTEXT")+"_url"),
			dataType:"json",
			default_text:i18n['default_text'][lang],
			update_value:"v",
			show_buttons:true,
			select_text:i18n['select_text'][lang],
			save_button:"<INPUT TYPE='SUBMIT' CLASS='inplace_save' VALUE='"+i18n['save'][lang]+"'></INPUT>",
			cancel_button:"<INPUT TYPE='SUBMIT' CLASS='inplace_cancel' VALUE='"+i18n['cancel'][lang]+"'></INPUT>",
			saving_text:i18n['saving'][lang],
			value_required:true,
			params:params,
			newvalue: function(new_html) { return urlencode(encode_utf8(new_html)) },
			success:function(response, el, original_html) { 
				if (response["status"] > 0) {
					// I get a full updated object, so let's display the field I want. 
					// How do I know? The field is given by 'COL' attr from original element
					var answer = response["message"][el.attr('COL')]

					if (self.attr("valtype") == "Boolean") {

						el.html(printYesOrNo(answer))
					} else {
						el.html(answer)
					}
				} else {
				errorMessageWaitingDiv(lang, response['message'])
					el.html(original_html).show("fast")
				}
			}, 
			error: function(response, el, original_html) {
				errorMessageWaitingDiv(lang, response['message'])
				el.html(original_html).show("fast")
			},
			callback: function(orig_id, new_html, original_html) {
				if (self.hasClass("group")) {
					// if it's a group. let's replace with checked stuff
					var selves = new Array();
					var colname = self.attr("COL")
					var tr = self.parents("TABLE").find("TR").filter(function() {
					if ($(this).find("INPUT.sec-checkbox").attr("checked")) {return true} else {return false}
					})
					tr.find("DIV[COL="+colname+"]").each(function() {
						$(this).trigger("click")
						$(this).children("form").children(".inplace_field").val(new_html);
						$(this).children("form").children(".inplace_save").trigger('click');
					})
					return false
				} else {return true}
			}
		})
	}
	})
}