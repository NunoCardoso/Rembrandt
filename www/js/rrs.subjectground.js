$().ready(function() {
	
	/* these are exclusively for admin, yet. */
	/* To demote them, change the ROLE status on clicked links and create saskia versions of restlets*/ 						
	$('A.SUBJECTGROUND_CREATE').live("click", function(ev, ui) {
		ev.preventDefault();
		modalSubjectgroundCreate($(this))
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-subjectground-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
	});
	
	$('A.SUBJECTGROUND_LIST').live("click", function(ev, ui) {
		ev.preventDefault();
		var a_clicked = $(this)
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : a_clicked.text())
		var api_key = Rembrandt.Util.getApiKey()
			
		showSlidableDIV({
			"title": title,
			"target":a_clicked.attr("TARGET"),
			"role":a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":true,
			"restlet_url":Rembrandt.Util.getServletEngineFromRole(a_clicked.attr('ROLE'), "subjectground"),
			"postdata":"do=list&l=10&o=0&lg="+lang+"&api_key="+api_key,
			"divRender":generateSubjectgroundListDIV, 
			"divRenderOptions":{},
			"sidemenu":null, 
			"sidemenuoptions":{}
		})					
	});
	
	$('A.SUBJECTGROUND_DELETE').live("click", function(ev, ui) {
		ev.preventDefault();
		modalSubjectgroundDelete($(this))
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-subjectground-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
		if (divshown.attr('id') == 'rrs-subjectground-show-'+$(this).attr('ID')) {
			divshown.find("DIV.rrs-pageable").html(i18n['subjectground_deleted'][lang])
			hideSubmeuOnSideMenu($("#main-side-menu-section-subjectground"))
		}
	})	

	$('A.SUBJECTGROUND_SHOW').live("click", function(ev, ui) {
		ev.preventDefault();
		var a_clicked = $(this)
		var id = a_clicked.attr("ID")
		var api_key = Rembrandt.Util.getApiKey()
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : a_clicked.text())
			
		showSlidableDIV({
			"title": title,
			"target":a_clicked.attr("TARGET"),
			"role":a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":true,
			"restlet_url":Rembrandt.Util.getServletEngineFromRole(a_clicked.attr('ROLE'), "subjectground"),
			"postdata":"do=show&id="+id+"&lg="+lang+	"&api_key="+api_key,
			"divRender":generateSubjectgroundShowDIV, 
			"divRenderOptions":{},
			"sidemenu":"subjectground", 
			"sidemenuoptions":{"id":id, "sgr_name":title}
		})		
	});

})

/*************************/
/*** content creation ****/
/*************************/

