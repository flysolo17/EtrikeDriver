package com.flysolo.etrikedriver.screens.main.bottom_nav.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import com.flysolo.etrikedriver.screens.main.components.ActionButtons
import com.flysolo.etrikedriver.R
import com.flysolo.etrikedriver.screens.shared.Avatar


@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    state: ProfileState,
    events: (ProfileEvents) -> Unit,

    ) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp),
        ) {
            Image(
                alignment = Alignment.TopStart,
                painter = painterResource(R.drawable.star),
                contentDescription = "Star",
                modifier = modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Avatar(url = state.user?.profile ?:"", size = 100.dp) { }
                Text("${state.user?.name}", style = MaterialTheme.typography.titleLarge)
                Text("Welcome to e-Trike", style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.Gray
                ))
            }
        }
        Box(
            modifier = modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButtons(
                    icon = Icons.Default.Book,
                    label = "Bookings",
                ) { }
                ActionButtons(
                    icon = Icons.Default.QrCodeScanner,
                    label = "Scan",
                ) { }
                ActionButtons(
                    icon = Icons.Default.History,
                    label = "Recent Activities",
                ) { }
                ActionButtons(
                    icon = Icons.Default.Favorite,
                    label = "Favorites",
                ) { }
                ActionButtons(
                    icon = Icons.Default.Logout,
                    label = "Logout",
                    onClick = {
                        events(ProfileEvents.OnLogout)
                    }
                )
            }
        }
    }
}