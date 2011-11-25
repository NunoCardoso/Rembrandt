
$().ready(function() {
	
	$('A.RDOC_CREATE').live("click", function(ev, ui) {
		ev.preventDefault();
		var a_clicked = $(this)
		var api_key = getAPIKey()
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : a_clicked.text())
		var col_id = a_clicked.attr('id')	
		
		showSlidableDIV({
			"title": title,
			"target":a_clicked.attr("TARGET"),
			"role":a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":false,
			"restlet_url":null,
			"postdata":null,
			"divcreator":generateRdocCreateDIV, 
			"divcreatoroptions":{'col_id':col_id},
			"sidemenu":null, 
			"sidemenuoptions":{}
		})				
	})

	$('A.RDOC_DELETE').live("click", function(ev, ui) {
		ev.preventDefault();
		modalRdocDelete($(this))
		// let's refresh the content. How? triggering the admin-pager link if the 
		// visible div is for a collection list or collection main page
		var divshown = $('DIV.main-slidable-div:visible')
	 
		if (divshown.attr('id') == 'rrs-collection-rdoc-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
	})
		
	// click to show a doc into a new/existing tab
	$('A.RDOC_SHOW').live("click", function(ev, ui) {

		ev.preventDefault();
		var a_clicked = $(this)
		var doc_id = a_clicked.attr("ID")
		var doc_original_id = a_clicked.attr("DOC_ORIGINAL_ID")
		var api_key = getAPIKey()
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : doc_original_id)
		 	
		showSlidableDIV({
			"title": title,
			"target":a_clicked.attr("TARGET"),
			"role":a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":true,
			"restlet_url":getServletEngineFromRole(a_clicked.attr('ROLE'), "rdoc"),
			"postdata":"do=show&doc_id="+doc_id+"&lg="+lang+	"&api_key="+api_key,
			"divcreator":generateRdocShowDIV, 
			"divcreatoroptions":{},
			"sidemenu":"rdoc", 
			"sidemenuoptions":{'id':doc_id}
		})		
	})
									
	$('A.RDOC_METADATA').live("click", function(ev, ui) {

		ev.preventDefault();
		var a_clicked = $(this)
		var doc_id = a_clicked.attr("ID")
		var doc_original_id = a_clicked.attr("DOC_ORIGINAL_ID")
		var api_key = getAPIKey()
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : doc_original_id)
		 	
		showSlidableDIV({
			"title": title,
			"target":a_clicked.attr("TARGET"),
			"role":a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":true,
			"restlet_url":getServletEngineFromRole(a_clicked.attr('ROLE'), "rdoc"),
			"postdata":"do=metadata&doc_id="+doc_id+"&lg="+lang+	"&api_key="+api_key,
			"divcreator":generateRdocMetadataDIV, 
			"divcreatoroptions":{},
			"sidemenu":"rdoc", 
			"sidemenuoptions":{'id':doc_id}
		})		
	})	

	$('A.SDOC_CREATE').live("click", function(ev, ui) {
		ev.preventDefault();
		var a_clicked = $(this)
		var api_key = getAPIKey()
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : a_clicked.text())
		var col_id = a_clicked.attr('id')	
		
		showSlidableDIV({
			"title": title,
			"target":a_clicked.attr("TARGET"),
			"role":a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":false,
			"restlet_url":null,
			"postdata":null,
			"divcreator":generateSdocCreateDIV, 
			"divcreatoroptions":{'col_id':col_id},
			"sidemenu":null, 
			"sidemenuoptions":{}
		})				
	})
	
	$('A.SDOC_CREATE_SUBMIT').live("click", function(ev, ui) {
		var a_clicked = ev.getTarget()
		var form = a_clicked.parents("FORM")
		var role = getRole(a_clicked)
		var servlet_url = getServletEngineFromRole(role, "sdoc")
		var api_key = getAPIKey()
		
		jQuery.ajax({ type:"POST", dataType:'json', url:servlet_url,
			contentType:"application/x-www-form-urlencoded",
			data: "do=create&sdoc_original_id="+urlencode(encode_utf8(form.find('#sdoc_original_id').val()))+
				   "&sdoc_comment="+urlencode(encode_utf8(form.find('#sdoc_comment').val()))+
				   "&sdoc_lang="+form.find('#sdoc_lang')+
					"&sdoc_content="+urlencode(encode_utf8(form.find('#sdoc_content').val()))+			
					"&lg="+lang+"&api_key="+api_key, 
			beforeSubmit: waitfunction(lang, form.find("#status")), 
			success: function(response) {
				if (response['status'] == -1) {
					form.find("#status").html(response['message'])
					form.find("A.SDOC_CREATE_SUBMIT SPAN").html(i18n['retry'][lang])			
				} else if (response['status'] == 0)  {
					form.find("#status").html(i18n['sdoc_created'][lang])
					form.find("A.SDOC_CREATE_SUBMIT").attr('disabled', true)
					form.find("A.SDOC_CREATE_SUBMIT").toggleClass('main-button', 'main-button-disabled')
					form.find("A.SDOC_CREATE_SUBMIT SPAN").html(i18n['OK'][lang])			
				}
			},
			error:function(response) {
				form.find("#status").html(response['message'])					
			}
		})
	})

	$('A.SDOC_DELETE').live("click", function(ev, ui) {
		ev.preventDefault();
		modalSdocDelete($(this))
		// let's refresh the content. How? triggering the admin-pager link if the 
		// visible div is for a collection list or collection main page
		var divshown = $('DIV.main-slidable-div:visible')
	 
		if (divshown.attr('id') == 'rrs-collection-sdoc-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
	})	
	
	// click to show a doc into a new/existing tab
	$('A.SDOC_SHOW').live("click", function(ev, ui) {

		ev.preventDefault();
		var a_clicked = $(this)
		var sdoc_id = a_clicked.attr("ID")
		var sdoc_original_id = a_clicked.attr("SDOC_ORIGINAL_ID")
		var api_key = getAPIKey()
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : sdoc_original_id)
		 	
		showSlidableDIV({
			"title": title,
			"target":a_clicked.attr("TARGET"),
			"role":a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":true,
			"restlet_url":getServletEngineFromRole(a_clicked.attr('ROLE'), "sdoc"),
			"postdata":"do=show&sdoc_id="+sdoc_id+"&lg="+lang+	"&api_key="+api_key,
			"divcreator":generateSdocShowDIV, 
			"divcreatoroptions":{},
			"sidemenu":"sdoc", 
			"sidemenuoptions":{'id':sdoc_id}
		})		
	})

})	

