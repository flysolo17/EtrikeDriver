package com.flysolo.etrikedriver.screens.main.booking

import com.flysolo.etrikedriver.models.franchise.Franchise
import com.flysolo.etrikedriver.models.transactions.TransactionWithUser
import com.flysolo.etrikedriver.models.users.User
import com.google.android.gms.maps.model.LatLng


data class BookingState(
    val isLoading : Boolean = false,
    val currentPosition : LatLng = LatLng(0.00,0.00),
    val transactionWithUser : List<TransactionWithUser> = emptyList(),
    val errors : String ? = null,
    val user : User? = null,
    val franchise : Franchise? = null,
    val isAccepting : Boolean = false,
    val isAccepted : String ? = null,
    val selectedTransaction : TransactionWithUser? = null
)