package com.flysolo.etrikedriver.screens.main.bottom_nav.profile.view_bookings

import com.flysolo.etrikedriver.models.transactions.TransactionStatus


sealed interface ViewBookingEvents {
    data class OnGetAllBookings(
        val driverID : String
    ) : ViewBookingEvents
    data class OnSelectTab(
        val tab : TransactionStatus,
        val index : Int
    ) : ViewBookingEvents
}