$().ready(function() {

	$('A.USER_SETTINGS').live('click', function(ev) {
		ev.preventDefault();
		userSettings()
	})
	
	// this is exclusive for admin: CREATE and DELETE are on the user.js	
	$('A.USER_LIST').live("click", function(ev, ui) {

 		ev.preventDefault();
		var a_clicked = $(this)
		var title = (a_clicked.attr("title") ? a_clicked.attr("title") : a_clicked.text())
		var api_key = getAPIKey()
		
		showSlidableDIV({
			"title": title,
			"target": "rrs-user-list", 
			"role": a_clicked.attr('ROLE'),
			"slide": getSlideOrientationFromLink(a_clicked),
			"ajax":true,
			"restlet_url":getServletEngineFromRole(a_clicked.attr('ROLE'), "user"), 
			"postdata":"do=list&l=10&o=0&lg="+lang+"&api_key="+api_key,
			"divcreator":generateUserListDIV, 
			"divcreatoroptions":{},
			"sidemenu":null, 
			"sidemenuoptions":{}
		})					
	});
	
	$('A.USER_LOGOUT').live('click', function(ev) {
		ev.preventDefault();	
		modalUserLogout()
	})
	
	$('A.USER_LOGIN').live('click', function(ev) {
		ev.preventDefault();	
		modalUserLogin()
	})

	$('A.USER_CREATE').live("click", function(ev, ui) {
		ev.preventDefault();
		modalUserCreate($(this))
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-user-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}		
	})
	
	$('A.USER_DELETE').live("click", function(ev, ui) {
		ev.preventDefault();
		modalUserDelete($(this))
		var divshown = $('DIV.main-slidable-div:visible')
		if (divshown.attr('id') == 'rrs-user-list') {
			divshown.find("A.MANAGE_PAGER").trigger('click')
		}
		if (divshown.attr('id') == 'rrs-user-show-'+$(this).attr('ID')) {
			divshown.find("DIV.rrs-pageable").html(i18n['user_deleted'][lang])
			hideSubmeuOnSideMenu($("#main-side-menu-section-user"))
		}
	})	

})

// displays in #main-body setitngs for user
function userSettings() {
	
	var api_key = getAPIKey()
	
	jQuery.ajax( {
		type:"POST", url:restlet_saskia_user_url, contentType:"application/x-www-form-urlencoded",
		data: "do=show&lg="+lang+"&api_key="+api_key, 
		beforeSubmit: waitMessageBeforeSubmit(lang),
		success: function(response) {
			if (response["status"] == -1) {
				errorMessageWaitingDiv(lang, response['message'])
			} else {
				var user = response["message"]
				var s = "<TABLE BORDER=0>";
				s += "<TR><TD>"+i18n['name'][lang]+"</TD><TD>"+user['usr_firstname']+" "+user['usr_lastname']+"</TD></TR>"
				s += "<TR><TD>"+i18n['email'][lang]+"</TD><TD>"+user['usr_email']+"</TD></TR>"
				s += "<TR><TD>"+i18n['password'][lang]+"</TD><TD><A HREF='#'>Change...</A></TD></TR>"
				s += "<TR><TD>Nº de colecções minhas</TD><TD>"+user['current_number_collections_owned']+" de "+user['usr_max_number_collections']+"</TD></TR>"
				s += "<TR><TD>Grupos</TD><TD>"
				var groups = user["usr_groups"].split(";")
				for (i in groups) {
					if (groups[i] != "") {
						s += "<DIV>"+groups[i]+"(x) </DIV> "
					}
				}
				s += "<A HREF='#'>Add...</A></TD></TR>"
				s += "<TR><TD>api key</TD><TD>"+user['usr_api_key']+"</TD></TR>"
				s += "<TR><TD>usr_max_docs_per_collection</TD><TD>"+user['usr_max_docs_per_collection']+"</TD></TR>"
				s += "<TR><TD>usr_max_daily_api_calls</TD><TD>"+user['usr_max_daily_api_calls']+"</TD></TR>"
				s += "<TR><TD>usr_current_daily_api_calls</TD><TD>"+user['usr_current_daily_api_calls']+"</TD></TR>"
				s += "<TR><TD>usr_total_api_calls</TD><TD>"+user['usr_total_api_calls']+"</TD></TR>"
				s += "<TR><TD>usr_date_last_api_call</TD><TD>"+user['usr_date_last_api_call']+"</TD></TR>"
				s += "<TR COLSPAN=2><TD><A HREF='#'>Apagar conta...</TD></TR>"
				s += "</TABLE>" 				
				$("#main-body").html(s)
				$("#main-breadcrumbles").html("<DIV CLASS='main-bradcrumbles-header-element'>"+
					i18n["user_settings"][lang]+"</DIV>")
	
			}
		},	
		error: function(response) {errorMessageWaitingDiv(lang, response)}		
	})
}

