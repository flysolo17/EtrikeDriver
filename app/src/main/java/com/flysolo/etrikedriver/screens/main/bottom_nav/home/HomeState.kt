package com.flysolo.etrikedriver.screens.main.bottom_nav.home

import com.flysolo.etrikedriver.models.transactions.TransactionWithUser
import com.flysolo.etrikedriver.models.transactions.Transactions
import com.flysolo.etrikedriver.models.wallet.Wallet


data class HomeState(
    val isLoading : Boolean = false,
    val errors : String ? = null,
    val user : com.flysolo.etrikedriver.models.users.User ? = null,
    val isGettingTransactions : Boolean = false,
    val ongoingTrips : List<TransactionWithUser> = emptyList(),
    val wallet: Wallet? = null,
)