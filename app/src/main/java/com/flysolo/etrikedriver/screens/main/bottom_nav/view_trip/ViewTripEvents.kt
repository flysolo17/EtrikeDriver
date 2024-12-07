package com.flysolo.etrikedriver.screens.main.bottom_nav.view_trip


sealed interface ViewTripEvents {
    data class OnViewTrip(
        val transactionID: String
    ) : ViewTripEvents


}