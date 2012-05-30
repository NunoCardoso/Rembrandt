var Rembrandt = Rembrandt || {};

Rembrandt.Waiting = (function ($) {
	"use strict"
	$(function () {
		
		$(document).ajaxError(function(e, xhr, settings, exception)  {
			$('.rrs-waiting-div-message').html(
				_getErrorMessage(i18n['service-unavailable-sorry'][lang] +
				": "+exception+ " "+ _getCloseButton()))
				console.log('erro a pedir ' + settings.url + ' \n'+'error:\n');
				console.log( xhr.responseText ); 
				console.log('e ' + e + ' \n'+'excepcao:\n');
				console.log( exception ); 
				$(".rrs-waiting-div").fadeIn()
		});
	
		$('.rrs-waiting-div-message A').live('click', function(ev) {
			ev.preventDefault();
			$('.rrs-waiting-div').fadeOut("fast")
		})
	});

	var _getCloseButton = function() {
		return "<A HREF='#' CLASS='rrs-waiting-div-close-button'>x</A>";
	},

	_getWaitMessage = function (message) {
		return "<div class='rrs-wait-message'>"+
		"<img src=\"img/loading.gif\"> "+message  + "</div>"
	},

	_getOkMessage = function (message) {
		return "<div class='rrs-ok-message'>"+message +"</div>"
	},
	
	_getErrorMessage = function (message) {
		return "<div class='rrs-error-message'>"+
		i18n['error'][lang]+": "+
		(message ? message : i18n['no-info-available'][lang]) +
		"</div>"
	},
	
	show = function(options) {
		if (!options) options = {} 
		var target = (!_.isUndefined(options.target) ? options.target : $(".rrs-waiting-div") ),
		    language = (!_.isUndefined(options.lang) ? options.lang : lang),
		    message = (!_.isUndefined(options.message) ? options.message : i18n['wait-please'][language])
		target.find($(".rrs-waiting-div-message")).html(_getWaitMessage(message))
		target.fadeIn("fast")
	},
	
	hide = function(options) {
		if (!options) options = {} 
		var target = (!_.isUndefined(options.target) ? options.target : $(".rrs-waiting-div") ),
		    language = (!_.isUndefined(options.lang) ? options.lang : lang),
		    message = (!_.isUndefined(options.message) ? options.message : i18n['OK'][language]),
			when = (!_.isUndefined(options.when) ? options.when : 1000)
		target.find($(".rrs-waiting-div-message")).html(_getOkMessage(message))
		target.delay(when)
		if (when != 0) {
			target.fadeOut("fast")
		}
	},
	
	error = function (response) {
		var res
		if (typeof(response) == "string" && 
			(response.startsWith("<html>") || response.startsWith("<!DOCTYPE HTML")) ) {
				response.replace(/<body>(.*?)<\/body>/ig, function(m, g1) {res = g1})
		} else if (response != null && typeof(response) == "object") {
			if (response.status == 503 || response.status == 500) {
				res = response.statusText
			} else if (response['status'] == -1 && response['message']) {
				res = response['message']
			}
		} else {
			res = response
		}
	
		$(".rrs-waiting-div-message").html(
			_getErrorMessage(res+ _getCloseButton() ))
		$(".rrs-waiting-div").fadeIn("fast")
	};
	
	return {
		"show" : show,
		"hide" : hide,
		"error" : error
	}
}(jQuery));
