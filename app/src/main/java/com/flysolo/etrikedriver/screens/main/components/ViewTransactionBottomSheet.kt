package com.flysolo.etrikedriver.screens.main.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.flysolo.etrikedriver.models.transactions.Transactions
import com.flysolo.etrikedriver.models.users.User
import com.flysolo.etrikedriver.screens.main.bottom_nav.utils.EtrikeMap
import com.flysolo.etrikedriver.screens.main.bottom_nav.utils.InformationCard
import com.flysolo.etrikedriver.screens.shared.Avatar
import com.flysolo.etrikedriver.utils.getLatLngFromAddress
import com.flysolo.etrikedriver.utils.toPhp
import com.google.android.gms.maps.model.LatLng


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewTransactionBottomSheet(
    modifier: Modifier = Modifier,
    sheetState : SheetState,
    transaction : Transactions,
    user: User ?,
    isLoading : Boolean,
    onDismiss : () -> Unit,
    accept : () -> Unit,
    onNavigateToStartDestination : () -> Unit,
    onNavigateToDropOffDestination: () -> Unit,
    map :  @Composable  () -> Unit,

) {


    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
    val pickUpLocation = transaction.rideDetails?.routes?.firstOrNull()?.legs?.firstOrNull()?.start_address
    val endLocation = transaction.rideDetails?.routes?.firstOrNull()?.legs?.firstOrNull()?.end_address

    val route = transaction.rideDetails?.routes?.firstOrNull()
    val leg = route?.legs?.firstOrNull()

    // Calculate the distance in kilometers (from meters)
    val distanceInKm = leg?.distance?.value?.let {
        it / 1000.0
    }
    val cost = transaction.payment.amount
    ModalBottomSheet(
        modifier = Modifier.wrapContentSize(),
        sheetState = sheetState,
        onDismissRequest = { onDismiss() }
    ) {
        LazyVerticalGrid(
            modifier = modifier.fillMaxSize(),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(span = { GridItemSpan((2)) }) {
                Box(
                    modifier = modifier.fillMaxWidth().padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Trip Information")
                }
            }
            item(span = { GridItemSpan((2)) }) {
                OutlinedCard(
                    modifier =  modifier.fillMaxWidth()
                ) {
                    ListItem(
                        modifier = modifier.fillMaxWidth(),
                        leadingContent = {
                            Avatar(
                                url = user?.profile ?: "",
                                size = 50.dp
                            ) { }
                        },
                        supportingContent = {
                            Text(user?.phone ?: "No phone number")
                        },
                        overlineContent = {
                            Text(
                                "Passenger's Information",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray
                                )
                            )
                        },
                        headlineContent =  {
                            Text(
                                user?.name ?: "Unknown",
                                style = MaterialTheme.typography.titleSmall.copy(),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
            item(span = { GridItemSpan((2)) }) {
                map()
            }
            item(span = { GridItemSpan((2)) }) {
                InformationCard(
                    modifier = modifier.clickable { onNavigateToStartDestination() },
                    label = "Pickup",
                    icon = Icons.Default.Place,
                    value = pickUpLocation
                )
            }
            item(span = { GridItemSpan((2)) }) {
                InformationCard(
                    modifier = modifier.clickable { onNavigateToDropOffDestination() },
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
                InformationCard(
                    label = "Total Amount",
                    icon = Icons.Default.Money,
                    value = transaction.payment.amount.toPhp()
                )
            }
            item(span = { GridItemSpan((2)) }) {
                Button(
                    enabled = !isLoading,

                    modifier = modifier.fillMaxWidth(),
                    onClick = accept,
                    shape = MaterialTheme.shapes.small
                ) {
                    if (isLoading) {
                        Row(
                            modifier = modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = modifier.size(18.dp)
                            )
                            Spacer(
                                modifier = modifier.width(8.dp)
                            )
                            Text("Accepting...")
                        }
                    } else {
                        Text("Accept", modifier = modifier.padding(8.dp))
                    }
                }
            }

        }
    }
}