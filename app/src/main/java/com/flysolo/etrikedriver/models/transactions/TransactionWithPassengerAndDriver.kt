package com.flysolo.etrikedriver.models.transactions

import com.flysolo.etrikedriver.models.users.User


data class TransactionWithPassengerAndDriver(
    val transactions: Transactions,
    val passenger : User? = null,
    val driver : User ? = null
)