package com.flysolo.etrikedriver.screens.auth.pin


import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import kotlinx.coroutines.delay



import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity

import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.material3.CircularProgressIndicator
import com.flysolo.etrikedriver.R
import com.flysolo.etrikedriver.config.AppRouter
import com.flysolo.etrikedriver.screens.main.create_biometric.components.ButtonGrid
import com.flysolo.etrikedriver.screens.main.security.LoadingDialog
import com.flysolo.etrikedriver.screens.main.security.displayMessage
import com.flysolo.etrikedriver.services.pin.BiometricPromptManager
import com.flysolo.etrikedriver.utils.ErrorScreen
import com.flysolo.etrikedriver.utils.shortToast


@Composable
fun PinScreen(
    modifier: Modifier = Modifier,
    state: PinState,
    events: (PinEvents) -> Unit,
    navHostController: NavHostController
) {
    val pinSize = state.pin.length

    val maxSize = 6
    val context  = LocalContext.current

     val promptManager by lazy {
        BiometricPromptManager(context as AppCompatActivity)
    }

    val biometricResult by promptManager.promptResults.collectAsState(
        initial = null
    )
    LaunchedEffect(biometricResult) {
        if (biometricResult != null) {

            delay(1000)
            context.shortToast(biometricResult.displayMessage())
            if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationSuccess) {
                navHostController.navigate(AppRouter.MAIN.route) {}
            }
        }
    }
    val enrollLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            println("Activity result: $it")

        }
    )

    LaunchedEffect(biometricResult) {
        if(biometricResult is BiometricPromptManager.BiometricResult.AuthenticationNotSet) {
            if(Build.VERSION.SDK_INT >= 30) {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                    )
                }
                enrollLauncher.launch(enrollIntent)
            }
        }
    }

    LaunchedEffect(state.verified) {
        if (state.verified) {
            navHostController.navigate(AppRouter.MAIN.route) {}
        } else {
            events.invoke(PinEvents.OnReset)
            context.shortToast("Invalid Pin")
        }
    }
    if (state.verifying) {
        LoadingDialog(title = "Verifying Pin")
    }
    LaunchedEffect(state.pin) {
        if (state.pin.length == maxSize) {
            events(PinEvents.OnCheckPin)
        }
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()
                !state.isLoading && state.errors != null -> ErrorScreen(
                    title = "${state.errors}"
                ) {
                    TextButton(onClick = {navHostController.popBackStack()}) { Text("Back") }
                }
                !state.isLoading && state.users != null  -> {
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = modifier.size(120.dp),
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(color = Color.White)
                        )
                        Text(
                            "Welcome Back, \n ${state.users.name}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                textAlign = TextAlign.Center
                            )
                        )
                        Text(
                            "Enter your PIN for ${state.users.email}",
                            style = MaterialTheme.typography.labelMedium.copy(
                            )
                        )
                        TextButton(
                            onClick = {}
                        ) { Text("Forgot PIN?") }
                        Spacer(
                            modifier = modifier.height(16.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(maxSize) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (state.pin.getOrNull(index) != null) MaterialTheme.colorScheme.background
                                            else Color.Transparent
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.background,
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))

                        ButtonGrid(
                            onClick = { events(PinEvents.OnPinChange(it)) },
                            onDelete = { events(PinEvents.OnDeletePin) },
                            onBiometricClick = {
                                promptManager.showBiometricPrompt(
                                    title = "Enable Biometrics",
                                    description = "Use biometrics to enter Etrike."
                                )
                            },
                            isBiometricEnabled = state.users.pin?.biometricEnabled == true
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Row(
                            modifier = modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = {}) {
                                Text("Logout")
                            }

                        }
                    }
                } else  -> {
                ErrorScreen(
                    title = "Unknown Error"
                ) {
                    TextButton(onClick = {navHostController.popBackStack()}) { Text("Back") }
                }
                }
            }
        }

    }
}