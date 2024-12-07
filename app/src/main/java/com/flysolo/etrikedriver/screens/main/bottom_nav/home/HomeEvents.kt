package com.flysolo.etrikedriver.screens.main.bottom_nav.home

import android.content.Context
import com.flysolo.etrikedriver.models.transactions.Transactions
import com.flysolo.etrikedriver.models.users.User


sealed interface HomeEvents {
    data class OnSetUser(
        val user: User ?
    ) : HomeEvents

    data class OnGetOngoingTrips(val driverID: String) : HomeEvents

    data class OnPickup(val transactions : Transactions,val context : Context) : HomeEvents


}