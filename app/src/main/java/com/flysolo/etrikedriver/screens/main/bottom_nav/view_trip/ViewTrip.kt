package com.flysolo.etrike.screens.main.view_trip

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.flysolo.etrikedriver.config.AppRouter
import com.flysolo.etrikedriver.models.transactions.TransactionStatus
import com.flysolo.etrikedriver.models.transactions.getDropOffCoordinates
import com.flysolo.etrikedriver.models.transactions.getPickupCoordinates
import com.flysolo.etrikedriver.models.users.User
import com.flysolo.etrikedriver.screens.main.bottom_nav.utils.InformationCard
import com.flysolo.etrikedriver.screens.main.bottom_nav.utils.decodePolyline
import com.flysolo.etrikedriver.screens.shared.Avatar
import com.flysolo.etrikedriver.screens.shared.BackButton

import com.flysolo.etrikedriver.utils.ErrorScreen
import com.flysolo.etrikedriver.utils.LoadingScreen

import com.flysolo.etrikedriver.screens.view_trip.ViewTripEvents
import com.flysolo.etrikedriver.screens.view_trip.ViewTripState
import com.flysolo.etrikedriver.utils.displayDate
import com.flysolo.etrikedriver.utils.displayTime
import com.flysolo.etrikedriver.utils.getLatLngFromAddress
import com.flysolo.etrikedriver.utils.shortToast
import com.flysolo.etrikedriver.utils.toPhp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewTripScreen(
    modifier: Modifier = Modifier,
    transactionID : String,
    state: ViewTripState,
    events: (ViewTripEvents) -> Unit,
    navHostController: NavHostController,
) {
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

    LaunchedEffect(transactionID) {
        if(transactionID.isNotEmpty()) {
            events(ViewTripEvents.OnViewTrip(transactionID))
        }
    }
    LaunchedEffect(state.messages) {
        if (state.messages != null) {
            context.shortToast(state.messages)
        }
    }



    Scaffold(
        topBar ={
            TopAppBar(
                title = {
                    Text("Trip Info")
                },
                navigationIcon = {
                    BackButton {
                        navHostController.popBackStack()
                    }
                }
            )
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when {
                state.isLoading -> {
                    LoadingScreen(title = "Getting Trip Info")
                }

                state.errors != null -> {
                    ErrorScreen(title = state.errors) {
                        Button(onClick = { navHostController.popBackStack() }) {
                            Text("Back")
                        }
                    }
                }

                else -> {
                    val pickUpLocation = state.transactions?.locationDetails?.pickup?.name
                    val endLocation = state.transactions?.locationDetails?.dropOff?.name

                    val route = state.transactions?.rideDetails?.routes?.firstOrNull()
                    val leg = route?.legs?.firstOrNull()

                    // Calculate the distance in kilometers (from meters)
                    val distanceInKm = leg?.distance?.value?.let {
                        it / 1000.0
                    }

                    val cost = state.transactions?.payment?.amount ?: 0.00
                    LazyVerticalGrid(
                        modifier = modifier.fillMaxSize(),
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item(span = { GridItemSpan((2)) }) {
                            Row(
                                modifier = modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                ) {
                                    Text("Status", style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Gray
                                    ))
                                    Text("${state.transactions?.status?.name}", style = MaterialTheme.typography.titleSmall)
                                }
                                if (state.transactions?.scheduleDate != null) {
                                    Column(
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text("${state.transactions.scheduleDate.displayDate()}", style = MaterialTheme.typography.titleMedium)
                                        Text("${state.transactions.scheduleDate.displayTime()}", style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.Gray
                                        ))
                                    }
                                }

                            }
                        }
                        item(span = { GridItemSpan((2)) }) {
                            PassengerListItem(
                                passenger = state.passenger,
                                onMessage = {
                                    navHostController.navigate(AppRouter.CONVERSATION.navigate(it))
                                }
                            )
                        }


                        val isConfirmed = state.transactions?.status == TransactionStatus.CONFIRMED
                        if (isConfirmed) {
                            item(span = { GridItemSpan((2)) }) {
                                Button(
                                    shape = MaterialTheme.shapes.small,
                                    modifier = modifier.fillMaxWidth().padding(8.dp),
                                    onClick = {

                                    },
                                ) {
                                    Text("Go To Pick Up Location", modifier = modifier.padding(8.dp))
                                }
                            }

                        }
                        item(span = { GridItemSpan((2)) }) {
                            val transaction = state.transactions
                            GoogleMap(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .height(400.dp)
                                    .clip(MaterialTheme.shapes.large),
                                cameraPositionState = cameraState,
                                uiSettings = MapUiSettings(
                                    zoomControlsEnabled = true,
                                    scrollGesturesEnabled = true,
                                    zoomGesturesEnabled = true,
                                    scrollGesturesEnabledDuringRotateOrZoom = false
                                ),
                                onMapLoaded = {
                                    val dropLocation = state.transactions?.getDropOffCoordinates()
                                    dropLocation?.let {
                                        cameraState.move(CameraUpdateFactory.newLatLngZoom(it, 10f))
                                    }

                                },
                                onMapClick = {}
                            ) {


                                val pickLocation = state.transactions?.getPickupCoordinates()
                                val dropLocation = state.transactions?.getDropOffCoordinates()
                                dropLocation?.let { d->
                                    Marker(
                                        state = rememberMarkerState(
                                            position = d
                                        ),
                                        title = "Drop Location",
                                        snippet = "${transaction?.locationDetails?.dropOff?.name}"
                                    )
                                }

                                pickLocation?.let {p ->
                                    Marker(
                                        state = rememberMarkerState(
                                            position = p
                                        ),
                                        title = "Pickup Location",
                                        snippet = "${transaction?.locationDetails?.pickup?.name}"
                                    )
                                }

                                transaction?.rideDetails?.routes?.firstOrNull()?.overview_polyline?.points?.let { encodedPolyline ->
                                    decodePolyline(encodedPolyline).let { polylinePoints ->
                                        Polyline(
                                            points = polylinePoints,
                                            color = MaterialTheme.colorScheme.primary,
                                            width = 15f
                                        )
                                    }
                                }


                            }
                        }
                        item(span = { GridItemSpan((2)) }) {
                            InformationCard(
                                modifier = modifier.clickable {
                                    val pickup = state
                                        .transactions
                                        ?.getPickupCoordinates()

                                    val gmmIntentUri = Uri.parse("google.navigation:q=${pickup?.latitude},${pickup?.longitude}.&mode=d")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }
                                    context.startActivity(mapIntent) },
                                label = "Pickup",
                                icon = Icons.Default.Place,
                                value = pickUpLocation
                            )
                        }
                        item(span = { GridItemSpan((2)) }) {
                            InformationCard(
                                modifier = modifier.clickable {
                                    val pickup = state
                                    .transactions
                                        ?.getDropOffCoordinates()
                                    ?: LatLng(0.00,0.00)

                                    val gmmIntentUri = Uri.parse("google.navigation:q=${pickup.latitude},${pickup.longitude}.&mode=d")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }
                                    context.startActivity(mapIntent) },
                                label = "Drop off",
                                icon = Icons.Default.Place,
                                value = endLocation
                            )
                        }
                        item() {
                            val data = if (distanceInKm != null) "${"%.2f".format(distanceInKm)} km" else null
                            InformationCard(
                                label = "Distance",
                                icon = Icons.Default.NearMe,
                                value = data
                            )
                        }

                        item() {
                            val status = state.transactions?.payment?.status?.name  ?: "UNPAID"
                            InformationCard(
                                label = "Total Amount",
                                icon = Icons.Default.Money,
                                value = cost.toPhp(),
                                desc = status
                            )
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun PassengerListItem(
    modifier: Modifier = Modifier,
    passenger : User?,
    onMessage : (String) -> Unit
) {
    OutlinedCard(
        modifier =  modifier.fillMaxWidth()
    ) {
        ListItem(
            modifier = modifier.fillMaxWidth(),
            leadingContent = {
                Avatar(
                    url = passenger?.profile ?: "",
                    size = 50.dp
                ) { }
            },
            supportingContent = {
                Text(passenger?.phone ?: "No phone number")
            },
            headlineContent =  {
                Text(
                    passenger?.name ?: "No Driver yet",
                    style = MaterialTheme.typography.titleSmall.copy(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            },
            trailingContent = {
                BadgedBox(
                    modifier = modifier.clickable {
                        onMessage(passenger?.id ?: "")
                    },
                    badge = { Badge() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = "Message"
                    )
                }

            }
        )
    }
}