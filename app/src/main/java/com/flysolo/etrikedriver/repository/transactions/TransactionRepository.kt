package com.flysolo.etrikedriver.repository.transactions

import com.flysolo.etrikedriver.models.transactions.TransactionWithPassengerAndDriver
import com.flysolo.etrikedriver.models.transactions.TransactionWithUser
import com.flysolo.etrikedriver.models.transactions.Transactions
import com.flysolo.etrikedriver.utils.UiState

interface TransactionRepository {
    suspend fun getAllPendingTransaction(
        callback : (Result<List<TransactionWithUser>>) -> Unit
    )
    suspend fun acceptTransaction(
        transactionID : String,
        driverID : String ,
        franchiseID : String
    ) : Result<String>

    suspend fun getMyOnGoingTransactions(
        driverID: String,
        result : (UiState<List<TransactionWithUser>>) -> Unit
    )


    suspend fun gotoPickupLocation(transactionID: String) : Result<String>

    suspend fun viewTripInfo(transactionID: String ,result: (UiState<TransactionWithPassengerAndDriver>) -> Unit)


    suspend fun getAllTransactions(driverID : String,result: (UiState<List<Transactions>>) -> Unit)
}