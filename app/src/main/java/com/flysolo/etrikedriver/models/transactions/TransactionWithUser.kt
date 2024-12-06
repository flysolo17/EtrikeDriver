package com.flysolo.etrikedriver.models.transactions

import com.flysolo.etrikedriver.models.users.User


data class TransactionWithUser(
    val user: User ? = null,
    val transactions: Transactions
)