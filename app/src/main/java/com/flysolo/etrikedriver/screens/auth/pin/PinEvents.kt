package com.flysolo.etrikedriver.screens.auth.pin


sealed interface PinEvents {
    data object OnGetUser : PinEvents
    data class OnPinChange(val pin : String) : PinEvents
    data object OnDeletePin : PinEvents
    data object OnReset : PinEvents
    data object OnCheckPin : PinEvents
}