package com.flysolo.etrikedriver.screens.main.bottom_nav.utils

import com.google.android.gms.maps.model.LatLng


// Function to decode polyline encoded string into a list of LatLng points
fun decodePolyline(encoded: String): List<LatLng> {
    val polyline = mutableListOf<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var shift = 0
        var result = 0
        var byte: Int
        do {
            byte = encoded[index++].toInt() - 63
            result = result or (byte and 0x1f shl shift)
            shift += 5
        } while (byte >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            byte = encoded[index++].toInt() - 63
            result = result or (byte and 0x1f shl shift)
            shift += 5
        } while (byte >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        polyline.add(LatLng(lat / 1E5, lng / 1E5))
    }

    return polyline
}