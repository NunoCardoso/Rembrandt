$().ready(function() {
	
	/* these are exclusively for admin, yet. */
	/* To demote them, change the ROLE status on clicked links and create saskia versions of restlets*/ 						
	$('A.SUBJECT_CREATE').live("click", function(ev, ui) {
		ev.preventDefault();
		modalSubjectCreate($(this))
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-subject-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
	});
	
	$('A.SUBJECT_LIST').live("click", function(ev, ui) {
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
			"restlet_url":getServletEngineFromRole(a_clicked.attr('ROLE'), "subject"),
			"postdata":"do=list&l=10&o=0&lg="+lang+"&api_key="+api_key,
			"divcreator":generateSubjectListDIV, 
			"divcreatoroptions":{},
			"sidemenu":null, 
			"sidemenuoptions":{}
		})					
	});
	
	$('A.SUBJECT_DELETE').live("click", function(ev, ui) {
		ev.preventDefault();
		modalSubjectDelete($(this))
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-subject-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
		if (divshown.attr('id') == 'rrs-subject-show-'+$(this).attr('ID')) {
			divshown.find("DIV.rrs-pageable").html(i18n['subject_deleted'][lang])
			hideSubmeuOnSideMenu($("#main-side-menu-section-subject"))
		}
	})	

	$('A.SUBJECT_SHOW').live("click", function(ev, ui) {
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
			"restlet_url":getServletEngineFromRole(a_clicked.attr('ROLE'), "subject"),
			"postdata":"do=show&id="+id+"&lg="+lang+	"&api_key="+api_key,
			"divcreator":generateSubjectShowDIV, 
			"divcreatoroptions":{},
			"sidemenu":"subject", 
			"sidemenuoptions":{"id":id, "sbj_name":title}
		})		
	});

})

/*************************/
/*** content creation ****/
/*************************/

