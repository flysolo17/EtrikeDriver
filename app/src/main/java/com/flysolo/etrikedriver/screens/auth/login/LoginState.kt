package com.flysolo.etrikedriver.screens.auth.login

import com.flysolo.etrikedriver.models.users.UserWithVerification

import com.flysolo.etrikedriver.utils.TextFieldData


data class LoginState(
    val isLoading : Boolean = false,
    val isSigningWithGoogle : Boolean = false,
    val user: UserWithVerification? = null,
    val errors : String ? = null,
    val email : TextFieldData = TextFieldData(),
    val password : TextFieldData = TextFieldData(),
    val isPasswordVisible : Boolean = false,


    )