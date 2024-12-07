package com.flysolo.etrikedriver.screens.main.conversation

import com.flysolo.etrikedriver.models.users.User


sealed interface ConversationEvents {
    data class OnSetUser(val user: User?) : ConversationEvents
    data class OnGetPassenger(val passengerID: String) : ConversationEvents
    data class OnGetConversation(val driverID : String) : ConversationEvents
    data class OnSendMessage(val message : String) : ConversationEvents
    data class OnMessageChange(val message: String) : ConversationEvents
}