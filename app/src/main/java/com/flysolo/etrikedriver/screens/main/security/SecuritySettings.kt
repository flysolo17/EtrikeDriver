package com.flysolo.etrikedriver.screens.main.security

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController

import com.flysolo.etrikedriver.config.AppRouter
import com.flysolo.etrikedriver.services.pin.BiometricPromptManager
import com.flysolo.etrikedriver.services.pin.PinEncryptionManager
import com.flysolo.etrikedriver.utils.EtrikeToBar
import com.flysolo.etrikedriver.utils.shortToast

import kotlinx.coroutines.delay


@Composable
fun SecuritySettingsScreen(
    modifier: Modifier = Modifier,
    state: SecuritySettingState,
    events: (SecuritySettingsEvents) -> Unit,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    var isLoading by remember {
        mutableStateOf(false)
    }

    val pinEncryptionManager = PinEncryptionManager(
        context
    )

    val promptManager by lazy {
        BiometricPromptManager(context as AppCompatActivity)
    }
    val biometricResult by promptManager.promptResults.collectAsState(
        initial = null
    )
    LaunchedEffect(biometricResult) {
        if (biometricResult != null) {
            isLoading = true
            delay(1000)
            context.shortToast(biometricResult.displayMessage())
            if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationSuccess) {
                isLoading = false
                events(SecuritySettingsEvents.OnEnableBiometrics)
            }
            isLoading = false
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
    if (isLoading) {
        LoadingDialog(title = "Verifying biometrics")
    }
    Scaffold(
        topBar = {
            EtrikeToBar(
                title = "Security Settings",
                onBack = { navHostController.popBackStack() }
            ) {}
        },
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = modifier.fillMaxWidth()
                )
            }
            Card(
                elevation = CardDefaults.cardElevation(5.dp),
                modifier = modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    TextButton(
                        onClick = {
                            navHostController.navigate(AppRouter.CREATE_PIN.route)
                        },
                        modifier = modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Change PIN")
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = "Arrow"
                            )
                        }
                    }
                    HorizontalDivider()
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Login using biometrics")
                        val isBiometricEnabled = state.users?.pin?.biometricEnabled ?: false
                        val pin = state.users?.pin?.pin
                        Switch( checked = isBiometricEnabled,
                            onCheckedChange = {
                            if (pin.isNullOrEmpty()) {
                                context.shortToast("Create PIN to enable biometrics")
                                return@Switch
                            }
                            promptManager.showBiometricPrompt(
                                title = "Enable Biometrics",
                                description = "Use biometrics to enter Etrike."
                            )
                        })
                    }
                }
            }
        }
    }
}



@Composable
fun LoadingDialog(
    title: String
) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Loading Spinner
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
