package com.flysolo.etrikedriver.screens.auth.change_password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrikedriver.repository.auth.AuthRepository
import com.flysolo.etrikedriver.utils.UiState


import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    var state by mutableStateOf(ChangePasswordState())
    fun events(e : ChangePasswordEvents) {
        when(e) {
            is ChangePasswordEvents.OnTogglePasswordVisibility -> togglePasswordVisibility(e.passwords)
            is ChangePasswordEvents.OnPasswordChanged -> passwordChanged(e.password,e.passwords)
            is ChangePasswordEvents.OnSubmit -> submit()
        }
    }

    private fun submit() {
        val old = state.oldPassword
        val new = state.newPassword
        val repeat = state.repeatPassword
        if (old.isError || new.isError || repeat.isError) {
            state = state.copy(errors = "Invalid Password form")
            return
        }
        viewModelScope.launch {
            authRepository.changePassword(old.value,new.value) {
                state =  when(it) {
                    is UiState.Error -> state.copy(
                        isLoading = false,
                        errors = it.message
                    )
                    UiState.Loading -> state.copy(isLoading = true, errors = null)
                    is UiState.Success -> state.copy(
                        isLoading = false,
                        errors = null,
                        changePasswordSuccess = it.data
                    )
                }
            }
        }
    }

    private fun passwordChanged(password: String, passwords: Passwords) {
        when (passwords) {
            Passwords.OLD -> {
                val current = state.oldPassword
                state = state.copy(oldPassword = current.copy(value = password))
            }
            Passwords.NEW -> {
                val current = state.newPassword
                state = if (password.length < 7) {
                    state.copy(newPassword = current.copy(value = password, isError = true, errorMessage = "Password length is less than 7 characters"))
                } else {
                    state.copy(newPassword = current.copy(value = password, isError = false, errorMessage = null))
                }
            }
            Passwords.REPEAT -> {
                val current = state.repeatPassword
                state = if (password != state.newPassword.value) {
                    state.copy(repeatPassword = current.copy(value = password, isError = true, errorMessage = "Passwords do not match"))
                } else {
                    state.copy(repeatPassword = current.copy(value = password, isError = false, errorMessage = null))
                }
            }
        }
    }

    private fun togglePasswordVisibility(passwords: Passwords) {
        state = when(passwords) {
            Passwords.OLD -> state.copy(isOldPasswordVisible = !state.isOldPasswordVisible)
            Passwords.NEW -> state.copy(isNewPasswordVisible = !state.isNewPasswordVisible)
            Passwords.REPEAT -> state.copy(isRepeatPasswordVisible = !state.isRepeatPasswordVisible)
        }
    }

}