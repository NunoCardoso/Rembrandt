$().ready(function() {
	
	/* these are exclusively for admin, yet. */
	/* To demote them, change the ROLE status on clicked links and create saskia versions of restlets*/ 						
	$('A.TASK_CREATE').live("click", function(ev, ui) {
		ev.preventDefault();
		modalTaskCreate($(this))
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-task-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
	});
	
	$('A.TASK_LIST').live("click", function(ev, ui) {
		ev.preventDefault();
		var a_clicked = $(this)
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : a_clicked.text())
		var api_key = getAPIKey()
			
		showSlidableDIV({
			"title": title,
			"target":a_clicked.attr("TARGET"),
			"role":a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":true,
			"restlet_url":getServletEngineFromRole(a_clicked.attr('ROLE'), "task"),
			"postdata":"do=list&l=10&o=0&lg="+lang+"&api_key="+api_key,
			"divcreator":generateTaskListDIV, 
			"divcreatoroptions":{},
			"sidemenu":null, 
			"sidemenuoptions":{}
		})					
	});
	
	$('A.TASK_DELETE').live("click", function(ev, ui) {
		ev.preventDefault();
		modalTaskDelete($(this))
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-task-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
		if (divshown.attr('id') == 'rrs-task-show-'+$(this).attr('ID')) {
			divshown.find("DIV.rrs-pageable").html(i18n['task_deleted'][lang])
			hideSubmeuOnSideMenu($("#main-side-menu-section-task"))
		}
	})	

	$('A.TASK_SHOW').live("click", function(ev, ui) {
		ev.preventDefault();
		var a_clicked = $(this)
		var id = a_clicked.attr("ID")
		var api_key = getAPIKey()
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : a_clicked.text())
			
		showSlidableDIV({
			"title": title,
			"target":a_clicked.attr("TARGET"),
			"role":a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":true,
			"restlet_url":getServletEngineFromRole(a_clicked.attr('ROLE'), "task"),
			"postdata":"do=show&id="+id+"&lg="+lang+	"&api_key="+api_key,
			"divcreator":generateTaskShowDIV, 
			"divcreatoroptions":{},
			"sidemenu":"task", 
			"sidemenuoptions":{"tsk_id":tsk_id, "tsk_name":title}
		})		
	});

})

/*************************/
/*** content creation ****/
/*************************/

