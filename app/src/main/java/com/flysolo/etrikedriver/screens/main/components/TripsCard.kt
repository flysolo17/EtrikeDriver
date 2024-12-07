package com.flysolo.etrikedriver.screens.main.components

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
import androidx.compose.ui.unit.dp

import com.flysolo.etrikedriver.models.transactions.Transactions
import com.flysolo.etrikedriver.models.transactions.toColor
import com.flysolo.etrikedriver.screens.main.bottom_nav.home.components.TripInfo
import com.flysolo.etrikedriver.utils.toPhp


@Composable
fun TripsCard(
    modifier: Modifier = Modifier,
    transactions: Transactions,
    onClick : () -> Unit
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
                    val pickLocation = transactions.rideDetails?.routes?.firstOrNull()?.legs?.firstOrNull()?.start_address
                    val dropLocation = transactions.rideDetails?.routes?.firstOrNull()?.legs?.firstOrNull()?.end_address

                    TripInfo(
                        label = "Pickup Location",
                        value = pickLocation ?: "unknown"
                    )
                    TripInfo(
                        label = "Pickup Location",
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
        }

    }

}