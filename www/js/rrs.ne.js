$().ready(function() {

	$('A.NE_CREATE').live("click", function(ev, ui) {
		ev.preventDefault();
		modalNECreate($(this))
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-ne-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
	});
	
	$('A.NE_LIST').live("click", function(ev, ui) {
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
			"restlet_url":Rembrandt.Util.getServletEngineFromRole(a_clicked.attr('ROLE'), "ne"),
			"postdata":"do=list&l=10&o=0&lg="+lang+"&api_key="+api_key,
			"divRender":generateNEListDIV, 
			"divRenderOptions":{},
			"sidemenu":null, 
			"sidemenuoptions":{}
		})					
	});
	
	$('A.NE_DELETE').live("click", function(ev, ui) {
		ev.preventDefault();
		modalNEDelete($(this))
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-ne-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
		if (divshown.attr('id') == 'rrs-ne-show-'+$(this).attr('ID')) {
			divshown.find("DIV.rrs-pageable").html(i18n['ne_deleted'][lang])
			hideSubmeuOnSideMenu($("#main-side-menu-section-ne"))
		}
	})	

	$('A.NE_SHOW').live("click", function(ev, ui) {
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
			"restlet_url":Rembrandt.Util.getServletEngineFromRole(a_clicked.attr('ROLE'), "ne"),
			"postdata":"do=show&id="+id+"&lg="+lang+	"&api_key="+api_key,
			"divRender":generateNEShowDIV, 
			"divRenderOptions":{},
			"sidemenu":"ne", 
			"sidemenuoptions":{"id":id, "ne_name":title}
		})		
	});
});		

/*************************/
/*** content creation ****/
/*************************/

