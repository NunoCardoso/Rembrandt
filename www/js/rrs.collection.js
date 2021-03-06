Rembrandt = Rembrandt || {};

Rembrandt.Collection = (function ($) {
	"use strict"
	$(function () {
	
		// 'new collection' button action, opens modal dialog 
		$('A.COLLECTION_CREATE').live("click", function(ev, ui) {
			ev.preventDefault();
			Rembrandt.Collection.createCollection(this);
		})
		
		// 'delete collection' button action, opens modal dialog 
		$('A.COLLECTION_DELETE').live("click", function(ev, ui) {
			ev.preventDefault();
			Rembrandt.Collection.deleteCollection(this);
		})
		
		// List collections for admin and/or user
		$('A.COLLECTION_LIST').live("click", function(ev, ui) {
			ev.preventDefault();
			Rembrandt.Collection.listCollections(this);
		})
		
		// TODO
		$('A.COLLECTION_REFRESH_STATS').live("click", function(ev, ui) {
			ev.preventDefault();
			Rembrandt.Collection.refreshStats(this);
		})
		
		$('A.COLLECTION_RDOC_LIST').live("click", function(ev, ui) {
			ev.preventDefault();
			Rembrandt.Collection.listRembrandtedDocs(this);
		})
		
		// List source docs
		$('A.COLLECTION_SDOC_LIST').live("click", function(ev, ui) {
			ev.preventDefault();
			Rembrandt.Collection.listSourceDocs(this);
		})
		
		$('A.COLLECTION_TASK_LIST').live("click", function(ev, ui) {
			ev.preventDefault();
			Rembrandt.Collection.listTasks(this);
		})
		
		$('A.COLLECTION_SHOW').live("click", function(ev, ui) {
			ev.preventDefault();
			Rembrandt.Collection.showCollection(this);
		})
	});
	
	var createCollection = function(context) {
		modalCollectionCreate(context);
	
		// let's refresh the content. How? triggering the admin-pager link if the 
		// visible div is for a collection list or collection main page
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-homepage-collection') {
			displayBodyOfSaskia()
		}
		if (divshown.attr('id') == 'rrs-collection-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
	}, 
	
	deleteCollection = function(context) {
		modalCollectionDelete(context);
	
		// let's refresh the content. How? triggering the admin-pager link if the 
		// visible div is for a collection list or collection main page
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-homepage-collection') {
			displayBodyOfSaskia()
		} 
		if (divshown.attr('id') == 'rrs-collection-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}	
	},
	
	listCollections = function (context) {

		var a_clicked = context,
			api_key = Rembrandt.Util.getApiKey(),
			role = $(a_clicked).attr('ROLE'),
			title = ($(a_clicked).attr("TITLE") ? $(a_clicked).attr("TITLE") : $(a_clicked).text()),
			target = $(a_clicked).attr("TARGET");
			
		showSlidableDIV({
			"title": 	title,
			"target": 	target,
			"role": 	role,
			"slide": 	getSlideOrientationFromLink(a_clicked),
			"ajax": 	true,
			"restlet_url":Rembrandt.Util.getServletEngineFromRole(role, "collection"),
			"postdata":"do=list&l=10&o=0&lg="+lang+"&api_key="+api_key,
			"divRender":Rembrandt.Collection.generateCollectionListDIV, 
			"divRenderOptions":{},
			"sidemenu":null, 
			"sidemenuoptions":{}
		})
	},

	refreshStats = function (context) {

		var a_clicked = context,
		 	col_id = $(a_clicked).attr("id"),
			title = ($(a_clicked).attr("TITLE") ? a_clicked.attr("TITLE") : a_clicked.text()),
			api_key = Rembrandt.Util.getApiKey(),
			role = $(a_clicked).attr('ROLE'),
			servlet_url = Rembrandt.Util.getServletEngineFromRole(role, "collection");

		a_clicked.attr('disabled', true)
		a_clicked.toggleClass("main-button", "main-button-disabled")

		jQuery.ajax({
			type:"POST", 
			url:Rembrandt.urls.restlet_saskia_collection_url, 
			dataType:"json",
			data:"lg="+lang+"&do=refreshstats&id="+col_id+"&api_key="+Rembrandt.Util.getApiKey(),
			beforeSubmit: button.find("SPAN").html(
				waitmessage(lang, i18n['refreshing_collection'][lang])), 
				
			success:function(response) {

				if (response["status"] == -1) {

					errorMessageWaitingDiv(lang, response)
					button.toggleClass('disabled',false)
						.toggleClass("main-button-disabled","main-button")
						.find("SPAN")
						.html(i18n['refresh'][lang])

				} else if (response["status"] == 0) {

					button.toggleClass("adminbuttondisabled","adminbutton")
						.find("SPAN")
						.html(i18n['OK'][lang])
				}	
			},
			error:function(response) {
				// put it back
				errorMessageWaitingDiv(lang, response)
				button.toggleClass('disabled',false)
					.toggleClass("main-button-disabled","main-button")
			}
		})
	},
	
	listRembrandtedDocs = function (context) {

		var a_clicked = context,
		    col_id = $(a_clicked).attr("id"),
		    api_key = Rembrandt.Util.getApiKey(),
		    title = ($(a_clicked).attr("title") ? $(a_clicked).attr("title") : $(a_clicked).text()),
			role = $(a_clicked).attr('ROLE'),
			target = $(a_clicked).attr("TARGET");

		showSlidableDIV({
			"title" 	: title,
			"target" 	:target,
			"role" 		:role,
			"slide"		: getSlideOrientationFromLink(a_clicked),
			"ajax"		:true,
			"restlet_url":Rembrandt.Util.getServletEngineFromRole(role, "rdoc"),
			"postdata":"do=list&ci="+col_id+"&l=10&o=0&lg="+lang+"&api_key="+api_key,
			"divRender" :Rembrandt.Collection.generateCollectionRdocListDIV, 
			"divRenderOptions":{},
			"sidemenu":"collection", 
			// only update col_id, no col_name -> it will change side menu header. Leave that to COLLECTION_SHOW
			"sidemenuoptions":{"id":col_id}//, "col_name":title}
		})	
	},

	listSourceDocs = function (context) {

		var a_clicked = context,
		    col_id = $(a_clicked).attr("id"),
		    api_key = Rembrandt.Util.getApiKey(),
		    title = ($(a_clicked).attr("title") ? $(a_clicked).attr("title") : $(a_clicked).text()),
			role = $(a_clicked).attr('ROLE'),
			target = $(a_clicked).attr("TARGET");

		showSlidableDIV({
			"title" 	: title,
			"target" 	:target,
			"role" 		:role,
			"slide"		: getSlideOrientationFromLink(a_clicked),
			"ajax"		:true,
			"restlet_url":Rembrandt.Util.getServletEngineFromRole(role, "sdoc"),
			"postdata" 	:"do=list&ci="+col_id+"&l=10&o=0&lg="+lang+	"&api_key="+api_key,
			"divRender" :Rembrandt.Collection.generateCollectionSdocListDIV, 
			"divRenderOptions":{},
			"sidemenu":"collection", 
			"sidemenuoptions":{"id":col_id}//, "col_name":title}
		})	
	},
	
	listTasks = function (context) {

		var a_clicked = context,
			col_id = $(a_clicked).attr("id"),
			api_key = Rembrandt.Util.getApiKey(),
			title = ($(a_clicked).attr("title") ? $(a_clicked).attr("title") : $(a_clicked).text()),
			role = $(a_clicked).attr('ROLE'),
			target = $(a_clicked).attr("TARGET");

		showSlidableDIV({
			"title" 	: title,
			"target" 	: target,
			"role" 		: role,
			"slide"		: getSlideOrientationFromLink(a_clicked),
			"ajax"		:true,
			"restlet_url" 	:Rembrandt.Util.getServletEngineFromRole(a_clicked.attr('ROLE'), "task"),
			"postdata"	:"do=list&ci="+col_id+"&l=10&o=0&lg="+lang+	"&api_key="+api_key,
			"divRender" :Rembrandt.Collection.generateCollectionSdocListDIV, 
			"divRenderOptions":{},
			"sidemenu" 	:"collection", 
			"sidemenuoptions":{"id":col_id}//, "col_name":title}
		})	
	},

	showCollection = function (context) {
		var a_clicked = $(context);
		var col_id = a_clicked.attr("ID")
		var api_key = Rembrandt.Util.getApiKey()
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : a_clicked.text())
			
		showSlidableDIV({
			"title": title,
			"target":a_clicked.attr("TARGET"),
			"role":a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":true,
			"restlet_url":Rembrandt.Util.getServletEngineFromRole(a_clicked.attr('ROLE'), "collection"),
			"postdata":"do=show&id="+col_id+"&lg="+lang+"&api_key="+api_key,
			"divRender":Rembrandt.Collection.generateCollectionShowDIV, 
			"divRenderOptions":{},
			"sidemenu":"collection", 
			"sidemenuoptions":{"id":col_id, "col_name":title}
		})
	},

	/*************************/
	/*** content creation ****/
	/*************************/

	/** generate collection list DIV */
	generateCollectionListDIV = function(response, su, role, options) {

		var context = "collection",
			res = response['result'],
			canadmin = (su || role.toLowerCase() == "col-admin"),
			editinplace = (canadmin  ? "editinplace" : ""),
			navigation = createPagerNavigation({
				"context":context,
				"contexts":"collections", 
				"response":response,
				"role":role, 
				"allowedSearchableFields":{
					"col_id":i18n['id'][lang], 
					"col_name":i18n['name'][lang], 
					"col_lang":i18n['lang'][lang], 
					"col_owner":i18n['owner'][lang], 
					"col_permission":i18n['permissions'][lang]
				}
			}),
			res_html = [];
		
		for(i in res) {
		
			var res_data = {
				 "id"			: res[i]['col_id'],
				 "col_name"		: res[i]['col_name'],
				 "col_lang"		: res[i]['col_lang'],
				 "col_comment"	: res[i]['col_comment'],
				 "contextU"		: context.toUpperCase(),
				 "context"		: context,
				 "role"			: role,
				 "editinplace" 	: editinplace,
				 "su"			: {
					"context"	: context,
					"editinplace": editinplace,
					"usr_login" : res[i]['col_owner']['usr_login'],
				 },
				"canadmin"		:{
				 	"id"			: res[i]['col_id'],
				 	"col_name"		: res[i]['col_name'],
				 	"role"			: role,
					"context"	: context,
					"contextU"	: context.toUpperCase(),
					"editinplace": editinplace,
					"permissions": 	i18n['permissions'][lang],
					"col_permission" : res[i]['col_permission'],
					"delete"	: i18n['delete'][lang]
				},
			}
			
			var res_template = "\
			<TR>\
				<TD>\
					<INPUT TYPE='CHECKBOX' CLASS='sec-checkbox'>\
				</TD>\
				<TH>\
					<A CLASS='{{contextU}}_SHOW' ID='{{id}}' TARGET='rrs-{{context}}-show-{{id}}' \
					TITLE='{{col_name}}' HREF='#' ROLE='{{role}}'>{{id}}</A>\
				</TH>\
				<TD>\
					<DIV CONTEXT='{{context}}' COL='col_name' ID='{{id}}' \
					CLASS='{{editinplace}} textfield'>{{col_name}}</DIV>\
				</TD>\
				<TD>\
					<DIV CONTEXT={{context}} COL='col_lang' ID='{{id}}' \
					CLASS='{{editinplace}} textfield'>{{col_lang}}</DIV>\
				</TD>\
			{{#su}}\
				<TD>\
					<DIV CONTEXT='{{context}}' COL='col_owner' COL2='usr_login' ID='{{id}}' \
					CLASS='{{editinplace}} autocompletetextfield'>\
						<DIV CLASS='saskia_object_tag'>{{usr_login}}</DIV>\
					</DIV>\
				</TD>\
			{{/su}}\
			{{#canadmin}}\
				<TD>\
					<DIV CONTEXT='{{context}}' COL='col_permission' ID='{{id}}' \
					CLASS='{{editinplace}} textfield'>{{col_permission}}</DIV>\
				</TD>\
			{{/canadmin}}\
			<TD>\
				<DIV CONTEXT='{{context}}' COL='col_comment' ID='{{id}}' \
				CLASS='{{editinplace}} textarea'>{{col_comment}}</DIV>\
			</TD>\
			{{#canadmin}}\
				<TD>\
					<A HREF='#' CLASS='{{contextU}}_DELETE main-button' ROLE='{{role}}' ID='{{id}}' TITLE='{{col_name}}'>\
						<SPAN>{{delete}}...</SPAN>\
					</A>\
				</TD>\
			{{/canadmin}}\
			</TR>";
			
			res_html.push({"html":Mustache.to_html(res_template, res_data)})
		}	

		var data = {
			"navigation"	: navigation,
			"context"		: context,
			"title"			: i18n[context+'_list'][lang],
			"id"			: i18n['id'][lang],
			"collection"	: i18n['collection'][lang],
			"lang"			: i18n['lang'][lang],
			"comment"	 	: i18n['comment'][lang],
			"all"			: i18n['all'][lang],
			"su"			:{
				"contextU"	: context.toUpperCase(),
				"title"		: i18n['create_new_'+context][lang],
				"role"		: role,
				"owner"		: i18n['owner'][lang]
			},
			"canadmin"		:{
				"permissions": 	i18n['permissions'][lang]
			},
			"res_html"		: res_html
		}
		
		var template = "\
<DIV ID='rrs-collection-list' CLASS='main-slidable-div' TITLE='{{title}}' STYLE='overflow:auto;'>\
<DIV CLASS='rrs-pageable'>{{{navigation}}}\
	<DIV CLASS='rrs-buttonrow'>\
	{{#su}}\
		<A HREF='#' CLASS='{{contentU}}_CREATE main-button' TITLE='{{title}}' ROLE='{{role}}'>\
			<SPAN>{{title}}</SPAN>\
		</A>\
	{{/su}}\
	</DIV>\
	<DIV>\
		<TABLE ID='rrs-{{context}}-list-table' CLASS='tablesorter'>\
			<THEAD>\
				<TR>\
					<TD>\
						<INPUT TYPE='CHECKBOX' CLASS='main-checkbox'>\
					</TD>\
					<TH>{{id}}</TH>\
					<TH>{{collection}}</TH>\
					<TH>{{lang}}</TH>\
					{{#su}}<TH>{{owner}}</TH>{{/su}}\
					{{#canadmin}}<TH>{{permissions}}</TH>{{/canadmin}}\
					<TH>{{comment}}</TH>\
					{{#canadmin}}<TD></TD>{{/canadmin}}\
				</TR>\
			</THEAD>\
			<TBODY>\
			{{#res_html}}\
				{{{html}}}\
			{{/res_html}}\
			<TFOOT>\
				<TR>\
					<TD>\
						<INPUT TYPE='CHECKBOX' CLASS='main-checkbox'>\
					</TD>\
					<TH>{{all}}</TH>\
					<TD>\
						<DIV CONTEXT='{{context}}' COL='col_name' CLASS='{{editinplace}} textfield group'>\
						</DIV>\
					</TD>\
					<TD>\
						<DIV CONTEXT='{{context}}' COL='col_lang' CLASS='{{editinplace}} textfield group'>\
						</DIV>\
					</TD>\
					{{#su}}\
						<TD>\
							<DIV CONTEXT='{{context}}' COL='col_owner' CLASS='{{editinplace}} autocompletetextfield group'>\
							</DIV>\
						</TD>\
					{{/su}}\
					{{#canadmin}}\
						<TD>\
							<DIV CONTEXT='{{context}}' COL='col_permission' CLASS='{{editinplace}} textfield group'>\
							</DIV>\
						</TD>\
					{{/canadmin}}\
					<TD>\
						<DIV CONTEXT='{{context}}' COL='col_comment' CLASS='{{editinplace}} textfield group'></DIV>\
					</TD>\
					{{#canadmin}}<TD></TD>{{/canadmin}}\
				</TR>\
			</TFOOT>\
		</TABLE>\
	</DIV>\
</DIV>\
</DIV>";
 		return $(Mustache.to_html(template, data));
	},

	/** generate collection show DIV */
	generateCollectionShowDIV = function(col, su, role, options) {

		var context = "collection",
			canadmin = (su || role.toLowerCase() == "col-admin"),
			editinplace = ( canadmin ? "editinplace" : ""),
			id = col['col_id'];
	
		var data = {
			"context"		: context,
			"id"			: id,
			"colname"		: col['col_name'],
			"collang"		: col['col_lang'], 
			"colcomment"	: col['col_comment'],
			"colpermission"	: col['col_permission'],
			"number_sdocs"	: col['number_sdocs'],
			"number_rdocs"	: col['number_rdocs'],
			"number_tasks"	: 0,
			"s_colname"		: Rembrandt.Util.shortenTitle(col['col_name']),
			"context-show"	: i18n[context+'-show'][lang],
			"l_id"			: i18n['id'][lang],
			"l_name"		: i18n['name'][lang],
			"l_lang"		: i18n['lang'][lang],
			"l_comment"		: i18n['comment'][lang],
			"l_permissions" : i18n['permissions'][lang],
			"l_sdocs"		: i18n['sdocs'][lang],
			"l_rdocs"		: i18n['rdocs'][lang],
			"l_tasks"		: i18n['tasks'][lang],
			"create_new_sdoc": i18n['create_new_sdoc'][lang],
			"create_new_context" : i18n['create_new_'+context][lang],
			"l_sdoc_import"	: i18n['sdoc_import'][lang],
			"l_sdoc_export"	: i18n['sdoc_export'][lang],
			"editinplace"	: editinplace,
			"role"			: role,
			"stats"			: i18n['stats'][lang],
			"delete"		: i18n['delete'][lang],
			"su"			:{
				"context"		: context,
				"id"			: id,
				"login"			: col['col_owner']['usr_login']
			},
			"canadmin"		:{
				"permissions": 	i18n['permissions'][lang]
			}
		}
		
		var template = "\
<DIV ID='rrs-{{context}}-show-{{id}}' CLASS='main-slidable-div' TITLE='{{s_colname}}' STYLE='overflow:auto;'>\
	<DIV CLASS='rrs-pageable'>\
		<H3>{{context-show}}</H3>\
		<TABLE ID='rrs-{{context}}-show-table tablesorter' BORDER=0>\
			<TR>\
				<TD>{{l_id}}:</TD>\
				<TD>{{id}}</TD>\
			</TR><TR>\
				<TD>{{l_name}}:</TD>\
				<TD><DIV CONTEXT='{{context}}' COL='col_name' ID='{{id}}' CLASS='{{editinplace}} textfield'>{{colname}}</DIV></TD>\
			</TR><TR>\
				<TD>{{l_lang}}:</TD>\
				<TD><DIV CONTEXT='{{context}}' COL='col_lang' ID='{{id}}' CLASS='{{editinplace}} textfield'>{{collang}}</DIV></TD>\
			</TR>\
			{{#su}}\
				<TR>\
					<TD><DIV CONTEXT='{{context}}' COL='col_owner' COL2='usr_login' ID='{{id}}' CLASS='{{editinplace}} autocompletetextfield'>\
						<DIV CLASS='saskia_object_tag'>{{login}}</DIV>\
					</DIV></TD>\
				</TR>\
			{{/su}}\
			<TR>\
				<TD>{{l_comment}}:</TD>\
				<TD><DIV CONTEXT='{{context}}' COL='col_comment' ID='{{id}}' CLASS='{{editinplace}} textfield'>{{colcomment}}</DIV></TD>\
			</TR>\
			{{#canadmin}}\
			<TR>\
				<TD>{{l_permissions}}:</TD>\
				<TD><DIV CONTEXT='{{context}}' COL='col_permission' ID='{{id}}' CLASS='{{editinplace}} textfield'>{{colpermission}}</DIV></TD>\
			</TR>\
			{{/canadmin}}\
			<TR>\
				<TD>\
					<A HREF='#' CLASS='COLLECTION_SDOC_LIST slide-vertically-link' TARGET='rrs-collection-sdoc-list-{{id}}' ID='{{id}}' ROLE='{{role}}'>\
					{{l_sdocs}}</A>:\
				</TD>\
				<TD>{{number_sdocs}}</TD>\
			</TR><TR>\
				<TD>\
					<A HREF='#' CLASS='COLLECTION_RDOC_LIST slide-vertically-link' TARGET='rrs-collection-rdoc-list-{{id}}' ID='{{id}}' ROLE='{{role}}'>\
					{{l_rdocs}}</A>:\
				</TD>\
				<TD>{{number_rdocs}}</TD>\
			</TR>\
			{{#canadmin}}\
				<TR>\
					<TD>\
						<A HREF='#' CLASS='COLLECTION_TASK_LIST slide-vertically-link' TARGET='rrs-collection-task-list-{{id}}' ID='{{id}}' ROLE='{{role}}'>\
						{{l_tasks}}</A>:\
					</TD>\
					<TD>{{number_tasks}}</TD>\
				</TR>\
			{{/canadmin}}\
		</TABLE>\
		<A HREF='#' CLASS='main-button'><SPAN>{{stats}}</SPAN></A>\
		{{#canadmin}}\
			<A HREF='#' CLASS='SDOC_CREATE main-button' ID='{{id}}' TARGET='rrs-collection-sdoc-create-{{id}}' \
			TITLE='{{create_new_sdoc}}' ROLE='{{role}}'>\
				<SPAN>{{create_new_sdoc}}</SPAN>\
			</A>\
			<A HREF='#' CLASS='SDOC_IMPORT main-button' ID='{{id}}' TARGET='rrs-collection-sdoc-import-{{id}}' \
			TITLE='{{l_sdoc_import}}' ROLE='{{role}}'>\
				<SPAN>{{l_sdoc_import}}</SPAN>\
			</A>\
			<A HREF='#' CLASS='SDOC_EXPORT main-button' ID='{{id}}' TARGET='rrs-collection-sdoc-export-{{id}}' \
			TITLE='{{l_sdoc_export}}' ROLE='{{role}}'>\
				<SPAN>{{l_sdoc_export}}</SPAN>\
			</A>\
			<A HREF='#' CLASS='SDOC_DELETE main-button' ID='{{id}}' TITLE='{{delete}}' ROLE='{{role}}'>\
				<SPAN>{{delete}}...</SPAN>\
			</A>\
		{{/canadmin}}\
	</DIV>\
</DIV>";
	
		return Mustache.to_html(template, data);
	},
	
	/** fill source doc info in page */
	generateCollectionSdocListDIV = function(response, su, role, options) {

		var context = "sdoc"
		var res = response['result']
		var perms = response['perms']
		var col_id = response['col_id']
		var canadmin = (su || perms['uoc_can_admin'])
		var editinplace = ( canadmin ? "editinplace" : "")
	
		// newdiv
		// keep rrs-collection prefix so that collection sub-menu pops up
	
		var newdiv = $("<DIV ID='rrs-collection-sdoc-list-"+col_id+"' CLASS='main-slidable-div' \
	 	TITLE='"+i18n[context+'_list'][lang]+"' STYLE='overflow:auto;'></DIV>")
	
		// Set pageable area for paging reposition
		var t = "<DIV CLASS='rrs-pageable'>" 
	
		// pager
		t += createPagerNavigation({
			"context":context,
			"contexts":"sdocs", 
			"response":response,
			"role":role, 
			"allowedSearchableFields":{
				"sdoc_id":i18n['id'][lang],
	  			"sdoc_original_id":i18n['originalid'][lang],
	  			"sdoc_webstore":i18n['webstore'][lang], 
	  			"sdoc_lang":i18n['lang'][lang], 
	  			"sdoc_doc":i18n['rdoc'][lang],
	  			"sdoc_comment":i18n['comment'][lang], 
	  			"sdoc_proc":i18n['processable'][lang]
			},
			'render':"Rembrandt.Collection.generateCollectionSdocListDIV"
		})
	
		// buttons
		t += "<DIV CLASS='rrs-buttonrow'>"
		if (canadmin) {
			t += "<A HREF='#' CLASS='"+context.toUpperCase()+"_CREATE main-button' ID='"+col_id+"' "
			t += "ROLE='"+role+"'><SPAN>"
			t += i18n['create_new_'+context][lang]+"</SPAN></A>"
		}
		t += "</DIV>"

	// table
		t += "<DIV>"
		t += "<TABLE ID='rrs-collection-sdoc-list-table' CLASS='tablesorter' >"
		t += "<THEAD><TR><TD><INPUT TYPE='CHECKBOX' CLASS='main-checkbox'></TD>"
		t += "<TH>"+i18n['id'][lang]+"</TH>";
		t += "<TH>"+i18n['originalid'][lang]+"</TH>"
		t += "<TH>"+i18n['webstore'][lang]+"</TH>";
		t += "<TH>"+i18n['lang'][lang]+"</TH>";
		t += "<TH>"+i18n['rdoc'][lang]+"</TH>";
		t += "<TH>"+i18n['comment'][lang]+"</TH>";
		t += "<TH>"+i18n['processable'][lang]+"</TH>";
		if (canadmin) {t += "<TD></TD>";}
		t += "</TR></THEAD><TBODY>"
	
		for(i in res) {
		
			var id = res[i]['sdoc_id']
			
			t += "<TR><TD><INPUT TYPE='CHECKBOX' CLASS='sec-checkbox'></TD>"
			t += "<TH><A CLASS='"+context.toUpperCase()+"_SHOW' ID='"+id+"' "
			t += "TARGET='rrs-"+context+"-show-"+id+"' TITLE='"+res[i]['sdoc_original_id']
			t += "' HREF='#' ROLE='"+role+"'>"+id+"</A></TH>"
		
			t += "<TD><DIV CONTEXT='"+context+"'  COL='sdoc_original_id' ID='"+id+"' "
			t += "CLASS='"+editinplace+" textfield'>"+res[i]['sdoc_original_id']+"</DIV></TD>"	
			t += "<TD><DIV CONTEXT='"+context+"'  COL='sdoc_webstore' ID='"+id+"' "
			t += "CLASS='"+editinplace+" textfield '>"+res[i]['sdoc_webstore']+"</DIV></TD>"	
			t += "<TD><DIV CONTEXT='"+context+"'  COL='sdoc_lang' ID='"+id+"' "
			t += "CLASS='"+editinplace+" selectfield lang'>"+res[i]['sdoc_lang']+"</DIV></TD>"	
			t += "<TD><DIV CONTEXT='"+context+"'  COL='sdoc_doc' ID='"+id+"' "
			t += "CLASS='"+editinplace+" textfield'>"+res[i]['sdoc_doc']+"</DIV></TD>"	
			t += "<TD><DIV CONTEXT='"+context+"'  COL='sdoc_comment' ID='"+id+"' "
			t += "CLASS='"+editinplace+" textarea'>"+(res[i]['sdoc_comment'] != null ? res[i]['sdoc_comment'] : "")+"</DIV></TD>"
			t += "<TD><DIV CONTEXT=''"+context+"'  COL='sdoc_proc' ID='"+id+"' "
			t += "CLASS='"+editinplace+" selectfield proc'>"+res[i]['sdoc_proc']+"</DIV></TD>"		
			if (canadmin) {
				t += "<TD><DIV><A HREF='#' ID='"+id+"' CLASS='"+context.toUpperCase()+"_DELETE main-button' "
				t += "ROLE='"+role+"' TITLE='"+res[i]['sdoc_original_id']+"'>"
				t += i18n['delete'][lang]+"</A></DIV></TD>";
			}
			t += "</TR>"
			
		}
		t += "<TFOOT><TR><TD><INPUT TYPE='CHECKBOX' CLASS='main-checkbox'></TD>"
		t += "<TD><DIV CONTEXT='"+context+"'  COL='sdoc_id' "
		t += "CLASS='"+editinplace+" textfield group'></DIV></TD>"
		t += "<TD><DIV CONTEXT='"+context+"'  COL='sdoc_original_id' "
		t += "CLASS='"+editinplace+" textfield group'></DIV></TD>"
		t += "<TD><DIV CONTEXT='"+context+"'  COL='sdoc_webstore' "
		t += "CLASS='"+editinplace+" textfield group'></DIV></TD>"
		t += "<TD><DIV CONTEXT='"+context+"'  COL='sdoc_lang' "
		t += "CLASS='"+editinplace+" selectfield lang group'></DIV></TD>"
		t += "<TD><DIV CONTEXT='"+context+"'  COL='sdoc_doc' "
		t += "CLASS='"+editinplace+" textfield group'></DIV></TD>"
		t += "<TD><DIV CONTEXT='"+context+"'  COL='sdoc_comment' "
		t += "CLASS='"+editinplace+" textarea group'></DIV></TD>"
		t += "<TD><DIV CONTEXT='"+context+"'  COL='sdoc_proc' "
		t += "CLASS='"+editinplace+" selectfield group proc'></DIV></TD>"
		if (canadmin) {t += "<TD></TD>";}
		t += "</TR></TFOOT>"	
		t += "</TABLE>"
		t += "</DIV></DIV>"
	
		newdiv.append(t)
		return newdiv
	},

	/** fill tagged doc info in page */
	generateCollectionRdocListDIV = function(response, su, role, options) {
		var context = "rdoc"
		var res = response['result']
		var perms = response['perms']
		var col_id = response['col_id']
		var canadmin = (su || perms['uoc_can_admin'])
		var editinplace = ( canadmin ? "editinplace" : "")
	
	// newdiv
	// keep rrs-collection prefix so that collection sub-menu pops up
	
	var newdiv = $("<DIV ID='rrs-collection-rdoc-list-"+col_id+"' CLASS='main-slidable-div' \
 TITLE='"+i18n[context+'_list'][lang]+"' STYLE='overflow:auto;'></DIV>")
	
	// Set pageable area for paging reposition
	var t = "<DIV CLASS='rrs-pageable'>" 
	
	// pager
		t += createPagerNavigation({
		"context":context,
		"contexts":"rdocs", 
		"response":response,
		"role":role, 
		"allowedSearchableFields":{
			"doc_id":i18n['id'][lang],
	  		"doc_original_id":i18n['originalid'][lang],
	  		"doc_webstore":i18n['webstore'][lang], 
	  		"doc_version":i18n['version'][lang], 
	  		"doc_lang":i18n['lang'][lang], 
	  		"doc_date_created":i18n['date_created'][lang], 
	  		"doc_date_tagged":i18n['date_tagged'][lang], 
	  		"doc_proc":i18n['processable'][lang],
	  		"doc_sync":i18n['syncable'][lang]
		},
		'render':"Rembrandt.Collection.generateCollectionRdocListDIV"
		})
	
			
	// buttons
	t += "<DIV CLASS='rrs-buttonrow'>"
	t += "</DIV>"

	// table
	t += "<DIV>"
	t += "<TABLE ID='rrs-collection-rdoc-list-table' CLASS='tablesorter' >"
	t += "<THEAD><TR><TD><INPUT TYPE='CHECKBOX' CLASS='main-checkbox'></TD>"
	t += "<TH>"+i18n['id'][lang]+"</TH>";
	t += "<TH>"+i18n['originalid'][lang]+"</TH>"
	t += "<TH>"+i18n['webstore'][lang]+"</TH>";
	t += "<TH>"+i18n['version'][lang]+"</TH>";
	t += "<TH>"+i18n['lang'][lang]+"</TH>";
	t += "<TH>"+i18n['date_created'][lang]+"</TH>";	
	t += "<TH>"+i18n['date_tagged'][lang]+"</TH>";
	t += "<TH>"+i18n['processable'][lang]+"</TH>";
	t += "<TH>"+i18n['syncable'][lang]+"</TH>";
	if (canadmin) {t += "<TD></TD>";}
	t += "</TR></THEAD><TBODY>"
	
		for(i in res) {
		
		var id = res[i]['doc_id']
		
		t += "<TR><TD><INPUT TYPE='CHECKBOX' CLASS='sec-checkbox'></TD>"
		t += "<TH><A CLASS='"+context.toUpperCase()+"_SHOW' ID='"+id+"' "
		t += "TARGET='rrs-"+context+"-show-"+id+"' TITLE='"+res[i]['doc_original_id']
		t += "' HREF='#' ROLE='"+role+"'>"+id+"</A></TH>"
		
		t += "<TD><DIV CONTEXT='"+context+"' COL='doc_original_id' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['doc_original_id']+"</DIV></TD>"	
		t += "<TD><DIV CONTEXT='"+context+"' COL='doc_webstore' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['doc_webstore']+"</DIV></TD>"
		t += "<TD><DIV CONTEXT='"+context+"' COL='doc_version' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['doc_version']+"</DIV></TD>"		
		t += "<TD><DIV CONTEXT='"+context+"' COL='doc_lang' ID='"+id+"' "
		t += "CLASS='"+editinplace+" selectfield lang'>"+res[i]['doc_lang']+"</DIV></TD>"	
		t += "<TD><DIV CONTEXT='"+context+"' COL='doc_date_created' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['doc_date_created']+"</DIV></TD>"	
		t += "<TD><DIV CONTEXT='"+context+"' COL='doc_date_tagged' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['doc_date_tagged']+"</DIV></TD>"	
		t += "<TD><DIV CONTEXT='"+context+"' COL='doc_proc' ID='"+id+"' "
		t += "CLASS='"+editinplace+" selectfield proc'>"+res[i]['doc_proc']+"</DIV></TD>"		
		t += "<TD><DIV CONTEXT='"+context+"' COL='doc_sync' ID='"+id+"' "
		t += "CLASS='"+editinplace+" selectfield sync'>"+res[i]['doc_sync']+"</DIV></TD>"		
		if (canadmin) {
			t += "<TD><DIV><A HREF='#' ID='"+id+"' CLASS='"+context.toUpperCase()+"_DELETE main-button' "
			t += "ROLE='"+role+"' TITLE='"+res[i]['doc_original_id']+"'>"
			t += i18n['delete'][lang]+"</A></DIV></TD>";
		}
		t += "</TR>"
			
		}
	t += "<TFOOT><TR><TD><INPUT TYPE='CHECKBOX' CLASS='main-checkbox'></TD>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='doc_id' "
	t += "CLASS='"+editinplace+" textfield group'></DIV></TD>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='doc_original_id' "
	t += "CLASS='"+editinplace+" textfield group'></DIV></TD>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='doc_webstore' "
	t += "CLASS='"+editinplace+" textfield group'></DIV></TD>"	
	t += "<TD><DIV CONTEXT='"+context+"' COL='doc_version' "
	t += "CLASS='"+editinplace+" textfield group'></DIV></TD>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='doc_lang' "
	t += "CLASS='"+editinplace+" selectfield lang group'></DIV></TD>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='doc_date_created' "
	t += "CLASS='"+editinplace+" textfield group'></DIV></TD>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='doc_date_tagged' "
	t += "CLASS='"+editinplace+" textfield group'></DIV></TD>"	
	t += "<TD><DIV CONTEXT='"+context+"' COL='doc_proc' "
	t += "CLASS='"+editinplace+" selectfield group proc'></DIV></TD>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='doc_sync' "
	t += "CLASS='"+editinplace+" selectfield group sync'></DIV></TD>"
	if (canadmin) {t += "<TD></TD>";}
	t += "</TR></TFOOT>"	
	t += "</TABLE>"
	t += "</DIV></DIV>"
	
	newdiv.append(t)
	return newdiv

	}, 

	/**************/
	/*** modal ****/
	/**************/

	modalCollectionCreate = function (button) {
	
	var api_key= Rembrandt.Util.getApiKey()
	var role = Rembrandt.Util.getRole(button)
	var servlet_url = Rembrandt.Util.getServletEngineFromRole(role, 'collection')
	
		$.modal("<div id='modalCreateCollection' class='rembrandt-modal' style='width:400px'>"+
		"<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
		"<div style='text-align:center; padding:10px;'>"+i18n['create_new_collection'][lang]+"</div>"+
		"<div style='text-align:left; padding:3px;'>"+
		"<form><table style='border:0px;border-spacing:3px;'>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["name"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='col_name'></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["lang"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='5' ID='col_lang'></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["comment"][lang]+"</TD>"+
		"<TD ALIGN=LEFT><TEXTAREA style='height:100px; width:250px;' ID='col_comment'></TEXTAREA></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["permissions"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT>"+
		"<TABLE BORDER=0><THEAD><TR><TH></TH>"+
		"<TH>"+i18n['read'][lang]+"</TH>"+
		"<TH>"+i18n['write'][lang]+"</TH>"+
		"<TH>"+i18n['admin'][lang]+"</TH></TR>"+
		"<TR><TD>"+i18n['group'][lang]+"</TD>"+
		"<TD><SELECT SIZE=1 ID='groupread'>"+
		"<OPTION VALUE='-' SELECTED>"+i18n["no"][lang]+"</OPTION>"+
		"<OPTION VALUE='r'>"+i18n["yes"][lang]+"</OPTION>"+
		"</SELECT></TD>"+
		"<TD><SELECT SIZE=1 ID='groupwrite'>"+
		"<OPTION VALUE='-' SELECTED>"+i18n["no"][lang]+"</OPTION>"+
		"<OPTION VALUE='w'>"+i18n["yes"][lang]+"</OPTION>"+
		"</SELECT></TD>"+
		"<TD><SELECT SIZE=1 ID='groupadmin'>"+
		"<OPTION VALUE='-' SELECTED>"+i18n["no"][lang]+"</OPTION>"+
		"<OPTION VALUE='a'>"+i18n["yes"][lang]+"</OPTION>"+
		"</SELECT></TD></TR>"+
		"<TR><TD>"+i18n['other'][lang]+"</TD>"+
		"<TD><SELECT SIZE=1 ID='otherread'>"+
		"<OPTION VALUE='-' SELECTED>"+i18n["no"][lang]+"</OPTION>"+
		"<OPTION VALUE='r'>"+i18n["yes"][lang]+"</OPTION>"+
		"</SELECT></TD>"+
		"<TD><SELECT SIZE=1 ID='otherwrite'>"+
		"<OPTION VALUE='-' SELECTED>"+i18n["no"][lang]+"</OPTION>"+
		"<OPTION VALUE='w'>"+i18n["yes"][lang]+"</OPTION>"+
		"</SELECT></TD>"+
		"<TD><SELECT SIZE=1 ID='otheradmin'>"+
		"<OPTION VALUE='-' SELECTED>"+i18n["no"][lang]+"</OPTION>"+
		"<OPTION VALUE='a'>"+i18n["yes"][lang]+"</OPTION>"+
		"</SELECT></TD></TR>"+
		"</TABLE></TD></TR></TABLE><BR>"+
		"<div id='rrs-waiting-div' style='text-align:center;margin-bottom:5px'>"+
		"<div class='rrs-waiting-div-message'></div></div> "+ 
		"<div id='buttons' style='text-align:center;'><input type='button' id='YesButton' value='"+
		i18n["yes"][lang]+", "+i18n["create"][lang]+"'><input type='button' id='NoButton' value='"+
		i18n["no"][lang]+", "+i18n["cancel"][lang]+"'></div></form>"+
      "</div></div>	", {
	
		onShow: function modalShow(dialog) {
			// fill out table
		dialog.data.find("#YesButton").click(function(ev) {
			
			var col_name = dialog.data.find("#col_name").val()
			var col_comment = dialog.data.find("#col_comment").val()
			var col_lang = dialog.data.find("#col_lang").val()
			var col_permission = "rwa" + 
				(dialog.data.find("#groupread option:selected").val())+
				(dialog.data.find("#groupwrite option:selected").val())+
				(dialog.data.find("#groupadmin option:selected").val())+
				(dialog.data.find("#otherread option:selected").val())+
				(dialog.data.find("#otherwrite option:selected").val())+
				(dialog.data.find("#otheradmin option:selected").val())
			
			var goodToGo = true
	
			if(col_name.length == 0) {
				errorMessageWaitingDiv(lang, i18n['formmismatch'][lang])
				dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
				goodToGo = false	
			}  
			
			if (goodToGo) {
				jQuery.ajax({ type:"POST", dataType:'json', url:servlet_url,
					contentType:"application/x-www-form-urlencoded",
					data: "do=create&col_name="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(col_name))+
					      "&col_comment="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(col_comment))+
						   "&col_lang="+col_lang+"&col_permission="+col_permission+						
							"&lg="+lang+"&api_key="+api_key, 
					beforeSubmit: waitMessageBeforeSubmit(lang),
					success: function(response) {
						if (response['status'] == -1) {
							errorMessageWaitingDiv(lang, response['message'])
							dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
							dialog.data.find("#buttons").show()	
						} else if (response['status'] == 0)  {
							showCustomMessageWaitingDiv(i18n['collection_created'][lang])
							dialog.data.find("#YesButton").hide()
							dialog.data.find("#NoButton").attr("value",i18n["OK"][lang])
						}
					},
					error:function(response) {
						errorMessageWaitingDiv(lang, response['message'])			
					}
				})
			}
		});
		dialog.data.find("#NoButton").click(function(ev) {
			ev.preventDefault();
			$.modal.close();
		});
		},
		overlayCss:{backgroundColor: '#888', cursor: 'wait'}
		});
	}, 

	// creates a modal window to switch collection. It will ask the server for user permissions 
	// in those collections.  

	// it has a spaecial confirmation, keep it away from generic delete model template
	modalCollectionDelete = function (button) {
	
	var api_key= Rembrandt.Util.getApiKey()
	var ci = button.attr("ID")
	var role = Rembrandt.Util.getRole(button)
	var servlet_collection_url = Rembrandt.Util.getServletEngineFromRole(role, 'collection')
	var servlet_user_url = Rembrandt.Util.getServletEngineFromRole(role, 'user')
	
		$.modal("<div id='modalDeleteCollection' class='rembrandt-modal' style='width:400px'>"+
		"<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
		"<div style='text-align:center; padding:10px;'>"+i18n['delete_collection'][lang]+"</div>"+
		"<div style='text-align:center; padding:3px;'>"+
		"<form><table style='border:0px;border-spacing:3px;'>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["password"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='PASSWORD' SIZE='25' ID='password'></TD></TR>"+
		"</TABLE><BR>"+
		"<div id='rrs-waiting-div' style='text-align:center;margin-bottom:5px'>"+
		"<div class='rrs-waiting-div-message'></div></div> "+ 
		"<div id='button1' style='text-align:center;'><input type='button' id='AuthButton' "+
		" value='"+i18n["authenticate"][lang]+"'></DIV></FORM>"+
		"<DIV ID='info'>Waiting for password...</DIV>"+
		"<FORM><div id='buttons' style='text-align:center;'>"+
		"<input type='button' id='YesButton' value='"+i18n["yes"][lang]+", "+i18n["create"][lang]+"' DISABLED>"+
		"<input type='button' id='NoButton' value='"+i18n["no"][lang]+", "+i18n["cancel"][lang]+"'>"+
		"</div></form></div></div>	", {
			
		onShow: function modalShow(dialog) {
			// fill out table
		   
			dialog.data.find("#AuthButton").click(function(ev) {
			
				var password = dialog.data.find("#password").val()
			
				jQuery.ajax( {type:"POST", url:servlet_user_url,
				contentType:"application/x-www-form-urlencoded",
				data:"do=auth&lg="+lang+"&p="+Rembrandt.Util.urlEncode(hex_md5(password))+
				"&api_key="+api_key,
				beforeSubmit: waitMessageBeforeSubmit(lang),
					success: function(response) {
						if (response['status'] == -1) {
							errorMessageWaitingDiv(lang, response['message'])			
						}	else if (response['status'] == 0) {
							sendCustomMessageWaitingDiv(response['message'])			
							dialog.data.find("#YesButton").attr('disabled','false')
							dialog.data.find("#NoButton").attr('disabled','false')
						}
					}, 
					error:function(response) {
						errorMessageWaitingDiv(lang, response['message'])			
					}
				})
			})
			
			dialog.data.find("#YesButton").click(function(ev) {
				ev.preventDefault();
							
				jQuery.ajax({ type:"POST", dataType:'json', url:servlet_user_url,
				contentType:"application/x-www-form-urlencoded",
				data: "do=delete&ci="+ci+"&lg="+lang+"&api_key="+api_key, 
				beforeSubmit: waitMessageBeforeSubmit(lang),
				success: function(response) {
					if (response['status'] == -1) {
						errorMessageWaitingDiv(lang, response['message'])			
					} else {
						sendCustomMessageWaitingDiv(response['message'])			
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
		})
	};
	
	return {
		"createCollection": createCollection,
		"deleteCollection": deleteCollection,
		"listCollections" : listCollections,
		"refreshStats" : refreshStats,
		"listRembrandtedDocs": listRembrandtedDocs,
		"listSourceDocs": listSourceDocs, 
		"listTasks": listTasks,	
		"showCollection": showCollection,
		"generateCollectionListDIV":  generateCollectionListDIV,
		"generateCollectionShowDIV" : generateCollectionShowDIV,
		"generateCollectionSdocListDIV" : generateCollectionSdocListDIV,
		"generateCollectionRdocListDIV" : generateCollectionRdocListDIV, 
		"modalCollectionCreate" : modalCollectionCreate,
		"modalCollectionDelete" : modalCollectionDelete
	};
}(jQuery));