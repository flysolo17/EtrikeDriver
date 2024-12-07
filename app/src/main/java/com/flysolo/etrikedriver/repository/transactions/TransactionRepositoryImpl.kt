package com.flysolo.etrikedriver.repository.transactions

import android.util.Log
import com.flysolo.etrikedriver.models.transactions.TransactionStatus
import com.flysolo.etrikedriver.models.transactions.TransactionWithPassengerAndDriver
import com.flysolo.etrikedriver.models.transactions.TransactionWithUser
import com.flysolo.etrikedriver.models.transactions.Transactions
import com.flysolo.etrikedriver.models.users.USER_COLLECTION
import com.flysolo.etrikedriver.utils.UiState
import com.flysolo.etrikedriver.utils.generateRandomNumberString
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date


const val TRANSACTION_COLLECTION  = "transactions"
class TransactionRepositoryImpl(
  private  val firestore: FirebaseFirestore,
): TransactionRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    override suspend fun getAllPendingTransaction(callback: (Result<List<TransactionWithUser>>) -> Unit) {
        firestore.collection(TRANSACTION_COLLECTION)
            .whereEqualTo("status", TransactionStatus.PENDING)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    callback(Result.failure(error))
                    return@addSnapshotListener
                }
                value?.let { snapshot ->
                    coroutineScope.launch {
                        val transactions = snapshot.toObjects(Transactions::class.java)
                        val transactionWithUserList = transactions.mapNotNull { transaction ->
                            val passengerID = transaction.passengerID ?: return@mapNotNull null
                            val user = firestore.collection(USER_COLLECTION)
                                .document(passengerID)
                                .get()
                                .await()
                                .toObject(com.flysolo.etrikedriver.models.users.User::class.java)
                            user?.let {
                                TransactionWithUser(
                                    user = it,
                                    transactions = transaction
                                )
                            }
                        }
                        callback(Result.success(transactionWithUserList))
                    }
                }
            }
    }

    override suspend fun acceptTransaction(
        transactionID: String,
        driverID: String,
        franchiseID: String
    ): Result<String> {
        return try {
            val result = firestore
                .collection(TRANSACTION_COLLECTION)
                .document(transactionID)
                .update(
                    "driverID",driverID,
                    "franchiseID",franchiseID,
                    "status",TransactionStatus.ACCEPTED,
                    "updatedAt",Date()
                ).await()
            Result.success("Successfully Accepted")
        } catch (e : Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyOnGoingTransactions(
        driverID: String,
        result: (UiState<List<TransactionWithUser>>) -> Unit
    ) {
        result.invoke(UiState.Loading)
        firestore.collection(TRANSACTION_COLLECTION)
            .whereEqualTo("driverID", driverID)
            .whereNotIn("status", listOf(TransactionStatus.COMPLETED, TransactionStatus.FAILED,TransactionStatus.CANCELLED))
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    result.invoke(UiState.Error(it.message.toString()))
                }
                value?.let {
                    val transactions = value.toObjects(Transactions::class.java)
                    val transactionWithUsers  = mutableListOf<TransactionWithUser>()
                    CoroutineScope(Dispatchers.IO).launch {
                        val transactionWithUser = transactions.map { transaction ->
                            val passerID = transaction.passengerID ?: ""
                            val passenger = if (passerID.isNotEmpty()) {
                                firestore.collection(USER_COLLECTION)
                                    .document(passerID)
                                    .get()
                                    .await()
                                    .toObject(com.flysolo.etrikedriver.models.users.User::class.java)
                            } else null
                            TransactionWithUser(
                                transactions = transaction,
                                user = passenger
                            )
                        }
                        transactionWithUsers.addAll(transactionWithUser)
                        result.invoke(UiState.Success(transactionWithUsers))
                    }
                }
            }
    }

