$().ready(function() {
	
	// 'new collection' button action, opens modal dialog 
	$('A.COLLECTION_CREATE').live("click", function(ev, ui) {
		ev.preventDefault();
		modalCollectionCreate($(this))
		
		// let's refresh the content. How? triggering the admin-pager link if the 
		// visible div is for a collection list or collection main page
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-homepage-collection') {
			displayBodyOfSaskia()				
		} 
		if (divshown.attr('id') == 'rrs-collection-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
	})

	// 'delete collection' button action, opens modal dialog 
	$('A.COLLECTION_DELETE').live("click", function(ev, ui) {
		ev.preventDefault();
		modalCollectionDelete($(this))
		// let's refresh the content. How? triggering the admin-pager link if the 
		// visible div is for a collection list or collection main page
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-homepage-collection') {
			displayBodyOfSaskia()				
		} 
		if (divshown.attr('id') == 'rrs-collection-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
	})
		
	// List collections for admin and/or user
	$('A.COLLECTION_LIST').live("click", function(ev, ui) {

		ev.preventDefault();
		var a_clicked = $(this)
		var api_key = getAPIKey()
			
		showSlidableDIV({
			"title": (a_clicked.attr("title") ? a_clicked.attr("title") : a_clicked.text()),
			"target":a_clicked.attr("TARGET"),
			"role":a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":true,
			"restlet_url":getServletEngineFromRole(a_clicked.attr('ROLE'), "collection"),
			"postdata":"do=list&l=10&o=0&lg="+lang+"&api_key="+api_key,
			"divcreator":generateCollectionListDIV, 
			"divcreatoroptions":{},
			"sidemenu":null, 
			"sidemenuoptions":{}
		})					
	});
		
// TODO
	$('A.COLLECTION_REFRESH_STATS').live("click", function(ev, ui) {
		ev.preventDefault();
		var a_clicked = $(this)
		var col_id = a_clicked.attr("id")
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : a_clicked.text())
		var api_key = getAPIKey()
		var role = a_clicked.attr('ROLE')
		var servlet_url = getServletEngineFromRole(role, "collection")

		a_clicked.attr('disabled',true)
		a_clicked.toggleClass("main-button","main-button-disabled")

		jQuery.ajax({
			type:"POST", url:restlet_saskia_collection_url, dataType:"json",
			data:"lg="+lang+"&do=refreshstats&id="+col_id+"&api_key="+getAPIKey(),
			beforeSubmit:  button.find("SPAN").html(waitmessage(lang, i18n['refreshing_collection'][lang])), 
			success:function(response) {
				if (response["status"] == -1) {
					errorMessageWaitingDiv(lang, response)
					button.toggleClass('disabled',false)
					button.toggleClass("main-button-disabled","main-button")
					button.find("SPAN").html(i18n['refresh'][lang])

				} else if (response["status"] == 0) {

					button.toggleClass("adminbuttondisabled","adminbutton")
					button.find("SPAN").html(i18n['OK'][lang])			
				}	
			},
			error:function(response) {
				// put it back
				errorMessageWaitingDiv(lang, response)
				button.toggleClass('disabled',false)
				button.toggleClass("main-button-disabled","main-button")
			}
		})
	})

	$('A.COLLECTION_RDOC_LIST').live("click", function(ev, ui) {
		ev.preventDefault();
		var a_clicked = $(this)
		var col_id = a_clicked.attr("id")
		var api_key = getAPIKey()
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : a_clicked.text())

		showSlidableDIV({
			"title": title,
			"target":a_clicked.attr("TARGET"),
			"role":a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":true,
			"restlet_url":getServletEngineFromRole(a_clicked.attr('ROLE'), "rdoc"),
			"postdata":"do=list&ci="+col_id+"&l=10&o=0&lg="+lang+	"&api_key="+api_key,
			"divcreator":generateCollectionRdocListDIV, 
			"divcreatoroptions":{},
			"sidemenu":"collection", 
			// only update col_id, no col_name -> it will change side menu header. Leave that to COLLECTION_SHOW
			"sidemenuoptions":{"id":col_id}//, "col_name":title}
		})	
	})

	// List source docs
	$('A.COLLECTION_SDOC_LIST').live("click", function(ev, ui) {
		ev.preventDefault();
		var a_clicked = $(this)
		var col_id = a_clicked.attr("id")
		var api_key = getAPIKey()
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : a_clicked.text())

		showSlidableDIV({
			"title": title,
			"target":a_clicked.attr("TARGET"),
			"role":a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":true,
			"restlet_url":getServletEngineFromRole(a_clicked.attr('ROLE'), "sdoc"),
			"postdata":"do=list&ci="+col_id+"&l=10&o=0&lg="+lang+	"&api_key="+api_key,
			"divcreator":generateCollectionSdocListDIV, 
			"divcreatoroptions":{},
			"sidemenu":"collection", 
			"sidemenuoptions":{"id":col_id}//, "col_name":title}
		})	
	})
	
		// List tasks
	$('A.COLLECTION_TASK_LIST').live("click", function(ev, ui) {
		ev.preventDefault();
		var a_clicked = $(this)
		var col_id = a_clicked.attr("id")
		var api_key = getAPIKey()
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : a_clicked.text())

		showSlidableDIV({
			"title": title,
			"target":a_clicked.attr("TARGET"),
			"role":a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":true,
			"restlet_url":getServletEngineFromRole(a_clicked.attr('ROLE'), "task"),
			"postdata":"do=list&ci="+col_id+"&l=10&o=0&lg="+lang+	"&api_key="+api_key,
			"divcreator":generateCollectionSdocListDIV, 
			"divcreatoroptions":{},
			"sidemenu":"collection", 
			"sidemenuoptions":{"id":col_id}//, "col_name":title}
		})	
	})

		// show collection, get details ans present in main-body div
	$('A.COLLECTION_SHOW').live("click", function(ev, ui) {
		ev.preventDefault();
		var a_clicked = $(this)
		var col_id = a_clicked.attr("ID")
		var api_key = getAPIKey()
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : a_clicked.text())
			
		showSlidableDIV({
			"title": title,
			"target":a_clicked.attr("TARGET"),
			"role":a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":true,
			"restlet_url":getServletEngineFromRole(a_clicked.attr('ROLE'), "collection"),
			"postdata":"do=show&id="+col_id+"&lg="+lang+	"&api_key="+api_key,
			"divcreator":generateCollectionShowDIV, 
			"divcreatoroptions":{},
			"sidemenu":"collection", 
			"sidemenuoptions":{"id":col_id, "col_name":title}
		})		
	});

})

