package com.flysolo.etrikedriver.screens.main.bottom_nav.profile.recent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.flysolo.etrikedriver.screens.wallet.ActivityCard
import com.flysolo.etrikedriver.utils.EtrikeToBar


@Composable
fun RecentActivityScreen(
    modifier: Modifier = Modifier,
    uid : String,
    state: RecentActivityState,
    events: (RecentActivityEvents) -> Unit,
    navHostController: NavHostController
) {
    LaunchedEffect(
        Unit
    ) {
        if (uid.isNotEmpty()) {
            events(RecentActivityEvents.OnGetActivities(uid))
        }
    }
    Scaffold(
        topBar = {
            EtrikeToBar(
                title = "Recent Activities",
                onBack = {
                    navHostController.popBackStack()
                }
            ) { }
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
            items(state.activities) {
                ActivityCard(
                    activity = it
                )
            }

        }
    }
}