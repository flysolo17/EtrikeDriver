package com.flysolo.etrikedriver.screens.auth.forgotPassword

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import com.flysolo.etrikedriver.R


import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    state : ForgotPasswordState,
    events: (ForgotPasswordEvents) -> Unit,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    LaunchedEffect(state) {
        if (state.errors !== null) {
            Toast.makeText(context,state.errors,Toast.LENGTH_SHORT).show()
        }
        if (state.isSent !== null) {
            Toast.makeText(context,state.isSent,Toast.LENGTH_SHORT).show()
            delay(1000)
            navHostController.popBackStack()
        }
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Forgot Password? ", style = MaterialTheme.typography.titleMedium) },
            colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        ), navigationIcon = {
            IconButton(onClick = { navHostController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },)
        }
    ) {
        Column(modifier = modifier
            .fillMaxSize()
            .padding(it)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.reset_password), style = MaterialTheme.typography.titleLarge)

            Text(
                text = stringResource(R.string.reset_message),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                modifier =  Modifier.padding(16.dp)
            )

            OutlinedTextField(state.email.value,
                onValueChange = {
                    events(ForgotPasswordEvents.OnEmailChaged(it))
                },
                modifier = modifier.fillMaxWidth(),
                isError = state.email.hasError,
                label = {
                    Text(text = "Email")
                },
                supportingText = {
                    Text(
                        text =  state.email.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Start
                    )
                },

                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Button(
                modifier = modifier.fillMaxWidth(),
                onClick = {
                    events(ForgotPasswordEvents.OnResetPassword(state.email.value)) },
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = modifier.size(18.dp)
                    )
                } else {
                    Text(text = "Send")
                }
            }
        }
    }


}