var restlet_saskia_user_url = 	"/Saskia/user";
var restlet_saskia_task_url =		"/Saskia/task"
var restlet_saskia_search_url = 	"/Saskia/search";
var restlet_renoir_search_url = 	"/Renoir/search";
var restlet_saskia_stats_url = 	"/Saskia/stats";
var restlet_saskia_collection_url = "/Saskia/collection";
var restlet_saskia_rdoc_url = 	"/Saskia/rdoc";
var restlet_saskia_sdoc_url = 	"/Saskia/sdoc";
var restlet_saskia_ne_url = 		"/Saskia/ne";
var restlet_suggestion_url = 		"/Renoir/suggest";
var restlet_dbosuggestion_url =	"/Renoir/dbosuggest";
var restlet_rembrandt_url = 		"/Rembrandt/api/rembrandt";
var mailconfirmregistration = 	"mailconfirmregistration.php";
var mailrecoverpassword = 			"mailrecoverpassword.php";

var selectLang={"pt":"Português","en":"Inglês"}
var selectProc={"OK":"READY","KO":"NOT_READY"}
var selectSync={"SO":"SO","SD":"SD","SN":"SN"}
var selectEdit={"UL":"UNLOCKED","LK":"LOCKED"}

var lang
var guest_api_key = "db924ad035a9523bcf92358fcb2329dac923bf9c";

$(document).ready(function() {
	lang = $("HTML").attr("lang")	
	// adding pause on effects - use like $("#mainImage").pause(5000).fadeOut();
	$.fn.pause = function(duration) {
    	$(this).animate({ dummy: 1 }, duration);
    	return this;
	};
})

// debug
function debug(text) {
	if (!jQuery.browser.msie){	
		if (window.console && console.log) console.log(text)
	}
}
//if (window.opera && opera.postError) opera.postError(text);

function decode_utf8( s ){
  	for(var a, b, i = -1, l = (s = s.split("")).length, o = String.fromCharCode, c = "charCodeAt"; ++i < l;
			((a = s[i][c](0)) & 0x80) &&
			(s[i] = (a & 0xfc) == 0xc0 && ((b = s[i + 1][c](0)) & 0xc0) == 0x80 ?
			o(((a & 0x03) << 6) + (b & 0x3f)) : o(128), s[++i] = "")
		);
		return s.join("");

}

function encode_utf8( s ){
		for(var c, i = -1, l = (s = s.split("")).length, o = String.fromCharCode; ++i < l;
			s[i] = (c = s[i].charCodeAt(0)) >= 127 ? o(0xc0 | (c >>> 6)) + o(0x80 | (c & 0x3f)) : s[i]
		);
		return s.join("");
}

