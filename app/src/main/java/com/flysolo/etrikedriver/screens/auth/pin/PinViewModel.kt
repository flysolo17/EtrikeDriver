package com.flysolo.etrikedriver.screens.auth.pin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrikedriver.repository.auth.AuthRepository
import com.flysolo.etrikedriver.services.pin.PinEncryptionManager
import com.flysolo.etrikedriver.utils.UiState

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PinViewModel @Inject constructor(
    private val authRepository : AuthRepository,
    private val pinEncryptionManager: PinEncryptionManager
) : ViewModel() {
    var state by mutableStateOf(PinState())
    init {
        events(PinEvents.OnGetUser)
    }
    fun events(e : PinEvents) {
        when(e) {
            PinEvents.OnGetUser -> getCurrentUser()
            is PinEvents.OnDeletePin -> deletePin()
            is PinEvents.OnPinChange -> pinChange(e.pin)
            PinEvents.OnReset -> reset()
            PinEvents.OnCheckPin -> checkPin()
        }
    }

    private fun checkPin() {
        viewModelScope.launch {
            state = state.copy(verifying = true)
            val currentPin = state.pin
            val correctPin =  pinEncryptionManager.decrypt( state.users?.pin?.pin ?: "")
            delay(1000)
            state = state.copy(
                verifying = false,
                verified = currentPin == correctPin
            )
            reset()
        }
    }

    private fun reset() {
        state = state.copy(
            pin = ""
        )
    }

    private fun pinChange(pin: String) {

        val current = state.pin + pin
        state = state.copy(pin = current)
    }

    private fun deletePin() {
        state = state.copy(
            pin = if (state.pin.isNotEmpty()) state.pin.substring(0, state.pin.length - 1) else state.pin
        )
    }


    private fun getCurrentUser() {
        viewModelScope.launch {
            authRepository.getCurrentUserInRealtime {
                state = when(it) {
                    is UiState.Error -> state.copy(
                        isLoading = false,
                        errors = it.message
                    )
                    UiState.Loading -> state.copy(
                        isLoading = true,
                        errors = null,
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
}