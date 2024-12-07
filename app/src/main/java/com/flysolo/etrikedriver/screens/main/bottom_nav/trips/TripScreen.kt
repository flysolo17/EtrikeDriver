package com.flysolo.etrikedriver.screens.main.bottom_nav.trips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.flysolo.etrikedriver.config.AppRouter
import com.flysolo.etrikedriver.screens.main.components.TripsCard
import com.flysolo.etrikedriver.utils.shortToast


@Composable
fun TripScreen(
    modifier: Modifier = Modifier,
    state: TripState,
    events: (TripEvents) -> Unit,
    navHostController: NavHostController
) {
    val context  = LocalContext.current
    LaunchedEffect(state.user) {
        state.user?.id?.let {
            events(TripEvents.OnGetTrips(it))
        }
    }
    LaunchedEffect(
        state.errors
    ) {
        if (state.errors != null) {
            context.shortToast(state.errors)
        }
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (state.isLoading) {
            item {
                LinearProgressIndicator(
                    modifier = modifier.fillMaxWidth()
                )
            }
        }
        if(state.trips.isEmpty()) {
            item {
                Box(modifier = modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("no trips yet")
                }
            }
        }
        items(state.trips) {
            TripsCard(transactions = it) {
                navHostController.navigate(AppRouter.VIEWTRIP.navigate(it.id?: ""))
            }
        }
    }

}