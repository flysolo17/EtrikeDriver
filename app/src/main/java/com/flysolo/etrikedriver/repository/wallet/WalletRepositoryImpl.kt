package com.flysolo.etrikedriver.repository.wallet

import android.util.Log
import com.flysolo.etrikedriver.models.wallet.CashOutRequest
import com.flysolo.etrikedriver.models.wallet.Wallet
import com.flysolo.etrikedriver.models.wallet.WalletActivity
import com.flysolo.etrikedriver.models.wallet.WalletHistory
import com.flysolo.etrikedriver.utils.UiState
import com.flysolo.etrikedriver.utils.generateRandomNumberString
import com.flysolo.etrikedriver.utils.generateRandomString
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FieldValue

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

const val WALLET_REPOSITORY = "wallets"
const val PAYOUT_REPOSITORY = "payouts"
const val WALLET_ACTIVITY_REPOSITORY= "activity"
const val WALLET_HISTORY_REPOSITORY = "wallet-history"
class WalletRepositoryImpl(
    private val firestore: FirebaseFirestore
): WalletRepository {
    override suspend fun createWallet(wallet: Wallet): Result<String> {
        return  try {
            firestore.collection(WALLET_REPOSITORY)
                .document(wallet.id ?: generateRandomString())
                .set(wallet)
                .await()
            Result.success("Wallet Created Successfully.")
        } catch (e  : Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyWallet(id: String, result: (UiState<Wallet?>) -> Unit) {
        result.invoke(UiState.Loading)
        firestore.collection(WALLET_REPOSITORY)
            .document(id)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    result(UiState.Error(error.localizedMessage?.toString() ?: error.message.toString()))
                    return@addSnapshotListener
                }

                value?.let { document ->
                    if (document.exists()) {
                        val wallet = document.toObject(Wallet::class.java)
                        result(UiState.Success(wallet))
                    } else {
                        result(UiState.Error("Wallet not found"))
                    }
                }
            }
    }

    override suspend fun getWalletHistory(
        id: String,
        result: (UiState<List<WalletHistory>>) -> Unit
    ) {
        result(UiState.Loading)
        firestore.collection(WALLET_HISTORY_REPOSITORY)
            .whereEqualTo("walletID",id)
            .orderBy("createdAt",Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    result(UiState.Error(it.localizedMessage.toString()))
                }
                value?.let {
                    val data = it.toObjects(WalletHistory::class.java)
                    result(UiState.Success(data))
                }
            }
    }

    override suspend fun getActivity(id: String, result: (UiState<List<WalletActivity>>) -> Unit) {
        result(UiState.Loading)
        firestore.collection(WALLET_ACTIVITY_REPOSITORY)
            .whereEqualTo("walletID",id)
            .orderBy("capturedTime",Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    Log.e(WALLET_ACTIVITY_REPOSITORY,it.localizedMessage,it)
                    result(UiState.Error(it.localizedMessage.toString()))
                }
                value?.let {
                    val data = it.toObjects(WalletActivity::class.java)
                    result(UiState.Success(data))
                }
            }
    }

    override suspend fun cashOut(cashOutRequest: CashOutRequest): Result<String> {
        return try {
            val id = generateRandomNumberString(7)
            cashOutRequest.id = id
            val batch = firestore.batch()
            batch.set(
                firestore.collection(PAYOUT_REPOSITORY).document(id),
                cashOutRequest
            )
            val amount = cashOutRequest.amount.value.toDoubleOrNull() ?: 0.00
            batch.set(
                firestore.collection(WALLET_ACTIVITY_REPOSITORY).document(id),
                WalletActivity(
                    id,
                    totalAmount = amount,
                    type =  "PAYOUT_REQUEST",
                    walletID =  cashOutRequest.walletID
                )
            )

            batch.update(
                firestore.collection(WALLET_REPOSITORY).document(cashOutRequest.walletID!!),
                "amount",
                FieldValue.increment(-amount)
            )

            batch.commit()
            Result.success("Payout Request Sent!")
        } catch (e: FirebaseException) {
            Result.failure(e)
        }
    }


}