/** generate user list DIV */
function generateUserListDIV(response, su, role, options) {

	// I might need the admin boolean to make changes to table layout
	var context = "collection"
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
		"contexts":"users", 
		"response":response,
		"role":role, 
		"allowedSearchableFields":{
			"usr_id":i18n['id'][lang], 
			"usr_login":i18n['user'][lang], 
			"usr_firstname":i18n['firstname'][lang], 
			"usr_lastname":i18n['lastname'][lang],
			"usr_email":i18n['email'][lang]
		}
	})
	
	var res = response['result']
		
	// buttons
	t += "<DIV CLASS='rrs-admin-buttonrow'><A HREF='#' CLASS='"+context.toUpperCase()+"_CREATE main-button' "
	t += "ROLE='"+role+"'><SPAN>"+i18n['create_new_'+context][lang]+"</SPAN></A></DIV>"

	// table 
	t += "<DIV>"
	t += "<TABLE ID='rrs-admin-"+context+"-list-table' CLASS='tablesorter "+ role + "'>"
	t += "<THEAD><TR><TH></TH>"
	t += "<TH>"+i18n['id'][lang]+"</TH>";
	t += "<TH>"+i18n['user'][lang]+"</TH>";
	t += "<TH>"+i18n['firstname'][lang]+"</TH>";
	t += "<TH>"+i18n['lastname'][lang]+"</TH>";
	t += "<TH>"+i18n['email'][lang]+"</TH>";
	t += "<TH>"+i18n['groups'][lang]+"</TH>";
	t += "<TH>"+i18n['enabled'][lang]+"</TH>";
	t += "<TH>"+i18n['superuser'][lang]+"</TH>";
	t += "<TH>"+i18n['api_key'][lang]+"</TH>";
	t += "<TH>"+i18n['max_number_collections'][lang]+"</TH>";
	t += "<TH>"+i18n['max_number_tasks'][lang]+"</TH>";
	t += "<TH>"+i18n['max_number_docs_per_collection'][lang]+"</TH>";
	t += "<TH>"+i18n['max_daily_api_calls'][lang]+"</TH>";
	t += "<TH>"+i18n['current_daily_api_calls'][lang]+"</TH>";
	t += "<TH>"+i18n['total_api_calls'][lang]+"</TH>";
	t += "<TH></TH></TR></THEAD><TBODY>"
	
	for(i in res) {
		
		var id = res[i]['usr_id']
		
		t += "<TR><TD><INPUT TYPE='CHECKBOX' CLASS='sec-checkbox'></TD>"
		t += "<TH><A CLASS='"+context.toUpperCase()+"_SHOW' ROLE='"+role+"' "+
		 "TARGET='rrs-admin-"+context+"-show-"+id+"' "+
		 "TITLE='"+res[i]['usr_login']+"' HREF='#'>"+id+"</A></TH>"

		t += "<TD><DIV CONTEXT='"+context+"' COL='usr_login' ID='"+id+"' "
		t += "CLASS=''"+editinplace+"' textfield'>"+res[i]['usr_login']+"</DIV></TD>"	
		
		t += "<TD><DIV CONTEXT='"+context+"' COL='usr_firstname' ID='"+id+"' "
		t += "CLASS=''"+editinplace+"' textfield'>"+res[i]['usr_firstname']+"</DIV></TD>"
		
		t += "<TD><DIV CONTEXT='"+context+"' COL='usr_lastname' ID='"+id+"' "
		t += "CLASS=''"+editinplace+"' textfield'>"+res[i]['usr_lastname']+"</DIV></TD>"	
		
		t += "<TD><DIV CONTEXT='"+context+"' COL='usr_email' ID='"+id+"' "
		t += "CLASS=''"+editinplace+"' textfield'>"+res[i]['usr_email']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"' COL='usr_groups' ID='"+id+"' "
		t += "CLASS=''"+editinplace+"' textfield'>"+res[i]['usr_groups']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='user' COL='usr_enabled' ID='"+res[i]['usr_id']+"' "
		t += "CLASS=''"+editinplace+"' selectfield 01'>"+printYesOrNo(res[i]['usr_enabled'])+"</DIV></TD>"

		t += "<TD><DIV CONTEXT='user' COL='usr_superuser' ID='"+res[i]['usr_id']+"' "
		t += "CLASS=''"+editinplace+"' selectfield 01'>"+printYesOrNo(res[i]['usr_superuser'])+"</DIV></TD>"

		t += "<TD><DIV CONTEXT='"+context+"' COL='usr_api_key' ID='"+id+"' "
		t += "CLASS=''"+editinplace+"' textfield'>"+res[i]['usr_api_key']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"' COL='usr_max_number_collections' ID='"+id+"' "
		t += "CLASS=''"+editinplace+"' textfield'>"+res[i]['usr_max_number_collections']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"' COL='usr_max_number_tasks' ID='"+id+"' "
		t += "CLASS=''"+editinplace+"' textfield'>"+res[i]['usr_max_number_tasks']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"' COL='usr_max_docs_per_collection' ID='"+id+"' "
		t += "CLASS=''"+editinplace+"' textfield'>"+res[i]['usr_max_docs_per_collection']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"' COL='usr_max_daily_api_calls' ID='"+id+"' "
		t += "CLASS=''"+editinplace+"' textfield'>"+res[i]['usr_max_daily_api_calls']+"</DIV></TD>"	
	
		t += "<TD><DIV CONTEXT='"+context+"' COL='usr_current_daily_api_calls' ID='"+id+"' "
		t += "CLASS=''"+editinplace+"' textfield'>"+res[i]['usr_current_daily_api_calls']+"</DIV></TD>"	

		t += "<TD><DIV CONTEXT='"+context+"' COL='usr_total_api_calls' ID='"+id+"' "
		t += "CLASS=''"+editinplace+"' textfield'>"+res[i]['usr_total_api_calls']+"</DIV></TD>"	

		t += "<TD><A HREF='#' ID='"+id+"' TITLE='"+res[i]['usr_login']+"' CLASS='"+context.toUpperCase()+"_DELETE main-button'>"
		t += "<SPAN>"+i18n['delete'][lang]+"...</SPAN></A></TD></TR>"
	}	
	


	t += "<TFOOT><TR><TD><INPUT TYPE='CHECKBOX' CLASS='main-checkbox'></TD>"
	t += "<TH>"+i18n['all'][lang]+"</TH>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='usr_login' "
	t += "CLASS=''"+editinplace+"' textfield group'></DIV></TD>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='usr_firstname' "
	t += "CLASS=''"+editinplace+"' textfield group'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='usr_lastname' "
	t += "CLASS=''"+editinplace+"' textfield group'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='usr_email' "
	t += "CLASS=''"+editinplace+"' textfield group'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='usr_groups' "
	t += "CLASS=''"+editinplace+"' textfield group'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='usr_enabled' "
	t += "CLASS=''"+editinplace+"' selectfield group 01'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='usr_superuser' "
	t += "CLASS=''"+editinplace+"' selectfield group 01'></DIV>"	
	t += "<TD><DIV CONTEXT='"+context+"' COL='usr_api_key' "
	t += "CLASS=''"+editinplace+"' textfield'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='usr_max_number_collections' "
	t += "CLASS=''"+editinplace+"' textfield'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='usr_max_number_tasks' "
	t += "CLASS=''"+editinplace+"' textfield'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='usr_max_docs_per_collection' "
	t += "CLASS=''"+editinplace+"' textfield'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='usr_max_daily_api_calls' "
	t += "CLASS=''"+editinplace+"' textfield'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='usr_current_daily_api_calls' "
	t += "CLASS=''"+editinplace+"' textfield'></DIV>"
	t += "<TD><DIV CONTEXT='"+context+"' COL='usr_total_api_calls' "
	t += "CLASS=''"+editinplace+"' textfield'></DIV>"
	t += "<TD></TD>"
	t += "</TR></TFOOT>"
	t += "</TABLE></DIV>"
	t += "</DIV></DIV>"
	
	newdiv.append(t)
	return newdiv
}


