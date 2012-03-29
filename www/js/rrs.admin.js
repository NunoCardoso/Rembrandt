Rembrandt = Rembrandt || {};

Rembrandt.urls = Rembrandt.urls || {};

_.extend(Rembrandt.urls, {
	restlet_admin_user_url		 	:	"/Saskia/admin/user",
	restlet_admin_collection_url	:	"/Saskia/admin/collection",
	restlet_admin_stats_url 		:	"/Saskia/admin/stats",
	restlet_admin_doc_url 			:	"/Saskia/admin/doc",
	restlet_admin_task_url 			:	"/Saskia/admin/task",
	restlet_admin_ne_url 			:	"/Saskia/admin/ne",
	restlet_admin_entity_url 		:	"/Saskia/admin/entity",
	restlet_admin_geoscope_url		:	"/Saskia/admin/geoscope",
	restlet_admin_subject_url 		:	"/Saskia/admin/subject",
	restlet_admin_subjectground_url	:	"/Saskia/admin/subjectground",
	restlet_admin_url 				:	"/Saskia/admin/"
});

// ROLE is just a hint on the UI to present: saskia or admin

Rembrandt.Admin = (function ($) {
	"use strict"
 	$(function () {
	
		/* Set user admin home page */
		$('A.USER_ADMIN', $("#rrs-user")).live("click", function(ev, ui) {
			// draw the layout
			ev.preventDefault();		
			var api_key=Rembrandt.Util.getApiKey()
		
			// Appearing this link means admin was already authenticated 
			$("body").html(
				Rembrandt.Admin.generateHomepageAdminHTML( $(this).attr('USR_PUB_KEY') )
			);
			addBreadcrumbleHeader(
				$("#rrs-homepage-admin").attr('title'), 'rrs-homepage-admin'
			);				
		})	
	});
 
/*********************/
/** CONTENT CREATION */
/*********************/

/** generate homepage admin HTML */
	var generateHomepageAdminHTML = function (pub_key) {
		var data = {
			"admin"				: i18n['admin'][lang],
			"collections"		: i18n['collections'][lang],
			"admin-collections"	: i18n['admin-collections'][lang],
			"users"				: i18n['users'][lang],
			"admin-users"		: i18n['admin-users'][lang],
			"nes"				: i18n['nes'][lang],
			"admin-nes"			: i18n['admin-nes'][lang],
			"entities"			: i18n['entities'][lang],
			"admin-entities"	: i18n['admin-entities'][lang],
			"geoscopes"			: i18n['geoscopes'][lang],
			"admin-geoscopes"	: i18n['admin-geoscopes'][lang],
			"subjects"			: i18n['subjects'][lang],
			"admin-subjects"	: i18n['admin-subjects'][lang],
			"subjectgrounds"	: i18n['subjectgrounds'][lang],
			"admin-subjectgrounds": i18n['admin-subjectgrounds'][lang],
			"pubkey"			: pub_key,			
			"homepage"			: i18n['homepage'][lang]
		},
									
		template = "\
<DIV ID='rrs-waiting-div' CLASS='rrs-waiting-div' style='display:none;'>\
	<DIV CLASS='rrs-waiting-div-message'></DIV>\
	<DIV CLASS='rrs-waiting-div-balloontip'></DIV>\
</DIV>\
<DIV ID='main-side-menu'>\
	<DIV ID='main-side-menu-header'>{{admin}}</DIV>\
	<DIV CLASS='main-side-menu-section'>\
		<DIV CLASS='main-side-menu-section-body'>\
			<DIV CLASS='rrs-saskia-logo'>\
				<IMG SRC='img/saskia-head.png'>\
			</DIV>\
			<DIV CLASS='main-side-menu-section-body-element'>\
				<A CLASS='COLLECTION_LIST' ROLE='admin' HREF='#' TITLE='{{collections}}' TARGET='rrs-admin-collection-list'>{{admin-collections}}</A>\
			</DIV>\
			<DIV CLASS='main-side-menu-section-body-element'>\
				<A CLASS='USER_LIST' ROLE='admin' HREF='#' TITLE='{{users}}' TARGET='rrs-admin-user-list'>{{admin-users}}</A>\
			</DIV>\
			<DIV CLASS='main-side-menu-section-body-element'>\
				<A CLASS='NE_LIST' ROLE='admin' HREF='#' TITLE='{{nes}}' TARGET='rrs-admin-ne-list'>{{admin-nes}}</A>\
			</DIV>\
			<DIV CLASS='main-side-menu-section-body-element'>\
				<A CLASS='ENTITY_LIST' ROLE='admin' HREF='#' TITLE='{{entities}}' TARGET='rrs-admin-entity-list'>{{admin-entities}}</A>\
			</DIV>\
			<DIV CLASS='main-side-menu-section-body-element'>\
				<A CLASS='GEOSCOPE_LIST' ROLE='admin' HREF='#' TITLE='{{geoscopes}}' TARGET='rrs-admin-geoscope-list'>{{admin-geoscopes}}</A>\
			</DIV>\
			<DIV CLASS='main-side-menu-section-body-element'>\
				<A CLASS='SUBJECT_LIST' ROLE='admin' HREF='#' TITLE='{{subjects}}' TARGET='rrs-admin-subject-list'>{{admin-subjects}}</A>\
			</DIV>\
			<DIV CLASS='main-side-menu-section-body-element'>\
				<A CLASS='SUBJECTGROUND_LIST' ROLE='admin' HREF='#' TITLE='{{subjectgrounds}}' TARGET='rrs-admin-subjectground-list'>{{admin-subjectgrounds}}</A>\
			</DIV>\
		</DIV>\
	</DIV>\
</DIV>\
<DIV ID='main-header-menu'>\
	<DIV ID='main-breadcrumbles'>\
		<DIV CLASS='main-breadcrumbles-element main-breadcrumbles-header-element' TARGET='rrs-admin-main'>{{admin}}</DIV>\
	</DIV>\
</DIV>\
<DIV ID='main-body' CLASS='main-body-admin' USR_PUB_KEY='{{pubkey}}'>\
	<DIV ID='rrs-homepage-admin' CLASS='main-slidable-div' TITLE='{{homepage}}'></DIV>\
</DIV>";
		
		return Mustache.to_html(template, data);
	};
	
	return {
		"generateHomepageAdminHTML": generateHomepageAdminHTML
	}
}(jQuery));