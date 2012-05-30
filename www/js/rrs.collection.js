var Rembrandt = Rembrandt || {};

Rembrandt.Collection = (function ($) {
	"use strict";
	$(function () {
		$('A.COLLECTION_CREATE').live("click", function (ev) {
			ev.preventDefault();
			_createCollection(this);
		});
		
		$('A.COLLECTION_DELETE').live("click", function (ev) {
			ev.preventDefault();
			_deleteCollection(this);
		});
		
		$('A.COLLECTION_LIST').live("click", function(ev) {
			ev.preventDefault();
			_listCollections(this);
		});
		
		$('A.COLLECTION_SWITCH').live("click", function(ev) {
			ev.preventDefault();
			_switchCollection(this);
		});
		
		$('A.COLLECTION_REFRESH_STATS').live("click", function(ev) {
			ev.preventDefault();
			_refreshStats(this);
		});
		
		$('A.COLLECTION_DOC_LIST').live("click", function(ev) {
			ev.preventDefault();
			_listDocs(this);
		});
		
		$('A.COLLECTION_TASK_LIST').live("click", function(ev) {
			ev.preventDefault();
			_listTasks(this);
		});
		
		$('A.COLLECTION_SHOW').live("click", function(ev) {
			ev.preventDefault();
			_showCollection(this);
		});
	});
	
	var _createCollection = function(context) {
		_modalCollectionCreate(context);
		_refreshBody()
	}, 
	
	_switchCollection = function(context) {
		_modalCollectionSwitch(context);
	}, 
	
	_deleteCollection = function(context) {
		_modalCollectionDelete(context);
		_refreshBody()
	},
	
	_refreshContent = function () {
		// let's refresh the content. How? triggering the admin-pager link if the 
		// visible div is for a collection list or collection main page
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-homepage-collection') {
			Saskia.display()
		}
		if (divshown.attr('id') == 'rrs-collection-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
	}, 
	
	_listCollections = function (context) {

		var a_clicked = context,
			api_key = Rembrandt.Util.getApiKey(),
			role = $(a_clicked).attr('ROLE'),
			title = ($(a_clicked).attr("TITLE") ? $(a_clicked).attr("TITLE") : $(a_clicked).text()),
			target = $(a_clicked).attr("TARGET");
			
		showSlidableDIV({
			"title"				: title,
			"target"			: target,
			"role"				: role,
			"slide"				: getSlideOrientationFromLink(a_clicked),
			"ajax"				: true,
			"restlet_url"		: Rembrandt.Util.getServletEngineFromRole(role, "collection")+"/list",
			"data"				: {"l":10, "o":0, "lg":lang, "api_key":api_key},
			"divRender"			: Rembrandt.Collection.generateCollectionListDIV, 
			"divRenderOptions"	: {},
			"sidemenu"			: null, 
			"sidemenuoptions"	: {}
		})
	},

	_refreshStats = function (context) {

		var a_clicked = context,
			col_id = parseInt($(a_clicked).attr("id")),
			title = a_clicked.attr("TITLE") || a_clicked.text(),
			api_key = Rembrandt.Util.getApiKey(),
			role = $(a_clicked).attr('ROLE'),
			servlet_url = Rembrandt.Util.getServletEngineFromRole(role, "collection");

		a_clicked.attr('disabled', true)
		a_clicked.toggleClass("main-button", "main-button-disabled")

		jQuery.ajax({
			type		: "POST", 
			url			: Rembrandt.urls.restlet_saskia_collection_url+"/refreshstats", 
			dataType	: "json",
			data		: JSON.stringify({
				"lg":lang, 
				"id":col_id, 
				"api_key=":Rembrandt.Util.getApiKey()
			}),
			beforeSubmit: Rembrandt.WaitingDiv.show({
				target: button.find("SPAN"),
				message:i18n['refreshing_collection'][lang]
			}), 
			success		: function (response) {

				if (response["status"] == -1) {

					Rembrandt.Waiting.error(response)
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
				Rembrandt.Waiting.error(response)
				button.toggleClass('disabled',false)
					.toggleClass("main-button-disabled","main-button")
			}
		})
	},
	
	_listDocs = function (context) {

		var a_clicked = context,
		    col_id = parseInt($(a_clicked).attr("id")),
		    api_key = Rembrandt.Util.getApiKey(),
		    title = $(a_clicked).attr("title") || $(a_clicked).text(),
			role = $(a_clicked).attr('ROLE'),
			target = $(a_clicked).attr("TARGET");

		showSlidableDIV({
			"title" 		: title,
			"target" 		: target,
			"role" 			: role,
			"slide"			: getSlideOrientationFromLink(a_clicked),
			"ajax"			: true,
			"restlet_url"	: Rembrandt.Util.getServletEngineFromRole(role, "doc")+"/list",
			"data"			: {"l":10, "o":0, "ci":col_id, "lg":lang, "api_key":api_key},
			"divRender" 	: Rembrandt.Collection.generateCollectionDocListDIV, 
			"divRenderOptions":{},
			"sidemenu"		: "collection", 
			// only update col_id, no col_name -> it will change side menu header. Leave that to COLLECTION_SHOW
			"sidemenuoptions": {"id":col_id}
		})	
	},
	
	_listTasks = function (context) {

		var a_clicked = context,
			col_id = parseInt($(a_clicked).attr("id")),
			api_key = Rembrandt.Util.getApiKey(),
			title = $(a_clicked).attr("title") || $(a_clicked).text(),
			role = $(a_clicked).attr('ROLE'),
			target = $(a_clicked).attr("TARGET");

		showSlidableDIV({
			"title" 		: title,
			"target" 		: target,
			"role" 			: role,
			"slide"			: getSlideOrientationFromLink(a_clicked),
			"ajax"			: true,
			"restlet_url" 	: Rembrandt.Util.getServletEngineFromRole(role, "task")+"/list",
			"data"			: {"l":10, "o":0, "ci":col_id, "lg":lang, "api_key":api_key},
			"divRender" :Rembrandt.Collection.generateCollectionTaskListDIV, 
			"divRenderOptions":{},
			"sidemenu" 		: "collection", 
			"sidemenuoptions": {"id":col_id}
		})	
	},

	_showCollection = function (context) {
		var a_clicked = $(context),
			col_id = parseInt(a_clicked.attr("ID")),
			api_key = Rembrandt.Util.getApiKey(),
			title = a_clicked.attr("title") || a_clicked.text(),
			role = $(a_clicked).attr('ROLE'),
			target = $(a_clicked).attr("TARGET");
			
		showSlidableDIV({
			"title"			: title,
			"target"		: target,
			"role"			: role,
			"slide"			: getSlideOrientationFromLink(a_clicked),
			"ajax"			: true,
			"restlet_url"	: Rembrandt.Util.getServletEngineFromRole(role, "collection")+"/show",
			"data"			: {"id":col_id, "lg":lang, "api_key":api_key},
			"divRender"		: Rembrandt.Collection.generateCollectionShowDIV, 
			"divRenderOptions":{},
			"sidemenu"		: "collection", 
			"sidemenuoptions":{"id":col_id}
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
			</TBODY>\
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
			"number_docs"	: col['number_docs'],
			"number_tagged_docs"	: col['number_tagged_docs'],
			"number_tasks"	: 0,
			"s_colname"		: Rembrandt.Util.shortenTitle(col['col_name']),
			"context-show"	: i18n[context+'-show'][lang],
			"l_id"			: i18n['id'][lang],
			"l_name"		: i18n['name'][lang],
			"l_lang"		: i18n['lang'][lang],
			"l_comment"		: i18n['comment'][lang],
			"l_permissions" : i18n['permissions'][lang],
			"l_docs"		: i18n['docs'][lang],
			"l_tasks"		: i18n['tasks'][lang],
			"create_new_doc": i18n['create_new_doc'][lang],
			"create_new_context" : i18n['create_new_'+context][lang],
			"l_doc_import"	: i18n['doc_import'][lang],
			"l_doc_export"	: i18n['doc_export'][lang],
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
					<A HREF='#' CLASS='COLLECTION_DOC_LIST slide-vertically-link' TARGET='rrs-collection-doc-list-{{id}}' ID='{{id}}' ROLE='{{role}}'>\
					{{l_docs}}</A>:\
				</TD>\
				<TD>{{number_docs}}</TD>\
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
			<A HREF='#' CLASS='DOC_CREATE main-button' ID='{{id}}' TARGET='rrs-collection-doc-create-{{id}}' \
			TITLE='{{create_new_doc}}' ROLE='{{role}}'>\
				<SPAN>{{create_new_doc}}</SPAN>\
			</A>\
			<A HREF='#' CLASS='DOC_IMPORT main-button' ID='{{id}}' TARGET='rrs-collection-doc-import-{{id}}' \
			TITLE='{{l_doc_import}}' ROLE='{{role}}'>\
				<SPAN>{{l_doc_import}}</SPAN>\
			</A>\
			<A HREF='#' CLASS='DOC_EXPORT main-button' ID='{{id}}' TARGET='rrs-collection-doc-export-{{id}}' \
			TITLE='{{l_doc_export}}' ROLE='{{role}}'>\
				<SPAN>{{l_doc_export}}</SPAN>\
			</A>\
			<A HREF='#' CLASS='DOC_DELETE main-button' ID='{{id}}' TITLE='{{delete}}' ROLE='{{role}}'>\
				<SPAN>{{delete}}...</SPAN>\
			</A>\
		{{/canadmin}}\
	</DIV>\
</DIV>";
	
		return Mustache.to_html(template, data);
	},

	/** fill tagged doc info in page */
	generateCollectionDocListDIV = function(response, su, role, options) {
		
		var context = "doc",
			res = response['result'],
			perms = response['perms'],
			col_id = response['col_id'],
			canadmin = (su || perms['uoc_can_admin']),
			editinplace = ( canadmin ? "editinplace" : ""),
			navigation = createPagerNavigation({
				"context":context,
				"contexts":"docs", 
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
				'render':"Rembrandt.Collection.generateCollectionDocListDIV"
			}),
			res_html = [];

		for(i in response["result"]) {
			
			var doc = response["result"][i]
			var res_data = {
				"id"				: doc['doc_id'],
				"contextU" 			: context.toUpperCase(),
				"context"			: context,
				"doc_original_id"	: doc['doc_original_id'],
				"doc_webstore"		: doc['doc_webstore'],
				"doc_version"		: doc['doc_version'],
				"doc_date_created"	: doc['doc_date_created'],
				"doc_date_tagged"	: doc['doc_date_tagged'],
				"doc_lang"			: doc['doc_lang'],
				"doc_proc"			: doc['doc_proc'],
				"doc_sync"			: doc['doc_sync'],
				"role"				: role,
				"editinplace"		: editinplace,
				"canadmin"			: {
					"delete"		: i18n['delete'][lang]
				}
			}

			var res_template = "\
				<TR>\
					<TD>\
						<INPUT TYPE='CHECKBOX' CLASS='sec-checkbox'>\
					</TD>\
					<TH>\
						<A CLASS='{{contextU}}_SHOW' ID='{{id}}' TARGET='rrs-{{context}}-show-{{id}}' \
						TITLE='{{doc_original_id}}' HREF='#' ROLE='{{role}}'>{{id}}</A>\
					</TH>\
					<TD>\
						<DIV CONTEXT='{{context}}' COL='doc_original_id' ID='{{id}}' CLASS='{{editinplace}} textfield'>{{doc_original_id}}</DIV>\
					</TD>\
					<TD>\
						<DIV CONTEXT='{{context}}' COL='doc_webstore' ID='{{id}}' CLASS='{{editinplace}} textfield'>{{doc_webstore}}</DIV>\
					</TD>\
					<TD>\
						<DIV CONTEXT='{{context}}' COL='doc_version' ID='{{id}}' CLASS='{{editinplace}} textfield'>{{doc_version}}</DIV>\
					</TD>\
					<TD>\
						<DIV CONTEXT='{{context}}' COL='doc_lang' ID='{{id}}' CLASS='{{editinplace}} selectfield lang'>{{doc_lang}}</DIV>\
					</TD>\
					<TD>\
						<DIV CONTEXT='{{context}}' COL='doc_date_created' ID='{{id}}' CLASS='{{editinplace}} textfield'>{{doc_date_created}}</DIV>\
					</TD>\
					<TD>\
						<DIV CONTEXT='{{context}}' COL='doc_date_tagged' ID='{{id}}' CLASS='{{editinplace}} textfield'>{{doc_date_tagged}}</DIV>\
					</TD>\
					<TD>\
						<DIV CONTEXT='{{context}}' COL='doc_proc' ID='{{id}}' CLASS='{{editinplace}} selectfield proc'>{{doc_proc}}</DIV>\
					</TD>\
					<TD>\
						<DIV CONTEXT='{{context}}' COL='doc_sync' ID='{{id}}' CLASS='{{editinplace}} selectfield sync'>{{doc_sync}}</DIV>\
					</TD>\
					{{#canadmin}}\
					<TD>\
						<DIV>\
							<A HREF='#' ID='{{id}}' CLASS='{{contextU}}_DELETE main-button' ROLE={{role}} TITLE='{{doc_original_id}}'>{{delete}}</A>\
						</DIV>\
					</TD>\
					{{/canadmin}}\
				</TR>";
		
			res_html.push({"html":Mustache.to_html(res_template, res_data)})
		}	
	
		var data = {
			"context"			: context,
			"col_id"			: col_id,
			"title"				: i18n[context+'_list'][lang],
			"colname"			: response['col_name'],
			"navigation"		: navigation,
			"res_html"			: res_html,
			"l_id"				: i18n['id'][lang],
			"l_originalid"		: i18n['originalid'][lang],
			"l_webstore"		: i18n['webstore'][lang],
			"l_version"			: i18n['version'][lang],
			"l_lang"			: i18n['lang'][lang],
			"l_date_created"	: i18n['date_created'][lang],
			"l_date_tagged"		: i18n['date_tagged'][lang],
			"l_processable"		: i18n['processable'][lang],
			"l_syncable"		: i18n['syncable'][lang],
			"canadmin"		:{
				"permissions": 	i18n['permissions'][lang]
			},
			"editinplace"		: editinplace
		}
	
		var template = "\
		<DIV ID='rrs-collection-doc-list-{{col_id}}' CLASS='main-slidable-div' TITLE='{{title}}' STYLE='overflow:auto;'>\
			<DIV CLASS='rrs-pageable'>{{{navigation}}}\
				<DIV CLASS='rrs-buttonrow'></DIV>\
			</DIV>\
			<DIV>\
				<TABLE ID='rrs-collection-doc-list-table' CLASS='tablesorter' >\
					<THEAD>\
						<TR>\
							<TD>\
								<INPUT TYPE='CHECKBOX' CLASS='main-checkbox'>\
							</TD>\
							<TH>{{l_id}}</TH>\
							<TH>{{l_originalid}}</TH>\
							<TH>{{l_webstore}}</TH>\
							<TH>{{l_version}}</TH>\
							<TH>{{l_lang}}</TH>\
							<TH>{{l_date_created}}</TH>\
							<TH>{{l_date_tagged}}</TH>\
							<TH>{{l_processable}}</TH>\
							<TH>{{l_syncable}}</TH>\
							{{#canadmin}}<TH></TH>{{/canadmin}}\
						</TR>\
					</THEAD>\
					<TBODY>\
						{{#res_html}}\
							{{{html}}}\
						{{/res_html}}\
					</TBODY>\
					<TFOOT>\
						<TR>\
							<TD>\
								<INPUT TYPE='CHECKBOX' CLASS='main-checkbox'>\
							</TD>\
							<TD>\
								<DIV CONTEXT='{{context}}' COL='doc_id' CLASS='{{editinplace}} textfield group'></DIV>\
							</TD>\
							<TD>\
								<DIV CONTEXT='{{context}}' COL='doc_original_id' CLASS='{{editinplace}} textfield group'></DIV>\
							</TD>\
							<TD>\
								<DIV CONTEXT='{{context}}' COL='doc_webstore' CLASS='{{editinplace}} textfield group'></DIV>\
							</TD>\
							<TD>\
								<DIV CONTEXT='{{context}}' COL='doc_version' CLASS='{{editinplace}} textfield group'></DIV>\
							</TD>\
							<TD>\
								<DIV CONTEXT='{{context}}' COL='doc_lang' CLASS='{{editinplace}} selectfield lang group'></DIV>\
							</TD>\
							<TD>\
								<DIV CONTEXT='{{context}}' COL='doc_date_created' CLASS='{{editinplace}} textfield group'></DIV>\
							</TD>\
							<TD>\
								<DIV CONTEXT='{{context}}' COL='doc_date_tagged' CLASS='{{editinplace}} textfield group'></DIV>\
							</TD>\
							<TD>\
								<DIV CONTEXT='{{context}}' COL='doc_proc' CLASS='{{editinplace}} selectfield group proc'></DIV>\
							</TD>\
							<TD>\
								<DIV CONTEXT='{{context}}' COL='doc_sync' CLASS='{{editinplace}} selectfield group sync'></DIV>\
							</TD>\
							{{#canadmin}}<TD></TD>{{/canadmin}}\
						</TR>\
					</TFOOT>\
				</TABLE>\
			</DIV>\
		</DIV>";
	
		return Mustache.to_html(template, data);
	}, 

	/**************/
	/*** modal ****/
	/**************/

	_modalCollectionCreate = function (button) {
	
		var api_key= Rembrandt.Util.getApiKey()
		var role = Rembrandt.Util.getRole(button)
		var servlet_url = Rembrandt.Util.getServletEngineFromRole(role, 'collection')
	
		var data = {
			"pressEscape"			: i18n['pressescape'][lang],
			"createNewCollection"	: i18n['create_new_collection'][lang],
			"name"					: i18n["name"][lang],
			"lang"					: i18n["lang"][lang],
			"comment" 				: i18n["comment"][lang],
			"permissions"			: i18n["permissions"][lang],
			"read"					: i18n['read'][lang], 
			"write"					: i18n['write'][lang], 
			"admin"					: i18n['admin'][lang], 
			"group"					: i18n['group'][lang], 
			"no"					: i18n["no"][lang],
			"yes"					: i18n["yes"][lang],
			"other" 				: i18n['other'][lang],
			"create"				: i18n["create"][lang],
			"cancel"				: i18n["cancel"][lang]
		}
		
		var template = "\
<div id='modalCreateCollection' class='rembrandt-modal' style='width:400px'>\
	<div class='rembrandt-modal-escape'>{{pressEscape}}</div>\
	<div style='text-align:center; padding:10px;'>{{createNewCollection}}</div>\
	<div style='text-align:left; padding:3px;'>\
		<form>\
			<table style='border:0px;border-spacing:3px;'>\
				<TR>\
					<TD ALIGN=RIGHT>{{name}}*</TD>\
					<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='col_name'></TD>\
				</TR>\
				<TR>\
					<TD ALIGN=RIGHT>{{lang}}*</TD>\
					<TD ALIGN=LEFT>\
						<INPUT TYPE='TEXT' SIZE='5' ID='col_lang'>\
					</TD>\
				</TR>\
				<TR>\
					<TD ALIGN=RIGHT>{{comment}}</TD>\
					<TD ALIGN=LEFT>\
						<TEXTAREA style='height:100px; width:250px;' ID='col_comment'></TEXTAREA>\
					</TD>\
				</TR>\
				<TR>\
					<TD ALIGN=RIGHT>{{permissions}}*</TD>\
					<TD ALIGN=LEFT>\
						<TABLE BORDER=0>\
							<THEAD>\
								<TR>\
									<TH></TH>\
									<TH>{{read}}</TH>\
									<TH>{{write}}</TH>\
									<TH>{{admin}}</TH>\
								</TR>\
								<TR>\
									<TD>{{group}}</TD>\
									<TD>\
										<SELECT SIZE=1 ID='groupread'>\
											<OPTION VALUE='-' SELECTED>{{no}}</OPTION>\
											<OPTION VALUE='r' SELECTED>{{yes}}</OPTION>\
										</SELECT>\
									</TD>\
									<TD>\
										<SELECT SIZE=1 ID='groupwrite'>\
											<OPTION VALUE='-' SELECTED>{{no}}</OPTION>\
											<OPTION VALUE='w' SELECTED>{{yes}}</OPTION>\
										</SELECT>\
									</TD>\
									<TD>\
										<SELECT SIZE=1 ID='groupadmin'>\
											<OPTION VALUE='-' SELECTED>{{no}}</OPTION>\
											<OPTION VALUE='a' SELECTED>{{yes}}</OPTION>\
										</SELECT>\
									</TD>\
								</TR>\
									<TD>{{other}}</TD>\
									<TD>\
										<SELECT SIZE=1 ID='otherread'>\
											<OPTION VALUE='-' SELECTED>{{no}}</OPTION>\
											<OPTION VALUE='r' SELECTED>{{yes}}</OPTION>\
										</SELECT>\
									</TD>\
									<TD>\
										<SELECT SIZE=1 ID='otherwrite'>\
											<OPTION VALUE='-' SELECTED>{{no}}</OPTION>\
											<OPTION VALUE='w' SELECTED>{{yes}}</OPTION>\
										</SELECT>\
									</TD>\
									<TD>\
										<SELECT SIZE=1 ID='otheradmin'>\
											<OPTION VALUE='-' SELECTED>{{no}}</OPTION>\
											<OPTION VALUE='a' SELECTED>{{yes}}</OPTION>\
										</SELECT>\
									</TD>\
								</TR>\
							</TABLE>\
						</TD>\
					</TR>\
				</TABLE>\
			<BR>\
			<div id='rrs-waiting-div' style='text-align:center;margin-bottom:5px'>\
				<div class='rrs-waiting-div-message'></div>\
			</div>\
			<div id='buttons' style='text-align:center;'>\
				<input type='button' id='YesButton' value='{{yes}}, {{create}}'>\
				<input type='button' id='NoButton' value='{{no}}, {{cancel}}'>\
			</div>\
		</form>\
	</div>\
</div>";

	$.modal(Mustache.to_html(template, data), {
	
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
				jQuery.ajax({ 
					type			: "POST", 
					dataType		:' json', 
					url				: servlet_url+"/create",
					contentType		: "application/json",
					data			: JSON.stringify({
						"col_name"		: Rembrandt.Util.encodeUtf8(col_name),
						"col_comment"	: Rembrandt.Util.encodeUtf8(col_comment),
						"col_lang"		: col_lang,
						"col_permission":col_permission,
						"lg" 			: lang,
						"api_key" 		:api_key
					}),
					beforeSubmit	: Rembrandt.Waiting.show(),
					success			: function (response) {
						if (response['status'] == -1) {
							Rembrandt.Waiting.error(response)
							dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
							dialog.data.find("#buttons").show()	
						} else if (response['status'] == 0)  {
							Rembrandt.Waiting.hide({
								message:i18n['collection_created'][lang],
								when: 3000
							})
							dialog.data.find("#YesButton").hide()
							dialog.data.find("#NoButton").attr("value",i18n["OK"][lang])
						}
					},
					error			: function(response) {
						Rembrandt.Waiting.error(response)
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

	/* first, AJAX call; then, table */
	_modalCollectionSwitch = function (button) {

		var servlet_collection_url = Rembrandt.Util.getServletEngineFromRole('saskia', 'collection')

		jQuery.ajax({ 
			type			: "POST", 
			dataType		:' json', 
			url				: servlet_collection_url+"/list-read",
			contentType		: "application/json",
			data			: JSON.stringify({
				"lg" 		: lang,
				"api_key"	: Rembrandt.Util.getApiKey()
			}),
			beforeSubmit	: Rembrandt.Waiting.show(),
			success			: function (response) {
				if (response['status'] == -1) {
					errorMessageWaitingDiv(lang, response['message'])
				} else {
					Rembrandt.Waiting.hide()
					// dados na resposta vão ser linhas na tabela
					var res_html = []
					for (var i in response['message']['result']) {

						var col = response['message']['result'][i]
						var res_data = {
							"col_id":col["col_id"],
							"col_name":col["col_name"]
						}
						var res_template = "\
<tr>\
	<td style='border:1px solid #1F3C58;'>\
		<a class='COLLECTION_SELECT' collectionid='{{col_id}}'>{{col_name}}</a>\
	</td>\
</tr>";
						res_html.push({"html":Mustache.to_html(res_template, res_data)})
					}
					
					var data = {
						"pressEscape" 		: i18n['pressescape'][lang],
						"switchCollection" 	: i18n['switchCollection'][lang],
						"no"				: i18n["no"][lang],
						"cancel"			: i18n["cancel"][lang],
						"res_html"		 	: res_html
					}

					var template  = "\
<div id='modalSwitchCollection' class='rembrandt-modal' style='width:400px'>\
	<div class='rembrandt-modal-escape'>{{pressEscape}}</div>\
	<div style='text-align:center; padding:10px;'>{{switchCollection}}</div>\
	<div style='text-align:center; padding:3px;overflow:scroll;max-height:200px;'>\
		<table style='margin:auto;border-spacing:3px;text-align:left;'>\
		{{#res_html}}\
			{{{html}}}\
		{{/res_html}}\
		</TABLE>\
	</div>\
	<div id='rrs-waiting-div' style='text-align:center;margin-bottom:5px'>\
		<div class='rrs-waiting-div-message'></div>\
	</div>\
	<div id='buttons' style='text-align:center;'>\
		<input type='button' id='NoButton' value='{{no}}, {{cancel}}'>\
	</div>\
</div>"
					var html = Mustache.to_html(template, data)
					$.modal(html, {
						onShow: function modalShow(dialog) {
							dialog.data.find("A.COLLECTION_SELECT").click(function(ev) {
								ev.preventDefault();
								$("#rrs-collections a.collection").attr("collection", 
								$(this).text())
								$("#rrs-collections a.collection").attr("collection_id", 
								$(this).attr("collectionid"))
								$("#rrs-collections a.collection").text($(this).text())
								$.modal.close();
							})
							dialog.data.find("#NoButton").click(function(ev) {
								ev.preventDefault();
								$.modal.close();
							});
						},
						overlayCss:{backgroundColor: '#888', cursor: 'wait'}
					})
				}
			},

			error:function(response) {
				errorMessageWaitingDiv(lang, response['message'])			
			}
		});
	},

	_modalCollectionDelete = function (button) {
	
		var api_key= Rembrandt.Util.getApiKey()
		var ci = parseInt($(button).attr("ID"))
		var role = Rembrandt.Util.getRole(button)
		var servlet_collection_url = Rembrandt.Util.getServletEngineFromRole(role, 'collection')
		var servlet_user_url = Rembrandt.Util.getServletEngineFromRole(role, 'user')
		
		var data = {
			"pressEscape" 			: i18n['pressescape'][lang],
			"deleteCollection" 		: i18n['delete_collection'][lang],
			"password" 				: i18n["password"][lang],
			"authenticate" 			: i18n["authenticate"][lang],
			"waitingForPassword" 	: i18n["waititngForPassword"][lang],
			"no"					: i18n["no"][lang],
			"yes"					: i18n["yes"][lang],
			"delete"				: i18n["delete"][lang],
			"cancel"				: i18n["cancel"][lang]
		}
		
		var template  = "\
<div id='modalDeleteCollection' class='rembrandt-modal' style='width:400px'>\
	<div class='rembrandt-modal-escape'>{{pressEscape}}</div>\
	<div style='text-align:center; padding:10px;'>{{deleteCollection}}</div>\
	<div style='text-align:center; padding:3px;'>\
		<form>\
			<table style='border:0px;border-spacing:3px;'>\
				<TR>\
					<TD ALIGN=RIGHT>{{password}}*</TD>\
					<TD ALIGN=LEFT>\
						<INPUT TYPE='PASSWORD' SIZE='25' ID='password'>\
					</TD>\
				</TR>\
			</TABLE>\
			<BR>\
			<div id='rrs-waiting-div' style='text-align:center;margin-bottom:5px'>\
				<div class='rrs-waiting-div-message'></div>\
			</div>\
			<div id='button1' style='text-align:center;'>\
				<input type='button' id='AuthButton' value='{{authenticate}}'>\
			</DIV>\
		</FORM>\
		<DIV ID='info'>{{waitingForPassword}}...</DIV>\
		<FORM>\
			<div id='buttons' style='text-align:center;'>\
				<input type='button' id='YesButton' value='{{yes}}, {{delete}}' DISABLED>\
				<input type='button' id='NoButton' value='{{no}}, {{cancel}}'>\
			</div>\
		</form>\
	</div>\
</div>"

		$.modal(Mustache.to_html(template, data), {
			
			onShow: function modalShow(dialog) {
				// fill out table
		   
			dialog.data.find("#AuthButton").click(function(ev) {
			
				var password = dialog.data.find("#password").val()
			
				jQuery.ajax({ 
					type			: "POST", 
					dataType		:' json', 
					url				: servlet_user_url+"/auth",
					contentType		: "application/json",
					data			: JSON.stringify({
						"lg" 		: lang,
						"p"			: hex_md5(password),
						"api_key"	: Rembrandt.Util.getApiKey()
					}),
					beforeSubmit	: Rembrandt.Waiting.show(),
					success			: function (response) {
						if (response['status'] == -1) {
							Rembrandt.Waiting.error(response)
						}	else if (response['status'] == 0) {
							Rembrandt.Waiting.hide({
								message: response['message'],
								when: 5000
							})
							dialog.data.find("#YesButton").attr('disabled','false')
							dialog.data.find("#NoButton").attr('disabled','false')
						}
					}, 
					error:function(response) {
						Rembrandt.Waiting.error(response)
					}
				})
			})
			
			dialog.data.find("#YesButton").click(function(ev) {
				ev.preventDefault();
							
				jQuery.ajax({ 
					type			: "POST", 
					dataType		:' json', 
					url				: servlet_user_url+"/delete",
					contentType		: "application/json",
					data			: JSON.stringify({
						"lg" 		: lang,
						"ci"		: ci,
						"api_key"	: Rembrandt.Util.getApiKey()
					}),
					beforeSubmit	: Rembrandt.Waiting.show(),
					success			: function (response) {
						if (response['status'] == -1) {
								Rembrandt.Waiting.error(response)
						} else {
							Rembrandt.Waiting.hide({
								message: response['message'],
								when: 5000
							})
						}
					},
					error:function(response) {
						Rembrandt.Waiting.error(response)
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
		"generateCollectionListDIV":  generateCollectionListDIV,
		"generateCollectionShowDIV" : generateCollectionShowDIV,
		"generateCollectionDocListDIV" : generateCollectionDocListDIV, 
	};
}(jQuery));