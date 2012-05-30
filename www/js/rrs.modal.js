var Rembrandt = Rembrandt || {};

Rembrandt.Modal = (function ($) {
	"use strict"

	var genericDeleteModal = function (options) {

		var data = {
			"context"		: options.context,
			"l_pressescape"	: i18n['pressescape'][lang],
			"l_delete"		: i18n['delete'][lang]+" "+i18n[options.context][lang],
			"l_ays"			: i18n['ays'][lang]+" "+i18n['user'][lang],
			"l_yes"			: i18n["yes"][lang]+", "+i18n["delete"][lang],
			"l_no"			: i18n["no"][lang]+", "+i18n["cancel"][lang],
			"info"			: options.info
		},
		
		template = "\
		<div id='modal{{context}}Delete' class='rembrandt-modal'>\
			<div class='rembrandt-modal-escape'>{{l_pressescape}}</div>\
			<div style='text-align:center; padding:10px;'>{{l_delete}}</div>\
			<div style='text-align:left; padding:3px;'>{{l_ays}} ?\
				<div id='info'>{{{info}}}</div>\
				<div id='rrs-waiting-div' style='text-align:center;margin-bottom:5px'>\
					<div class='rrs-waiting-div-message'></div>\
				</div>\
				<div id='buttons' style='text-align:center;'>\
					<input type='button' id='YesButton' value='{{l_yes}}'>\
					<input type='button' id='NoButton' value='{{l_no}}'>\
				</div>\
			</div>\
		</div>";
		
		$.modal( Mustache.to_html(template, data), {
			onShow: function modalShow(dialog) {
				dialog.data.find("#YesButton").click(function(ev) {
					jQuery.ajax( {
						type			: "POST", 
						url				: options.servlet_url,	
						contentType		: "application/json",
						data			: JSON.stringify(options.data),
						beforeSubmit	: Rembrandt.Waiting.show(),
						success			: function(response) {
							if (response['status'] == -1) {
								Rembrandt.Waiting.error()
								dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
								dialog.data.find("#buttons").show()	
							} else if (response['status'] == 0)  {
								Rembrandt.Waiting.hide({
									message	: options.success_message,
									when	: 5000
								})
								dialog.data.find("#YesButton").hide()
								dialog.data.find("#NoButton").attr("value",i18n["OK"][lang])
							}
						},
						error:function(response) {
							Rembrandt.Waiting.error()
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
	};

	return {
		"genericDeleteModal" : genericDeleteModal
	}
}(jQuery));
