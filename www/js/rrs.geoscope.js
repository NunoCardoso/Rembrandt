$().ready(function() {

	/* these are exclusively for admin, yet. */
	/* To demote them, change the ROLE status on clicked links and create saskia versions of restlets*/ 						
	$('A.GEOSCOPE_CREATE').live("click", function(ev, ui) {
		ev.preventDefault();
		modalGeoscopeCreate($(this))
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-geoscope-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
	});
	
	$('A.GEOSCOPE_LIST').live("click", function(ev, ui) {
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
			"restlet_url":Rembrandt.Util.getServletEngineFromRole(a_clicked.attr('ROLE'), "geoscope"),
			"postdata":"do=list&l=10&o=0&lg="+lang+"&api_key="+api_key,
			"divRender":generateGeoscopeListDIV, 
			"divRenderOptions":{},
			"sidemenu":null, 
			"sidemenuoptions":{}
		})					
	});
	
	$('A.GEOSCOPE_DELETE').live("click", function(ev, ui) {
		ev.preventDefault();
		modalGeoscopeDelete($(this))
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-geoscope-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
		if (divshown.attr('id') == 'rrs-geoscope-show-'+$(this).attr('ID')) {
			divshown.find("DIV.rrs-pageable").html(i18n['geoscope_deleted'][lang])
			hideSubmeuOnSideMenu($("#main-side-menu-section-geoscope"))
		}
	})	

	$('A.GEOSCOPE_SHOW').live("click", function(ev, ui) {
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
			"restlet_url":Rembrandt.Util.getServletEngineFromRole(a_clicked.attr('ROLE'), "geoscope"),
			"postdata":"do=show&id="+id+"&lg="+lang+	"&api_key="+api_key,
			"divRender":generateGeoscopeShowDIV, 
			"divRenderOptions":{},
			"sidemenu":"geoscope", 
			"sidemenuoptions":{"id":id, "geo_name":title}
		})		
	});

})

/*************************/
/*** content creation ****/
/*************************/