/*****************/
/**** MODALS *****/
/*****************/

// simple login modal
function modalUserLogin() {
		$.modal("<div id='modalUserLogin' class='rembrandt-modal'>"+
      "<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
	  "<div style='text-align:center; padding:10px;'>"+i18n['loginuser'][lang]+"</div>"+
	  "<div style='text-align:left; padding:3px;'>"+
		"<form><DIV style='text-align:left'><table>"+
			"<TR><TD ALIGN=RIGHT>"+i18n["user"][lang]+"</TD>"+
			"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='user'></TD></TR>"+
			"<TR><TD ALIGN=RIGHT>"+i18n["password"][lang]+"</TD>"+
			"<TD ALIGN=LEFT><INPUT TYPE='PASSWORD' SIZE='25' ID='password'></TD></TR>"+
			"<TR><TD COLSPAN=2><INPUT ID='rememberme' TYPE='CHECKBOX'> "+i18n['rememberme'][lang]+"</TD></TABLE></DIV>"+
			"<P><A HREF='#' ID='newuser'>"+i18n["newuser"][lang]+"?</A> | "+
			"<A HREF='#' ID='forgotpassword'>"+i18n["forgotpassword"][lang]+"</A></P><BR>"+
			"<div id='login-status' style='text-align:center;margin-bottom:5px'></div> "+ 
			"<div id='buttons' style='text-align:center;'><input type='button' id='LoginButton' value='"+i18n["enterlogin"][lang]+"'>"+
			"<input type='button' id='NoButton' value='"+i18n["no"][lang]+", "+i18n["cancel"][lang]+"'></div></form>"+
      "</div>"+
	"</div>", {
	onShow: function modalShow(dialog) {
		dialog.data.find("#LoginButton").click(function(ev) {
			ev.preventDefault()
			// make the UI
			var user_login = dialog.data.find("#user").val()
			var user_password = dialog.data.find("#password").val()
			var rememberme = dialog.data.find("#rememberme:checked").val()
			// hide div buttons, let's use login-status 
			var goodToGo = true
			if (user_login == ""|| user_password == "") {
				dialog.data.find("#login-status").show().html(i18n['formmismatch'][lang])
				dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
				goodToGo = false	
			}  
			if (goodToGo) {
				dialog.data.find("#buttons").hide()
				// show login-status
				dialog.data.find("#login-status").show()
			
			// make the AJAX query
				jQuery.ajax( {type:"POST", url:restlet_saskia_user_url,
				contentType:"application/x-www-form-urlencoded",
				data: "do=login&lg="+lang+"&u="+urlencode(user_login)+"&p="+urlencode(hex_md5(user_password)),
				beforeSubmit: waitfunction(lang, dialog.data.find("#login-status")), 
				
				success: function(response) {
					// status is 0 for good login, -1 otherwise
					if (response['status'] == -1) {
						dialog.data.find("#login-status").html(response['message'])
						dialog.data.find("#LoginButton").attr("value",i18n['retry'][lang])
						dialog.data.find("#buttons").show()	
						
					} else if (response['status'] == 0)  {
						var user = response['message']
						var user_name = user['usr_firstname']+" "+user['usr_lastname']
						var user_id = user['usr_id']
						dialog.data.find("#login-status").html(i18n['welcome'][lang]+", "+user_name)
						
						// update rembrandt-user div
						$("#rrs-user").html(i18n['user'][lang]+": "+ user_name)
						$("#rrs-user").attr('USER', user_login)
						$("#rrs-user").attr('USER_ID', user_id)
						
						$("#rrs-user").append(" | <A HREF='#' CLASS='USER_SETTINGS'>"+i18n['settings'][lang]+"</A> | ")								
						// set cookie info, case I want to be remembered
						if (rememberme != null) {
							$.cookie('user_login', user_login) 
							$.cookie('user_name', user_name) 
							$.cookie('user_id', user_id) 
							$.cookie('api_key', user['usr_api_key'])
							if(!isUndefined(user['usr_pub_key_decoder'])) {
								$.cookie('su', user['usr_pub_key_decoder'])
							}
							$("#rrs-user").attr('api_key', user['usr_api_key'])	
						} else {
							$("#rrs-user").attr('api_key', user['usr_api_key'])					
						}
						
						// usr_pub_key is only sent from restlet if is confirmed as superuser
						if (!isUndefined(user["usr_pub_key"]) && validateSu(user["usr_pub_key"])) {
							$("#rrs-user").append("<A HREF='#' CLASS='USER_ADMIN' USR_PUB_KEY='"+user["usr_pub_key"]+"'>"+ i18n['admin'][lang]+"</A> | ")
							//TODO
						//	$.getScript("js/rembrandt.admin.js")
						
						}
						
						$("#rrs-user").append("<A HREF='#' CLASS='USER_LOGOUT'>"+ i18n['logout'][lang]+"</A>")
							
						//update rembrandt collection div
						var current_collection = getCollection()
						var current_collection_id = getCollectionID()
						
						// change only if the current collection does not have read privileges 
						// ajax perm to restlet_saskia_col_url do=list-all&api_key=&lg=
						
						var res = i18n['collection'][lang]+": <A CLASS='collection' HREF='#' "
						res += " COLLECTION='"+$("#rrs-collections").attr("DEFAULT")
						res += " COLLECTION_ID='"+$("#rrs-collections").attr("DEFAULT_ID")+"'>"
						res += $("#rrs-collections").attr("DEFAULT") + "</A>"	
						
						$("#rrs-collections").html(res)
						dialog.data.find("#LoginButton").hide()
						
						// ask for a page refresh
						if(typeof updateDisplay == 'function') { updateDisplay();}
						
						// Let's reuse NoButon for a OK
						dialog.data.find("#NoButton").attr('value',i18n['OK'][lang])
						dialog.data.find("#buttons").show()	
					}
				}, 
				error:function(response) {
					dialog.data.find("#login-status").html(response['message'])
					dialog.data.find("#LoginButton").attr("value",i18n['retry'][lang])
					dialog.data.find("#buttons").show()	
				}
			})	
			}				
		});
		dialog.data.find("#NoButton").click(function(ev) {
			ev.preventDefault();
			$.modal.close();
 		})
		dialog.data.find("#newuser").click(function(ev) {
			ev.preventDefault();
			$.modal.close();
			modalUserCreate();
 		})
		dialog.data.find("#forgotpassword").click(function(ev) {
			ev.preventDefault();
			$.modal.close();
			modalUserForgotPassword()
 		})
	},
	overlayCss:{backgroundColor: '#888', cursor: 'wait'}
	});
}   

