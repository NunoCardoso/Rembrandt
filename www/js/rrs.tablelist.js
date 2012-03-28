
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
		if (_.isUndefined(action)) action = "list"
		
		var collection_id = pagerdiv.attr("COLID") // collection id, if found
		var limit = pagerdiv.find("#limit option:selected").val() // limit
		// offset eith comes from an arrow's link attr, or from the box
		var offset = (a_clicked.attr("OFFSET") != null ? a_clicked.attr("OFFSET") : 
			((parseInt(pagerdiv.find("INPUT#page").val())-1)*limit) ) 
		var selected = pagerdiv.find("#admin-filter-column-name option:selected").val() // selected
		var column = (selected ? selected: "") // column
		var value = pagerdiv.find("#admin-filter-column-value").val() // value to filter
		var role = pagerdiv.attr("ROLE") 		
		var api_key = Rembrandt.Util.getApiKey()
	   
		// for customized list render functions
		var render = pagerdiv.attr("RENDER")
		
		jQuery.ajax({type:'POST', url:Rembrandt.Util.getServletEngineFromRole(role,context),
		contentType:"application/x-www-form-urlencoded", dataType:'json',
		data: "do="+action+"&l="+limit+"&o="+offset+"&lg="+lang+
		(_.isUndefined(collection_id) ? "" : "&ci="+collection_id)+ // collection_id is undefined if we are paging collections!
		"&c="+column+"&v="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(value))+"&api_key="+api_key, 
		beforeSubmit: waitMessageBeforeSubmit(lang),
		
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
				
				divtoupdate = pagerdiv.parents('DIV.main-slidable-div')
				
				var functiontocall = (!_.isUndefined(render) ?  eval(render) : 
				eval("Rembrandt."+Rembrandt.Util.UpperCaseFirstLetter(context)+".generate"+ Rembrandt.Util.UpperCaseFirstLetter(context)+"ListDIV") )
				
				var newdiv = functiontocall.call(response['message'],su, role, {})
				$("DIV.rrs-pageable", divtoupdate).html( $("DIV.rrs-pageable", $(newdiv)).html() )
								
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
	
	var data = {
		limit		: options.response['limit'], // max amount of itens per page
		page		: options.response['page'], // real amount of itens per page
		offset		: options.response['offset'],
		total		: options.response['total'],
		column		: options.response['column'],
		value		: (!_.isUndefined(options.response['value']) ?  options.response['value'] : ""),
		collection_id: options.response['col_id'],
		context		: options.context,
		role		: options.role,
		l_contexts	: i18n[options.contexts][lang],
		l_showing	: i18n['showing'][lang],
		l_page		: i18n['page'][lang],
		l_of		: i18n['of'][lang],
		l_perpage	: i18n['perpage'][lang],
		l_filter_by : i18n['filterby'][lang],
		l_having	: i18n['having'][lang],
		l_go		: i18n['go'][lang]
	} // only relevant for sdoc, rdoc, etc

	data.action = (!_.isUndefined(options.action) ? options.action : "list")
	data.render = (!_.isUndefined(options.render) ? options.render : "")	
	
	data.firstpage = (data.total > 0 ? 1 : 0),
	data.currpage  = Math.ceil((parseInt(data.offset)+parseInt(data.page))/data.limit)
	data.lastpage  = Math.ceil(data.total/data.limit)
	data.prevpage  = (data.currpage-1 < data.firstpage? data.firstpage : data.currpage-1)
	data.nextpage  = (data.currpage+1 > data.lastpage ? data.lastpage  : data.currpage+1)

	data.searchableFields = [] 
	for (i in options.allowedSearchableFields) { 
		var defcolumn = ""
		if (!(data.column === undefined) && i == data.column) defcolumn=" SELECTED" 
		data.searchableFields.push({
			"html" :"<OPTION VALUE='"+i+"'"+defcolumn+">"+options.allowedSearchableFields[i]+"</OPTION>"
		})
	}
	
	data.isfirstpage = (
		(data.currpage == data.firstpage) ? {
			action		: data.action,
			firstoffset : 0
		}
		: false
	);
	data.isprevpage = (
		(data.currpage == data.prevpage) ? {
			action		: data.action,
			prevoffset 	: (data.prevpage-1)*data.limit
		}
		: false
	)
	data.isnextpage = (
		(data.currpage == data.nextpage) ? {
			action		: data.action,
			nextoffset : (data.nextpage-1)*data.limit
		}
		: false
	);
	data.islastpage = (
		(data.currpage == data.lastpage) ? {
			action		: data.action,
			lastoffset 	: (data.lastpage-1)*data.limit
		}
		: false
	)

	data.selected_10 = (data.limit == 10 ? " SELECTED" : "")
	data.selected_20 = (data.limit == 20 ? " SELECTED" : "")
	data.selected_50 = (data.limit == 50 ? " SELECTED" : "")

	/* pager
	  vars: l(limit) in some A.MANAGE_PAGER
			o(offset) in some MANAGE_PAGER	
			do(context) in the DIV.rrs-admin-pager
			ci(collection_id), in the DIV.rrs-admin-pager (if required)
			c(column) in the SELECT.admin-filter-column-name
			v(value) in the admin-filter-column-value 
			lg(lang) & api_key 
	*/
	
	var template = "\
<DIV CLASS='rrs-admin-pager' CONTEXT='{{context}}' COLID='{{collection_id}}' ROLE='{{role}}' RENDER='{{render}}'>\
	<DIV>{{total}} {{l_contexts}}. {{l_showing}} {{l_page}} {{currpage}} {{l_of}} {{lastpage}}, \
		<SELECT ID='limit' SIZE=1>\
			<OPTION VALUE='10' {{selected_10}}>10</OPTION>\
			<OPTION VALUE='20' {{selected_20}}>20</OPTION>\
			<OPTION VALUE='50' {{selected_50}}>50</OPTION>\
		</SELECT>{{l_contexts}} {{l_perpage}}.\
	</DIV>\
	<DIV CLASS='rrs-admin-arrows'>\
	{{#isfirstpage}}\
		<IMG SRC='img/arrow-first.png' style='vertical-align: middle; opacity:0.4'>\
	{{/isfirstpage}}\
	{{^isfirstpage}}\
		<A HREF='#' DO='{{action}}' CLASS='MANAGE_PAGER' OFFSET='{{firstoffset}}'>\
		<IMG style='vertical-align: middle;' SRC='img/arrow-first.png'></A>\
	{{/isfirstpage}}\
	{{#isprevpage}}\
		<IMG SRC='img/arrow-left.png' style='vertical-align: middle; opacity:0.4'>\
	{{/isprevpage}}\
	{{^isprevpage}}\
		<A HREF='#' DO='{{action}}' CLASS='MANAGE_PAGER' OFFSET='{{prevoffset}}'>\
		<IMG style='vertical-align: middle;' SRC='img/arrow-left.png'></A>\
	{{/isprevpage}}\
	<FORM ID='page' style='display:inline;'>\
		<INPUT STYLE='display:inline;' TYPE='TEXT' ID='page' SIZE=3 VALUE='{{currpage}}'>\
	</FORM>\
	{{#isnextpage}}\
		<IMG SRC='img/arrow-right.png' style='vertical-align: middle; opacity:0.4'>\
	{{/isnextpage}}\
	{{^isnextpage}}\
		<A HREF='#' DO='{{action}}' CLASS='MANAGE_PAGER' OFFSET='{{nextoffset}}'>\
		<IMG style='vertical-align: middle;' SRC='img/arrow-right.png'></A>\
	{{/isnextpage}}\
	{{#islastpage}}\
		<IMG SRC='img/arrow-last.png' style='vertical-align: middle; opacity:0.4'>\
	{{/islastpage}}\
	{{^islastpage}}\
		<A HREF='#' DO='{{action}}' CLASS='MANAGE_PAGER' OFFSET='{{lastoffset}}'>\
		<IMG style='vertical-align: middle;' SRC='img/arrow-last.png'></A>\
	{{/islastpage}}\
	</DIV>\
	{{! searchable selection}}\
	<DIV>{{l_filter_by}} \
		<SELECT ID='admin-filter-column-name' SIZE=1>\
			<OPTION VALUE='' DEFAULT></OPTION>\
			{{#searchableFields}}\
				{{{html}}}\
			{{/searchableFields}}\
		</SELECT>, {{l_having}} <INPUT TYPE='TEXT' ID='admin-filter-column-value' SIZE=10 VALUE='{{value}}'>\
		<A HREF='#' CLASS='MANAGE_PAGER main-button' DO='{{action}}'>\
			<SPAN>{{l_go}}</SPAN>\
		</A>\
	</DIV>\
</DIV>";

	return Mustache.to_html(template, data);
}

