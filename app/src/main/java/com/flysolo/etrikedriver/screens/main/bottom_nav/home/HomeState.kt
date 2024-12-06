package com.flysolo.etrikedriver.screens.main.bottom_nav.home




data class HomeState(
    val isLoading : Boolean = false,
    val errors : String ? = null,
    val user : com.flysolo.etrikedriver.models.users.User ? = null,

)