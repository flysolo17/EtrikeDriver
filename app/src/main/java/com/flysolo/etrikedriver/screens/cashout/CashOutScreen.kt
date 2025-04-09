package com.flysolo.etrikedriver.screens.cashout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.flysolo.etrikedriver.config.AppRouter
import com.flysolo.etrikedriver.screens.shared.BackButton
import com.flysolo.etrikedriver.utils.ErrorScreen
import com.flysolo.etrikedriver.utils.LoadingScreen
import com.flysolo.etrikedriver.utils.RecipientTypeDropdown
import com.flysolo.etrikedriver.utils.shortToast
import com.flysolo.etrikedriver.utils.toPhp
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashOutScreen(
    modifier: Modifier = Modifier,
    id : String,
    state: CashOutState,
    events: (CashOutEvents) -> Unit,
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    LaunchedEffect(id) {
        if (id.isNotEmpty()) {
            events(CashOutEvents.OnGetWallet(id))
        }
    }
    LaunchedEffect(state.confirm) {
        state.confirm.success?.also {
            context.shortToast(it)
            delay(1000)
            navHostController.popBackStack()
        } ?: state.confirm.error?.let { context.shortToast(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = { Text("Cash Out") },
                navigationIcon = {
                    BackButton {
                        navHostController.popBackStack()
                    }
                },
                actions = {

                }
            )
        }
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
                        MainCashOutScreen(
                            modifier = modifier,
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
fun MainCashOutScreen(
    modifier: Modifier = Modifier,
    state: CashOutState,
    events: (CashOutEvents) -> Unit,
    navHostController: NavHostController
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)

    ) {
        Card(
            modifier = modifier.fillMaxWidth().padding(
                bottom = 12.dp
            ).clickable {
                navHostController.navigate(AppRouter.WALLET.navigate(state.wallet?.id ?: ""))
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
            }
        }


        val color = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        )
        TextField(
            colors = color,
            label = {
                Text("Amount")
            },
            value = state.amount.value,
            onValueChange = {
                events(CashOutEvents.OnAmountChange(it))
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.fillMaxWidth(),
            isError = state.amount.hasError,
            supportingText = {
                if (state.amount.hasError) {
                    Text("${state.amount.errorMessage}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.error
                        ))
                }
            }
        )

        Text(
            "Paypal Account Info",
            modifier = modifier.padding(
                vertical = 12.dp
            ),
            style = MaterialTheme.typography.titleMedium
        )
        RecipientTypeDropdown(
            recipients = RecipientType.entries,
            selected = state.recipientType
        ) {
            events(CashOutEvents.OnRecipientChange(it))
        }

        TextField(
            value = state.receiver.value,
            onValueChange = {
                events(CashOutEvents.OnPaypalAccountChange(it))
            },
            label = {
                Text("Paypal Account")
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.fillMaxWidth(),
            isError = state.receiver.hasError,
            supportingText = {
                if (state.receiver.hasError) {
                    Text("${state.receiver.errorMessage}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.error
                        ))
                }
            },
            colors = color,
        )
        Spacer(
            modifier = modifier.fillMaxHeight().weight(1f)
        )
        Button(
            onClick = { events(CashOutEvents.OnConfirm) },
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            enabled = state.amount.value.isNotEmpty() &&
                    !state.amount.hasError &&
                    state.amount.value.toDoubleOrNull()?.let { it >= 500 } == true &&
                    state.wallet?.amount?.let { it >= (state.amount.value.toDoubleOrNull() ?: 0.0) } == true &&
                    !state.receiver.hasError && !state.confirm.isLoading
        ) {
            Box(
                modifier = modifier.fillMaxWidth().padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.confirm.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Confirm", modifier = modifier.padding(6.dp))
                }
            }

        }
    }

}