/*************************/
/*** content creation ****/
/*************************/

/** generate collection list DIV */
function generateCollectionListDIV(response, su, role, options) {

	var context = "collection"
	var res = response['result']
	var canadmin = (su || role.toLowerCase() == "col-admin")
	var editinplace = (canadmin  ? "editinplace" : "")

	// newdiv
	var newdiv = $("<DIV ID='rrs-collection-list' CLASS='main-slidable-div' "+
	" TITLE='"+i18n[context+'_list'][lang]+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	// Set pageable area for paging reposition
	t = "<DIV CLASS='rrs-pageable'>" 
	
	// pager
	t += createPagerNavigation({
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
	})
	
		
	// buttons
	t += "<DIV CLASS='rrs-buttonrow'>"
	if (su) {
		// goes to modal, don't need target
		t += "<A HREF='#' CLASS='"+context.toUpperCase()+"_CREATE main-button' TITLE='"+i18n['create_new_'+context][lang]+"' "
		t += "ROLE='"+role+"'><SPAN>"
		t += i18n['create_new_'+context][lang]+"</SPAN></A>"
	}
	t += "</DIV>"

	// table - 6 cols
	t += "<DIV>"
	t += "<TABLE ID='rrs-"+context+"-list-table' CLASS='tablesorter' >"
	t += "<THEAD><TR><TD><INPUT TYPE='CHECKBOX' CLASS='main-checkbox'></TD>"
	t += "<TH>"+i18n['id'][lang]+"</TH>";
	t += "<TH>"+i18n['collection'][lang]+"</TH>";
	t += "<TH>"+i18n['lang'][lang]+"</TH>";
	if (su) {t += "<TH>"+i18n['owner'][lang]+"</TH>";}
	if (canadmin) {t += "<TH>"+i18n['permissions'][lang]+"</TH>";}
	t += "<TH>"+i18n['comment'][lang]+"</TH>";
	if (canadmin) {t += "<TD></TD>";}
	t += "</TR></THEAD><TBODY>"
	
	for(i in res) {
		
		var id = res[i]['col_id']
		
		t += "<TR><TD><INPUT TYPE='CHECKBOX' CLASS='sec-checkbox'></TD>"
		t += "<TH><A CLASS='"+context.toUpperCase()+"_SHOW' ID='"+id+"' "+
		"TARGET='rrs-"+context+"-show-"+id+"' TITLE='"+
		res[i]['col_name']+"' HREF='#' ROLE='"+role+"'>"+id+"</A></TH>"
		t += "<TD><DIV CONTEXT='"+context+"'  COL='col_name' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['col_name']+"</DIV></TD>"	
		t += "<TD><DIV CONTEXT='"+context+"'  COL='col_lang' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textfield'>"+res[i]['col_lang']+"</DIV></TD>"	
		// begin of a saskia object							
		if (su) {
			t += "<TD><DIV CONTEXT='"+context+"'  COL='col_owner' COL2='usr_login' ID='"+id+"' "
			t += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"+res[i]['col_owner']['usr_login']+
			"</DIV></DIV></TD>"		
		}
		// end of a saskia object
		if (canadmin) {
			t += "<TD><DIV CONTEXT='"+context+"'  COL='col_permission' ID='"+id+"' "
			t += "CLASS='"+editinplace+" textfield'>"+res[i]['col_permission']+"</DIV></TD>"		
		}
		t += "<TD><DIV CONTEXT='"+context+"'  COL='col_comment' ID='"+id+"' "
		t += "CLASS='"+editinplace+" textarea'>"+res[i]['col_comment']+"</DIV></TD>"
		if (canadmin) {
			t += "<TD><A HREF='#' CLASS='"+context.toUpperCase()+"_DELETE main-button' ROLE='"+role+"' "
			t += "ID='"+id+"' TITLE='"+res[i]['col_name']+"'><SPAN>"
			t += i18n['delete'][lang]+"...</SPAN></A></TD>";
		}
		t += "</TR>"
	}	
	
	t += "<TFOOT><TR><TD><INPUT TYPE='CHECKBOX' CLASS='main-checkbox'></TD>"
	t += "<TH>"+i18n['all'][lang]+"</TH>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='col_name' "
	t += "CLASS='"+editinplace+" textfield group'></DIV></TD>"
	t += "<TD><DIV CONTEXT='"+context+"'  COL='col_lang' "
	t += "CLASS='"+editinplace+" textfield group'></DIV></TD>"
	if (su) {
		t += "<TD><DIV CONTEXT='"+context+"'  COL='col_owner' "
		t += "CLASS='"+editinplace+" autocompletetextfield group'></DIV></TD>"
	}
	if (canadmin) {
		t += "<TD><DIV CONTEXT='"+context+"'  COL='col_permission' "
		t += "CLASS='"+editinplace+" textfield group'></DIV></TD>"
	}
	t += "<TD><DIV CONTEXT='"+context+"'  COL='col_comment' "
	t += "CLASS='"+editinplace+" textfield group'></DIV>"
	if (canadmin) {t += "<TD></TD>"}
	t += "</TR></TFOOT>"
	t += "</TABLE></DIV>"
	t += "</DIV>"

	newdiv.append(t)
	return newdiv
}

/** generate collection show DIV */
function generateCollectionShowDIV(col, su, role, options) {

	var context = "collection"
	var number_sdocs = col['number_sdocs']
	var number_rdocs = col['number_rdocs']
	var canadmin = (su || role.toLowerCase() == "col-admin")
	var editinplace = ( canadmin ? "editinplace" : "")
	
	var id = col['col_id']
	
	// newdiv
	var newdiv = $("<DIV ID='rrs-"+context+"-show-"+id+"' CLASS='main-slidable-div' "+
	" TITLE='"+shortenTitle(col['col_name'])+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	var s = "<DIV CLASS='rrs-pageable'>" 

	s += "<H3>"+i18n[context+'-show'][lang]+"</H3>"				
	s += "<TABLE ID='rrs-"+context+"-show-table tablesorter' BORDER=0>"
	s += "<TR><TD>"+i18n['id'][lang]+":</TD><TD>"+id+"</TD></TR>";

	s += "<TR><TD>"+i18n['name'][lang]+":</TD><TD>";	
	s += "<DIV CONTEXT='"+context+"'  COL='col_name' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['col_name']+"</DIV></TD></TR>";

	s += "<TR><TD>"+i18n['lang'][lang]+":</TD><TD>";
	s += "<DIV CONTEXT='"+context+"'  COL='col_lang' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['col_lang']+"</DIV></TD></TR>";

	if (su) {
		t += "<TR><TD><DIV CONTEXT='"+context+"'  COL='col_owner' COL2='usr_login' ID='"+id+"' "
		t += "CLASS='"+editinplace+" autocompletetextfield'><DIV CLASS='saskia_object_tag'>"+col['col_owner']['usr_login']
		t += "</DIV></DIV></TD></TR>"		
	}
		
	s += "<TR><TD>"+i18n['comment'][lang]+":</TD>";
	s += "<DIV CONTEXT='"+context+"'  COL='col_comment' ID='"+id+"' "
 	s += "CLASS='"+editinplace+" textfield'>"+col['col_comment']+"</DIV></TD></TR>";
	
	if (canadmin) {
		s += "<TR><TD>"+i18n['permissions'][lang]+":</TD>";
		s += "<DIV CONTEXT='"+context+"'  COL='col_permission' ID='"+id+"' "
		s += "CLASS='"+editinplace+" textfield'>"+col['col_permission']+"</DIV></TD></TR>";
	}
	
	s += "<TR><TD><A HREF='#' CLASS='COLLECTION_SDOC_LIST slide-vertically-link' "+
	   "TARGET='rrs-collection-sdoc-list-"+col['col_id']+"' ID='"+col['col_id']+"' ROLE='"+role+"'>"+
		i18n['sdocs'][lang]+"</A>:</TD><TD>"+number_sdocs+"</TD></TR>";
	s += "<TR><TD><A HREF='#' CLASS='COLLECTION_RDOC_LIST slide-vertically-link' "+
		"TARGET='rrs-collection-rdoc-list-"+col['col_id']+"' ID='"+col['col_id']+"' ROLE='"+role+"'>"+
		i18n['rdocs'][lang]+"</A>:</TD><TD>"+number_rdocs+"</TD></TR>";
	if (canadmin) {
		s += "<TR><TD><A HREF='#' CLASS='COLLECTION_TASK_LIST slide-vertically-link' "+
		"TARGET='rrs-collection-task-list-"+col['col_id']+"' ID='"+col['col_id']+"' ROLE='"+role+"'>"+
		i18n['tasks'][lang]+"</A>:</TD><TD>0</TD></TR>";
	}
	s += "</TABLE>"; 
	
	s += "<A HREF='#' CLASS='main-button'><SPAN>"+i18n['stats'][lang]+"</SPAN></A>";
	
	if (canadmin) {
		
		s += "<A HREF='#' CLASS='SDOC_CREATE main-button' ID='"+id+"' TARGET='rrs-collection-sdoc-create-"+id+"' "
		s += " TITLE='"+i18n['create_new_sdoc'][lang]+"' ROLE='"+role+"'><SPAN>"
		s += i18n['create_new_'+context][lang]+"</SPAN></A>"	
		
		s += "<A HREF='#' CLASS='SDOC_IMPORT main-button' ID='"+id+"' TARGET='rrs-collection-sdoc-import-"+id+"' "
		s += " TITLE='"+i18n['sdoc_import'][lang]+"' ROLE='"+role+"'><SPAN>"
		s += i18n['sdoc_import'][lang]+"</SPAN></A>"	

		s += "<A HREF='#' CLASS='SDOC_EXPORT main-button' ID='"+id+"' TARGET='rrs-collection-sdoc-export-"+id+"' "
		s += " TITLE='"+i18n['sdoc_export'][lang]+"' ROLE='"+role+"'><SPAN>"
		s += i18n['sdoc_export'][lang]+"</SPAN></A>"	
		
		s += "<A HREF='#' CLASS='SDOC_DELETE main-button' ID='"+id+"' "
		s += " TITLE='"+i18n['delete'][lang]+"' ROLE='"+role+"'><SPAN> "
		s += i18n['delete'][lang]+"...</SPAN></A></TD>";
	}
	s += "</DIV>"
	
	newdiv.append(s)
	return newdiv
}

/** fill source doc info in page */
function generateCollectionSdocListDIV(response, su, role, options) {

	var context = "sdoc"
	var res = response['result']
	var perms = response['perms']
	var col_id = response['col_id']
	var canadmin = (su || perms['uoc_can_admin'])
	var editinplace = ( canadmin ? "editinplace" : "")
	
	// newdiv
	// keep rrs-collection prefix so that collection sub-menu pops up
	
	var newdiv = $("<DIV ID='rrs-collection-sdoc-list' CLASS='main-slidable-div' "+
	" TITLE='"+i18n[context+'_list'][lang]+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	// Set pageable area for paging reposition
	t = "<DIV CLASS='rrs-pageable'>" 
	
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
		'render':'generateCollectionSdocListDIV' // if not,  it'll default to generateSdocListDIV
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
}

/** fill tagged doc info in page */
function generateCollectionRdocListDIV(response, su, role, options) {
	var context = "rdoc"
	var res = response['result']
	var perms = response['perms']
	var canadmin = (su || perms['uoc_can_admin'])
	var editinplace = ( canadmin ? "editinplace" : "")
	
	// newdiv
	// keep rrs-collection prefix so that collection sub-menu pops up
	
	var newdiv = $("<DIV ID='rrs-collection-rdoc-list' CLASS='main-slidable-div' "+
	" TITLE='"+i18n[context+'_list'][lang]+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	// Set pageable area for paging reposition
	t = "<DIV CLASS='rrs-pageable'>" 
	
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
		'render':'generateCollectionRdocListDIV' 
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

}

/**************/
/*** modal ****/
/**************/

function modalCollectionCreate(button) {
	
	var api_key= getAPIKey()
	var role = getRole(button)
	var servlet_url = getServletEngineFromRole(role, 'collection')
	
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
					data: "do=create&col_name="+urlencode(encode_utf8(col_name))+
					      "&col_comment="+urlencode(encode_utf8(col_comment))+
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
}
// creates a modal window to switch collection. It will ask the server for user permissions 
// in those collections.  

// it has a spaecial confirmation, keep it away from generic delete model template
function modalCollectionDelete(button) {
	
	var api_key= getAPIKey()
	var ci = button.attr("ID")
	var role = getRole(button)
	var servlet_collection_url = getServletEngineFromRole(role, 'collection')
	var servlet_user_url = getServletEngineFromRole(role, 'user')
	
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
				data:"do=auth&lg="+lang+"&p="+urlencode(hex_md5(password))+
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
	});
}