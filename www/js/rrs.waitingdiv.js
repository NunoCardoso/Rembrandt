// functions regarding waiting div
$(document).ready(function() {
   $(document).ajaxError(function(e, xhr, settings, exception)  {
		$('.rrs-waiting-div-message').html(errormessage(lang, i18n['service-unavailable-sorry'][lang] + getWaitingDivCloseButton()))	
		debug('erro a pedir ' + settings.url + ' \n'+'error:\n' + xhr.responseText ); 
		debug('e ' + e + ' \n'+'excepcao:\n' + exception ); 
		$(".rrs-waiting-div").show()
	});
	
	$('.rrs-waiting-div-message A').live('click', function(ev) {
		ev.preventDefault();
		$('.rrs-waiting-div').hide("fast")
	})
})

function getWaitingDivCloseButton() {
	return "<A HREF='#' CLASS='rrs-waiting-div-close-button'>x</A>";
}

function waitmessage(language, message) {
	return "<div style='text-align:left'>"+
	"<img src=\"img/loading.gif\"> "+(message ? message : i18n['wait-please'][language]) + "</div>"
}

function waitfunction(language, display) {
    display.html(waitmessage(language));
    display.slideDown("slow");
}


function errormessage(language, message) {
	if (isUndefined(language)) languge = "en"
	return "<div class='rrs-error-message'>"+i18n['error'][language]+": "+
	(message ? message : i18n['no-info-available'][language]) +"</div>"
}

function waitMessageBeforeSubmit(lang) {
	$(".rrs-waiting-div-message").html(waitmessage(lang))
	$(".rrs-waiting-div").show()
}


function showCustomMessageWaitingDiv(message) {
	$(".rrs-waiting-div-message").html(message)
	$(".rrs-waiting-div").show()
}

function hideWaitingDiv() {
	$(".rrs-waiting-div").hide("fast")
}

function errorMessageWaitingDiv(lang, response) {
//	debug(response)
//	debug(typeof(response))
	var res
	if (typeof(response) == "string" && 
	   (response.startsWith("<html>") || response.startsWith("<!DOCTYPE HTML")) ) {
		sourcecode.replace(/<body>(.*?)<\/body>/ig, function(m, g1) {res = g1})
	} else if (response != null && typeof(response) == "object") {
		if (response.status == 503 || response.status == 500) {
			res = response.statusText
		} else if (response['status'] == -1 && response['message']){
			res = response['message']
		}
	} else {
		res = response
	}
	$(".rrs-waiting-div-message").html(errormessage(lang, res+ getWaitingDivCloseButton()))
	$(".rrs-waiting-div").show()
}