function modalUserForgotPassword() {
	
	$.modal("<div id='modalForgotPasswordUser' class='rembrandt-modal'>"+
      "<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
	  "<div style='text-align:center; padding:10px;'>"+i18n['forgotpasswordentermail'][lang]+"</div>"+
	  "<div style='text-align:left; padding:3px;'>"+
		"<form><INPUT TYPE='TEXT' SIZE='25' ID='email'><BR>"+
			"<div id='login-status' style='text-align:center;margin-bottom:5px'></div> "+ 
			"<div id='buttons' style='text-align:center;'>"+
			"<input type='button' id='SendButton' value='"+i18n["recoverpassword"][lang]+"'>"+
			"<input type='button' id='OKButton' value='"+i18n["cancel"][lang]+"'>"+
			"</div></form>"+
      "</div>"+
	"</div>	", { 
		onShow: function modalShow(dialog) {

		dialog.data.find("#SendButton").click(function(ev) {
			ev.preventDefault();
			var user_email = dialog.data.find("#email").val()
			dialog.data.find("#SendButton").attr("disabled",true)
			jQuery.ajax( {
				type:"POST", 
				url:restlet_saskia_user_url,
				contentType:"application/x-www-form-urlencoded",
				data: "do=recoverpassword&lg="+lang+"&em="+urlencode(user_email), 
				beforeSubmit: waitfunction(lang, dialog.data.find("#login-status")), 
				success: function(response) {
					// status is 0 for good login, -1 otherwise
					if (response['status'] == -1) {
						dialog.data.find("#login-status").html(response['message'])
						dialog.data.find("#SendButton").attr("disabled",false)
					}
					else if (response['status'] == 0) {
						// i have a newpassword & tmp_api_key to process
						jQuery.ajax( {
							type:"POST", 
							url:mailrecoverpassword,
							contentType:"application/x-www-form-urlencoded",
							data: "newpassword="+urlencode(response['newpassword'])+"&tmp_api_key="+
							urlencode(response['tmp_api_key'])+"&lang="+lang+
							"&email="+urlencode(user_email), 
							success: function(response) {
								dialog.data.find("#login-status").html(response)
								dialog.data.find("#OKButton").attr("value",i18n["OK"][lang])
								dialog.data.find("#SendButton").hide()
							}
						})
					}
				}
			})
		})
		dialog.data.find("#OKButton").click(function(ev) {
			ev.preventDefault();
			$.modal.close();
		})
	},
	overlayCss:{backgroundColor: '#888', cursor: 'wait'}
	})	
}
		
