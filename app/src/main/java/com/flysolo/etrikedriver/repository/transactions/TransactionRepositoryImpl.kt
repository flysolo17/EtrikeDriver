package com.flysolo.etrikedriver.repository.transactions

import com.flysolo.etrikedriver.models.transactions.TransactionStatus
import com.flysolo.etrikedriver.models.transactions.TransactionWithUser
import com.flysolo.etrikedriver.models.transactions.Transactions
import com.flysolo.etrikedriver.models.users.USER_COLLECTION
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
}