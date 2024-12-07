package com.flysolo.etrikedriver.screens.main.bottom_nav.trips

import com.flysolo.etrikedriver.models.users.User


sealed interface TripEvents {
    data class OnSetUser(
        val user: User?
    ) : TripEvents

    data class OnGetTrips(
        val id : String,
    ) : TripEvents

}