function modalUserChangePassword() {

	var user = getUser()
	
	$.modal("<div id='modalChangePassword' class='rembrandt-modal'>"+
      "<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
	  "<div style='text-align:center; padding:10px;'>"+i18n['changepassword'][lang]+":</div>"+
	  "<div style='text-align:left; padding:3px;'>"+
		"<form><DIV style='text-align:left'><table>"+
			"<TR><TD ALIGN=RIGHT>"+i18n["loginname"][lang]+"</TD>"+
			"<TD ID='username'></TD></TR>"+
			"<TR><TD ALIGN=RIGHT>"+i18n["oldpassword"][lang]+"</TD>"+
			"<TD ALIGN=LEFT><INPUT TYPE='PASSWORD' SIZE='15' ID='oldpassword'></TD></TR>"+
			"<TR><TD ALIGN=RIGHT>"+i18n["newpassword"][lang]+"</TD>"+
			"<TD ALIGN=LEFT><INPUT TYPE='PASSWORD' SIZE='15' ID='newpassword'></TD></TR>"+
			"<TR><TD ALIGN=RIGHT>"+i18n["repeatpassword"][lang]+"</TD>"+
			"<TD ALIGN=LEFT><INPUT TYPE='PASSWORD' SIZE='15' ID='repeatpassword'></TD></TR>"+
			"</TABLE></DIV>"+
			"<div id='login-status' style='text-align:center;margin-bottom:5px'></div> "+ 
			"<div id='buttons' style='text-align:center;'>"+
			"<input type='button' id='YesButton' value='"+i18n["yes"][lang]+", "+i18n["change"][lang]+"'>"+
			"<input type='button' id='NoButton' value='"+i18n["no"][lang]+", "+i18n["cancel"][lang]+"'></div></form>"+
      "</div>"+
	"</div>	", {
		onShow: function modalShow(dialog) {		
			dialog.data.find("#username").html(user)
			dialog.data.find("#YesButton").click(function(ev) {
				var newpassword = dialog.data.find("#newpassword").val()
				var oldpassword = dialog.data.find("#oldpassword").val()
				var repeatpassword = dialog.data.find("#repeatpassword").val()
				var goodToGo = true
				if(newpassword != repeatpassword) {
					dialog.data.find("#login-status").show().html(i18n['passwordmismatch'][lang])
					dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
					goodToGo = false	
				}	  
				if (!newpassword || !oldpassword) {
					dialog.data.find("#login-status").show().html(i18n['formmismatch'][lang])
					dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
					goodToGo = false	
				}
				if (goodToGo) {
					jQuery.ajax( {
						type:"POST", 
						url:restlet_saskia_user_url,
						contentType:"application/x-www-form-urlencoded",
						data: "do=changepassword&lg="+lang+"&u="+urlencode(user)+
					       "&op="+urlencode(hex_md5(oldpassword))+
						   "&np="+urlencode(hex_md5(newpassword)),
						beforeSubmit: waitfunction(lang, dialog.data.find("#login-status")), 
						success: function(response) {
							

							
						// status is 0 for good change, -1 otherwise
							if (response['status'] == -1) {
								dialog.data.find("#login-status").html(response['message'])
								dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
								dialog.data.find("#buttons").show()	
							} else if (response['status'] == 0)  {
								// change rembrandt-user div
								dialog.data.find("#login-status").html(response['message'])
								dialog.data.find("#YesButton").hide()
							// Let's reuse NoButon for a OK
								dialog.data.find("#NoButton").attr('value',i18n['OK'][lang])
								dialog.data.find("#buttons").show()	
							}
						}
					})
				}
			})							
			dialog.data.find("#NoButton").click(function(ev) {
				ev.preventDefault();
				$.modal.close();
			})
		},
		overlayCss:{backgroundColor: '#888', cursor: 'wait'}
	})
}

