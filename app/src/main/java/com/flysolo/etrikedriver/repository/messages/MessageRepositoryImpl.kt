package com.flysolo.etrikedriver.repository.messages

import android.util.Log
import com.flysolo.etrikedriver.models.messages.Message
import com.flysolo.etrikedriver.models.messages.UserWithMessage
import com.flysolo.etrikedriver.models.users.USER_COLLECTION
import com.flysolo.etrikedriver.models.users.User
import com.flysolo.etrikedriver.utils.UiState

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


const val MESSAGE_COLLECTION = "messages"
class MessageRepositoryImpl(
    private val auth : FirebaseAuth,
    private val firestore: FirebaseFirestore
): MessageRepository {
    override suspend fun sendMessage(message: Message): Result<String> {
        return try {
            firestore.collection(MESSAGE_COLLECTION)
                .document(message.id!!)
                .set(message)
                .await()
            Result.success(message.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getAllMessages(): Result<List<Message>> {
        val uid = auth.currentUser?.uid ?: return Result.success(emptyList())
        val result = CompletableDeferred<Result<List<Message>>>()
        try {
            firestore.collection(MESSAGE_COLLECTION)
                .where(
                    Filter.or(
                        Filter.equalTo("senderID", uid),
                        Filter.equalTo("receiverID", uid)
                    )
                )
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        result.complete(Result.failure(Exception("Failed to get messages: ${error.message}")))
                        return@addSnapshotListener
                    }
                    if (snapshots != null) {
                        val messages = snapshots.toObjects(Message::class.java)
                        result.complete(Result.success(messages))
                    }
                }
            return result.await()
        } catch (e: Exception) {
            return Result.failure(Exception("Failed to get messages: ${e.message}"))
        }
    }

    override suspend fun getConversation(
        userID: String,
        otherID: String,
        result: (UiState<List<Message>>) -> Unit
    ) {
        result.invoke(UiState.Loading)
        firestore.collection(MESSAGE_COLLECTION)
            .where(
                Filter.or(
                    Filter.or(
                        Filter.equalTo("senderID", userID),
                        Filter.equalTo("receiverID", otherID)
                    ),
                    Filter.or(
                        Filter.equalTo("senderID", otherID),
                        Filter.equalTo("receiverID", userID)
                    )
                )
            )
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    result.invoke(UiState.Error("Failed to get conversation: ${error.message}"))
                    Log.d("message", error.message ?: "", error)
                }
                if (snapshots != null) {
                    val messages = snapshots.toObjects(Message::class.java)
                    Log.d("message", messages.toString())
                    result.invoke(UiState.Success(messages))
                }
            }
    }

    override suspend fun getUnSeenMessages(myID: String): Result<List<Message>> {
        return try {
            val result = firestore.collection(MESSAGE_COLLECTION)
                .whereEqualTo("receiverID", myID)
                .whereEqualTo("seen", false)
                .get()
                .await()
                .toObjects(Message::class.java)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get unseen messages: ${e.message}"))
        }
    }

    override suspend fun getUserWithMessages(
        myID: String,
        result: (UiState<List<UserWithMessage>>) -> Unit
    ) {
        result(UiState.Loading)
        callbackFlow {
            val listenerRegistration = firestore.collection(MESSAGE_COLLECTION)
                .where(
                    Filter.or(
                        Filter.equalTo("senderID", myID),
                        Filter.equalTo("receiverID", myID)
                    )
                ).addSnapshotListener { value, error ->
                    error?.let { exception ->
                        Log.e(MESSAGE_COLLECTION, "Error fetching messages: ${exception.localizedMessage}")
                        close(exception)
                        return@addSnapshotListener
                    }

                    value?.let { querySnapshot ->
                        val messages = querySnapshot.toObjects(Message::class.java)
                        Log.d(MESSAGE_COLLECTION, "Fetched ${messages.size} messages")
                        trySend(messages)
                    }
                }

            awaitClose {
                Log.d(MESSAGE_COLLECTION, "Listener registration removed")
                listenerRegistration.remove()
            }
        }.collect { messages ->
            val groupedMessages = messages.groupBy { message ->
                if (message.senderID == myID) message.receiverID else message.senderID
            }

            val userWithMessages = groupedMessages.mapNotNull { (otherId, messages) ->
                otherId?.let {
                    val user = firestore.collection(USER_COLLECTION)
                        .document(otherId)
                        .get()
                        .await()
                        .toObject(User::class.java)

                    user?.let {
                        UserWithMessage(user = user, messages = messages.sortedByDescending { it.createdAt })
                    }
                }
            }

            Log.d(MESSAGE_COLLECTION, "Processed ${userWithMessages.size} users with messages")
            result(UiState.Success(userWithMessages))
        }
    }

}

