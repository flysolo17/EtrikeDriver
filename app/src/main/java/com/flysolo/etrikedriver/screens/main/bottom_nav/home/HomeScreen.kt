package com.flysolo.etrikedriver.screens.main.bottom_nav.home

import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.flysolo.etrikedriver.R
import com.flysolo.etrikedriver.config.AppRouter
import com.flysolo.etrikedriver.models.users.User
import com.flysolo.etrikedriver.screens.main.bottom_nav.home.components.RecentTripsLayout
import com.flysolo.etrikedriver.screens.nav.BottomNavigationItems
import com.flysolo.etrikedriver.screens.shared.Avatar
import com.flysolo.etrikedriver.utils.getLatLngFromAddress
import com.flysolo.etrikedriver.utils.toPhp
import com.google.android.gms.maps.model.LatLng


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeState,
    events: (HomeEvents) -> Unit,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    LaunchedEffect(state.user) {
        state.user?.id?.let {
            events(HomeEvents.OnGetOngoingTrips(it))
            events.invoke(HomeEvents.OnGetWallet(it))
        }
    }
    LazyVerticalGrid(
      columns =GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (state.user != null) {
            item(
                span = { GridItemSpan(2) }
            ) {
                UserWelcomeSection(
                    user = state.user
                )
            }
        }

        item(
            span = { GridItemSpan(2) }
        ) {
            Text("E-trike Wallet",
                modifier = modifier.padding(8.dp),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        item(
            span = { GridItemSpan(2) }
        ) {
            Card(
                modifier = modifier.fillMaxWidth().clickable {
                    navHostController.navigate(AppRouter.WALLET.navigate(state.user?.id ?: ""))
                },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Row(
                    modifier = modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(
                        modifier = modifier.fillMaxWidth().weight(1f)
                    ) {
                        Text("Balance", style = MaterialTheme.typography.labelMedium)
                        Text((state.wallet?.amount?: 0.00).toPhp(), style = MaterialTheme.typography.titleLarge)
                    }
                    Button(
                        onClick = {
                            if (state.wallet == null) {
                                navHostController.navigate(AppRouter.PHONE.route)
                            } else {
                                navHostController.navigate(AppRouter.CASHOUT.navigate(
                                    id = state.wallet.id ?: ""
                                ))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    ) {
                        Text("Cash out")
                    }
                }
            }
        }





        item(
            span = { GridItemSpan(2) }
        ) {
            Text("Services",
                modifier = modifier.padding(8.dp),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ))
        }

        item {
            ServiceCard(
                image = R.drawable.queue,
                label = "Queue",
                onClick = {
                    navHostController.navigate(AppRouter.QUEUE.route)
                }
            )
        }
        item {
            ServiceCard(
                image = R.drawable.calendar,
                label = "Bookings",
                onClick = {
                    navHostController.navigate(AppRouter.BOOKINGS.route)
                }
            )
        }

        if (state.ongoingTrips.isNotEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Ongoing Trips (${state.ongoingTrips.size})",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                }
            }

            item(span = { GridItemSpan(2) }) {
                RecentTripsLayout(
                    trips =  state.ongoingTrips,
                    isLoading = state.isGettingTransactions,
                    goToPickUp = {
                        events.invoke(HomeEvents.OnPickup(it,context))
                    },
                    onTripSelected = {
                        navHostController.navigate(AppRouter.VIEWTRIP.navigate(it))
                    },
                    message = {
                        navHostController.navigate(AppRouter.CONVERSATION.navigate(it))
                    }
                )
            }
        }


    }
}

@Composable
fun UserWelcomeSection(
    modifier: Modifier = Modifier,
    user: User
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ){
        Column(
            modifier = modifier.weight(1f)
        ) {
            Text("Welcome to e-trike driver app")
            Text("${user.name}", style = MaterialTheme.typography.titleLarge)
        }
        Avatar(
            url = user.profile ?: "",
            size = 80.dp
        ) { }
    }

}


@Composable
fun ServiceCard(
    modifier: Modifier = Modifier,
    @DrawableRes image : Int,
    label : String,
    onClick : () -> Unit
) {
    OutlinedCard(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .size(50.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}