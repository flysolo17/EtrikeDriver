package com.flysolo.etrikedriver.screens.main.queue

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.flysolo.etrikedriver.models.transactions.Transactions
import com.flysolo.etrikedriver.models.users.User
import com.flysolo.etrikedriver.screens.main.bottom_nav.utils.EtrikeMap
import com.flysolo.etrikedriver.screens.main.bottom_nav.utils.InformationCard
import com.flysolo.etrikedriver.screens.main.bottom_nav.utils.decodePolyline
import com.flysolo.etrikedriver.screens.main.components.ViewTransactionBottomSheet
import com.flysolo.etrikedriver.screens.shared.Avatar
import com.flysolo.etrikedriver.screens.shared.BackButton
import com.flysolo.etrikedriver.utils.calculateDistanceInKm
import com.flysolo.etrikedriver.utils.display
import com.flysolo.etrikedriver.utils.displayDate
import com.flysolo.etrikedriver.utils.getLatLngFromAddress
import com.flysolo.etrikedriver.utils.toPhp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay


@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(
    modifier: Modifier = Modifier,
    state: QueueState,
    events: (QueueEvents) -> Unit,
    navHostController: NavHostController
) {


    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    LaunchedEffect(state.user) {
        state.user?.let {
            events(QueueEvents.OnGetFranchise(it.id?: ""))
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
            onDismiss = { events.invoke(QueueEvents.OnSelectTransction(null)) },
        accept ={
            events(QueueEvents.AcceptTransaction(
                transactionID = state.selectedTransaction.transactions.id ?: "",
                driverID = state.user?.id ?: "",
                franchiseID = state.franchise?.franchiseNumber ?: ""
            ))
        },
        onNavigateToStartDestination = {
            val pickup = state.selectedTransaction
                .transactions
                .rideDetails
                ?.routes
                ?.firstOrNull()
                ?.legs
                ?.firstOrNull()
                ?.start_address
                ?.getLatLngFromAddress(context)
                ?: LatLng(0.00,0.00)

            val gmmIntentUri = Uri.parse("google.navigation:q=${pickup.latitude},${pickup.longitude}.&mode=d")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
            }
            context.startActivity(mapIntent)
        },
            onNavigateToDropOffDestination = {
                val pickup = state.selectedTransaction
                    .transactions
                    .rideDetails
                    ?.routes
                    ?.firstOrNull()
                    ?.legs
                    ?.firstOrNull()
                    ?.end_address
                    ?.getLatLngFromAddress(context)
                    ?: LatLng(0.00,0.00)

                val gmmIntentUri = Uri.parse("google.navigation:q=${pickup.latitude},${pickup.longitude}.&mode=d")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                    setPackage("com.google.android.apps.maps")
                }
                context.startActivity(mapIntent)
            }
        ) {
            val transaction = state.selectedTransaction.transactions
            val pickup = transaction.rideDetails?.routes?.firstOrNull()?.legs?.firstOrNull()?.start_address?.getLatLngFromAddress(context) ?: LatLng(0.00,0.00)
            val dropoff = transaction.rideDetails?.routes?.firstOrNull()?.legs?.firstOrNull()?.end_address?.getLatLngFromAddress(context) ?: LatLng(0.00,0.00)
            val boundsBuilder = LatLngBounds.Builder()
            boundsBuilder.include(pickup)
            boundsBuilder.include(dropoff)
            val bounds = boundsBuilder.build()
            GoogleMap(
                modifier = modifier.fillMaxWidth().height(300.dp).clip(MaterialTheme.shapes.large),
                cameraPositionState = cameraState,
                onMapLoaded = {
                    val padding = 100
                    cameraState.move(CameraUpdateFactory.newLatLngBounds(bounds, padding))
                },
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
                    events(QueueEvents.OnSetCurrentLocation(newLocation))
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
                    Text("Queue")
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
            items(state.transactionWithUser) {
                val pickUpLocation = it.transactions.rideDetails?.routes?.firstOrNull()?.legs?.firstOrNull()?.start_address?.getLatLngFromAddress(context) ?: LatLng(0.00,0.00)
                val distanceInKm = String.format("%.2f km",calculateDistanceInKm(state.currentPosition,pickUpLocation))
                TransactionWithUserCard(
                    transaction = it.transactions,
                    user = it.user,
                    distance = distanceInKm,
                    onClick = {
                        events(QueueEvents.OnSelectTransction(it))
                    },

                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionWithUserCard(
    modifier: Modifier = Modifier,
    distance : String,
    transaction : Transactions,
    user: User ?,
    onClick : () -> Unit,

) {

    val pickUpLocation = transaction.rideDetails?.routes?.firstOrNull()?.legs?.firstOrNull()?.start_address
    val endLocation = transaction.rideDetails?.routes?.firstOrNull()?.legs?.firstOrNull()?.end_address
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
    val route = transaction.rideDetails?.routes?.firstOrNull()
    val leg = route?.legs?.firstOrNull()

    // Calculate the distance in kilometers (from meters)
    val distanceInKm = leg?.distance?.value?.let {
        it / 1000.0
    }

    val cost = distanceInKm?.let { it * 20 }
    ListItem(
        overlineContent = {
            if (transaction.scheduleDate != null) {
                Text("${transaction.scheduleDate.display()}", style = MaterialTheme.typography.titleSmall)
            }
        },
        leadingContent = {
            Avatar(
                url = user?.profile ?: "",
                size = 50.dp
            ) { }
        },
        headlineContent = {
            Text(user?.name ?: "unknown user")
        },
        supportingContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = "place",
                    tint = Color.Red,
                    modifier = modifier.size(18.dp)
                )

                Text("${distance} away", style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.Gray
                ))
            }
        },
        trailingContent = {
            TextButton(
                onClick = {
                    onClick()
                }
            ) { Text("View") }
        }

    )
}
private const val REQUEST_LOCATION_PERMISSION = 1