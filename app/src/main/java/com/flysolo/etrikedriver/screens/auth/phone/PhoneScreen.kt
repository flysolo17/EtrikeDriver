package com.flysolo.etrikedriver.screens.auth.phone

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flysolo.etrikedriver.ui.theme.EtrikeDriverTheme
import com.flysolo.etrikedriver.utils.shortToast

import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneScreen(
    modifier: Modifier = Modifier,
    state: PhoneState,
    events: (PhoneEvents) -> Unit,
    navHostController: NavHostController
) {
    val context = LocalContext.current

    // Handle side effects
    LaunchedEffect(state.user) {
        state.user?.phone?.let { events(PhoneEvents.OnPhoneChange(it.toPhoneNumber())) }
    }
    LaunchedEffect(state.isVerified) {
        if (state.isVerified) {
            context.shortToast("Wallet Enabled")
            delay(1000)
            navHostController.popBackStack()
        }
    }
    LaunchedEffect(state.errors) {
        state.errors?.let { context.shortToast(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Enable your wallet") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Prevents layout issues
        ) {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = state.phone,
                        onValueChange = { input ->
                            events(PhoneEvents.OnPhoneChange(input))
                        },
                        prefix = {
                            Text("+63")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        singleLine = true,
                        label = { Text("Enter phone number") },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        shape = MaterialTheme.shapes.medium


                    )

                    TextButton(
                        enabled = state.countdown == 0,
                        onClick = {
                            events(PhoneEvents.OnSendOTP(context as Activity, state.phone))
                        }
                    ) {
                        Text(if (state.countdown == 0) "Send" else state.countdown.toString())
                    }
                }

                // OTP Input
                TextField(
                    value = state.otp,
                    onValueChange = { events(PhoneEvents.OnOtpChange(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    label = { Text("Confirm OTP") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // Enable Wallet Button
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { events(PhoneEvents.OnVerifyOtp) },
                shape = MaterialTheme.shapes.small,
                enabled = !state.isLoading
            ) {
                Row(
                    modifier = Modifier.padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Wallet, contentDescription = "Wallet")
                    Spacer(Modifier.width(8.dp))
                    Text("Enable Wallet")
                }
            }
        }
    }
}

private fun String.toPhoneNumber(): String {
    val digitsOnly = this.filter { it.isDigit() }
    return if (digitsOnly.startsWith("0") && digitsOnly.length == 11) {
        digitsOnly.drop(1)
    } else {
        digitsOnly
    }
}


@Preview
@Composable
private fun PhoneScreenPrev() {
    EtrikeDriverTheme   {
        PhoneScreen(
            state = PhoneState(),
            events = {},
            navHostController = rememberNavController()
        )
    }
}