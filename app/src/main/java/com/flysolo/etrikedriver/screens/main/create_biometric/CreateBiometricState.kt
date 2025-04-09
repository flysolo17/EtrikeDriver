package com.flysolo.etrikedriver.screens.main.create_biometric

import com.flysolo.etrikedriver.models.users.User


data class CreateBiometricState(
    val users : User? = null,
    val isLoading :Boolean = false,
    val selectedPage  : Int = 0,
    val currentPin : String  = "",
    val pin : String  = "",
    val confirmedPin : String = "",
    val errors : String  ? = null,
    val isBiometricEnabled : Boolean = false,
    val isVerifyingPin : Boolean = false,
    val isChanged : String ? = null,
)