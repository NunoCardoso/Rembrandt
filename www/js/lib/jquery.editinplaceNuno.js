/*
 * Another In Place Editor - a jQuery edit in place plugin
 *
 * Copyright (c) 2009 Dave Hauenstein
 *
 * License:
 * This source file is subject to the BSD license bundled with this package.
 * Available online: {@link http://www.opensource.org/licenses/bsd-license.php}
 * If you did not receive a copy of the license, and are unable to obtain it,
 * email davehauenstein@gmail.com,
 * and I will send you a copy.
 *
 * Project home:
 * http://code.google.com/p/jquery-in-place-editor/
 *
 */

/*
 * Version 1.0.1
 *
 * bg_out (string) default: transparent hex code of background color on restore from hover
 * bg_over (string) default: #ffc hex code of background color on hover
 * callback (function) default: null function to be called when editing is complete; cancels ajax submission to the url param
 * cancel_button (string) default: <input type=”submit” class=”inplace_cancel” value=”Cancel”/> image button tag to use as “Cancel” button
 * default_text (string) default: “(Click here to add text)” text to show up if the element that has this functionality is empty
 * element_id (string) default: element_id name of parameter holding element_id
 * error (function) this function gets called if server responds with an error
 * field_type (string) “text”, “textarea”, or “select”; default: “text” The type of form field that will appear on instantiation
 * on_blur (string) “save” or null; default: “save” what to do on blur; will be overridden if $param show_buttons is true
 * original_html (string) default: original_html name of parameter holding original_html
 * params (string) example: first_name=dave&last_name=hauenstein paramters sent via the post request to the server
 * save_button (string) default: <input type=”submit” class=”inplace_save” value=”Save”/> image button tag to use as “Save” button
 * saving_image (string) default: uses saving text specify an image location instead of text while server is saving
 * saving_text (string) default: “Saving…” text to be used when server is saving information
 * select_options (string) comma delimited list of options if field_type is set to select
 * select_text (string)default text to show up in select box
 * show_buttons (boolean) default: false will show the buttons: cancel or save; will automatically cancel out the onBlur functionality
 * success (function) default: null this function gets called if server responds with a success
 * textarea_cols (integer) default: 25 set cols attribute of textarea, if field_type is set to textarea
 * textarea_rows (integer) default: 10 set rows attribute of textarea, if field_type is set to textarea
 * update_value (string) default: update_value name of parameter holding update_value
 * url (string) POST URL to send edited content
 * value_required (string) default: false if set to true, the element will not be saved unless a value is entered
 *
 */

