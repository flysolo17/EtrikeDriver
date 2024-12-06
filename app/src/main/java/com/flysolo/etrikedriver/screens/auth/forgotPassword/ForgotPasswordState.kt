package com.flysolo.etrikedriver.screens.auth.forgotPassword


import com.flysolo.etrikedriver.utils.TextFieldData

data class ForgotPasswordState(
    val isLoading : Boolean = false,
    val isSent : String ? = null,
    val errors : String ? = null,
    val email : TextFieldData = TextFieldData()
)