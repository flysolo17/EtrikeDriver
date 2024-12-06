package com.flysolo.etrikedriver.screens.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.flysolo.etrikedriver.repository.auth.AuthRepository
import com.flysolo.etrikedriver.utils.TextFieldData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository : AuthRepository
) : ViewModel() {
    var state by mutableStateOf(LoginState())
    init {
        events(LoginEvents.OnGetCurrentUser)
    }
    fun events(e : LoginEvents) {
        when(e) {
            is LoginEvents.SignInWithGoogle -> loginWithGoogle(e.idToken)
            is LoginEvents.OnEmailChange -> emailChange(e.email)
            is LoginEvents.OnPasswordChange -> passwordChange(e.password)
            LoginEvents.OnTogglePasswordVisibility -> state = state.copy(isPasswordVisible = !state.isPasswordVisible)
            LoginEvents.OnGetCurrentUser -> getCurrentUser()
            LoginEvents.OnLogin -> login()
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val result = authRepository.getCurrentUser()
            result.onSuccess { user ->
                state = state.copy(
                    user = user, isLoading = false)
            }.onFailure { exception ->
                state = state.copy(
                    errors = exception.localizedMessage,
                    isLoading = false
                )
            }
        }
    }

    private fun passwordChange(password: String) {
        state = state.copy(
            password = state.password.copy(
                value = password
            )
        )
    }

    private fun emailChange(email: String) {
        val trimmedEmail = email.trim()
        val errorMessage = when {
            trimmedEmail.isEmpty() -> "Email cannot be empty."
            !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> "Invalid email format."
            else -> null
        }
        val hasError = errorMessage != null
        state = state.copy(
            email = TextFieldData(
                value = trimmedEmail,
                hasError = hasError,
                errorMessage = errorMessage
            )
        )
    }


    private fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            state = state.copy(isSigningWithGoogle = true)
            val result = authRepository.signInWithGoogle(idToken)
            result.onSuccess { user ->
                state = state.copy(
                    user = user,
                )
            }.onFailure { exception ->
                state = state.copy(
                    errors = exception.localizedMessage,
                )
            }
            state = state.copy(isSigningWithGoogle = false)
        }
    }
    private fun login() {
        if (state.email.hasError || state.password.hasError) {
            return
        }
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val result = authRepository.signInWithEmailAndPassword(email = state.email.value, password = state.password.value)
            result.onSuccess { user ->
                state = state.copy(
                    user = user,
                )
            }.onFailure { exception ->
                state = state.copy(
                    errors = exception.localizedMessage,
                )
            }
            state = state.copy(isLoading = false)
        }
    }
}