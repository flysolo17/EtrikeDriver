package com.flysolo.etrikedriver.screens.main.create_biometric

import com.flysolo.etrikedriver.models.users.User


sealed interface CreateBiometricEvents {
    data class OnCurrentPinChange(val currentPin : String) : CreateBiometricEvents
    data class OnVerifyPin(val pin : String , val currentPin: String) : CreateBiometricEvents
    data class OnPinChange(val pin : String) : CreateBiometricEvents
    data class OnConfirmPinChange(val pin : String) : CreateBiometricEvents
    data object OnDeletePin : CreateBiometricEvents
    data class OnSave(val encryptedPin : String) : CreateBiometricEvents
    data class OnSetUser(val user: User?) : CreateBiometricEvents
    data class OnNext(val index :Int) : CreateBiometricEvents
}