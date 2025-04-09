package com.flysolo.etrikedriver.screens.main.messages

import com.flysolo.etrikedriver.models.messages.UserWithMessage


data class MessagesState(
    val isLoading : Boolean = false,
    val userWithMessage: List<UserWithMessage> = emptyList(),
    val errors : String ? = null,
)