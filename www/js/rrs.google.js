
/** note that points_array not only contains latitude and longitude,
but also references on the document and position.*/
function createGoogleMap(map_div, points_array, polylines_array) {

         var map = new GMap2(document.getElementById(map_div.attr('id')))
        // definir os pontos
         var points = [];
		 var polylines = [];
		 for (var i=0; i < points_array.length; i++) { 
         	points.push(new GLatLng(points_array[i]['Latitude'],points_array[i]['Longitude']));
         //points.push(new GLatLng(-14.1,-49.6));
         }

// A polyline must be splitted by space, then by comma (ex: -34.497291564941,-58.408298492432)
		for (var i=0; i < polylines_array.length; i++) { 
			var raw_points_array =  polylines_array[i].split(" ")
			var polyline_points = [];
			for (var j in raw_points_array) {
				var index = raw_points_array[j].indexOf(",")
				if (index > 0) {
					polyline_points.push(new GLatLng(
						raw_points_array[j].substring(0, index), 
						raw_points_array[j].substring(index+1,raw_points_array[j].length)));
				}
			}
			
			polylines.push(new GPolyline(polyline_points));
         }
        // definir a BB
        var bounds = new GLatLngBounds;
        for (var i=0; i<points.length; i++) {
                bounds.extend(points[i]);
    	}
    	//document.write(bounds.getCenter());
        var zoom = map.getBoundsZoomLevel(bounds);
        var center = bounds.getCenter()
        map.setCenter(center)
    	map.setZoom(zoom)

        var mgr = new MarkerManager(map);
		var markers = new Array()
        for (var i=0; i<points_array.length; i++) {
      	
			var icon = new GIcon(G_DEFAULT_ICON);
			
			var position = points_array[i]["Position"]
			// use the document position, if available, to select numbered icons
			if (position) {
				icon.image = "img/maps/iconr"+position+".png"
			} 
		//	var marker = new GMarker(points[i])
			var marker = createMarker(points[i], icon, position)
			markers.push(marker)	
     	
        }  
 	 mgr.addMarkers(markers,zoom);

	 for (var i=0; i<polylines.length; i++) {
	 	map.addOverlay(polylines[i])
	}

     map.addControl(new GLargeMapControl())
     mgr.refresh()
}


function createMarker(points, icon, position) {
	var marker = new GMarker(points, {icon:icon})
			// add the reference to the A.name position
			marker.value="pos"+position
		
		// add an event in the marker, so that it jumps to the document snippet 
			GEvent.addListener(marker, 'click', function(ev) {
				// get the value of the marker, it has the Anchor name.
				// clean previous anchor name, insert new one in the location
				var index = window.location.href.indexOf("#")
			   window.location.href = window.location.href.substring(0, 
				 (index > 0 ? index : window.location.href.length))+"#"+marker.value
				
				$(".rrs-snippet").removeClass("rrs-searchresult-snippet-active")
				$("#rrs-snippet-"+marker.value).addClass("rrs-searchresult-snippet-active")
				
			});
		
		return marker
}

/*var polyline = new GPolyline([
  new GLatLng(37.4419, -122.1419),
  new GLatLng(37.4519, -122.1519)
], "#ff0000", 10);
map.addOverlay(polyline);*/