/** generate subjectground list DIV */
function generateSubjectgroundListDIV(response, su, role, options) {

	var context = "subjectground"
	var res = response['result']
	var canadmin = (su || role.toLowerCase() == "col-admin")
	var editinplace = (canadmin  ? "editinplace" : "")

	// newdiv
	var newdiv = $("<DIV ID='rrs-subjectground-list' CLASS='main-slidable-div' "+
	" TITLE='"+i18n[context+'_list'][lang]+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	// Set pageable area for paging reposition
	t = "<DIV CLASS='rrs-pageable'>" 
	
	// pager
	t += createPagerNavigation({
		"context":context, 
		"contexts":"subjectgrounds", 
		"response":response,
		"role":role, 
		"allowedSearchableFields":{
			"sgr_id":i18n['id'][lang], 
			"sgr_subject":i18n['subject'][lang], 
			"sgr_geoscope":i18n['geoscope'][lang], 
			"sgr_wikipedia_category":i18n['wikipedia_category'][lang], 
			"sgr_dbpedia_resource":i18n['dbpedia_resource'][lang],
			"sgr_dbpedia_class":i18n['dbpedia_class'][lang]
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
	t += "<TH>"+i18n['subject'][lang]+"</TH>";
	t += "<TH>"+i18n['geoscope'][lang]+"</TH>";
	t += "<TH>"+i18n['wikipedia_category'][lang]+"</TH>";
	t += "<TH>"+i18n['dbpedia_resource'][lang]+"</TH>";
	t += "<TH>"+i18n['dbpedia_class'][lang]+"</TH>";
	t += "<TH>"+i18n['comment'][lang]+"</TH>";
	if (su) {t += "<TD></TD>";}
	t += "</TR></THEAD><TBODY>"
	
	for(i in res) {
		
		var id = res[i]['sgr_id']
		
		t += "<TR><TD><INPUT TYPE='CHECKBOX' CLASS='sec-checkbox'></TD>"
		t += "<TH><A CLASS='"+context.toUpperCase()+"_SHOW' ROLE='"+role+"' "+
		 "TARGET='rrs-"+context+"-show-"+id+"' ID='"+id+"' "+
		 "TITLE='"+res[i]['sgr_name']+"' HREF='#'>"+id+"</A></TH>"

		t += "<TD><DIV CONTEXT='"+context+"'  COL='sgr_subject' COL2='sbj_subject' ID='"+id+"' "
		t += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"
		t += res[i]['sgr_subject']['sbj_subject']+"</DIV></DIV></TD>"	
		
		t += "<TD><DIV CONTEXT='"+context+"'  COL='sgr_geoscope' COL2='geo_name' ID='"+id+"' "
		t += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"
		t += res[i]['sgr_geoscope']['geo_name']+"</DIV></DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"'  COL='sgr_wikipedia_category' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['sgr_wikipedia_category']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"'  COL='sgr_dbpedia_resource' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['sgr_dbpedia_resource']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"'  COL='sgr_dbpedia_class' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['sgr_dbpedia_class']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"'  COL='sgr_comment' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textarea'>"+res[i]['sgr_comment']+"</DIV></TD>"	

		if (su) {
			t += "<TD><A HREF='#' ID='"+id+"' TITLE='"+res[i]['sgr_subject']['sbj_subject']+" "
			t += res[i]['sgr_geoscope']['geo_name']+"' CLASS='"+context+"_DELETE main-button'"
			t += "ROLE='"+role+"'><SPAN>"+i18n['delete'][lang]+"...</SPAN></A></TD>";
		}
		t += "</TR>"
	}	

	t += "<TFOOT><TR><TD><INPUT TYPE='CHECKBOX' CLASS='main-checkbox'></TD>"
	t += "<TH>"+i18n['all'][lang]+"</TH>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='sgr_subject' "
	t += "CLASS='"+editinplace+" autocompletetextfield group'></DIV></TD>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='sgr_geoscope' "
	t += "CLASS='"+editinplace+" autocompletetextfield group'></DIV></TD>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='sgr_wikipedia_category' "
	t += "CLASS='"+editinplace+" textfield group'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='sgr_dbpedia_resource' "
	t += "CLASS='"+editinplace+" textfield group'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='sgr_dbpedia_class' "
	t += "CLASS='"+editinplace+" textfield group'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='sgr_comment' "
	t += "CLASS='"+editinplace+" textarea group'></DIV>"
	if (su) {t += "<TD></TD>"}
	t += "</TR></TFOOT>"
	t += "</TABLE></DIV>"
	t += "</DIV></DIV>"
	
	newdiv.append(t)
	return newdiv
}

function generateSubjectgroundShowDIV(response, su, role, options) {

	var context = "subjectground"
	var canadmin = (su || role.toLowerCase() == "col-admin")
	var editinplace = ( canadmin ? "editinplace" : "")
	var id = response['sgr_id']
	
	// newdiv
	var newdiv = $("<DIV ID='rrs-"+context+"-show-"+id+"' CLASS='main-slidable-div' "+
	" TITLE='"+Rembrandt.Util.shortenTitle(response['sgr_name'])+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	var s = "<DIV CLASS='rrs-pageable'>" 

	s += "<H3>"+i18n[context+'-show'][lang]+"</H3>"				
	s += "<TABLE ID='rrs-"+context+"-show-table tablesorter' BORDER=0>"
	s += "<TR><TD>"+i18n['id'][lang]+":</TD><TD>"+id+"</TD></TR>";

	// begin of a saskia object							
	t += "<TD><DIV CONTEXT='"+context+"'  COL='sgr_subject' COL2='sbj_subject' ID='"+id+"' "
	t += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"+res[i]['sgr_subject']['sbj_subject']+
	"</DIV></DIV></TD>"		
	// end of a saskia object

	// begin of a saskia object							
	t += "<TD><DIV CONTEXT='"+context+"'  COL='sgr_geoscope' COL2='geo_name' ID='"+id+"' "
	t += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"+res[i]['sgr_geoscope']['geo_name']+
	"</DIV></DIV></TD>"		
	// end of a saskia object

	s += "<TR><TD>"+i18n['wikipedia_resource'][lang]+":</TD><TD>";	
	s += "<DIV CONTEXT='"+context+"'  COL='sgr_wikipedia_resource' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['sgr_wikipedia_resource']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['dbpedia_resource'][lang]+":</TD><TD>";
	s += "<DIV CONTEXT='"+context+"'  COL='sgr_dbpedia_resource' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['sgr_dbpedia_resource']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['dbpedia_class'][lang]+":</TD><TD>";
	s += "<DIV CONTEXT='"+context+"'  COL='sgr_dbpedia_class' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['sgr_dbpedia_class']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['comment'][lang]+":</TD><TD>";
	s += "<DIV CONTEXT='"+context+"'  COL='sgr_comment' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textarea'>"+col['sgr_comment']+"</DIV></TD></TR>";

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

function modalSubjectgroundCreate(button) {
	
	var api_key= Rembrandt.Util.getApiKey()
	var role = Rembrandt.Util.getRole(button)
	var servlet_url = Rembrandt.Util.getServletEngineFromRole(role, 'subjectground')
		
	$.modal("<div id='modalCreateSubjectground' class='rembrandt-modal'>"+
      "<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
	   "<div style='text-align:center; padding:10px;'>"+i18n['create_new_subjectground'][lang]+"</div>"+
	   "<div style='text-align:left; padding:3px;'>"+
		"<form><table style='border:0px;border-spacing:3px;'>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["subject"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='sgr_subject'></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["geosope"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='sgr_geoscope'></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["wikipedia_category"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='sgr_wikipedia_category'></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["dbpedia_resource"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='sgr_dbpedia_resource'></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["dbpedia_class"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='sgr_dbpedia_class'></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["comment"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><TEXTAREA ID='sgr_comment'></TEXTAREA></TD></TR>"+
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
			dialog.data.find("#sgr_subject").autocomplete(restlet_dbosuggestion_url, {
				minChars:3, dataType: "json", 
				mustMatch: false, autoFill: false, matchContains: false,
				multipleSeparator:"",
				extraParams:{"t" : "subject"},
				parse: theParse, 
				formatItem: theFormatItem,
				formatMatch: theFormatMatch,
				formatResult: theFormatResult
			})
			
			dialog.data.find("#sgr_geoscope").autocomplete(restlet_dbosuggestion_url, {
				minChars:3, dataType: "json", 
				mustMatch: false, autoFill: false, matchContains: false,
				multipleSeparator:"",
				extraParams:{"t" : "geoscope"},
				parse: theParse, 
				formatItem: theFormatItem,
				formatMatch: theFormatMatch,
				formatResult: theFormatResult
			})
			
			dialog.data.find("#YesButton").click(function(ev) {

				jQuery.ajax( {
					type:"POST", url:servlet_url, contentType:"application/x-www-form-urlencoded",
					data: "do=create&lg="+lang+
					"&sgr_subject="+dialog.data.find("#sgr_subject").val()+
					"&sgr_geoscope="+dialog.data.find("#sgr_geoscope").val()+
					"&sgr_wikipedia_category="+dialog.data.find("#sgr_wikipedia_category").val()+
					"&sgr_dbpedia_resource="+dialog.data.find("#sgr_dbpedia_resource").val()+
					"&sgr_dbpedia_class="+dialog.data.find("#sgr_dbpedia_class").val()+
					"&sgr_comment="+dialog.data.find("#sgr_comment").val()+
					"&api_key="+api_key, 
					beforeSubmit: waitMessageBeforeSubmit(lang),

					success: function(response) {
						if (response['status'] == -1) {
							errorMessageWaitingDiv(lang, response['message'])
							dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
							dialog.data.find("#buttons").show()	
						} else if (response['status'] == 0)  {
							showCustomMessageWaitingDiv(i18n['subjectground_created'][lang])
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

function modalSubjectgroundDelete(button) {
	
	var context = "subjectground"
	
	genericDeleteModel({
		'context':context,
		'id': button.attr("ID"),
		'info':button.attr('TITLE'),
		'servlet_url': Rembrandt.Util.getServletEngineFromRole(Rembrandt.Util.getRole(button), context),
		'postdata' : "do=delete&id="+button.attr("ID")+"&lg="+lang+"&api_key="+Rembrandt.Util.getApiKey(),
		'success_message' : i18n['subjectground_deleted'][lang]
	})
}	