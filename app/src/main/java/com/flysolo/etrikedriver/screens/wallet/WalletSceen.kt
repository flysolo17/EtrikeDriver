package com.flysolo.etrikedriver.screens.wallet


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import com.flysolo.etrikedriver.config.AppRouter
import com.flysolo.etrikedriver.models.wallet.Wallet
import com.flysolo.etrikedriver.models.wallet.WalletActivity
import com.flysolo.etrikedriver.screens.shared.BackButton
import com.flysolo.etrikedriver.ui.theme.Green
import com.flysolo.etrikedriver.utils.ErrorScreen
import com.flysolo.etrikedriver.utils.EtrikeQrCode
import com.flysolo.etrikedriver.utils.LoadingScreen
import com.flysolo.etrikedriver.utils.display
import com.flysolo.etrikedriver.utils.toPhp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    modifier: Modifier = Modifier,
    id : String,
    state: WalletState,
    events: (WalletEvents) -> Unit,
    navHostController: NavHostController
) {
    LaunchedEffect(id) {
        if (id.isNotEmpty()) {
            events(WalletEvents.OnGetMyWallet(id))
            events(WalletEvents.OnGetWalletHistory(id))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor =MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = { Text("") },
                navigationIcon = {
                    BackButton {
                        navHostController.popBackStack()
                    }
                },
                actions = {

                }
            )
        },
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> LoadingScreen(title= "Getting You Wallet")
                state.errors != null -> ErrorScreen(
                    title = state.errors
                ) {

                }

                else -> {
                    val wallet = state.wallet
                    if(wallet != null) {
                        MainWalletLayout(
                            wallet = wallet,
                            state = state,
                            events = events,
                            navHostController = navHostController
                        )
                    } else {
                        Column(
                            modifier = modifier,
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Enable Your Wallet",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(
                                modifier = modifier.height(12.dp)
                            )
                            Button(
                                onClick = { navHostController.navigate(AppRouter.PHONE.route) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                )) {
                                Text("Enable")
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun MainWalletLayout(
    modifier: Modifier = Modifier,
    wallet: Wallet,
    state: WalletState,
    events: (WalletEvents) -> Unit,
    navHostController: NavHostController
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        //wallet QR Card
        item {
            Surface(
                modifier = modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                contentColor =  MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(
                    bottomStart = 12.dp,
                    bottomEnd = 12.dp
                )
            ) {
                Column(
                    modifier = modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    EtrikeQrCode(content = wallet.id ?: "")
                    Text("${wallet.id}", style = MaterialTheme.typography.labelSmall)
                    HorizontalDivider()
                    Row(
                        modifier = modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Balance", fontWeight = FontWeight.Bold)
                        Text(
                            wallet.amount.toPhp(),
                            style = MaterialTheme.typography.titleLarge.copy(

                            ))
                    }

                }
            }

        }

        item {
            Text("Activity",
                style = MaterialTheme.typography.titleLarge,
                modifier = modifier.padding(
                    16.dp
                ))
        }

        item {
            if(state.activity.isLoading) {
                LinearProgressIndicator(
                    modifier = modifier.fillMaxWidth()
                )
            }
        }

        items(state.activity.data) {
            ActivityCard(
                activity = it
            )
        }
    }
}

@Composable
fun ActivityCard(
    modifier: Modifier = Modifier,
    activity: WalletActivity
) {
    ListItem(
        modifier = modifier.fillMaxWidth(),
        headlineContent = { Text(activity.type ?: "") },
        supportingContent = {
            Text(activity.capturedTime.display())
        },
        trailingContent = {
            val amount = activity.totalAmount.toPhp()
            val isPositive = activity.type.equals("PAYMENT")
            val text = if (isPositive) "+${amount}" else "-${amount}"
            Text(
                text,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = if (isPositive) Green else MaterialTheme.colorScheme.error
                ))
        }
    )
}