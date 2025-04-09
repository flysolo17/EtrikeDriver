package com.flysolo.etrikedriver.screens.auth.login

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import com.flysolo.etrikedriver.R
import com.flysolo.etrikedriver.config.AppRouter
import com.flysolo.etrikedriver.utils.EtrikeColors
import com.flysolo.etrikedriver.utils.shortToast

import com.google.android.gms.auth.api.identity.BeginSignInRequest


import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    state: LoginState,
    events: (LoginEvents) -> Unit,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val oneTapClient = remember { Identity.getSignInClient(context) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        handleSignInResult(activityResult.data, oneTapClient, context, events)
    }

    LaunchedEffect(state.user) {
        state.user?.let { user ->
            val route = when {
                !user.isVerified -> AppRouter.VERIFICATION.route
                !user.user.pin?.pin.isNullOrEmpty() -> AppRouter.PIN.route
                else -> AppRouter.MAIN.route
            }
            navHostController.navigate(route) {
                popUpTo(AppRouter.LOGIN.route) { inclusive = true }
            }
        }
    }

    LaunchedEffect(state.errors) {
        state.errors?.let {
            context.shortToast("${state.errors}")
        }
    }

    Scaffold {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.logo),
                modifier = modifier.size(200.dp)
            )
            Spacer(
                modifier = modifier.height(height = 16.dp)
            )
            TextField(
                modifier = modifier.fillMaxWidth(),
                colors = TextFieldDefaults.EtrikeColors(),
                placeholder = { Text(stringResource(id = R.string.email)) },
                value = state.email.value,
                onValueChange = {events(LoginEvents.OnEmailChange(it))},
                isError = state.email.hasError,
                maxLines = 1,
                shape = MaterialTheme.shapes.medium,
                supportingText = {
                    Text(
                        state.email.errorMessage ?: "",
                        style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.error
                    ))
                }
            )

            TextField(
                modifier = modifier.fillMaxWidth(),
                colors = TextFieldDefaults.EtrikeColors(),
                placeholder = { Text(stringResource(R.string.password)) },
                value = state.password.value,
                onValueChange = { events(LoginEvents.OnPasswordChange(it)) },
                isError = state.password.hasError,
                maxLines = 1,
                shape = MaterialTheme.shapes.medium,
                supportingText = {
                    Text(
                        text = state.password.errorMessage ?: "",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.error
                        )
                    )

                },
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { events(LoginEvents.OnTogglePasswordVisibility) }) {
                        Icon(
                            imageVector = if (state.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (state.isPasswordVisible) stringResource(R.string.hide_password) else stringResource(
                                R.string.show_password
                            )
                        )
                    }
                }
            )

            TextButton(
                modifier = modifier.align(Alignment.End),
                onClick = { navHostController.navigate(AppRouter.FORGOT_PASSWORD.route) }) { Text("Forgot Password ?") }
            Spacer(
                modifier = modifier.height(8.dp)
            )
            Button(
                modifier = modifier.fillMaxWidth(),
                onClick = {events.invoke(LoginEvents.OnLogin)},
                enabled = !state.isLoading,
                shape = MaterialTheme.shapes.medium,
            ) {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Sign in")
                    }
                }
            }

            Spacer(
                modifier = modifier.weight(1f)
            )

            TermsAndPrivacyText(
                modifier = modifier
                    .wrapContentSize()
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp),
                onTermsClick = {},
                onPrivacyClick = {}
            )


        }
    }
}

@Composable
fun TermsAndPrivacyText(
    modifier: Modifier = Modifier,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    val annotatedString = buildAnnotatedString {
        append("By proceeding you agree to ")

        // Add clickable "Terms and Conditions"
        pushStringAnnotation(
            tag = "TERMS",
            annotation = "terms" // Tag to identify the click
        )
        withStyle(
            style = SpanStyle(
                color = Color.Blue,

            )
        ) {
            append("Terms and Conditions")
        }
        pop() // End of clickable text

        append(" and acknowledge you have read the ")

        // Add clickable "Privacy Policy"
        pushStringAnnotation(
            tag = "PRIVACY",
            annotation = "privacy" // Tag to identify the click
        )
        withStyle(
            style = SpanStyle(
                color = Color.Blue,
            )
        ) {
            append("Privacy Policy")
        }
        pop()
        append(".")
    }


    ClickableText(
        modifier = modifier,
        text = annotatedString,
        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                .firstOrNull()?.let {
                    onTermsClick()
                }
            annotatedString.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                .firstOrNull()?.let {
                    onPrivacyClick()
                }
        }
    )
}


private fun startGoogleSignIn(
    context: Context,
    oneTapClient: SignInClient,
    launcher: ActivityResultLauncher<IntentSenderRequest>
) {
    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(context.getString(R.string.gcp_id))
                .setFilterByAuthorizedAccounts(false) // Allow all accounts
                .build()
        )
        .build()

    oneTapClient.beginSignIn(signInRequest)
        .addOnSuccessListener { result ->
            try {
                val intentSender = result.pendingIntent.intentSender
                launcher.launch(IntentSenderRequest.Builder(intentSender).build())
            } catch (e: Exception) {
                context.shortToast("Sign-in failed: ${e.localizedMessage}")
            }
        }
        .addOnFailureListener { e ->
            context.shortToast("Google Sign-In failed: ${e.localizedMessage}")
        }
}

private fun handleSignInResult(
    data: Intent?,
    oneTapClient: SignInClient,
    context: Context,
    events: (LoginEvents) -> Unit
) {
    if (data != null) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                events(LoginEvents.SignInWithGoogle(idToken))
            } else {
                context.shortToast("ID Token is null")
            }
        } catch (e: ApiException) {
            context.shortToast("\"Error: ${e.localizedMessage}\"")
        }
    }
}