//    override suspend fun getMyOnGoingTransactions(driverID: String): Result<List<TransactionWithUser>> {
//        return try {
//            val result = CompletableDeferred<Result<List<TransactionWithUser>>>()
//            firestore.collection(TRANSACTION_COLLECTION)
//                .whereEqualTo("driverID", driverID)
//                .whereNotIn("status", listOf(TransactionStatus.COMPLETED, TransactionStatus.FAILED,TransactionStatus.CANCELLED))
//                .orderBy("updatedAt", Query.Direction.DESCENDING)
//                .orderBy("createdAt", Query.Direction.DESCENDING)
//                .addSnapshotListener { value, error ->
//                    if (error != null) {
//                        result.complete(Result.failure(Exception("Failed to get transactions: ${error.message}")))
//                    } else if (value != null) {
//                        val transactions = value.toObjects(Transactions::class.java)
//                        Log.d("transactions",transactions.toString())
//                        CoroutineScope(Dispatchers.IO).launch {
//                            val transactionWithDrivers = transactions.map { transaction ->
//                                val passerID = transaction.passengerID ?: ""
//                                val passenger = if (passerID.isNotEmpty()) {
//                                    firestore.collection(USER_COLLECTION)
//                                        .document(passerID)
//                                        .get()
//                                        .await()
//                                        .toObject(com.flysolo.etrikedriver.models.users.User::class.java)
//                                } else null
//                                TransactionWithUser(
//                                    transactions = transaction,
//                                    user = passenger
//                                )
//                            }
//                            Log.d("transactions",transactionWithDrivers.toString())
//                            result.complete(Result.success(transactionWithDrivers))
//                        }
//                    }
//                }
//            result.await()
//        } catch (e: Exception) {
//            Result.failure(Exception("Failed to get transactions: ${e.message}"))
//        }
//    }

    override suspend fun gotoPickupLocation(transactionID: String): Result<String> {
        return try {
            val result = firestore
                .collection(TRANSACTION_COLLECTION)
                .document(transactionID)
                .update("status",TransactionStatus.OTW, "updatedAt", Date())
                .await()
            Result.success("Successfully Accepted")
        } catch (e : Exception) {
            Result.failure(e)
        }
    }

    override suspend fun viewTripInfo(
        transactionID: String,
        result: (UiState<TransactionWithPassengerAndDriver>) -> Unit
    ) {
        val transactionRef = firestore.collection(TRANSACTION_COLLECTION).document(transactionID)
        result.invoke(UiState.Loading)
        transactionRef.addSnapshotListener { value, error ->
            error?.let {
                result.invoke(UiState.Error(it.message.toString()))
                return@addSnapshotListener
            }
            value?.let { snapshot ->
                val transaction = snapshot.toObject(Transactions::class.java)
                if (transaction == null) {
                    result.invoke(UiState.Error("Transaction not found!"))
                    return@addSnapshotListener
                }

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val driverInfo = transaction.driverID?.let { driverID ->
                            firestore.collection(USER_COLLECTION)
                                .document(driverID)
                                .get()
                                .await()
                                .toObject(com.flysolo.etrikedriver.models.users.User::class.java)
                        }

                        val passengerInfo = transaction.passengerID?.let { passengerID ->
                            firestore.collection(USER_COLLECTION)
                                .document(passengerID)
                                .get()
                                .await()
                                .toObject(com.flysolo.etrikedriver.models.users.User::class.java)
                        }

                        val transactionWithDetails = TransactionWithPassengerAndDriver(
                            transactions = transaction,
                            passenger = passengerInfo,
                            driver = driverInfo
                        )

                        withContext(Dispatchers.Main) {
                            result(UiState.Success(transactionWithDetails))
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            result(UiState.Error(e.message.toString()))
                        }
                    }
                }
            }
        }
    }


    override suspend fun getAllTransactions(
        driverID: String,
        result: (UiState<List<Transactions>>) -> Unit
    ) {
        result.invoke(UiState.Loading)
        firestore.collection(TRANSACTION_COLLECTION)
            .whereEqualTo("driverID", driverID)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    result.invoke(UiState.Error(it.message.toString()))
                    Log.d(TRANSACTION_COLLECTION,it.message.toString(),error)
                }
                value?.let {
                    val transactions = value.toObjects(Transactions::class.java)
                    result.invoke(UiState.Success(transactions))
                    Log.d(TRANSACTION_COLLECTION,transactions.toString(),error)
                }
            }
    }



}