function modalUserCreate() {

	$.modal("<div id='modalNewUser' class='rembrandt-modal'>"+
      "<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
	  "<div style='text-align:center; padding:10px;'>"+i18n['newuser'][lang]+"</div>"+
	  "<div style='text-align:left; padding:3px;'>"+
		"<form><table style='border:0px;border-spacing:3px;'>"+
			"<TR><TD ALIGN=RIGHT>"+i18n["user"][lang]+"*</TD>"+
			"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='user'></TD></TR>"+
			"<TR><TD ALIGN=RIGHT>"+i18n["password"][lang]+"*</TD>"+
			"<TD ALIGN=LEFT><INPUT TYPE='PASSWORD' SIZE='25' ID='password'></TD></TR>"+
			"<TR><TD ALIGN=RIGHT>"+i18n["repeatpassword"][lang]+"*</TD>"+
			"<TD ALIGN=LEFT><INPUT TYPE='PASSWORD' SIZE='25' ID='password2'></TD></TR>"+
			"<TR><TD ALIGN=RIGHT>"+i18n["firstname"][lang]+"*</TD>"+
			"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='firstname'></TD></TR>"+
			"<TR><TD ALIGN=RIGHT>"+i18n["lastname"][lang]+"*</TD>"+
			"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='lastname'></TD></TR>"+
			"<TR><TD ALIGN=RIGHT>"+i18n["email"][lang]+"*</TD>"+
			"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='25' ID='email'></TD></TR>"+
			"<TR><TD ALIGN=RIGHT>"+i18n["whatdayistoday"][lang]+"? *</TD>"+
			"<TD ALIGN=LEFT><INPUT TYPE='TEXT' SIZE='3' ID='captcha'></TD></TR>"+
			"</TABLE><BR>"+
			"<div id='login-status' style='text-align:center;margin-bottom:5px'></div> "+ 
			"<div id='buttons' style='text-align:center;'><input type='button' id='YesButton' value='"+i18n["yes"][lang]+", "+i18n["register"][lang]+"'>"+
			"<input type='button' id='NoButton' value='"+i18n["no"][lang]+", "+i18n["cancel"][lang]+"'></div></form>"+
      "</div>"+
	"</div>	", {
		onShow: function modalShow(dialog) {
		dialog.data.find("#YesButton").click(function(ev) {
			
			var user_login = dialog.data.find("#user").val()
			var user_password = dialog.data.find("#password").val()
			var user_password2 = dialog.data.find("#password2").val()
			var user_firstname = dialog.data.find("#firstname").val()
			var user_lastname = dialog.data.find("#lastname").val()
			var user_email = dialog.data.find("#email").val()
			var captcha = dialog.data.find("#captcha").val()
			var goodToGo = true
			
			if(user_password != user_password2) {
				dialog.data.find("#login-status").show().html(i18n['passwordmismatch'][lang])
				dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
				goodToGo = false	
			}  
			if(!user_password.length >= 6) {
				dialog.data.find("#login-status").show().html(i18n['passwordtoosmall'][lang])
				dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
				goodToGo = false	
			}  
			
			if (!user_firstname || !user_lastname) {
				dialog.data.find("#login-status").show().html(i18n['formmismatch'][lang])
				dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
				goodToGo = false	
			}
			
			if (!user_login.length >= 6) {
				dialog.data.find("#login-status").show().html(i18n['logintoosmall'][lang])
				dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
				goodToGo = false	
			}
			var userpatt =/^[a-zA-Z0-9_]*$/;
			var m = user_login.match(userpatt);
			if (m == null) {
				dialog.data.find("#login-status").show().html(i18n['loginchars'][lang])
				dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
				goodToGo = false	
			}
			
			if (!emailCheck(user_email)) {
				dialog.data.find("#login-status").show().html(i18n['invalid_email'][lang])
				dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
				goodToGo = false	
			}
			
			var day = new Date().getDate()
			debug(day+" "+captcha)
			if(captcha != day) {
				dialog.data.find("#login-status").show().html(i18n['captchamismatch'][lang])
				dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])	
				goodToGo = false	
			}
			
			if (goodToGo) {
				jQuery.ajax( {
					type:"POST", 
					url:restlet_saskia_user_url,
					contentType:"application/x-www-form-urlencoded",
					data: "do=register&lg="+lang+"&u="+urlencode(user_login)+
					       "&p="+urlencode(hex_md5(user_password))+
						   "&fn="+urlencode(user_firstname)+
						   "&ln="+urlencode(user_lastname)+
						   "&em="+urlencode(user_email), 
					beforeSubmit: waitfunction(lang, dialog.data.find("#login-status")), 
					success: function(response) {

						// status is 0 for good login, -1 otherwise
						if (response['status'] == -1) {
							dialog.data.find("#login-status").html(response['message'])
							dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
							dialog.data.find("#buttons").show()	
						} else if (response['status'] == 0)  {
							
							//send confirmation mail
							jQuery.ajax( {
							type:"POST", 
							url:mailconfirmregistration,
							contentType:"application/x-www-form-urlencoded",
							data: "api_key="+urlencode(response['message']['usr_api_key'])+"&lang="+lang+
							"&email="+urlencode(user_email),
							success: function(response) {
								dialog.data.find("#login-status").html(response)
								dialog.data.find("#YesButton").hide()
								dialog.data.find("#NoButton").attr("value",i18n["OK"][lang])
							}
							})
						}
					}
				})
			}
		});
		dialog.data.find("#NoButton").click(function(ev) {
			ev.preventDefault();
			$.modal.close();
		})
	},
	overlayCss:{backgroundColor: '#888', cursor: 'wait'}
	});
}					

