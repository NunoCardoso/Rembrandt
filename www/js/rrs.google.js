/** note that points_array not only contains latitude and longitude,
but also references on the document and position.*/

function createGoogleMap(map_div, points_array, polylines_array) {

	var map = map_div.gmap3({action: 'init'})

	for (var i=0; i < points_array.length; i++) { 
		
		var position = points_array[i]["Position"]
		var image;
		if (position) {
			image = "img/maps/iconr"+position+".png"
		} else {
			image = "img/maps/iconr1.png"
		}

		map.gmap3({
			action: 'addMarker',
			latLng:[points_array[i]['Latitude'], points_array[i]['Longitude']],
			marker:{
				options:{
					icon:new google.maps.MarkerImage(image),
					data:points_array[i]["Position"]
				},
				events:{
					click: function(marker){
						var snippet_id = marker.data
						$(".rrs-searchresult-snippet-pos").removeClass("rrs-searchresult-snippet-active")
						$("#rrs-searchresult-snippet-pos"+snippet_id).addClass("rrs-searchresult-snippet-active")
						$('html, body').animate({
						         scrollTop: $("#rrs-searchresult-snippet-pos"+snippet_id).offset().top
						 }, 2000);
						
					}
				}
			}
		})
	}
	
	if (!_.isUndefined(polylines_array) ) {
		// A polyline must be splitted by space, then by comma (ex: -34.497291564941,-58.408298492432)
		for (var i=0; i < polylines_array.length; i++) { 
			var raw_points_array =  polylines_array[i].split(" ")
			var polyline_points = [];
			for (var j in raw_points_array) {
				var index = raw_points_array[j].indexOf(",")
				if (index > 0) {
					polyline_points.push([
						raw_points_array[j].substring(0, index), 
						raw_points_array[j].substring(index+1,raw_points_array[j].length)
					]);
				}
			}
			map.gmap3({ 
				action: 'addPolygon',
				options:{
					strokeColor: "#FF0000",
					strokeOpacity: 0.7,
					strokeWeight: 2,
					fillColor: "#FF0000",
					fillOpacity: 0.35
				},
				paths:polyline_points
			})
		}
	}
	map.gmap3({action:"autofit"})
}