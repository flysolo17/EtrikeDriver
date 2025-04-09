package com.flysolo.etrikedriver.screens.main.booking

import com.flysolo.etrikedriver.models.transactions.TransactionWithUser
import com.flysolo.etrikedriver.models.users.User
import com.flysolo.etrikedriver.screens.main.queue.QueueEvents
import com.google.android.gms.maps.model.LatLng


sealed interface BookingEvents  {
    data object OnGetPendingTransactions : BookingEvents
    data class OnSetUser(val user: User? ) : BookingEvents
    data class OnSetCurrentLocation(val latLng: LatLng) : BookingEvents
    data class OnGetFranchise(val userID : String) : BookingEvents
    data class AcceptTransaction(
        val transactionID : String,
        val driverID : String,
        val franchiseID : String,

        ) : BookingEvents

    data class OnSelectTransction(
        val transactionWithUser: TransactionWithUser?
    ) : BookingEvents
}