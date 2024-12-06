package com.flysolo.etrikedriver.screens.main.bottom_nav.utils

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.flysolo.etrikedriver.models.directions.GooglePlacesInfo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch


@Composable
fun EtrikeMap(
    modifier: Modifier = Modifier,
    pickUpLocation: LatLng?,
    dropOffLocation: LatLng?,
    info: GooglePlacesInfo?
) {
    val cameraPositionState = rememberCameraPositionState()


    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        onMapLoaded = {
            if (pickUpLocation != null && dropOffLocation != null) {
                val centerPosition = LatLng(
                  (pickUpLocation.latitude ?: (0.0 + dropOffLocation.latitude) ?: 0.0) / 2,
                  (pickUpLocation.longitude ?: (0.0 + dropOffLocation.longitude) ?: 0.0) / 2
                )

                cameraPositionState.move(
                    update = CameraUpdateFactory.newLatLngZoom(centerPosition, 12f) // Adjust zoom level as needed
                )
            }

        },
        onMapClick = {}
    ) {
        pickUpLocation?.let {
            Marker(
                state = rememberMarkerState(position = it),
                title = "My Current Position",
                snippet = "This is your current location."
            )
        }

        info?.routes?.firstOrNull()?.overview_polyline?.points?.let { encodedPolyline ->
            decodePolyline(encodedPolyline)?.let { polylinePoints ->
                Polyline(
                    points = polylinePoints,
                    color = MaterialTheme.colorScheme.primary,
                    width = 15f
                )
            }
        }


        dropOffLocation?.let {
            Marker(
                state = rememberMarkerState(position = it),
                title = "Drop Location",
                snippet = "This is your destination."
            )
        }
    }
}
