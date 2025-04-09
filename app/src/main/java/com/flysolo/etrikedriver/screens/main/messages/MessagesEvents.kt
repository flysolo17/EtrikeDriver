package com.flysolo.etrike.screens.main.messages



sealed interface MessagesEvents {
    data class OnGetMyMessages(
        val id : String
    ) : MessagesEvents
}