/*************************/
/*** content creation ****/
/*************************/

function generateSdocCreateDIV(response, su, role, options) {

	// response = null
	var context = "sdoc"
	var canadmin = (su || role.toLowerCase() == "col-admin")
	var editinplace = ( canadmin ? "editinplace" : "")
	
	var col_id = options.id
	
	// newdiv
	var newdiv = $("<DIV ID='rrs-"+context+"-create-"+id+"' CLASS='main-slidable-div' "+
	" TITLE='"+i18n[context+'_create'][lang]+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	s += "<FORM>"
	s += "<TABLE ID='rrs-"+context+"-create-table tablesorter' BORDER=0>"

	s += "<TR><TD>"+i18n['originalid'][lang]+":</TD>";	
	s += "<TD><INPUT TYPE='TEXT' name='sdoc_original_id' ID='sdoc_original_id'></TD></TR>";
	s += "<TR><TD>"+i18n['lang'][lang]+":</TD>";	
	s += "<TD><INPUT TYPE='TEXT' name='sdoc_lang' ID='sdoc_lang' SIZE=4></TD></TR>";
	s += "<TR><TD>"+i18n['content'][lang]+":</TD>";	
	s += "<TD><TEXTAREA  name='sdoc_content' ID='sdoc_content'></TD></TR>";
	s += "<TR><TD>"+i18n['comment'][lang]+":</TD>";	
	s += "<TD><TEXTAREA  name='sdoc_comment' ID='sdoc_comment'></TD></TR>";
	s += "<DIV ID='status' style='text-align:center;margin-bottom:5px'></div> "
	s += "<DIV id='buttons' style='text-align:center;'>"
	s += "<A ROLE='"+role+"' CLASS='SDOC_CREATE_SUBMIT main-button'>"
	s += "<SPAN>"+i18n["yes"][lang]+", "+i18n["create"][lang]+"'></SPAN></A>"
	s += "<A ROLE='"+role+"' CLASS='SDOC_CREATE_CANCEL main-button'>"
	s += "<SPAN>"+i18n["no"][lang]+", "+i18n["cancel"][lang]+"'></SPAN></A>"
	s += "</DIV></TABLE></form>"
	
	newdiv.append(s)
	return newdiv
}

// TODO
//function generateRdocCreateDIV(response, su, role, options) {

function generateRdocMetadataDIV(doc, su, role, options) {
	
	var context="rdoc"
	var doc_id = doc["doc_id"]
	var doc_original_id = doc["doc_original_id"]
//	var texttitle = doc["doc_content"]["title"]
//	var textbody = doc["doc_content"]["body"]
					
	var canadmin = (su || role.toLowerCase() == "col-admin")
	
	var newdiv = $("<DIV ID='rrs-"+context+"-metadata-"+doc_id+"' CLASS='main-slidable-div' "+
	" TITLE='"+shortenTitle(doc_original_id)+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	newdiv.append(doc["doc_content"])
	return newdiv						
}

function generateSdocShowDIV(doc, su, role, options) {
	var context="sdoc"
	var sdoc_id = doc["sdoc_id"]
	var sdoc_original_id = doc["sdoc_original_id"]
	var texttitle = doc["sdoc_content"]["title"]
	var textbody = doc["sdoc_content"]["body"]
					
	var canadmin = (su || role.toLowerCase() == "col-admin")
	
	// newdiv
	var newdiv = $("<DIV ID='rrs-"+context+"-show-"+sdoc_id+"' CLASS='main-slidable-div' "+
	" TITLE='"+shortenTitle(sdoc_original_id)+"' STYLE='display:none;overflow:auto;'></DIV>")
		
	// now that we have a doc_div, let's set it up. 
	appendDocDisplayTo(newdiv)
					
	addDocumentTitleToDocDisplay(newdiv, texttitle)
	addDocumentBodyToDocDisplay(newdiv, textbody)
	
	return newdiv
}

function generateRdocShowDIV(doc, su, role, options) {
	
	var context="rdoc"

	var doc_id = doc["doc_id"]
	var doc_original_id = doc["doc_original_id"]
	var texttitle = doc["doc_content"]["title"]
	var textbody = doc["doc_content"]["body"]
					
	var canadmin = (su || role.toLowerCase() == "col-admin")
	
	// newdiv
	var newdiv = $("<DIV ID='rrs-"+context+"-show-"+doc_id+"' CLASS='main-slidable-div' "+
	" TITLE='"+shortenTitle(doc_original_id)+"' STYLE='display:none;overflow:auto;'></DIV>")
	
	var htmltitle = rembrandt2HTML(texttitle)
	var htmlbody = rembrandt2HTML(textbody)
	
	// now that we have a doc_div, let's set it up. 
	appendDocDisplayTo(newdiv)
					
	addDocumentTitleToDocDisplay(newdiv, htmltitle)
	addDocumentBodyToDocDisplay(newdiv, htmlbody)
	
	return newdiv
}

/**************/
/*** modal ****/
/**************/

function modalRdocDelete(button) {

	var context = "rdoc"

	genericDeleteModel({
		'context':context,
		'id': button.attr("ID"),
		'info':button.attr('TITLE'),
		'servlet_url': getServletEngineFromRole(getRole(button), context),
		'postdata' : "do=delete&id="+button.attr("ID")+"&lg="+lang+"&api_key="+getAPIKey(),
		'success_message' : i18n['rdoc_deleted'][lang]
	})	
}

function modalSdocDelete(button) {

	var context = "sdoc"
	
	genericDeleteModel({
		'context':context,
		'id': button.attr("ID"),
		'info':button.attr('TITLE'),
		'servlet_url': getServletEngineFromRole(getRole(button), context),
		'postdata' : "do=delete&id="+button.attr("ID")+"&lg="+lang+"&api_key="+getAPIKey(),
		'success_message' : i18n['sdoc_deleted'][lang]
	})
}


// modalCreateNEforDOC

// modalDeleteNEfromDOC