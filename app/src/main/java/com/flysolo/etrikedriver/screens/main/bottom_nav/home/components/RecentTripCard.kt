package com.flysolo.etrikedriver.screens.main.bottom_nav.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flysolo.etrikedriver.models.directions.Distance
import com.flysolo.etrikedriver.models.directions.Duration
import com.flysolo.etrikedriver.models.directions.GeocodedWaypoints
import com.flysolo.etrikedriver.models.directions.GooglePlacesInfo
import com.flysolo.etrikedriver.models.directions.Legs
import com.flysolo.etrikedriver.models.directions.OverviewPolyline
import com.flysolo.etrikedriver.models.directions.Routes
import com.flysolo.etrikedriver.models.transactions.Payment
import com.flysolo.etrikedriver.models.transactions.PaymentMethod
import com.flysolo.etrikedriver.models.transactions.TransactionStatus
import com.flysolo.etrikedriver.models.transactions.Transactions
import com.flysolo.etrikedriver.models.transactions.toColor
import com.flysolo.etrikedriver.models.users.User
import com.flysolo.etrikedriver.screens.shared.Avatar
import com.flysolo.etrikedriver.ui.theme.EtrikeDriverTheme
import com.flysolo.etrikedriver.utils.toPhp

import java.util.Date


@Composable
fun RecentTripCard(
    modifier: Modifier = Modifier,
    transactions: Transactions,
    passenger: User?,
    onClick : () -> Unit,
    goToPickUp : () -> Unit,
    onMessagePassenger : (String) -> Unit,
) {
    OutlinedCard(
        onClick =onClick,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            Box(
                modifier = modifier
                    .wrapContentSize()
                    .background(
                        color = transactions.status.toColor(),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("${transactions.status.name}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White
                    )
                )
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()

            ) {
                Column(
                    modifier = modifier.weight(1f).padding(4.dp)
                ) {
                    val pickLocation = transactions.locationDetails.pickup?.name
                    val dropLocation = transactions.locationDetails?.dropOff?.name

                    TripInfo(
                        label = "Pickup Location",
                        value = pickLocation ?: "unknown"
                    )
                    TripInfo(
                        label = "Drop Off Location",
                        value = dropLocation ?: "unknown"
                    )
                }
                Column(
                    modifier = modifier.weight(.6f)
                ) {
                    val distanceKm = transactions.rideDetails?.routes?.firstOrNull()?.legs?.firstOrNull()?.distance?.value?.let {
                        it / 1000.0
                    }?.let {
                        String.format("%.2f km", it)
                    } ?: "Unknown"

                    TripInfo(
                        label = "Amount",
                        value = transactions.payment.amount.toPhp()
                    )
                    TripInfo(
                        label = "Distance",
                        value = distanceKm
                    )
                }
            }
            val isAccepted = transactions.status == TransactionStatus.ACCEPTED
            if (isAccepted) {
                Card(
                    modifier = modifier.fillMaxWidth().padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Row(
                        modifier = modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            modifier = modifier.size(16.dp),
                            imageVector = Icons.Default.Warning,
                            contentDescription = "warning"
                        )
                        Text("Waiting to the passenger to confirm the trip so that you can go to the pick up location", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            passenger?.let {
                ListItem(
                    leadingContent = {
                        Avatar(
                            url = "${passenger.profile}",
                            size = 40.dp
                        ) { }
                    },
                    headlineContent = { Text(passenger.name ?: "no passenger yet") },
                    supportingContent = {
                        passenger.phone?.let {
                            Text("$it")
                        }

                    },
                    trailingContent = {
                        BadgedBox(
                            modifier = modifier.clickable { onMessagePassenger(passenger.id!!) },
                            badge = { }
                        ) {    Icon(
                            imageVector = Icons.Filled.Message,
                            contentDescription = ""
                        ) }

                    }
                )
            }

            val isConfirmed = transactions.status == TransactionStatus.CONFIRMED
            if (isConfirmed) {
                Button(
                    shape = MaterialTheme.shapes.small,
                    modifier = modifier.fillMaxWidth().padding(8.dp),
                    onClick = goToPickUp,
                ) { Text("Go To Pick Up Location", modifier = modifier.padding(8.dp)) }
            }

        }

    }
}


@Composable
fun TripInfo(
    modifier: Modifier = Modifier,
    label : String ,
    value : String
) {
    ListItem(
        overlineContent = { Text(label, style = MaterialTheme.typography.labelSmall.copy(
            color = Color.Gray
        )) },
        headlineContent = {
            Text(value, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    )
}
@Preview(
    showBackground = true
)
@Composable
private fun RecentTripCard() {
    EtrikeDriverTheme   {
        val sampleGooglePlacesInfo = GooglePlacesInfo(
            geocoded_waypoints = listOf(
                GeocodedWaypoints(
                    geocoder_status = "OK",
                    place_id = "ChIJN1t_tDeuEmsRUsoyG83frY4",
                    types = listOf("street_address")
                )
            ),
            routes = listOf(
                Routes(
                    summary = "US-101 N",
                    overview_polyline = OverviewPolyline(
                        points = "a~l~Fjk~uOwHJy@P"
                    ),
                    legs = listOf(
                        Legs(
                            distance = Distance(
                                text = "10 km",
                                value = 10000
                            ),
                            duration = Duration(
                                text = "15 mins",
                                value = 900
                            ),
                            start_address = "1600 Amphitheatre Parkway, Mountain View, CA",
                            end_address = "1 Hacker Way, Menlo Park, CA"
                        )
                    )
                )
            ),
            status = "OK"
        )

        val sampleTransaction = Transactions(
            id = "txn12345",
            passengerID = "passenger6789",
            driverID = "driver54321",
            franchiseID = "franchise9876",
            status = TransactionStatus.CONFIRMED,
            rideDetails = sampleGooglePlacesInfo,
            payment = Payment(
                id = "payment123",
                amount = 150.75,
                method = PaymentMethod.WALLET,
                createdAt = Date(),
                updatedAt = Date()
            ),
            note = "Pickup at main gate",
            scheduleDate = Date(), // Replace with a specific Date if needed
            createdAt = Date(),
            updatedAt = Date()
        )
        RecentTripCard(
            transactions = sampleTransaction,
            onClick = {},
            passenger = null,
            goToPickUp = {},
            onMessagePassenger = {}
        )


    }
}