function modalUserDelete(button) {
	
	var api_key=getAPIKey()
	
	$.modal("<div id='modalDeleteUser' class='rembrandt-modal'>"+
      "<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
	  "<div style='text-align:center; padding:10px;'>"+i18n['delete'][lang]+" "+i18n['user'][lang]+"</div>"+
	  "<div style='text-align:left; padding:3px;'>"+i18n['ays'][lang]+i18n['user'][lang]+"?"+
	  "<div id='user_info'></div>"+
		"<div id='login-status' style='text-align:center;margin-bottom:5px'></div> "+ 
		"<div id='buttons' style='text-align:center;'><input type='button' id='YesButton' value='"+i18n["yes"][lang]+", "+i18n["delete"][lang]+"'>"+
		"<input type='button' id='NoButton' value='"+i18n["no"][lang]+", "+i18n["cancel"][lang]+"'></div></form>"+
      "</div></div>	",{
		onShow: function modalShow(dialog) {
			// fill out table
		dialog.data.find("#user_info").html(usr_id+":"+usr_login)
		dialog.data.find("#YesButton").click(function(ev) {
			
			jQuery.ajax( {
				type:"POST", url:restlet_admin_user_url,
				contentType:"application/x-www-form-urlencoded",
				data: "do=delete&ui="+usr_id+"&lg="+lang+"&api_key="+api_key,
				beforeSubmit: waitfunction(lang, dialog.data.find("#login-status")), 
				success: function(response) {

					// status is 0 for good deletion, -1 for error
					if (response['status'] == -1) {
						dialog.data.find("#login-status").html(response['message'])
						// yes button kept hidden, cancel button is the new OK button
						dialog.data.find("#YesButton").hide()
						dialog.data.find("#NoButton").attr("value",i18n['OK'][lang])
					} else if (response['status'] > 0)  {
						//status is the number of deleted users 
						dialog.data.find("#login-status").html(response['message'])
						dialog.data.find("#YesButton").hide()
						dialog.data.find("#NoButton").attr("value",i18n["OK"][lang])
						
						// let's refresh the content. How? triggering the admin-pager link
						var a = button.parents("DIV.ui-tabs-panel:first").find("A.MANAGE_PAGER")
						a.trigger('click')
					}
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

function modalUserLogout() {
	user = getUser()
	$.modal( "<div id='modalLogoutUser' class='rembrandt-modal'>"+
      "<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
	   "<div style='text-align:center; padding:10px;'>"+i18n['logoutareyousure'][lang]+
	   "<div style='display:inline;' id='user'></div>?</div>"+
	   "<div style='text-align:left; padding:3px;'>"+
		"<form><div style='text-align:center;'>"+
		"<input type='button' id='YesButton' value='"+i18n["yes"][lang]+", "+i18n["logout"][lang]+"'>"+
		"<input type='button' id='NoButton' value='"+i18n["no"][lang]+", "+i18n["cancel"][lang]+"'>"+
		"</div></form></div></div>", {

		onShow: function modalShow(dialog) {
		
		dialog.data.find("#user").html(user)
		
		dialog.data.find("#YesButton").click(function(ev) {
			ev.preventDefault();
			$("#rrs-user").html(i18n['guest'][lang]+". <A HREF='#' CLASS='USER_LOGIN'>"+
			 i18n['login'][lang]+" / "+i18n['register'][lang]+"</A>")						
			// unset cookie.
			$.cookie('user_login', null) 
			$.cookie('user_name', null) 
			$.cookie('user_id', null) 
			$.cookie('api_key', null)
			$.cookie('collection', null)
			$.cookie('usr_pub_key', null)
			$.cookie('usr_pub_key_decoder', null)
			
			//update rembrandt collection div
			$("#rrs-collections").html(i18n['collection'][lang]+": "+
			"<A class='collection' HREF='#' COLLECTION='"+$("#rrs-collections").attr("DEFAULT")+
			" COLLECTION_ID='"+$("#rrs-collections").attr("DEFAULT_ID")+"'>"+
			$("#rrs-collections").attr("DEFAULT")+"</A>")
			$.modal.close();
		});
		dialog.data.find("#NoButton").click(function(ev) {
			ev.preventDefault();
			$.modal.close();
		})
	},
	overlayCss:{backgroundColor: '#888', cursor: 'wait'}
	});
}   