/** generate NE list DIV */
function generateNEListDIV(response, su, role, options) {

	var context = "ne"
	var res = response['result']
	var canadmin = (su || role.toLowerCase() == "col-admin")
	var editinplace = (canadmin  ? ""+editinplace+"" : "")

	// newdiv
	var newdiv = $("<DIV ID='rrs-entity-list' CLASS='main-slidable-div' "+
	" TITLE='"+i18n[context+'_list'][lang]+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	// Set pageable area for paging reposition
	t = "<DIV CLASS='rrs-pageable'>" 
	
	// pager
	t += createPagerNavigation({
		"context":context, 
		"contexts":"nes", 
		"response":response,
		"role":role, 
		"allowedSearchableFields":{
			"ne_id":i18n['id'][lang], 
			"ne_name":i18n['name'][lang],
			"ne_lang":i18n['lang'][lang],
			"ne_category":i18n['ne_category'][lang], 
			"ne_type":i18n['ne_type'][lang],
			"ne_subtype":i18n['ne_subtype'][lang],
			"ne_entity":i18n['entity'][lang]
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
	t += "<TH>"+i18n['lang'][lang]+"</TH>";
	t += "<TH>"+i18n['ne_category'][lang]+"</TH>";
	t += "<TH>"+i18n['ne_type'][lang]+"</TH>";
	t += "<TH>"+i18n['ne_subtype'][lang]+"</TH>";
	t += "<TH>"+i18n['ne_entity'][lang]+"</TH>";
	if (su) {t += "<TH></TH>";}
	t += "</TR></THEAD><TBODY>"
	
	for(i in res) {
		
		var id = res[i]['ne_id']
		
		t += "<TR><TD><INPUT TYPE='CHECKBOX' CLASS='sec-checkbox'></TD>"
		t += "<TH><A CLASS='"+context.toUpperCase()+"_SHOW' ROLE='"+role+"' "+
		 "TARGET='rrs-"+context+"-show-"+id+"' "+
		 "TITLE='"+res[i]['ent_name']+"' HREF='#'>"+id+"</A></TH>"

		// begin of a saskia object							
		t += "<TD><DIV CONTEXT='"+context+"'  COL='ne_name' COL2='nen_name' ID='"+id+"' "
		t += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"+res[i]['ne_name']['nen_name']+
		"</DIV></DIV></TD>"		
		// end of a saskia object

		t += "<TD><DIV CONTEXT='"+context+"'  COL='ne_lang' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['ne_lang']+"</DIV></TD>"	
		
		// begin of a saskia object							
		t += "<TD><DIV CONTEXT='"+context+"'  COL='ne_category' COL2='nec_category' ID='"+id+"' "
		t += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"+res[i]['ne_category']['nec_category']+
		"</DIV></DIV></TD>"		
		// end of a saskia object
		
		// begin of a saskia object							
		t += "<TD><DIV CONTEXT='"+context+"'  COL='ne_type' COL2='net_type' ID='"+id+"' "
		t += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"+res[i]['ne_type']['net_type']+
		"</DIV></DIV></TD>"		
		// end of a saskia object

		// begin of a saskia object							
		t += "<TD><DIV CONTEXT='"+context+"'  COL='ne_subtype' COL2='nes_subtype' ID='"+id+"' "
		t += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"+res[i]['ne_subtype']['nes_subtype']+
		"</DIV></DIV></TD>"		
		// end of a saskia object
		
		// begin of a saskia object							
		t += "<TD><DIV CONTEXT='"+context+"'  COL='ne_entity' COL2='ent_dbpedia_resource' ID='"+id+"' "
		t += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"+res[i]['ne_entity']['ent_dbpedia_resource']+
		"</DIV></DIV></TD>"		
		// end of a saskia object
		if (su) {
			t += "<TD><A HREF='#' ROLE='"+role+"' ID='"+id+"' TITLE='"+res[i]['geo_name']+"' CLASS='"+context+"_DELETE main-button'>"
			t += "<SPAN>"+i18n['delete'][lang]+"...</SPAN></A></TD>";
		}
		t += "</TR>"
	}	

	t += "<TFOOT><TR><TD><INPUT TYPE='CHECKBOX' CLASS='main-checkbox'></TD>"
	t += "<TH>"+i18n['all'][lang]+"</TH>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='ne_name' "
	t += "CLASS='"+editinplace+" autocompletetextfield group'></DIV></TD>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='ne_lang' "
	t += "CLASS='"+editinplace+" textfield group'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='ne_category' "
	t += "CLASS='"+editinplace+" autocompletetextfield group'></DIV></TD>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='ne_type' "
	t += "CLASS='"+editinplace+" autocompletetextfield group'></DIV></TD>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='ne_subtype' "
	t += "CLASS='"+editinplace+" autocompletetextfield group'></DIV></TD>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='ne_entity' "
	t += "CLASS='"+editinplace+" autocompletetextfield group'></DIV></TD>"
	if (su) {t += "<TD></TD>"}
	t += "</TR></TFOOT>"
	t += "</TABLE></DIV>"
	t += "</DIV></DIV>"
	
	newdiv.append(t)
	return newdiv
}

function generateNEShowDIV(response, su, role, options) {

	var context = "ne"
	var canadmin = (su || role.toLowerCase() == "col-admin")
	var editinplace = ( canadmin ? "editinplace" : "")
	var id = response['ne_id']
	
	// newdiv
	var newdiv = $("<DIV ID='rrs-"+context+"-show-"+id+"' CLASS='main-slidable-div' "+
	" TITLE='"+Rembrandt.Util.shortenTitle(response['ne_name']['nen_name'])+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	var s = "<DIV CLASS='rrs-pageable'>" 

	s += "<H3>"+i18n[context+'-show'][lang]+"</H3>"				
	s += "<TABLE ID='rrs-"+context+"-show-table tablesorter' BORDER=0>"
	s += "<TR><TD>"+i18n['id'][lang]+":</TD><TD>"+id+"</TD></TR>";

	s += "<TR><TD>"+i18n['name'][lang]+":</TD><TD>";	
	s += "<DIV CONTEXT='"+context+"' COL='ne_name' COL2='nen_name' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"
	s += response['ne_name']['nen_name']+"</DIV></DIV></TD></TR>";

	s += "<TR><TD>"+i18n['lang'][lang]+":</TD><TD>";
	s += "<DIV CONTEXT='"+context+"' COL='ne_lang' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+response['ne_lang']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['category'][lang]+":</TD><TD>";
	s += "<DIV CONTEXT='"+context+"' COL='ne_category' COL2='nec_category' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"
	s +=  response['ne_category']['nec_category']+"</DIV></DIV></TD></TR>";

	s += "<TR><TD>"+i18n['type'][lang]+":</TD><TD>";
	s += "<DIV CONTEXT='"+context+"' COL='ne_type' COL2='net_type' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"
	s +=  response['ne_type']['net_type']+"</DIV></DIV></TD></TR>";

	s += "<TR><TD>"+i18n['subtype'][lang]+":</TD><TD>";
	s += "<DIV CONTEXT='"+context+"' COL='ne_subtype' COL2='nes_subtype' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"
	s +=  response['ne_subtype']['nes_subtype']+"</DIV></DIV></TD></TR>";

	s += "<TR><TD>"+i18n['entity'][lang]+":</TD><TD>";
	s += "<DIV CONTEXT='"+context+"' COL='ne_entity' COL2='ent_name' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"
	s +=  response['ne_entity']['ent_name']+"</DIV></DIV></TD></TR>";

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
/*************/

function modalNECreate (button) {
	
	var api_key= Rembrandt.Util.getApiKey()
	var role = Rembrandt.Util.getRole(button)
	var servlet_url = Rembrandt.Util.getServletEngineFromRole(role, 'ne')
	
	// admin or user
	
	$.modal("<div id='modalCreateNE' class='rembrandt-modal'>"+
      "<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
	  "<div style='text-align:center; padding:10px;'>"+i18n['create_new_ne'][lang]+"</div>"+
	  "<div style='text-align:left; padding:3px;'>"+
		"<form class='selectNEclassForm'>"+
		"<TABLE><TR><TD ALIGN=RIGHT>"+i18n["terms"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='ne_name'></TD></TR>"+
		"<TABLE><TR><TD ALIGN=RIGHT>"+i18n["lang"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='3' ID='ne_lang'></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["category"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><select size=1 id='selectNEclass' onChange='fillNEtypes($(this));'></select></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["type"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><select size=1 id='selectNEtype' onChange='fillNEsubtypes($(this));' disabled></select></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["subtype"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><select size=1 id='selectNEsubtype' disabled></select></TD></TR>"+
		"<TR><TD ALIGN=RIGHT>"+i18n["entity"][lang]+"*</TD>"+
		"<TD ALIGN=LEFT><INPUT TYPE='TEXT' ID='entity' SIZE=20></TD></TR>"+
		"<BR><BR>"+
		"<div id='rrs-waiting-div' style='text-align:center;margin-bottom:5px'>"+
		"<div class='rrs-waiting-div-message'></div></div> "+ 
		"<div id='buttons' style='text-align:center;'><input type='button' id='YesButton' value='"+
		i18n["yes"][lang]+", "+i18n["create"][lang]+"'><input type='button' id='NoButton' value='"+
		i18n["no"][lang]+", "+i18n["cancel"][lang]+"'></div></form></div></div>	", {
			
		onShow: function modalShow(dialog) {
			// fill out table
			fillNEclasses(dialog.data.find("#selectNEclass"))
			
			// make it use the entity picker
			dialog.data.find("#entity").autocomplete(restlet_dbosuggestion_url, {
				minChars:3, dataType: "json", 
				mustMatch: false, autoFill: false, matchContains: false,
				multipleSeparator:"",
				extraParams:{"t" : "entity"},
				parse: theParse, 
				formatItem: theFormatItem,
				formatMatch: theFormatMatch,
				formatResult: theFormatResult
			})
			dialog.data.find("#entity").result(theResult)
			
			dialog.data.find("#YesButton").click(function(ev) {

				var selectedClass = dialog.data.find("#selectNEclass option:selected").text();
				if (selectedClass == "--") {selectedClass = 'null';}
				var selectedType = dialog.data.find("#selectNEtype option:selected").text();
				if (selectedType === undefined || selectedType == "--") {selectedType = 'null'}
				var selectedSubtype = dialog.data.find("#selectNEsubtype option:selected").text();
				if (selectedSubtype === undefined || selectedSubtype == "--") {selectedSubtype = 'null'}
				var selectedEntity = dialog.data.find("#entity").text();
				if (selectedEntity === undefined || selectedEntity == "--") {selectedEntity = 'null'}
		
				jQuery.ajax( {
					type:"POST", url:servlet_url,
					contentType:"application/x-www-form-urlencoded",
					data: "do=create&lg="+lang+"&ne_name="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(ne_name))+
					"&c1="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(selectedClass))+
					"&c2="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(selectedType))+
					"&c3="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(selectedSubtype))+
					"&ent="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(selectedEntity))+
					"&api_key="+api_key,					 
					beforeSubmit: Rembrandt.Waiting.show(),

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


// admin only
function modalNEDelete(button) {
	
	var context = "ne"
	
	genericDeleteModel({
		'context':context,
		'id': button.attr("ID"),
		'info':button.attr('TITLE'),
		'servlet_url': Rembrandt.Util.getServletEngineFromRole(Rembrandt.Util.getRole(button), context),
		'postdata' : "do=delete&id="+button.attr("ID")+"&lg="+lang+"&api_key="+Rembrandt.Util.getApiKey(),
		'success_message' : i18n['ne_deleted'][lang]
	})
}	
