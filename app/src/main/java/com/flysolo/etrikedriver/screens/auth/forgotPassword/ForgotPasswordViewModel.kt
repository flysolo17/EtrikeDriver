package com.flysolo.etrikedriver.screens.auth.forgotPassword

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrikedriver.repository.auth.AuthRepository

import com.flysolo.etrikedriver.screens.auth.forgotPassword.ForgotPasswordEvents
import com.flysolo.etrikedriver.screens.auth.forgotPassword.ForgotPasswordState

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel

class ForgotPasswordViewModel @Inject constructor(
     private val repository: AuthRepository
) : ViewModel() {
    var state by mutableStateOf(ForgotPasswordState())


    fun events(events: ForgotPasswordEvents) {
        when(events) {
            is ForgotPasswordEvents.OnResetPassword -> resetPassword(events.email)
            is ForgotPasswordEvents.OnEmailChaged -> emailChanged(events.email)
        }
    }

    private fun emailChanged(email: String) {
        val hasError =  !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val errorMessage = if (hasError)  {
            "Invalid email"
        } else {
            null
        }
        val newEmail = state.email.copy(
            value = email,
            hasError = hasError,
            errorMessage = errorMessage
        )
        state = state.copy(
            email = newEmail,
        )
    }

    private fun resetPassword(email: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            repository.forgotPassword(email).onSuccess {
                state = state.copy(isSent = it, isLoading = false)
            }.onFailure {
                state = state.copy(errors = it.message.toString(), isLoading = false)
            }
        }

    }
}