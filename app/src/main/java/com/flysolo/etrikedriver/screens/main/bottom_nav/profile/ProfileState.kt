package com.flysolo.etrikedriver.screens.main.bottom_nav.profile

import com.flysolo.etrikedriver.models.users.User


data class ProfileState(
    val isLoading : Boolean = false,
    val user : User? = null,
    val errors : String ? = null,
    val messages : String ? = null,
    val isLoggedOut : String? = null
)