
/** leave them here, it's admin stuff, this js should be loaded only for susers */
var restlet_admin_user_url =			"/Saskia/admin/user"
var restlet_admin_collection_url =	"/Saskia/admin/collection"
var restlet_admin_stats_url =			"/Saskia/admin/stats"
var restlet_admin_sdoc_url =			"/Saskia/admin/sdoc"
var restlet_admin_rdoc_url =			"/Saskia/admin/rdoc"
var restlet_admin_task_url = 			"/Saskia/admin/task"
var restlet_admin_ne_url = 			"/Saskia/admin/ne"
var restlet_admin_entity_url =      "/Saskia/admin/entity"
var restlet_admin_geoscope_url =		"/Saskia/admin/geoscope"
var restlet_admin_subject_url = 		"/Saskia/admin/subject"
var restlet_admin_subjectground_url="/Saskia/admin/subjectground"
var restlet_admin_url =             "/Saskia/admin/"

// ROLE is just a hint on the UI to present: saskia or admin

$().ready(function() {
	 
	/* Set user admin home page */
	$('A.USER_ADMIN', $("#rrs-user")).live("click", function(ev, ui) {
		// draw the layout
		ev.preventDefault();		
		var api_key=getAPIKey()
		
		// Appearing this link means admin was already authenticated 
		$("#main-content").html(generateHomepageAdminHTML( $(this).attr('USR_PUB_KEY')))
		addBreadcrumbleHeader($("#rrs-homepage-admin").attr('title'), 'rrs-homepage-admin')				
	})	
})
 
/*********************/
/** CONTENT CREATION */
/*********************/

/** generate homepage admin HTML */
function generateHomepageAdminHTML(pub_key) {
	return "<DIV ID='main-side-menu'><DIV ID='main-side-menu-header'>"+
	 	i18n['admin'][lang]+"</DIV><DIV CLASS='main-side-menu-section'>"+
		"<DIV CLASS='main-side-menu-section-body'>"+
		"<DIV CLASS='main-side-menu-section-body-element'>"+
		"<A CLASS='COLLECTION_LIST' ROLE='admin' HREF='#' TITLE='"+i18n['collections'][lang]+
		"' TARGET='rrs-admin-collection-list'>"+i18n['admin-collections'][lang]+"</A></DIV>"+
		"<DIV CLASS='main-side-menu-section-body-element'>"+
		"<A CLASS='USER_LIST' ROLE='admin' HREF='#' TITLE='"+i18n['users'][lang]+"' TARGET='rrs-admin-user-list'>"+
		i18n['admin-users'][lang]+"</A></DIV>"+
			
		// col_stats
		"<DIV CLASS='main-side-menu-section-body-element'>"+
		"<A CLASS='NE_LIST' ROLE='admin' HREF='#' TITLE='"+i18n['nes'][lang]+"' TARGET='rrs-admin-ne-list'>"+
		i18n['admin-nes'][lang]+"</A></DIV>"+
		"<DIV CLASS='main-side-menu-section-body-element'>"+
		"<A CLASS='ENTITY_LIST' ROLE='admin' HREF='#' TITLE='"+i18n['entities'][lang]+"' TARGET='rrs-admin-entity-list'>"+
		i18n['admin-entities'][lang]+"</A></DIV>"+
		"<DIV CLASS='main-side-menu-section-body-element'>"+
		"<A CLASS='GEOSCOPE_LIST' ROLE='admin' HREF='#' TITLE='"+i18n['geoscopes'][lang]+
		"' TARGET='rrs-admin-geoscope-list'>"+	i18n['admin-geoscopes'][lang]+"</A></DIV>"+
		"<DIV CLASS='main-side-menu-section-body-element'>"+
		"<A CLASS='SUBJECT_LIST' ROLE='admin' HREF='#' TITLE='"+i18n['subjects'][lang]+
		"' TARGET='rrs-admin-subject-list'>"+i18n['admin-subjects'][lang]+"</A></DIV>"+
		"<DIV CLASS='main-side-menu-section-body-element'>"+
		"<A CLASS='SUBJECTGROUND_LIST' ROLE='admin' HREF='#' TITLE='"+i18n['subjectgrounds'][lang]+
		"' TARGET='rrs-admin-subjectground-list'>"+i18n['admin-subjectgrounds'][lang]+"</A></DIV>"+
		"</DIV></DIV></DIV>"+

		"<DIV ID='main-right-space'><DIV ID='main-header-menu'>"+
		"<DIV ID='rrs-waiting-div' CLASS='rrs-waiting-div' style='display:none;'>"+
		"<DIV CLASS='rrs-waiting-div-message'></DIV>"+
		"</DIV>  <DIV ID='main-breadcrumbles'> "+
		"<DIV CLASS='main-breadcrumbles-element main-breadcrumbles-header-element' TARGET='rrs-admin-main'>"+
		i18n['admin'][lang]+"</DIV></DIV></DIV>"+ 
		"<DIV ID='main-body' CLASS='main-body-admin' USR_PUB_KEY='"+pub_key+"'>"+
		"<DIV ID='rrs-homepage-admin' CLASS='main-slidable-div' TITLE='"+
		i18n['homepage'][lang]+"'></DIV></DIV>"
}



