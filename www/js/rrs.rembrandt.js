Rembrandt = Rembrandt || {};

Rembrandt.Api = (function ($) {
	"use strict"

	$(function () {

		$("#rembrandt-submit-button").live("click",function(ev) {

			ev.preventDefault();
			var submitbutton = $(this)
			var form = $(this).parents("FORM")
			submitbutton.attr('disabled',true)
			// now, what's the rembrandt-results target DIV? It's hidden on a FORM attribute, TARGET. 
			var display = $("#"+form.attr("target"))

			// wipe everything
			Rembrandt.Display.wipe(display); 

			var submissionlang = $("#submissionLang").val() || lang;
			var text= $("#text").val()

			jQuery.ajax({ 
				type:"POST", 
				url:Rembrandt.urls.restlet_rembrandt_url, 
				contentType:"application/x-www-form-urlencoded",
				data: "db="+Rembrandt.Util.urlEncode(Rembrandt.Util.encodeUtf8(text))+
					"&slg="+submissionlang+"&lg="+lang+
					"&api_key="+Rembrandt.constants.guest_api_key+"&f=json",
				beforeSubmit: Rembrandt.Waiting.show(),
				// respond with JSON
				success: function (response) {
					if (response["status"] == -1) {
						Rembrandt.Waiting.error(response)
					} else {
						Rembrandt.Waiting.hide()
						var doc = response['message']["doc"]
						var nes = (!_.isUndefined (response['message']["nes"]) ? response['message']["nes"] : null)
						var patches = (!_.isUndefined (response['message']["patches"]) ? response['message']["patches"] : null)
						var commits = (!_.isUndefined (response['message']["commits"]) ? response['message']["commits"] : null)
						
						var doc_content = doc["doc_content"]
						var html = Rembrandt.Api.Rembrandt2HTML(doc_content, nes)
						Rembrandt.Display.addDoc(display, doc)
						Rembrandt.Display.addDocumentContent(display, html["title"], html["body"])
						Rembrandt.Display.addOriginalNEs(display, nes)
						Rembrandt.Display.addPatches(display, patches)
						Rembrandt.Display.addCommits(display, commits)
						Rembrandt.DisplayNE.setupNEs(display)
					}
				},
				error: function (response) {
					Rembrandt.Waiting.error(response)
				}
			});

			submitbutton.attr('disabled',false)
		});
	});

	var _parseText = function(response) {
		var response2 = ""
		var state = 0;
		var sindex = 0;
		var tindex = 0;
		for(var i=0; i<response.length; i++) {

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
		return response2
	}, 
	/*
	Rembrandt2HTML = function (response) {

		if (!response) return response
		var response2 = _parseText(response)

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
		return d.html()	 
	},*/
	
	NE2HTML = function(ne, nr_sentence, nr_term, no_body) {
		var ne_div = "<DIV "
		var ne_class = "CLASS='NE "
		var ne_attr = "S='"+nr_sentence+"' T='"+nr_term+"' "
		var no_body = (_.isUndefined(no_body) ? false : no_body)
		
		if (!_.isUndefined(ne.ne_category) && ne.ne_category != null) {
			var cat = ne.ne_category.nec_category.replace(/@/,"")
			ne_class += cat+" "
			ne_attr += "CATEGORY='"+cat+"' "
		} 
		if (!_.isUndefined(ne.ne_type) && ne.ne_type != null) {
			var cat = ne.ne_type.net_type.replace(/@/,"")
			ne_class += cat+" "
			ne_attr += "TYPE='"+cat+"' "
		}
		if (!_.isUndefined(ne.ne_subtype) && ne.ne_subtype != null) {
			var cat = ne.ne_subtype.nes_subtype.replace(/@/,"")
			ne_class += cat+" "
			ne_attr += "SUBTYPE='"+cat+"' "
		}
		if (!_.isUndefined(ne.ne_entity) && ne.ne_entity != null) {
			ne_attr += "ENTITY_ID='"+ne.ne_entity.ent_id+"' "
			ne_attr += "ENTITY_CLASS='"+ne.ne_entity.ent_dbpedia_class+"' "
			ne_attr += "ENTITY_RESOURCE='"+ne.ne_entity.ent_dbpedia_resource+"' "
		}

		ne_class += "' "
		ne_div += ne_class
		ne_div += ne_attr
		ne_div += "NE_ID='"+ne.ne_id+"' "
		ne_div += "NE_LANG='"+ne.ne_lang+"' "
		if (no_body) {
			ne_div += "/>"
		} else {
			ne_div += ">"+ne.ne_name.nen_name+"</DIV>"
		}
		return ne_div
	},
	
	// this one has dhn e nes separated from doc
	Rembrandt2HTML = function (doc_content, nes) {

		if (!doc_content) return doc_content
		var title = _parseText(doc_content["title"])
		var body = _parseText(doc_content["body"])
		
		var title_j = $(document.createElement("DIV")).append(title)
		var body_j = $(document.createElement("DIV")).append(body)
		
		for (var i in nes) {
			var ne = nes[i].ne,
				nr_sentence = nes[i].sentence,
				nr_term = nes[i].term,
				section = nes[i].section,
				ne_div = NE2HTML(ne, nr_sentence, nr_term, true),
				sentence,
				next_selectors = [],
				terms
			
			// now, build the selector to wrap them
			if (section == "T") {
				sentence = title_j.find("UL[s="+nr_sentence+"]")
			} else if (section == "B") {
				sentence = body_j.find("UL[s="+nr_sentence+"]")
			}
			
			for (var i = nr_term; i < nr_term + ne.ne_name.nen_nr_terms; i++) {
				next_selectors.push("LI[T="+i+"]")
			}
			
			terms = sentence.find(next_selectors.join(","))
			terms.wrapAll(ne_div)
		}

		return {
			"title":title_j.html(),
			"body": body_j.html()
		}
	};
	
	
	return {
		"Rembrandt2HTML"	: Rembrandt2HTML,
		"NE2HTML"			: NE2HTML
	};
}(jQuery));
