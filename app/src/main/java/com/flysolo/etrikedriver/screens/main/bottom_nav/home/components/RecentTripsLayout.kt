package com.flysolo.etrikedriver.screens.main.bottom_nav.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flysolo.etrikedriver.models.transactions.TransactionWithUser
import com.flysolo.etrikedriver.models.transactions.Transactions


@Composable
fun RecentTripsLayout(
    modifier: Modifier = Modifier,
    trips : List<TransactionWithUser>,
    isLoading : Boolean,
    onTripSelected : (String) -> Unit,
    goToPickUp : (Transactions) -> Unit,
    message : (String) -> Unit
) {
    LazyRow (
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isLoading) {
            item {
                Box(
                    modifier = modifier.fillMaxSize().height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        if (trips.isEmpty()) {
            item {
                Box(
                    modifier = modifier.fillMaxWidth().height(250.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text("No Ongoing Trips")
                }
            }
        }
        if (!isLoading && trips.isNotEmpty()) {
            items(trips) {
                Box(modifier = modifier.fillParentMaxWidth()) {
                    RecentTripCard(
                        transactions = it.transactions,
                        passenger = it.user,
                        onMessagePassenger = {
                            message(it)
                        },
                        goToPickUp = {
                            goToPickUp(it.transactions)
                        },
                        onClick = {
                            it.transactions.id?.let {
                                onTripSelected(it)
                            }
                        }
                    )
                }

            }
        }

    }
}