/** generate geoscope list DIV */
function generateGeoscopeListDIV(response, su, role, options) {

	var context = "geoscope"
	var res = response['result']
	var canadmin = (su || role.toLowerCase() == "col-admin")
	var editinplace = (canadmin  ? "editinplace" : "")

	// newdiv
	var newdiv = $("<DIV ID='rrs-"+context+"-list' CLASS='main-slidable-div' "+
	" TITLE='"+i18n[context+'_list'][lang]+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	// Set pageable area for paging reposition
	t = "<DIV CLASS='rrs-pageable'>" 
	
	// pager
	t += createPagerNavigation({
		"context":context, 
		"contexts":"geoscopes", 
		"response":response,
		"role":role, 
		"allowedSearchableFields":{
			"geo_id":i18n['id'][lang], 
	    	"geo_name":i18n['name'][lang],
			"geo_woeid":i18n['woeid'][lang],
			"geo_woeid_type":i18n['type'][lang],
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
	t += "<TH>"+i18n['woeid'][lang]+"</TH>";
	t += "<TH>"+i18n['type'][lang]+"</TH>";
	if (su) {t += "<TH></TH>";}
	t += "</TR></THEAD><TBODY>"
	
	for(i in res) {
		
		var id = res[i]['geo_id']
		
		t += "<TR><TD><INPUT TYPE='CHECKBOX' CLASS='sec-checkbox'></TD>"
		t += "<TH><A CLASS='"+context.toUpperCase()+"_SHOW' ROLE='"+role+"' "+
		 "TARGET='rrs-admin-"+context+"-show-"+id+"' "+
		 "TITLE='"+res[i]['geo_name']+"' HREF='#'>"+id+"</A></TH>"

		t += "<TD><DIV CONTEXT='"+context+"'  COL='geo_name' ID='"+id+"' "
		t += "CLASS='editinplace textfield'>"+res[i]['geo_name']+"</DIV></TD>"	
		
		t += "<TD><DIV CONTEXT='"+context+"'  COL='geo_woeid' ID='"+id+"' "
		t += "CLASS='editinplace textfield'>"+res[i]['geo_woeid']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"'  COL='geo_woeid_type' ID='"+id+"' "
		t += "CLASS='editinplace textfield'>"+res[i]['geo_woeid_type']+"</DIV></TD>"	

		if (su) {
			t += "<TD><A HREF='#' ID='"+id+"' TITLE='"+res[i]['geo_name']+"' CLASS='"
			t += context+"_DELETE main-button' ROLE='"+role+"'>"
			t += "<SPAN>"+i18n['delete'][lang]+"...</SPAN></A></TD>";
		}
		t += "</TR>"
	}	

	t += "<TFOOT><TR><TD><INPUT TYPE='CHECKBOX' CLASS='main-checkbox'></TD>"
	t += "<TH>"+i18n['all'][lang]+"</TH>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='geo_name' "
	t += "CLASS='editinplace textfield group'></DIV></TD>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='geo_woeid' "
	t += "CLASS='editinplace textfield group'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='geo_woeid_type' "
	t += "CLASS='editinplace textfield group'></DIV>"
	if (su) {t += "<TD></TD>"}
	t += "</TR></TFOOT>"
	t += "</TABLE></DIV>"
	t += "</DIV></DIV>"
	
	newdiv.append(t)
	return newdiv
}

/** generate geoscope show DIV */
function generateGeoscopeShowDIV(response, su, role, options) {

	var context = "geoscope"
	var canadmin = (su || role.toLowerCase() == "col-admin")
	var editinplace = ( canadmin ? "editinplace" : "")
	var id = response['geo_id']
	
	// newdiv
	var newdiv = $("<DIV ID='rrs-"+context+"-show-"+id+"' CLASS='main-slidable-div' "+
	" TITLE='"+Rembrandt.Util.shortenTitle(response['geo_name'])+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	var s = "<DIV CLASS='rrs-pageable'>" 

	s += "<H3>"+i18n[context+'-show'][lang]+"</H3>"				
	s += "<TABLE ID='rrs-"+context+"-show-table tablesorter' BORDER=0>"
	s += "<TR><TD>"+i18n['id'][lang]+":</TD><TD>"+id+"</TD></TR>";

	s += "<TR><TD>"+i18n['name'][lang]+":</TD><TD>";	
	s += "<DIV CONTEXT='"+context+"'  COL='geo_name' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['geo_name']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['woeid'][lang]+":</TD><TD>";	
	s += "<DIV CONTEXT='"+context+"'  COL='geo_woeid' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['geo_woeid']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['type'][lang]+":</TD><TD>";	
	s += "<DIV CONTEXT='"+context+"'  COL='geo_woeid_type' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['geo_woeid_type']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['place'][lang]+":</TD><TD>";	
	s += "<DIV CONTEXT='"+context+"'  COL='geo_woeid_place' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textarea'>"+col['geo_woeid_place']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['parent'][lang]+":</TD><TD>";	
	s += "<DIV CONTEXT='"+context+"'  COL='geo_woeid_parent' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['geo_woeid_parent']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['ancestors'][lang]+":</TD><TD>";	
	s += "<DIV CONTEXT='"+context+"'  COL='geo_woeid_ancestors' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['geo_woeid_ancestors']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['belongsto'][lang]+":</TD><TD>";	
	s += "<DIV CONTEXT='"+context+"'  COL='geo_woeid_belongsto' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['geo_woeid_belongsto']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['neighbors'][lang]+":</TD><TD>";	
	s += "<DIV CONTEXT='"+context+"'  COL='geo_woeid_neighbors' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['geo_woeid_neighbors']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['siblings'][lang]+":</TD><TD>";	
	s += "<DIV CONTEXT='"+context+"'  COL='geo_woeid_siblings' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['geo_woeid_siblings']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['children'][lang]+":</TD><TD>";	
	s += "<DIV CONTEXT='"+context+"'  COL='geo_woeid_children' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['geo_woeid_children']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['geonetpt02'][lang]+":</TD><TD>";	
	s += "<DIV CONTEXT='"+context+"'  COL='geo_woeid_geonetpt02' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['geo_woeid_geonetpt02']+"</DIV></TD></TR>";

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

function modalGeoscopeCreate(button) {
	
	var api_key= Rembrandt.Util.getApiKey()
	var role = Rembrandt.Util.getRole(button)
	var servlet_url = Rembrandt.Util.getServletEngineFromRole(role, 'geoscope')
	
	$.modal("<div id='modalGeoscopeCreate' class='rembrandt-modal'>"+
      "<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
	   "<div style='text-align:center; padding:10px;'>"+i18n['create_new_geoscope'][lang]+"</div>"+
	   "<div style='text-align:left; padding:3px;'>"+
		"<form><table style='border:0px;border-spacing:3px;'>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["name"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='geo_name'></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["woeid"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='geo_woeid'></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["woeid_type"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='woeid_type'></TD></TR>"+
		"<BR><BR>	"+
		"<div id='rrs-waiting-div' style='text-align:center;margin-bottom:5px'>"+
		"<div class='rrs-waiting-div-message'></div></div> "+ 
		"<div id='buttons' style='text-align:center;'>"+
		"<input type='button' id='YesButton' value='"+i18n["yes"][lang]+", "+i18n["create"][lang]+"'>"+
		"<input type='button' id='NoButton' value='"+i18n["no"][lang]+", "+i18n["cancel"][lang]+"'></div></form>"+
      "</div></div>	", {

		onShow: function modalShow(dialog) {
			// fill out table
			
			dialog.data.find("#YesButton").click(function(ev) {

				jQuery.ajax( {
					type:"POST", url:servlet_url,
					contentType:"application/x-www-form-urlencoded",
					data: "do=create&lg="+lang+
					"&geo_name="+dialog.data.find("#geo_name").val()+
					"&geo_woeid="+dialog.data.find("#geo_woeid").val()+
					"&geo_woeid_type="+dialog.data.find("#geo_woeid_type").val()+
					"&api_key="+api_key, 
					beforeSubmit: waitfunction(lang, dialog.data.find("#login-status")), 
					success: function(response) {
						if (response['status'] == -1) {
							errorMessageWaitingDiv(lang, response['message'])
							dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
							dialog.data.find("#buttons").show()	
						} else if (response['status'] == 0)  {
							showCustomMessageWaitingDiv(i18n['entity_created'][lang])
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

function modalDeleteGeoscope(button) {
	
	var context = "geoscope"
	
	genericDeleteModel({
		'context':context,
		'id': button.attr("ID"),
		'info':button.attr('TITLE'),
		'servlet_url': Rembrandt.Util.getServletEngineFromRole(Rembrandt.Util.getRole(button), context),
		'postdata' : "do=delete&id="+button.attr("ID")+"&lg="+lang+"&api_key="+Rembrandt.Util.getApiKey(),
		'success_message' : i18n['gescope_deleted'][lang]
	})
}	
	
