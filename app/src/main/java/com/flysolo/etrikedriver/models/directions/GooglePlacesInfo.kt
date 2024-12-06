package com.flysolo.etrikedriver.models.directions
data class Distance(
    val text: String? = null,
    val value: Int? = null
)

data class Duration(
    val text: String? = null,
    val value: Int? = null
)

data class GeocodedWaypoints(
    val geocoder_status: String? = null,
    val place_id: String? = null,
    val types: List<String>? = null
)

data class Legs(
    val distance: Distance? = null,
    val duration: Duration? = null,
    val start_address: String? = null,
    val end_address: String? = null
)

data class OverviewPolyline(
    val points: String? = null
)

data class Routes(
    val summary: String? = null,
    val overview_polyline: OverviewPolyline? = null,
    val legs: List<Legs>? = null
)

data class GooglePlacesInfo(
    val geocoded_waypoints: List<GeocodedWaypoints>? = null,
    val routes: List<Routes>? = null,
    val status: String? = null
)
