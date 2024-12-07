package com.flysolo.etrikedriver.screens.main.conversation

import com.flysolo.etrikedriver.models.messages.Message
import com.flysolo.etrikedriver.models.users.User


data class ConversationState(
    val isLoading : Boolean = false,
    val messages : List<Message> = emptyList(),
    val passenger : User? = null,
    val user : User ?  = null,
    val errors : String ? = null,
    val message : String  = "",
    val isSendingMessage : Boolean = false
)