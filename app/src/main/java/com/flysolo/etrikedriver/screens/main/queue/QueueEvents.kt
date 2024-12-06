package com.flysolo.etrikedriver.screens.main.queue

import com.flysolo.etrikedriver.models.transactions.TransactionWithUser
import com.flysolo.etrikedriver.models.users.User
import com.google.android.gms.maps.model.LatLng


sealed interface QueueEvents {
    data object OnGetPendingTransactions : QueueEvents
    data class OnSetUser(val user: User ? ) : QueueEvents
    data class OnSetCurrentLocation(val latLng: LatLng) : QueueEvents
    data class OnGetFranchise(val userID : String) : QueueEvents
    data class AcceptTransaction(
        val transactionID : String,
        val driverID : String,
        val franchiseID : String,

    ) : QueueEvents

    data class OnSelectTransction(
        val transactionWithUser: TransactionWithUser ?
    ) : QueueEvents
}