package com.flysolo.etrikedriver.screens.auth.verification

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import com.flysolo.etrikedriver.R
import com.flysolo.etrikedriver.config.AppRouter
import com.flysolo.etrikedriver.utils.shortToast


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    modifier: Modifier = Modifier,
    state: VerificationState,
    events: (VerificationEvents) -> Unit,
    navHostController: NavHostController
) {
    val context = LocalContext.current


    LaunchedEffect(true) {
        events(VerificationEvents.OnSendVerification(context))
    }

    LaunchedEffect(state) {
        if (state.isVerified) {
            context.shortToast("Successfully Verified")
            if (state.users != null) {
                navHostController.navigate(AppRouter.MAIN.route) {
                    popUpTo(AppRouter.LOGIN.route) { inclusive = true }
                    launchSingleTop = true
                }

            }
        }
    }
    Scaffold {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.verification), contentDescription = "Verification")
            Spacer(modifier = modifier.height(12.dp))
            Text(text = "Verify your email address", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = modifier.height(8.dp))
            Text(
                text = "In order to start using Etrike App, you need to verify your email address.",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray),
                textAlign = TextAlign.Center,
                modifier = modifier.padding(8.dp)
            )
            Spacer(modifier = modifier.height(8.dp))

            Button (
                onClick = { events(VerificationEvents.OnSendVerification(context)) },
                shape = MaterialTheme.shapes.medium,
                enabled = !state.isLoading && state.timer == 0
            ) {
                Text(
                    text = if (state.timer == 0) "Verify email address" else "${state.timer} seconds"
                )
            }
        }
    }
}