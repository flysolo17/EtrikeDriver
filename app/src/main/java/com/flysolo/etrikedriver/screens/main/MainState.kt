package com.flysolo.etrikedriver.screens.main

import com.flysolo.etrikedriver.models.franchise.Franchise
import com.flysolo.etrikedriver.models.users.User


data class MainState(
    val isLoading : Boolean = false,
    val user: User? = null,
    val errors : String ? = null,
    val franchises : List<Franchise> = emptyList()
)