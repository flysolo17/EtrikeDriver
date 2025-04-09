package com.flysolo.etrike.screens.main.create_biometric

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrikedriver.models.users.Pin

import com.flysolo.etrikedriver.repository.auth.AuthRepository
import com.flysolo.etrikedriver.screens.main.create_biometric.CreateBiometricEvents
import com.flysolo.etrikedriver.screens.main.create_biometric.CreateBiometricState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreateBiometricViewModel @Inject constructor(
    private val authRepository : AuthRepository
) : ViewModel() {
    var state by mutableStateOf(CreateBiometricState())
    fun events(e : CreateBiometricEvents) {
        when(e) {
            is CreateBiometricEvents.OnSetUser -> {
                val noPinYet = e.user?.pin?.pin.isNullOrEmpty()
                state = state.copy(
                    users = e.user,
                    selectedPage =if (noPinYet) 1 else 0
                )
            }
            CreateBiometricEvents.OnDeletePin -> deletePin()

            is CreateBiometricEvents.OnConfirmPinChange -> state = state.copy(
                confirmedPin =state.confirmedPin +  e.pin
            )
            is CreateBiometricEvents.OnPinChange -> state= state.copy(
                pin =state.pin + e.pin
            )
            is CreateBiometricEvents.OnCurrentPinChange -> state = state.copy(
                currentPin =state.currentPin + e.currentPin
            )

            is CreateBiometricEvents.OnVerifyPin -> verify(e.pin,e.currentPin)
            is CreateBiometricEvents.OnNext -> state = state.copy(
                selectedPage = e.index
            )

            is CreateBiometricEvents.OnSave ->savePin(e.encryptedPin)
        }
    }

    private fun verify(pin: String, currentPin: String) {
        viewModelScope.launch {
            state =state.copy(
                isVerifyingPin = true
            )
            delay(1000)
            state = if (pin == currentPin) state.copy(
                isVerifyingPin = false,
                selectedPage = 1,
            ) else state.copy(
                isVerifyingPin = false,
                selectedPage = 0,
                errors = "Wrong Pin",
                currentPin = ""
            )
            delay(1000)
            state = state.copy(
                errors = null
            )
        }

    }


    private fun savePin(encryptedPin : String) {
        state = state.copy(isLoading = true)
        val uid = state.users?.id ?: ""
        val newPin = Pin(
            pin = encryptedPin,
            biometricEnabled = state.users?.pin?.biometricEnabled ?: false
        )
        viewModelScope.launch {
            authRepository.OnChangePin(uid,newPin).onSuccess {
                state = state.copy(

                    isLoading = false,
                    isChanged = it
                )
            }.onFailure {
                state = state.copy(
                    isLoading = false,
                    errors = it.localizedMessage?.toString()
                )
            }
        }

    }

    private fun deletePin() {
        state = when (state.selectedPage) {
            0 -> {
                // Remove the last character from the currentPin
                val pin = state.currentPin.dropLast(1)
                state.copy(currentPin = pin)
            }
            1 -> {
                // Remove the last character from the pin
                val pin = state.pin.dropLast(1)
                state.copy(pin = pin)
            }
            else -> {
                // Remove the last character from the confirmedPin
                val confirmedPin = state.confirmedPin.dropLast(1)
                state.copy(confirmedPin = confirmedPin)
            }
        }
    }

}