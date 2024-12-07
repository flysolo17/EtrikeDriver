package com.flysolo.etrikedriver.screens.main.conversation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrikedriver.models.messages.Message
import com.flysolo.etrikedriver.repository.auth.AuthRepository
import com.flysolo.etrikedriver.repository.messages.MessageRepository

import com.flysolo.etrikedriver.screens.main.conversation.ConversationEvents
import com.flysolo.etrikedriver.screens.main.conversation.ConversationState
import com.flysolo.etrikedriver.utils.UiState
import com.flysolo.etrikedriver.utils.generateRandomNumberString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

import javax.inject.Inject


@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val messageRepository : MessageRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    var state by mutableStateOf(ConversationState())
    fun events(e : ConversationEvents) {
        when(e) {
            is ConversationEvents.OnGetConversation -> getConvo(e.driverID)
            is ConversationEvents.OnSetUser -> state = state.copy(user = e.user)

            is ConversationEvents.OnSendMessage -> sendMessage(e.message)
            is ConversationEvents.OnMessageChange -> state = state.copy(message = e.message)
            is ConversationEvents.OnGetPassenger -> getPassengerInfo(e.passengerID)
        }
    }

    private fun getPassengerInfo(passengerID: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            authRepository.getUser(passengerID).onSuccess {
                state = state.copy(
                    isLoading = false,
                    passenger = it
                )
                events(ConversationEvents.OnGetConversation(passengerID))
            }.onFailure {
                state = state.copy(
                    isLoading = false,
                    errors = it.localizedMessage
                )
            }
        }
    }

    private fun sendMessage(message: String) {
        viewModelScope.launch {
            val messages = Message(
                id = generateRandomNumberString(),
                senderID = state.user?.id,
                receiverID = state.passenger?.id,
                message = message,
            )
            state = state.copy(
                isSendingMessage = true
            )
            messageRepository.sendMessage(message = messages).onSuccess {
                state = state.copy(
                    isSendingMessage = false,
                    message = ""
                )
            }.onFailure {
                state = state.copy(
                    isSendingMessage = false,
                    message = ""
                )
            }


        }
    }


    private fun getConvo(passengerID: String) {
        if (state.user?.id == null && state.passenger?.id == null) {
            return
        }
        viewModelScope.launch {
            messageRepository.getConversation(state.user?.id!!,state.passenger?.id!!) {
             state  = when(it) {
                    is UiState.Error -> state.copy(
                        isLoading = false,
                        errors = it.message
                    )
                    UiState.Loading -> state.copy(
                        isLoading = true
                    )
                    is UiState.Success -> {

                        state.copy(
                            isLoading = false,
                            messages = it.data
                        )
                    }
                }
            }
        }
    }
}