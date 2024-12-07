package com.flysolo.etrikedriver.screens.main.bottom_nav.view_trip

import com.flysolo.etrikedriver.models.transactions.Transactions
import com.flysolo.etrikedriver.models.users.User


data class ViewTripState(
    val isLoading : Boolean  = false,
    val transactions: Transactions? = null,
    val passenger : User? = null,
    val driver : User ? = null,
    val errors : String ? = null,
    val isAcceptingDriver : Boolean = false,
    val isDecliningDriver : Boolean = false,
    val messages : String ? = null
)