jQuery.fn.editInPlace = function(options) {

	/* DEFINE THE DEFAULT SETTINGS, SWITCH THEM WITH THE OPTIONS USER PROVIDES */
	var settings = {
		url:				"",
		params:				"",
		field_type:			"text",
		// added by Nuno
		dataType:           "html",
		select_options:		"",
		textarea_cols:		"25",
		textarea_rows:		"10",
		bg_over:			"#ffc",
		bg_out:				"transparent",
		saving_text:		"Saving...",
		saving_image:		"",
		default_text:		"(Click here to add text)",
		select_text:		"Choose new value",
		value_required:		null,
		element_id:			"element_id",
		update_value:		"update_value",
		original_html:		"original_html",
		save_button:		'<button class="inplace_save">Save</button>',
		cancel_button:		'<button class="inplace_cancel">Cancel</button>',
		show_buttons:		false,
		on_blur:			"save",
		callback:			null,
		// added by Nuno
		newvalue:  			null,
		success:			null,
		error:				function(request){
          alert("Failed to save value: " + request.responseText || 'Unspecified Error');
        }
	};

	if(options) {
		jQuery.extend(settings, options);
	}

	/* preload the loading icon if it exists */
	if(settings.saving_image != ""){
		var loading_image = new Image();
		loading_image.src = settings.saving_image;
	}

	/* THIS FUNCTION WILL TRIM WHITESPACE FROM BEFORE/AFTER A STRING */
	String.prototype.trim = function() {
		return this.replace(/^\s+/, '')
		.replace(/\s+$/, '');
	};

	/* THIS FUNCTION WILL ESCAPE ANY HTML ENTITIES SO "Quoted Values" work */
	String.prototype.escape_html = function() {
		return this.replace(/&/g, "&amp;")
							 .replace(/</g, "&lt;")
							 .replace(/>/g, "&gt;")
							 .replace(/"/g, "&quot;");
  };

	/* CREATE THE INPLACE EDITOR */
	return this.each(function(){

		if(jQuery(this).html() == "") jQuery(this).html(settings.default_text);

		var editing = false;

		//save the original element - for change of scope
		var original_element = jQuery(this);

		var click_count = 0;

		jQuery(this)

		.mouseover(function(){
			jQuery(this).css("background", settings.bg_over);
		})

		.mouseout(function(){
			jQuery(this).css("background", settings.bg_out);
		})

		.click(function(){
			click_count++;

			if(!editing) {
				editing = true;

				//save original text - for cancellation functionality
				var original_html = jQuery(this).html();
				var buttons_code  = (settings.show_buttons) ? settings.save_button + ' ' + settings.cancel_button : '';

				//if html is our default text, clear it out to prevent saving accidentally
				if (original_html == settings.default_text) jQuery(this).html('');

				if (settings.field_type == "textarea") {
					var use_field_type = '<textarea name="inplace_value" class="inplace_field" '+//rows="' + settings.textarea_rows + '" cols="' + settings.textarea_cols + '">'
					'style="width:100%;">' + jQuery(this).text().trim().escape_html() + '</textarea><BR>';
				} else if(settings.field_type == "text") {
					var use_field_type = '<input type="text" name="inplace_value" class="inplace_field" value="' +
							jQuery(this).text().trim().escape_html() + '" />';
				} else if(settings.field_type == "select") {
					var optionsArray = settings.select_options.split(',');
					var use_field_type = '<select name="inplace_value" class="inplace_field"><option value="">' + settings.select_text + '</option>';
					for(var i=0; i<optionsArray.length; i++){
						var optionsValuesArray = optionsArray[i].split(':');
						var use_value = optionsValuesArray[1] || optionsValuesArray[0];
						var selected = use_value == original_html ? 'selected="selected" ' : '';
						use_field_type += '<option ' + selected + 'value="' + use_value.trim().escape_html() + '">' + optionsValuesArray[0].trim().escape_html() + '</option>';
                    }
					use_field_type += '</select>';
				}
				// added by Nuno
				else if(settings.field_type == "autocompletetextfield") {
					// let's just add an ID. When I add the HTML to the DOM, make the autocomplete.
					var autocomplete_id = jQuery(this).attr("CONTEXT")+"-"+jQuery(this).attr("ID")
					var use_field_type = '<input id="'+autocomplete_id+'" type="text" name="inplace_value" class="inplace_field" value="' +
					jQuery(this).text().trim().escape_html() + '" /><BR>';
				}
			
				/* insert the new in place form after the element they click, then empty out the original element */
				jQuery(this).html('<form class="inplace_form" style="display: inline; margin: 0; padding: 0;">' + use_field_type + ' ' + buttons_code + '</form>');
				var what = jQuery(this).attr("COL")
				
				//debug(settings.field_type+" "+autocomplete_id)
				// added by Nuno
				if(settings.field_type == "autocompletetextfield") {
					$('#'+autocomplete_id).autocomplete(restlet_dbosuggestion_url, {
						minChars:2, dataType: "json", 
						multiple: false,
						mustMatch: false, autoFill: false, matchContains: false,
						extraParams:{"t" : what},
						parse: theParse, 
						formatItem: theFormatItem
						//formatMatch: theFormatMatch,
						//formatResult: theFormatResult
					})
					
				// what to do when an option is selected.
				// default behavior is like a submit, not good. 
					$('#'+autocomplete_id).result(theResult)
				}

			}/* END- if(!editing) -END */

			if(click_count == 1)
			{
				function cancelAction()
				{
					editing = false;
					click_count = 0;

					/* put the original background color in */
					original_element.css("background", settings.bg_out);

					/* put back the original text */
					original_element.html(original_html);

					return false;
				}

				function saveAction()
				{
					/* put the original background color in */
					original_element.css("background", settings.bg_out);

                    var this_elem = jQuery(this);

// added by Nuno 
					var new_html = (this_elem.is('form')) ? this_elem.children(0).val() : this_elem.parent().children(0).val();
                    if (settings.newvalue) {
                  		new_html = settings.newvalue(new_html)
					}
					/* set saving message */
					if(settings.saving_image != ""){
						var saving_message = '<img src="' + settings.saving_image + '" alt="Saving..." />';
					} else {
						var saving_message = settings.saving_text;
					}

					/* place the saving text/image in the original element */
					original_element.html(saving_message);

					if(settings.params != ""){
// added by Nuno - there's a bug where a resubmission gives two &&						
                       if (!settings.params.startsWith("&")) {
                          settings.params = "&" + settings.params;
					}
 }

// added by Nuno - let's lake callback more useful - returns true or false to proceed or not...
// good for group selects
                    var goodToGo = true
					if(settings.callback) {
						goodToGo = goodToGo && settings.callback(original_element.attr("id"), new_html, original_html);
					}
					
					if (settings.value_required && (new_html == "" || new_html == undefined)) {
						editing = false;
						click_count = 0;
						original_element.html(original_html);
						alert("Error: You must enter a value to save this field");
                        goodToGo = false
					}
					if (!goodToGo) {
						editing = false;
						click_count = 0;
						original_element.html(original_html);
					} else {
						// edited by Nuno
						var apikey = getAPIKey()
//debug (settings.update_value + '=' + new_html + '&' + settings.element_id + '=' + original_element.attr("id") + settings.params + '&' + settings.original_html + '=' + original_html)
						jQuery.ajax({
							url: settings.url,
							type: "POST",
							data: settings.update_value + '=' + new_html + '&' + settings.element_id + '=' + 
							original_element.attr("id") + settings.params + '&' + settings.original_html + '=' + 
							urlencode(original_html),
							// added by Nuno
							dataType: settings.dataType,
							complete: function(request){
								editing = false;
								click_count = 0;
							},
							success: function(html){
								/* if the text returned by the server is empty, */
   								/* put a marker as text in the original element */
								// changed by Nuno, so that success callback performs before
								if (settings.success) {
									settings.success(html, original_element, original_html)
								}	else {
									var new_text = html || settings.default_text;
									/* put the newly updated info into the original element */
									original_element.html(new_text);
								}
							},
							error: function(request) {
// Changed by Nuno Cardoso
								if (settings.error) settings.error(request, original_element, original_html);
								else original_element.html(original_html);

							}
						});
					}

					return false;
				}

				/* set the focus to the new input element */
				original_element.children("form").children(".inplace_field").focus().select();

				/* CLICK CANCEL BUTTON functionality */
				original_element.children("form").children(".inplace_cancel").click(cancelAction);

				/* CLICK SAVE BUTTON functionality */
				original_element.children("form").children(".inplace_save").click(saveAction);

                /* if cancel/save buttons should be shown, cancel blur functionality */
                if(!settings.show_buttons){
                    /* if on_blur is set to save, set the save funcion */
    				if(settings.on_blur == "save")
    					original_element.children("form").children(".inplace_field").blur(saveAction);
    				/* if on_blur is set to cancel, set the cancel funcion */
    				else
    					original_element.children("form").children(".inplace_field").blur(cancelAction);
                }

				/* hit esc key */
				$(document).keyup(function(event){
				    if (event.keyCode == 27) {
						cancelAction();
				    }
				});

                original_element.children("form").submit(saveAction);

			}/* END- if(click_count == 1) -END */
		});
	});
};
						
function theFormatItem(row) {return "[ID:"+row[0]+"] "+row[1]}
function theFormatMatch(row) {
debug("theFormatMatch invoked for")
debug(row)
debug("returning "+row[1])
 return row[1]} // + " | " + row[1]}
function theFormatResult(row) {
   debug ('theformatresult')
  debug(row)
return row[1]} // 
function theResult(event, data, formatted) {
// data returns ['2','ncardoso'], formatted returns ncardoso (as given by the FormatResult)
  debug("theresult")
  debug(data[1])
return data[1]
}
							
function theParse(data) {
debug("theParse invoked for")
debug(data)
	var parsed = []
	var rows = data['message']
	for (var i=0; i < rows.length; i++) {
		parsed[parsed.length] = {
			data: rows[i], value: rows[i][0], result: rows[i][0]
		}
	}
debug("returning")
debug(parsed)
	return parsed
}