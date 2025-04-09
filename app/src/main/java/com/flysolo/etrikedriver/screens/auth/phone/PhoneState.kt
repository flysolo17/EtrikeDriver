package com.flysolo.etrikedriver.screens.auth.phone

import com.flysolo.etrikedriver.models.users.User


data class PhoneState(
    val isLoading : Boolean = false,
    val user : User? = null,
    val phone : String = "",
    val errors : String ? = null,

    val verificationId: String? = null,
    val otp: String = "",
    val isVerified: Boolean = false,
    val countdown : Int = 0,
)