function updateEditInPlace(div) {
	
	var api_key = Rembrandt.Util.getApiKey();
	
	$(".editinplace", div).each(function() {
		var self = $(this)
		var url = eval("Rembrandt.urls.restlet_admin_"+self.attr("CONTEXT")+"_url")
		
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
				return Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(new_html)) 
			}, */ 
			success:function(response, el, original_html) { 
				
				if (response["status"] > 0) {
					// I get a full updated object, so let's display the field I want.
					// if it's a saskia object (that is, it has a DIV.saskia_object_tag instead of text),
					// check the COL2 attr, and recreate the DIV.
					
					var answer = response["message"][el.attr('COL')]
					var sobj = el.attr('COL2')
					if (_.isUndefined(sobj)) {
						el.html(answer)
					} else {
						el.html("<DIV CLASS='saskia_object_tag'>"+answer[sobj]+"</DIV>")
					}
		
				} else {
					el.html(errormessage(lang, response['message'])).animate({dummy:1}, 1000).hide("fast")
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
			newvalue: function(new_html) { return Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(new_html)) },
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
			newvalue: function(new_html) { return Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(new_html)) },
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
			for(i in Rembrandt.options.selectLang) {it[it.length] = Rembrandt.options.selectLang[i]+":"+i}
			select_options = it.join(", ")
		}
		if (self.hasClass("proc")) {
			var it = new Array();
			for(i in Rembrandt.options.selectProc) {it[it.length] = Rembrandt.options.selectProc[i]+":"+i}
			select_options = it.join(", ")
		}
		if (self.hasClass("sync")) {
			var it = new Array();
			for(i in Rembrandt.options.selectSync) {it[it.length] = Rembrandt.options.selectSync[i]+":"+i}
			select_options = it.join(", ")
		}
		if (self.hasClass("edit")) {
			var it = new Array();
			for(i in Rembrandt.options.selectEdit) {it[it.length] = Rembrandt.options.selectEdit[i]+":"+i}
			select_options = it.join(", ")
		}
			
		self.editInPlace({
			field_type:"select",
			select_options:select_options,
			url:eval("Rembrandt.urls.restlet_admin_"+self.attr("CONTEXT")+"_url"),
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
			newvalue: function(new_html) { return Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(new_html)) },
			success:function(response, el, original_html) { 
				if (response["status"] > 0) {
					// I get a full updated object, so let's display the field I want. 
					// How do I know? The field is given by 'COL' attr from original element
					var answer = response["message"][el.attr('COL')]

					if (self.attr("valtype") == "Boolean") {

						el.html(Rembrandt.Util.printYesOrNo(answer))
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