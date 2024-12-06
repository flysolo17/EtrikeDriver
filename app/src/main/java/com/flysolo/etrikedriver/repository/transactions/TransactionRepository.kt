package com.flysolo.etrikedriver.repository.transactions

import com.flysolo.etrikedriver.models.transactions.TransactionWithUser
import com.flysolo.etrikedriver.models.transactions.Transactions

interface TransactionRepository {
    suspend fun getAllPendingTransaction(
        callback : (Result<List<TransactionWithUser>>) -> Unit
    )
    suspend fun acceptTransaction(
        transactionID : String,
        driverID : String ,
        franchiseID : String
    ) : Result<String>
}