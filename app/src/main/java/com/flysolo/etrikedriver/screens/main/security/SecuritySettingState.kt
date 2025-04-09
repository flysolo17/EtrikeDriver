package com.flysolo.etrikedriver.screens.main.security

import com.flysolo.etrikedriver.models.users.User
import com.flysolo.etrikedriver.services.pin.BiometricPromptManager


data class SecuritySettingState(
    val isLoading : Boolean = false,
    val errors : String ? = null,
    val users : User? = null,
    val messages : String ? = null,
)


fun BiometricPromptManager.BiometricResult?.displayMessage(): String {
    return when(this) {
        is BiometricPromptManager.BiometricResult.AuthenticationError -> {
            this.error
        }
        BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
            "Authentication failed"
        }
        BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
            "Authentication not set"
        }
        BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
            "Authentication success"
        }
        BiometricPromptManager.BiometricResult.FeatureUnavailable -> {
            "Feature unavailable"
        }
        BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
            "Hardware unavailable"
        }

        null -> "Not found!"
    }
}