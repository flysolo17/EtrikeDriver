package com.flysolo.etrike.screens.main.security

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrikedriver.repository.auth.AuthRepository

import com.flysolo.etrikedriver.screens.main.security.SecuritySettingState
import com.flysolo.etrikedriver.screens.main.security.SecuritySettingsEvents
import com.flysolo.etrikedriver.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SecuritySettingsViewModel @Inject constructor(
    private val authRepository : AuthRepository
) : ViewModel() {
    var state by mutableStateOf(SecuritySettingState())
    init {
        events(SecuritySettingsEvents.OnGetUser)
    }
    fun events(e : SecuritySettingsEvents) {
        when(e) {
            SecuritySettingsEvents.OnEnableBiometrics -> enableBiometrics()
            SecuritySettingsEvents.OnGetUser -> getUser()
        }
    }

    private fun getUser() {
        viewModelScope.launch {
            authRepository.getCurrentUserInRealtime {
                state = when(it) {
                    is UiState.Error -> state.copy(
                        isLoading = false,
                        errors = it.message
                    )
                    UiState.Loading -> state.copy(
                        isLoading = true,
                        errors = null
                    )
                    is UiState.Success -> state.copy(
                        isLoading = false,
                        errors = null,
                        users = it.data

                    )
                }
            }
        }
    }

    private fun enableBiometrics() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)

            val currentUser = state.users
            val newPin = currentUser?.pin?.copy(
                biometricEnabled = currentUser.pin.biometricEnabled.not()
            )

            val uid = currentUser?.id.orEmpty()
            delay(1000)
            authRepository.OnBiometricEnabled(uid, newPin!!)
                .onSuccess {
                    state = state.copy(
                        isLoading = false,
                        messages = it
                    )
                }
                .onFailure {
                    state = state.copy(
                        isLoading = false,
                        errors = it.localizedMessage.orEmpty()
                    )
                }
        }
    }

}