function emailCheck (emailStr) {
  // credits: Sandeep V. Tamhankar (stamhankar@hotmail.com) 
  /* The following variable tells the rest of the function whether or not
  to verify that the address ends in a two-letter country or well-known
  TLD.  1 means check it, 0 means don't. */
  var checkTLD=1;
 /* The following is the list of known TLDs that an e-mail address must end with. */
  var knownDomsPat=/^(com|net|org|edu|int|mil|gov|arpa|biz|aero|name|coop|info|pro|museum)$/;
  /* The following pattern is used to check if the entered e-mail address
  fits the user@domain format.  It also is used to separate the username
  from the domain. */
  var emailPat=/^(.+)@(.+)$/;
  /* The following string represents the pattern for matching all special
   characters.  We don't want to allow special characters in the address. 
  These characters include ( ) < > @ , ; : \ " . [ ] */
  var specialChars="\\(\\)><@,;:\\\\\\\"\\.\\[\\]";
  /* The following string represents the range of characters allowed in a 
  username or domainname.  It really states which chars aren't allowed.*/
  var validChars="\[^\\s" + specialChars + "\]";
  /* The following pattern applies if the "user" is a quoted string (in
  which case, there are no rules about which characters are allowed
  and which aren't; anything goes).  E.g. "jiminy cricket"@disney.com
  is a legal e-mail address. */
   var quotedUser="(\"[^\"]*\")";
  /* The following pattern applies for domains that are IP addresses,
  rather than symbolic names.  E.g. joe@[123.124.233.4] is a legal
  e-mail address. NOTE: The square brackets are required. */
  var ipDomainPat=/^\[(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\]$/;
  /* The following string represents an atom (basically a series of non-special characters.) */
  var atom=validChars + '+';
  /* The following string represents one word in the typical username.
  For example, in john.doe@somewhere.com, john and doe are words.
  Basically, a word is either an atom or quoted string. */
  var word="(" + atom + "|" + quotedUser + ")";
  // The following pattern describes the structure of the user
  var userPat=new RegExp("^" + word + "(\\." + word + ")*$");
  /* The following pattern describes the structure of a normal symbolic
  domain, as opposed to ipDomainPat, shown above. */
  var domainPat=new RegExp("^" + atom + "(\\." + atom +")*$");
  /* Finally, let's start trying to figure out if the supplied address is valid. */
  /* Begin with the coarse pattern to simply break up user@domain into
  different pieces that are easy to analyze. */
  var matchArray=emailStr.match(emailPat);
  if (matchArray==null) {
	/* Too many/few @'s or something; basically, this address doesn't
	even fit the general mould of a valid e-mail address. */
//	alert("Email address seems incorrect (check @ and .'s)");
	return false;
  }
	var user=matchArray[1];
	var domain=matchArray[2];
	// Start by checking that only basic ASCII characters are in the strings (0-127).
	for (i=0; i<user.length; i++) {
		if (user.charCodeAt(i)>127) {
			//alert("Ths username contains invalid characters.");
			return false;
   		}
	}
	for (i=0; i<domain.length; i++) {
		if (domain.charCodeAt(i)>127) {
			alert("Ths domain name contains invalid characters.");
			return false;
   		}
	}	
	// See if "user" is valid 	
	if (user.match(userPat)==null) {
		// user is not valid
		alert("The username doesn't seem to be valid.");
		return false;
	}
	/* if the e-mail address is at an IP address (as opposed to a symbolic
		host name) make sure the IP address is valid. */
		var IPArray=domain.match(ipDomainPat);
		if (IPArray!=null) {			
			// this is an IP address
			for (var i=1;i<=4;i++) {
				if (IPArray[i]>255) {
					alert("Destination IP address is invalid!");
					return false;
   				}
			}
		return true;
	}
// Domain is symbolic name.  Check if it's valid.
	var atomPat=new RegExp("^" + atom + "$");
	var domArr=domain.split(".");
	var len=domArr.length;
	for (i=0;i<len;i++) {
		if (domArr[i].search(atomPat)==-1) {
			alert("The domain name does not seem to be valid.");
			return false;
   		}
	}
/* domain name seems valid, but now make sure that it ends in a
known top-level domain (like com, edu, gov) or a two-letter word,
representing country (uk, nl), and that there's a hostname preceding 
the domain or country. */
	if (checkTLD && domArr[domArr.length-1].length!=2 && 
		domArr[domArr.length-1].search(knownDomsPat)==-1) {
			alert("The address must end in a well-known domain or two letter " + "country.");
			return false;
		}
// Make sure there's a host name preceding the domain.
	if (len<2) {
		alert("This address is missing a hostname!");
		return false;
	}
	// If we've gotten this far, everything's valid!
	return true;
}

function getCollection() {
	return $("A.collection", $("#rrs-collections")).attr("COLLECTION") 
}

function getCollectionID() {
	return $("A.collection", $("#rrs-collections")).attr("COLLECTION_ID") 
}

function getQueryVariable(key) {
  var query = window.location.search.substring(1);
  var vars = query.split("&");
  for (var i=0;i<vars.length;i++) {
    var pair = vars[i].split("=");
    if (pair[0] == key) {
      return pair[1];
    }
  } 
}

// ROLE can be 'ADMIN' (su), 'COL-ADMIN' (col-admin), 'COL-WRITE' (col-writer), 'COL-READ'
function getRole(button) {
	return button.attr('ROLE')
}

/*function getServletEngineFromPubKey(usr_pub_key, context) {
	return (validateSu(usr_pub_key) ? eval("restlet_admin_"+context+"_url") : eval("restlet_saskia_"+context+"_url")) 
}*/

function getServletEngineFromRole(role, context) {
	if (!role || !context) {
		debug("getServletEngine called without role or context!")
		return false
	} 
	var therole = (role.toLowerCase() == "admin" ? "admin" : "saskia" )
	return eval("restlet_"+therole+"_"+context+"_url")
}

function getUser() {
	return $("#rrs-user").attr("USER") 
}

function getUserID() {
	return $("#rrs-user").attr("USER_ID") 
}

function getAPIKey() {
	var api_key= $("#rrs-user").attr("api_key")
	if (isUndefined(api_key) || api_key == "") api_key=$.cookie("api_key") // if cookie is set   
	if (isUndefined(api_key) || api_key == "") api_key = guest_api_key
	return api_key
}

function getPubKey() {
	return $("#main-body").attr("USR_PUB_KEY")
}

function isUndefined(variable) {
	return typeof(variable)=="undefined" || variable == null
}

function printYesOrNo(item) {
	if (item == true || item == 1) {return i18n["yes"][lang]} 
	if (item == false || item == 0) {return i18n["no"][lang]} 
}

function setPubKey(pub_key) {
	return $("#main-body").attr("USR_PUB_KEY",pub_key)
}

function setQueryVariable(list) {

 var query = window.location.search
  var qmark = ( (query.length == 0 || query.substring(0,1) == "?") ? "?" : "")
  if (qmark == "?") query = query.substring(1)

  var vars = new Array()
  if (query.length > 0) {
	vars = query.split("&");
  	var found = false

    for (key in list) {
		  for (var i=0;i<vars.length;i++) {
    	  var pair = vars[i].split("=");
    	  if (pair[0] == key) {
      		found = true
	  		   vars[i]=pair[0]+"="+list[key];
    		}
  		}
    	if (!found) vars[vars.length] = ""+key+"="+list[key]
	}
  }
  return qmark+vars.join("&")
}

// limit to 25 characters, put ... on it
function shortenTitle(string) {
 if (string.length < 25) return string
 else return string.substring(0,25)+"..."	
}

function UpperCaseFirstLetter(string) {
   return string.substr(0, 1).toUpperCase() + string.substr(1);
}

function urlencode (str) {
	str = escape(str);
	return str.replace(/[*+\/@]|%20/g, function (s) {
	switch (s) {
		case "*": s = "%2A"; break;
		case "+": s = "%2B"; break;
		case "/": s = "%2F"; break;
		case "@": s = "%40"; break;
		case "%20": s = "+"; break;
	}
	return s;
	});
}

function validateSu(usr_pub_key) {	
	
	var su = $.cookie("su")
	if (isUndefined(su) || isUndefined(usr_pub_key)) return false
	return (su == hex_md5(usr_pub_key)) 
}

String.prototype.endsWith = function(str)
{return (this.match(str+"$")==str)}

String.prototype.startsWith = function(str)
{return (this.match("^"+str)==str)}