/** generate subject list DIV */
function generateSubjectListDIV(response, su, role, options) {

	var context = "subject"
	var res = response['result']
	var canadmin = (su || role.toLowerCase() == "col-admin")
	var editinplace = (canadmin  ? "editinplace" : "")

	// newdiv
	var newdiv = $("<DIV ID='rrs-subject-list' CLASS='main-slidable-div' "+
	" TITLE='"+i18n[context+'_list'][lang]+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	// Set pageable area for paging reposition
	t = "<DIV CLASS='rrs-pageable'>" 
	
	// pager
	t += createPagerNavigation({
		"context":context, 
		"contexts":"subjects", 
		"response":response,
		 "role":role, 
		"allowedSearchableFields":{
			"sbj_id":i18n['id'][lang], 
	    	"sbj_subject":i18n['name'][lang],
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
	if (su) {t += "<TD></TD>";}
	t += "</TR></THEAD><TBODY>"
	
	for(i in res) {
		
		var id = res[i]['sbj_id']
		
		t += "<TR><TD><INPUT TYPE='CHECKBOX' CLASS='sec-checkbox'></TD>"
		t += "<TH><A CLASS='"+context.toUpperCase()+"_SHOW' ROLE='"+role+"' "+
		 "TARGET='rrs-"+context+"-show-"+id+"' ID='"+id+"' "+
		 "TITLE='"+res[i]['sbj_subject']+"' HREF='#'>"+id+"</A></TH>"

		t += "<TD><DIV CONTEXT='"+context+"'  COL='sbj_subject' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['sbj_subject']+"</DIV></TD>"	
		
		if (su) {
			t += "<TD><A HREF='#' ID='"+id+"' TITLE='"+res[i]['sbj_subject']+"' CLASS='"+context+"_DELETE main-button'"
			t += "ROLE='"+role+"'><SPAN>"+i18n['delete'][lang]+"...</SPAN></A></TD>";
		}
		t += "</TR>"
	}	

	t += "<TFOOT><TR><TD><INPUT TYPE='CHECKBOX' CLASS='main-checkbox'></TD>"
	t += "<TH>"+i18n['all'][lang]+"</TH>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='sbj_subject' "
	t += "CLASS='"+editinplace+" textfield group'></DIV></TD>"
	if (su) {t += "<TD></TD>"}
	t += "</TR></TFOOT>"
	t += "</TABLE></DIV>"
	t += "</DIV></DIV>"
	
	newdiv.append(t)
	return newdiv
}

function generateSubjectShowDIV(response, su, role, options) {

	var context = "subject"
	var canadmin = (su || role.toLowerCase() == "col-admin")
	var editinplace = ( canadmin ? "editinplace" : "")
	var id = response['sbj_id']
	
	// newdiv
	var newdiv = $("<DIV ID='rrs-"+context+"-show-"+id+"' CLASS='main-slidable-div' "+
	" TITLE='"+shortenTitle(response['sbj_subject'])+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	var s = "<DIV CLASS='rrs-pageable'>" 

	s += "<H3>"+i18n[context+'-show'][lang]+"</H3>"				
	s += "<TABLE ID='rrs-"+context+"-show-table tablesorter' BORDER=0>"
	s += "<TR><TD>"+i18n['id'][lang]+":</TD><TD>"+id+"</TD></TR>";

	s += "<TR><TD>"+i18n['name'][lang]+":</TD><TD>";	
	s += "<DIV CONTEXT='"+context+"'  COL='sbj_subject' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['sbj_subject']+"</DIV></TD></TR>";

	s += "</TABLE>"; 
	
	if (su) {
	
		s += "<A HREF='#' CLASS='"+context.toUpperCase()+"_DELETE main-button' ID='"+id+"' "
		s += " TITLE='"+i18n['delete'][lang]+"' ROLE='"+role+"'><SPAN> "
		s += i18n['delete'][lang]+"...</SPAN></A></TD>";
	}
	
	s += "</DIV>"
}

/**************/
/*** modal ****/
/**************/

function modalSubjectCreate(button) {
	
	var api_key= getAPIKey()
	var role = getRole(button)
	var servlet_url = getServletEngineFromRole(role, 'subject')
		
	$.modal("<div id='modalCreateSubject' class='rembrandt-modal'>"+
      "<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
	   "<div style='text-align:center; padding:10px;'>"+i18n['create_new_subject'][lang]+"</div>"+
	   "<div style='text-align:left; padding:3px;'>"+
		"<form><table style='border:0px;border-spacing:3px;'>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["name"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='sbj_subject'></TD></TR>"+
		"<BR><BR>	"+
		"<div id='rrs-waiting-div' style='text-align:center;margin-bottom:5px'>"+
		"<div class='rrs-waiting-div-message'></div></div> "+ 
		"<div id='buttons' style='text-align:center;'>"+
		"<input type='button' id='YesButton' value='"+i18n["yes"][lang]+", "+i18n["create"][lang]+"'>"+
		"<input type='button' id='NoButton' value='"+i18n["no"][lang]+", "+i18n["cancel"][lang]+"'>"+
		"</div></form></div></div>	", {
			
		onShow: function modalShow(dialog) {
			// fill out table
			
			dialog.data.find("#YesButton").click(function(ev) {

				jQuery.ajax( {
					type:"POST", url:servlet_url, contentType:"application/x-www-form-urlencoded",
					data: "do=create&lg="+lang+
					"&sbj_subject="+dialog.data.find("#sbj_subject").val()+
					"&api_key="+api_key, 
					beforeSubmit: waitMessageBeforeSubmit(lang),

					success: function(response) {
						if (response['status'] == -1) {
							errorMessageWaitingDiv(lang, response['message'])
							dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
							dialog.data.find("#buttons").show()	
						} else if (response['status'] == 0)  {
							showCustomMessageWaitingDiv(i18n['subject_created'][lang])
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

function modalSubjectDelete(button) {
	
	var context = "subject"
	
	genericDeleteModel({
		'context':context,
		'id': button.attr("ID"),
		'info':button.attr('TITLE'),
		'servlet_url': getServletEngineFromRole(getRole(button), context),
		'postdata' : "do=delete&id="+button.attr("ID")+"&lg="+lang+"&api_key="+getAPIKey(),
		'success_message' : i18n['subject_deleted'][lang]
	})
}	