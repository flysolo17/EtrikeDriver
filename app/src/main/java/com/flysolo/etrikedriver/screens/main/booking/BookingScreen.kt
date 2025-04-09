package com.flysolo.etrikedriver.screens.main.booking

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.flysolo.etrikedriver.screens.main.bottom_nav.utils.decodePolyline
import com.flysolo.etrikedriver.screens.main.components.ViewTransactionBottomSheet

import com.flysolo.etrikedriver.screens.main.queue.TransactionWithUserCard
import com.flysolo.etrikedriver.screens.shared.BackButton
import com.flysolo.etrikedriver.utils.calculateDistanceInKm
import com.flysolo.etrikedriver.utils.getLatLngFromAddress
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    modifier: Modifier = Modifier,
    state: BookingState,
    events: (BookingEvents)->Unit,
    navHostController: NavHostController
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    LaunchedEffect(state.user) {
        state.user?.let {
            events(BookingEvents.OnGetFranchise(it.id?: ""))
        }
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fineLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    )
    val coarseLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val locationPermissionGranted = fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            coarseLocationPermission == PackageManager.PERMISSION_GRANTED

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val cameraState = rememberCameraPositionState()


    if (state.selectedTransaction != null) {
        ViewTransactionBottomSheet(
            sheetState = sheetState,
            transaction =state.selectedTransaction.transactions,
            user= state.selectedTransaction.user ,
            isLoading = state.isAccepting,
            onDismiss = { events.invoke(BookingEvents.OnSelectTransction(null)) },
            accept ={
                events(
                    BookingEvents.AcceptTransaction(
                    transactionID = state.selectedTransaction.transactions.id ?: "",
                    driverID = state.user?.id ?: "",
                    franchiseID = state.franchise?.franchiseNumber ?: ""
                ))
            },
            onNavigateToStartDestination = {
                val transaction = state.selectedTransaction.transactions
                val pickup = LatLng(
                    transaction.locationDetails.pickup?.latitude ?: 0.00,
                    transaction.locationDetails.pickup?.longitude ?: 0.00
                )
                val gmmIntentUri = Uri.parse("google.navigation:q=${pickup.latitude},${pickup.longitude}.&mode=d")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                    setPackage("com.google.android.apps.maps")
                }
                context.startActivity(mapIntent)
            },
            onNavigateToDropOffDestination = {
                val transaction = state.selectedTransaction.transactions
                val dropOff = LatLng(
                    transaction.locationDetails.dropOff?.latitude ?: 0.00,
                    transaction.locationDetails.dropOff?.longitude ?: 0.00
                )

                val gmmIntentUri = Uri.parse("google.navigation:q=${dropOff.latitude},${dropOff.longitude}.&mode=d")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                    setPackage("com.google.android.apps.maps")
                }
                context.startActivity(mapIntent)
            }
        ) {
            val transaction = state.selectedTransaction.transactions
            val pickup = LatLng(
                transaction.locationDetails.pickup?.latitude ?: 0.00,
                transaction.locationDetails.pickup?.longitude ?: 0.00
            )
            val dropoff = LatLng(
                transaction.locationDetails.dropOff?.latitude ?: 0.00,
                transaction.locationDetails.dropOff?.longitude ?: 0.00
            )
            val boundsBuilder = LatLngBounds.Builder()
            boundsBuilder.include(pickup)
            boundsBuilder.include(dropoff)
            val bounds = boundsBuilder.build()


            GoogleMap(
                modifier = modifier.fillMaxWidth().height(300.dp).clip(MaterialTheme.shapes.large),
                cameraPositionState = cameraState,
                onMapLoaded = {
                    val padding = 10
                    cameraState.move(CameraUpdateFactory.newLatLngBounds(bounds, padding))
                },
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    scrollGesturesEnabled = true,
                    zoomGesturesEnabled = true,
                    scrollGesturesEnabledDuringRotateOrZoom = false
                ),
                onMapClick = {}
            ) {
                Marker(
                    state = rememberMarkerState(position = pickup),
                    title = "Pickup Location",
                    snippet = "This is your current location."
                )
                transaction.rideDetails?.routes?.firstOrNull()?.overview_polyline?.points?.let { encodedPolyline ->
                    decodePolyline(encodedPolyline).let { polylinePoints ->
                        Polyline(
                            points = polylinePoints,
                            color = MaterialTheme.colorScheme.primary,
                            width = 15f
                        )
                    }
                }

                Marker(
                    state = rememberMarkerState(position = dropoff),
                    title = "Drop Location",
                    snippet = "This is your destination."
                )
            }

        }
    }

    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted && isGpsEnabled) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val newLocation = LatLng(it.latitude, it.longitude)
                    events(BookingEvents.OnSetCurrentLocation(newLocation))
                }
            }
        }
    }

    if (!locationPermissionGranted) {
        (context as Activity).requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_PERMISSION
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Bookings")
                },
                navigationIcon = { BackButton() {} }
            )
        }
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (state.isLoading) {
                item {
                    LinearProgressIndicator(
                        modifier = modifier.fillMaxWidth()
                    )
                }

            }
            items(state.transactionWithUser) {
                val pickUpLocation = LatLng(
                    it.transactions.locationDetails.pickup?.latitude ?: 0.00,
                    it.transactions.locationDetails.pickup?.longitude ?: 0.00
                )
                val distanceInKm = String.format("%.2f km",
                    calculateDistanceInKm(state.currentPosition,pickUpLocation)
                )

                TransactionWithUserCard(
                    transaction = it.transactions,
                    user = it.user,
                    distance = distanceInKm,
                    onClick = {
                        events(BookingEvents.OnSelectTransction(it))
                    },

                    )
            }
        }
    }
}

private const val REQUEST_LOCATION_PERMISSION = 1