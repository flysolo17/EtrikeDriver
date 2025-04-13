package com.flysolo.etrike.screens.main.bottom_nav.profile.view_bookings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.flysolo.etrikedriver.config.AppRouter
import com.flysolo.etrikedriver.models.transactions.TransactionStatus

import com.flysolo.etrikedriver.screens.main.bottom_nav.profile.view_bookings.ViewBookingEvents
import com.flysolo.etrikedriver.screens.main.bottom_nav.profile.view_bookings.ViewBookingState
import com.flysolo.etrikedriver.screens.main.components.TripsCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewBooking(
    modifier: Modifier = Modifier,
    uid : String,
    state: ViewBookingState,
    events: (ViewBookingEvents) -> Unit,
    navHostController: NavHostController
) {
    LaunchedEffect(Unit) {
        if (uid.isNotEmpty()) {
            events(ViewBookingEvents.OnGetAllBookings(uid))
        }
    }
    val tabs = TransactionStatus.entries
        //add a tabbar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bookings") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navHostController.popBackStack()
                        }
                    ) { Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = ""
                    ) }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(it).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            item {
                val selectedTab = state.selectedTab
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                ) {
                    tabs.forEachIndexed { index, status ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { events(ViewBookingEvents.OnSelectTab(status,index)) },
                            text = { Text(status.name) }
                        )
                    }
                }
            }
            if (state.isLoading) {
                item {
                    LinearProgressIndicator(
                        modifier = modifier.fillMaxWidth()
                    )
                }
            }
            items(state.filteredTransactions) {
                TripsCard(
                    transactions = it
                ) {
                    navHostController.navigate(AppRouter.VIEWTRIP.navigate(it.id ?: ""))
                }
            }
        }
    }
}