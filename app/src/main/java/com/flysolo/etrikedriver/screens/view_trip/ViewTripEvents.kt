package com.flysolo.etrikedriver.screens.view_trip

import android.view.View

sealed interface ViewTripEvents {
    data class OnViewTrip(
        val transactionID: String
    ) : ViewTripEvents


}