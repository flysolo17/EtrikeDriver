package com.flysolo.etrikedriver.repository.messages

import com.flysolo.etrikedriver.models.messages.Message
import com.flysolo.etrikedriver.models.messages.UserWithMessage
import com.flysolo.etrikedriver.utils.UiState


interface MessageRepository {
    suspend fun sendMessage(
        message: Message
    ) : Result<String>

    suspend fun getAllMessages() : Result<List<Message>>

    suspend fun getConversation(userID : String, otherID : String,result : (UiState<List<Message>>) -> Unit)

    suspend fun getUnSeenMessages(
        myID : String,
    ) : Result<List<Message>>



    suspend fun getUserWithMessages(
        myID: String,
        result: (UiState<List<UserWithMessage>>) -> Unit
    )
}