package com.flysolo.etrikedriver.screens.main.bottom_nav.trips

import com.flysolo.etrikedriver.models.transactions.Transactions
import com.flysolo.etrikedriver.models.users.User


data class TripState(
    val user : User?  = null,
    val isLoading : Boolean = false,
    val trips : List<Transactions> = emptyList(),
    val errors : String ? = null
)