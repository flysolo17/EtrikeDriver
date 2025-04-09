package com.flysolo.etrikedriver.screens.main.bottom_nav.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import com.flysolo.etrikedriver.screens.main.components.ActionButtons
import com.flysolo.etrikedriver.R
import com.flysolo.etrikedriver.config.AppRouter
import com.flysolo.etrikedriver.screens.shared.Avatar
import com.flysolo.etrikedriver.utils.DeleteAccountConfirmationDialog
import com.flysolo.etrikedriver.utils.shortToast
import kotlinx.coroutines.delay


@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    state: ProfileState,
    events: (ProfileEvents) -> Unit,
    mainNavHostController: NavHostController,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            events(ProfileEvents.ChangeProfile(it))
        }
    }
    if (showDialog) {
        DeleteAccountConfirmationDialog(
            onConfirm = {
                showDialog = false
                events.invoke(ProfileEvents.OnDeleteAccount(it))
            },
            onDismiss = { showDialog = false }
        )
    }

    LaunchedEffect(state.errors) {
        state.errors?.let {
            context.shortToast(state.errors)
        }
    }
    LaunchedEffect(state.isLoggedOut) {
        state.isLoggedOut?.let {
            context.shortToast(state.isLoggedOut)
            delay(1000)
            mainNavHostController.navigate(AppRouter.AUTH.route)
        }
    }
    LaunchedEffect(state.messages) {
        state.messages?.let {
            context.shortToast(state.messages)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(300.dp),
            )    {
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
                    Avatar(url = state.user?.profile ?: "", size = 100.dp) {
                        imagePickerLauncher.launch("image/*")
                    }
                    Text("${state.user?.name}", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Welcome to e-Trike",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.Gray
                        )
                    )
                }
            }
        }
        item {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
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
                        icon = Icons.Default.Edit,
                        label = "Edit Profile",
                    ) { navHostController.navigate(AppRouter.EDIT_PROFILE.route) }
                    ActionButtons(
                        icon = Icons.Default.Password,
                        label = "Change Password",
                    ) { navHostController.navigate(AppRouter.CHANGE_PASSWORD.route) }
                    ActionButtons(
                        icon = Icons.Default.DeleteForever,
                        label = "Delete Account",
                    ) { showDialog = !showDialog }
                    ActionButtons(
                        icon = Icons.Default.Security,
                        label = "Security Settings",
                    ) { navHostController.navigate(AppRouter.SECURITY_SETTINGS.route) }
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

}