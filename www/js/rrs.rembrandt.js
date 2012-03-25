
var submissionLang = ""

$(document).ready(function() {
	
	fillMenusAndModals($("#rrs-messages"))

   $("#rembrandt-submit-button").live("click",function(ev) {
		
		ev.preventDefault();
		var submitbutton = $(this)
		var form = $(this).parents("FORM")
		submitbutton.attr('disabled',true)
		// now, what's the rembrandt-results target DIV? It's hidden on a FORM attribute, TARGET. 
		var display = $("#"+form.attr("target"))

		// wipe everything
		wipeDisplay(display); 
		// now show it...
		display.show().slideDown("fast")
				
		submissionlang = $("#submissionLang").val()
		var text= $("#text").val()

		jQuery.ajax( { type:"POST", url:restlet_rembrandt_url, 
		    contentType:"application/x-www-form-urlencoded",
			data: "db="+urlencode(encode_utf8(text))+"&slg="+submissionlang+"&lg="+lang+
			"&api_key="+guest_api_key,
			beforeSubmit: waitMessageBeforeSubmit(lang),
			// respond with JSON
			success: function (response) {
				if (response["status"] == -1) {
					errorMessageWaitingDiv(lang, response['message'])		
				} else {
					hideWaitingDiv()
				
				   var doc = response['message']["document"]

					var htmltitle = rembrandt2HTML(doc["title"])
					var htmlbody = rembrandt2HTML(doc["body"])
				
					// clear loading animation
				//	$("#rrs-doc-display-status", display).html("Ready.")

					addDocumentTitleToDocDisplay(display, htmltitle)
					addDocumentBodyToDocDisplay(display, htmlbody)
					
					// setup NE DIVs				
					setupNEs(display)	// sets both, I hope	
				}
			},
			error: function () {
				errorMessageWaitingDiv(lang, response['message'])		
			}
		});
		debug("done")

		submitbutton.attr('disabled',false)
	});
});

/*******************/
/** AUX FUNCTIONS **/
/*******************/
				
function HTML2Rembrandt(sourcecode) {
	if (!sourcecode) return sourcecode
	sourcecode = sourcecode.replace(/([\Q[]{}\E])/ig,"\\$1") // escape terms
	sourcecode = sourcecode.replace(/<li[^>]*>/ig,"[") // replace <li>
	sourcecode = sourcecode.replace(/<\/li>/ig,"]") // replace </li>
	sourcecode = sourcecode.replace(/<ul[^>]*>/ig,"{") // replace <ul>
	sourcecode = sourcecode.replace(/<\/ul>/ig,"}") // replace </ul>
	// now, we have only divs with classes: ALT, SUBALT, NE
	// let's collect them in a stack, so that we can know its matching closing tag

	var closedivs = new Array();
	var re=/<(\/?)div([^>]*)>/ig
	var re2=/(\w+)="(.+?)"/ig
	
	sourcecode = sourcecode.replace(re, function(m, g1, g2) {
		var answer = ""
	
		// opening DIV
		if (g1 == "") {

			var hash_attrs = new Array();

			// hash attrs
			g2.replace(re2, function(m, g3, g4) {
				 
				 hash_attrs[g3.toUpperCase()]=g4
			})
			 // get classes
			 var classes = hash_attrs["CLASS"].split(/ /)
			
			// SUBALT
			 if (jQuery.inArray("SUBALT", classes) >= 0) {					
				
				answer = "<A"+hash_attrs["ALT"]+">"
				closedivs.push("</A"+hash_attrs["ALT"]+">")
				//ALT
			 } else if (jQuery.inArray("ALT", classes)>= 0) {
				
				answer= "<ALT>"
				closedivs.push("</ALT>")
				//NE
			} else if (jQuery.inArray("NE", classes)>= 0) {
				
				var netag = "<"+i18n["ne"][lang]
				closedivs.push("</EM>")
				netag += " ID=\""+hash_attrs["ID"]+"\""
				netag += " S=\""+hash_attrs["S"]+"\""
				netag += " T=\""+hash_attrs["T"]+"\""
				if (hash_attrs["C1"]) netag += " C1=\""+hash_attrs["C1"]+"\""
				if (hash_attrs["C2"]) netag += " C2=\""+hash_attrs["C2"]+"\""
				if (hash_attrs["C3"]) netag += " C3=\""+hash_attrs["C2"]+"\""
				if (hash_attrs["WK"]) netag += " WK=\""+hash_attrs["WK"]+"\""
				if (hash_attrs["DB"]) netag += " DB=\""+hash_attrs["DB"]+"\""
				if (hash_attrs["RI"]) netag += " RI=\""+hash_attrs["RI"]+"\""
				if (hash_attrs["RT"]) netag += " RT=\""+hash_attrs["RT"]+"\""
				netag += ">"
				answer = netag
			}
		// closing div
		} else if (g1 == "/") {
			answer = closedivs.pop()
		}
		return ""+answer
	})
	return sourcecode.replace(/</g,"&lt;").replace(/>/g,"&gt;")
}

function rembrandt2HTML(response) {
	if (!response) return response
	//debug("Response length: "+response.length)
	var state = 0;
	var sindex = 0;
	var tindex = 0;
	var response2 = ""
	for(i=0; i<response.length; i++) {
		
		switch(state) {
			case 0:
            if (response.charAt(i) == "{") {
                response2 += "<UL S=\""+(sindex++)+"\">";
				tindex = 0; state = 1;
            } 
            break;
            case 1:
             if (response.charAt(i) == "[") {
                response2 += "<LI T=\""+(tindex++)+"\">";
                state=2;
             } else if (response.charAt(i) == "}") {
                 response2 += "</UL>";
                 state=0;
             } else {
        	 // it's a tag info... transcribe it raw
	 			response2 += response.charAt(i);
             }
   			 break;
        	 case 2:
             if (response.charAt(i) == "]") {
                response2 += "</LI>"
                state=1;
             } else if (response.charAt(i)  == "\\") {
                state=99;
                // add for now... we may keep it, we may remove it.
                response2 += response.charAt(i);
             } else {
                response2 += response.charAt(i);
             }
             break
             case 99:
             if ((response.charAt(i) == "[") || (response.charAt(i) == "]") || 
			     (response.charAt(i) == "{") || (response.charAt(i) == "}")) {
                // replace last character
                 response2 = (response2.substring(0, response2.length-1)+ response.charAt(i));
             } else { 
                 response2 += response.charAt(i);
             }
             state = 2;
             break
         }//switch		
	}//for each char
	
	/* reconvert EM tags to DIV */
	response2 = response2.replace(/<EM/gi,"<DIV").replace(/<\/EM>/gi,"<\/DIV>")
        response2 = response2.replace(/<NE/gi,"<DIV").replace(/<\/NE>/gi,"<\/DIV>")
	response2 = response2.replace(/<ALT>/gi,"<DIV CLASS=\"ALT\">").replace(/<\/ALT>/gi,"<\/DIV>")
	response2 = response2.replace(/<SUBALT>/gi,"<DIV CLASS=\"SUBALT\" ALT=\"$1\"").replace(/<\/SUBALT>/gi,"<\/DIV>")

	var d = $(document.createElement("DIV")).append(response2)
	// this is where jQuery rules... let's aim for all DIVs that are NOT ALT or SUBALT
	d.find("DIV:not(DIV.ALT, DIV.SUBALT)").each(function() {
		$(this).addClass("NE")
		$(this).addClass($(this).attr("C1")).addClass($(this).attr("C2")).addClass($(this).attr("C3"))	
	});
//	debug("Convert: "+d.html())
	return d.html()	 
}

