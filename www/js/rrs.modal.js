var Rembrandt = Rembrandt || {};

Rembrandt.Modal = (function ($) {
	"use strict"

	var genericDeleteModal = function (options) {

		$.modal("<div id='modal"+options.context+"Delete' class='rembrandt-modal'>"+
			"<div class='rembrandt-modal-escape'>"+i18n['pressescape'][lang]+"</div>"+
			"<div style='text-align:center; padding:10px;'>"+i18n['delete'][lang]+" "+i18n[options.context][lang]+"</div>"+
			"<div style='text-align:left; padding:3px;'>"+i18n['ays'][lang]+" "+i18n['user'][lang]+"?"+
			"<div id='info'></div>"+
			"<div id='rrs-waiting-div' style='text-align:center;margin-bottom:5px'>"+
			"<div class='rrs-waiting-div-message'></div></div> "+ 
			"<div id='buttons' style='text-align:center;'>"+
			"<input type='button' id='YesButton' value='"+i18n["yes"][lang]+", "+i18n["delete"][lang]+"'>"+
			"<input type='button' id='NoButton' value='"+i18n["no"][lang]+", "+i18n["cancel"][lang]+"'>"+
			"</div></form></div></div>", {

			onShow: function modalShow(dialog) {
				// fill out table
				dialog.data.find("#info").html(options.info)
				dialog.data.find("#YesButton").click(function(ev) {
			
				jQuery.ajax( {
					type:"POST", url:options.servlet_url,	
					contentType:"application/x-www-form-urlencoded",
					data: options.postdata,
					beforeSubmit: waitMessageBeforeSubmit(lang),
					success: function(response) {
						if (response['status'] == -1) {
							errorMessageWaitingDiv(lang, response['message'])
							dialog.data.find("#YesButton").attr("value",i18n['retry'][lang])
							dialog.data.find("#buttons").show()	
						} else if (response['status'] == 0)  {
							showCustomMessageWaitingDiv(options.success_message)
							dialog.data.find("#YesButton").hide()
							dialog.data.find("#NoButton").attr("value",i18n["OK"][lang])
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
	};
	
	return -{
		"genericDeleteModal" : genericDeleteModal
	}
	
}(jQuery));