/** generate task list DIV */
function generateTaskListDIV(response, su, role, options) {

	var context = "task"
	var res = response['result']
	var canadmin = (su || role.toLowerCase() == "col-admin")
	var editinplace = (canadmin  ? "editinplace" : "")

	// newdiv
	var newdiv = $("<DIV ID='rrs-task-list' CLASS='main-slidable-div' "+
	" TITLE='"+i18n[context+'_list'][lang]+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	// Set pageable area for paging reposition
	t = "<DIV CLASS='rrs-pageable'>" 
	
	// pager
	t += createPagerNavigation({
		"context":context, 
		"contexts":"tasks", 
		"response":response,
		"role":role, 
		"allowedSearchableFields":{
			"tsk_id":i18n['id'][lang], 
			"tsk_task":i18n['name'][lang], 
			"tsk_user":i18n['user'][lang], 
			"tsk_collection":i18n['collection'][lang], 
			"tsk_type":i18n['type'][lang], 
			"tsk_priority":i18n['priority'][lang],
			"tsk_scope":i18n['task_scope'][lang],
			"tsk_persistence":i18n['task_persistence'][lang],
			"tsk_status":i18n['status'][lang],
			"tsk_comment":i18n['comment'][lang],
		}
	})
	
	var res = response['result']
		
	// buttons
	t += "<DIV CLASS='rrs-buttonrow'>"
	if (su) {
		t += "<A HREF='#' CLASS='"+context.toUpperCase()+"_CREATE main-button' "
		t += "ROLE='"+role+"' TITLE='"+i18n['create_new_'+context][lang]+"'><SPAN>"
		t += i18n['create_new_'+context][lang]+"</SPAN></A>";
	}
	t += "</DIV>"

	// table 
	t += "<DIV>"
	t += "<TABLE ID='rrs-"+context+"-list-table' CLASS='tablesorter "+ role + "'>"
	t += "<THEAD><TR><TH></TH>"
	t += "<TH>"+i18n['id'][lang]+"</TH>";
	t += "<TH>"+i18n['name'][lang]+"</TH>";
	if (su) {t += "<TH>"+i18n['user'][lang]+"</TH>";}
	if (su) {t += "<TH>"+i18n['collection'][lang]+"</TH>";}
	t += "<TH>"+i18n['type'][lang]+"</TH>";
	t += "<TH>"+i18n['priority'][lang]+"</TH>";
	t += "<TH>"+i18n['limit'][lang]+"</TH>";
	t += "<TH>"+i18n['offset'][lang]+"</TH>";
	t += "<TH>"+i18n['done'][lang]+"</TH>";
	t += "<TH>"+i18n['task_scope'][lang]+"</TH>";
	t += "<TH>"+i18n['task_persistence'][lang]+"</TH>";
	t += "<TH>"+i18n['status'][lang]+"</TH>";
	t += "<TH>"+i18n['comment'][lang]+"</TH>";
	if (canadmin) {t += "<TD></TD>";}
	t += "</TR></THEAD><TBODY>"
	
	for(i in res) {
		
		var id = res[i]['tsk_id']
		
		t += "<TR><TD><INPUT TYPE='CHECKBOX' CLASS='sec-checkbox'></TD>"
		t += "<TH><A CLASS='"+context.toUpperCase()+"_SHOW' ROLE='"+role+"' "+
		 "TARGET='rrs-"+context+"-show-"+id+"' ID='"+id+"' "+
		 "TITLE='"+res[i]['tsk_name']+"' HREF='#'>"+id+"</A></TH>"

		t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_task' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['tsk_task']+"</DIV></TD>"	

		if (su) {
			t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_user' COL2='user_login' ID='"+id+"' "
			t += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"
			t += res[i]['tsk_user']['user_login']+"</DIV></DIV></TD>"	
		
			t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_collection' COL2='col_name' ID='"+id+"' "
			t += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"
			t += res[i]['tsk_collection']['col_name']+"</DIV></DIV></TD>"	
		}
		
		t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_type' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['tsk_type']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_priority' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['tsk_priority']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_limit' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['tsk_limit']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_offset' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['tsk_offset']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_done' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['tsk_done']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_scope' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['tsk_scope']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_persistence' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['tsk_persistence']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_status' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['tsk_status']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_comment' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textarea'>"+res[i]['tsk_comment']+"</DIV></TD>"	

		if (canadmin) {
			t += "<TD><A HREF='#' ID='"+id+"' TITLE='"+id+"' CLASS='"+context+"_DELETE main-button'"
			t += "ROLE='"+role+"'><SPAN>"+i18n['delete'][lang]+"...</SPAN></A></TD>";
		}
		t += "</TR>"
	}	

	t += "<TFOOT><TR><TD><INPUT TYPE='CHECKBOX' CLASS='main-checkbox'></TD>"
	t += "<TH>"+i18n['all'][lang]+"</TH>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_task' "
	t += "CLASS='"+editinplace+" textfield group'></DIV>"
	if (su) {
		t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_user' "
		t += "CLASS='"+editinplace+" autocompletetextfield group'></DIV></TD>"
		t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_collection' "
		t += "CLASS='"+editinplace+" autocompletetextfield group'></DIV></TD>"
	}
	t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_type' "
	t += "CLASS='"+editinplace+" textfield group'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_priority' "
	t += "CLASS='"+editinplace+" textfield group'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_limit' "
	t += "CLASS='"+editinplace+" textfield group'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_offset' "
	t += "CLASS='"+editinplace+" textfield group'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_done' "
	t += "CLASS='"+editinplace+" textfield group'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_scope' "
	t += "CLASS='"+editinplace+" textfield group'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_persistence' "
	t += "CLASS='"+editinplace+" textfield group'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_status' "
	t += "CLASS='"+editinplace+" textfield group'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_comment' "
	t += "CLASS='"+editinplace+" textarea group'></DIV>"
	if (canadmin) {t += "<TD></TD>"}
	t += "</TR></TFOOT>"
	t += "</TABLE></DIV>"
	t += "</DIV></DIV>"
	
	newdiv.append(t)
	return newdiv
}

function generateTaskShowDIV(response, su, role, options) {

	var context = "task"
	var canadmin = (su || role.toLowerCase() == "col-admin")
	var editinplace = ( canadmin ? "editinplace" : "")
	var id = response['tsk_id']
	
	// newdiv
	var newdiv = $("<DIV ID='rrs-"+context+"-show-"+id+"' CLASS='main-slidable-div' "+
	" TITLE='"+shortenTitle(response['tsk_name'])+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	var s = "<DIV CLASS='rrs-pageable'>" 

	s += "<H3>"+i18n[context+'-show'][lang]+"</H3>"				
	s += "<TABLE ID='rrs-"+context+"-show-table tablesorter' BORDER=0>"
	s += "<TR><TD>"+i18n['id'][lang]+":</TD><TD>"+id+"</TD></TR>";

	s += "<TR><TD>"+i18n['name'][lang]+":</TD><TD>";	
	s += "<DIV CONTEXT='"+context+"'  COL='tsk_task' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['tsk_task']+"</DIV></TD></TR>";

	if (su) {
		// begin of a saskia object							
		t += "<TD><DIV CONTEXT='"+context+"' COL='tsk_owner' COL2='usr_login' ID='"+id+"' "
		t += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"+res[i]['tsk_owner']['usr_login']+
		"</DIV></DIV></TD>"		
		// end of a saskia object
	
		// begin of a saskia object							
		t += "<TD><DIV CONTEXT='"+context+"'  COL='tsk_collection' COL2='col_name' ID='"+id+"' "
		t += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"+res[i]['tsk_collection']['col_name']+
		"</DIV></DIV></TD>"		
		// end of a saskia object
	}
	
	s += "<TR><TD>"+i18n['type'][lang]+":</TD><TD>";	
	s += "<DIV CONTEXT='"+context+"'  COL='tsk_type' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['tsk_type']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['priority'][lang]+":</TD><TD>";
	s += "<DIV CONTEXT='"+context+"'  COL='priority' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['priority']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['limit'][lang]+":</TD><TD>";
	s += "<DIV CONTEXT='"+context+"'  COL='tsk_limit' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['tsk_limit']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['offset'][lang]+":</TD><TD>";
	s += "<DIV CONTEXT='"+context+"'  COL='tsk_offset' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['tsk_offset']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['done'][lang]+":</TD><TD>";
	s += "<DIV CONTEXT='"+context+"'  COL='tsk_done' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['tsk_done']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['task_scope'][lang]+":</TD><TD>";
	s += "<DIV CONTEXT='"+context+"'  COL='tsk_scope' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['tsk_scope']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['task_persistence'][lang]+":</TD><TD>";
	s += "<DIV CONTEXT='"+context+"'  COL='tsk_persistence' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['tsk_persistence']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['status'][lang]+":</TD><TD>";
	s += "<DIV CONTEXT='"+context+"'  COL='tsk_status' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['tsk_status']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['comment'][lang]+":</TD><TD>";
	s += "<DIV CONTEXT='"+context+"'  COL='tsk_comment' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textarea'>"+col['tsk_comment']+"</DIV></TD></TR>";

	s += "</TABLE>"; 
	
	if (canadmin) {
	
		s += "<A HREF='#' CLASS='"+context.toUpperCase()+"_DELETE main-button' ID='"+id+"' "
		s += " TITLE='"+i18n['delete'][lang]+"' ROLE='"+role+"'><SPAN> "
		s += i18n['delete'][lang]+"...</SPAN></A></TD>";
	}
	
	s += "</DIV>"
}

/**************/
/*** modal ****/
/**************/

function modalTaskCreate(button) {
	
	var api_key= getAPIKey()
	var role = getRole(button)
	var servlet_url = getServletEngineFromRole(role, 'task')
		
	$.modal("<div id='modalCreateTask' class='rembrandt-modal'>"+
      "<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
	   "<div style='text-align:center; padding:10px;'>"+i18n['create_new_task'][lang]+"</div>"+
	   "<div style='text-align:left; padding:3px;'>"+
		"<form><table style='border:0px;border-spacing:3px;'>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["name"][lang]+"</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='30' ID='tsk_task' value='0'></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["owner"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='tsk_owner'></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["collection"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='tsk_collection'></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["wikipedia_category"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><SELECT SIZE='1' ID='tsk_type'>"+
			"<OPTION VALUE='S2R'>"+i18n['task_type_s2r'][lang]+"</OPTION>"+
			"<OPTION VALUE='R2P'>"+i18n['task_type_r2p'][lang]+"</OPTION>"+
			"<OPTION VALUE='GEO'>"+i18n['task_type_geo'][lang]+"</OPTION>"+
			"<OPTION VALUE='TIM'>"+i18n['task_type_tim'][lang]+"</OPTION>"+
		"</SELECT></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["priority"][lang]+"</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='4' ID='tsk_priority' value='0'></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["limit"][lang]+"</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='6' ID='tsk_limit' value='0'></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["offset"][lang]+"</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='6' ID='tsk_offset' value='0'></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["task_scope"][lang]+"</TD>"+
		"<TD ALIGN=LEFT><SELECT SIZE='1' ID='tsk_scope'>"+
			"<OPTION VALUE='BAT'>"+i18n['task_scope_bat'][lang]+"</OPTION>"+
			"<OPTION VALUE='SRV'>"+i18n['task_scope_srv'][lang]+"</OPTION>"+
		"</SELECT></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["task_persistence"][lang]+"</TD>"+
		"<TD ALIGN=LEFT><SELECT SIZE='1' ID='tsk_persistence'>"+
			"<OPTION VALUE='TMP'>"+i18n['task_persistence_tmp'][lang]+"</OPTION>"+
			"<OPTION VALUE='PRM'>"+i18n['task_persistence_prm'][lang]+"</OPTION>"+
		"</SELECT></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["comment"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><TEXTAREA ID='tsk_comment'></TEXTAREA></TD></TR>"+
		"<BR><BR>	"+
		"<div id='rrs-waiting-div' style='text-align:center;margin-bottom:5px'>"+
		"<div class='rrs-waiting-div-message'></div></div> "+ 
		"<div id='buttons' style='text-align:center;'>"+
		"<input type='button' id='YesButton' value='"+i18n["yes"][lang]+", "+i18n["create"][lang]+"'>"+
		"<input type='button' id='NoButton' value='"+i18n["no"][lang]+", "+i18n["cancel"][lang]+"'>"+
		"</div></form></div></div>	", {
			
		onShow: function modalShow(dialog) {
			// fill out table
			
			// make it use the entity picker
			dialog.data.find("#tsk_owner").autocomplete(restlet_dbosuggestion_url, {
				minChars:3, dataType: "json", 
				mustMatch: false, autoFill: false, matchContains: false,
				multipleSeparator:"",
				extraParams:{"t" : "user"},
				parse: theParse, 
				formatItem: theFormatItem,
				formatMatch: theFormatMatch,
				formatResult: theFormatResult
			})
			
			dialog.data.find("#tsk_collection").autocomplete(restlet_dbosuggestion_url, {
				minChars:3, dataType: "json", 
				mustMatch: false, autoFill: false, matchContains: false,
				multipleSeparator:"",
				extraParams:{"t" : "collection"},
				parse: theParse, 
				formatItem: theFormatItem,
				formatMatch: theFormatMatch,
				formatResult: theFormatResult
			})
			
			dialog.data.find("#YesButton").click(function(ev) {

				jQuery.ajax( {
					type:"POST", url:servlet_url, contentType:"application/x-www-form-urlencoded",
					data: "do=create&lg="+lang+
					"&tsk_task="+dialog.data.find("#tsk_task").val()+
					"&tsk_owner="+dialog.data.find("#tsk_owner").val()+
					"&tsk_collection="+dialog.data.find("#tsk_collection").val()+
					"&tsk_type="+dialog.data.find("#tsk_type :selected").val()+
					"&tsk_priority="+dialog.data.find("#tsk_priority").val()+
					"&tsk_limit="+dialog.data.find("#tsk_limit").val()+
					"&tsk_offset="+dialog.data.find("#tsk_offset").val()+
					"&tsk_scope="+dialog.data.find("#tsk_scope :selected").val()+
					"&tsk_persistence="+dialog.data.find("#tsk_persistence :selected").val()+
					"&tsk_comment="+dialog.data.find("#tsk_comment").val()+
					"&api_key="+api_key, 
					beforeSubmit: waitMessageBeforeSubmit(lang),

					success: function(response) {
						if (response['status'] == -1) {
							errorMessageWaitingDiv(lang, response['message'])
							dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
							dialog.data.find("#buttons").show()	
						} else if (response['status'] == 0)  {
							showCustomMessageWaitingDiv(i18n['task_created'][lang])
							dialog.data.find("#YesButton").hide()
							dialog.data.find("#NoButton").attr("value",i18n["OK"][lang])
						}
					},
					error:function(response) {
						errorMessageWaitingDiv(lang, response['message'])			
					}
				})
			})
			dialog.data.find("#NoButton").click(function(ev) {
				ev.preventDefault();
				$.modal.close();
			});
		},
		overlayCss:{backgroundColor: '#888', cursor: 'wait'}
	});
}	

function modalTaskDelete(button) {
	
	var context = "task"
	
	genericDeleteModel({
		'context':context,
		'id': button.attr("ID"),
		'info':button.attr('TITLE'),
		'servlet_url': getServletEngineFromRole(getRole(button), context),
		'postdata' : "do=delete&id="+button.attr("ID")+"&lg="+lang+"&api_key="+getAPIKey(),
		'success_message' : i18n['task_deleted'][lang]
	})
}	