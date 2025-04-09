package com.flysolo.etrikedriver.screens.main.messages

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.flysolo.etrike.screens.main.messages.MessagesEvents
import com.flysolo.etrikedriver.repository.messages.MessageRepository
import com.flysolo.etrikedriver.utils.UiState

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val messagesRepository : MessageRepository
) : ViewModel() {
    var state by mutableStateOf(MessagesState())
    fun events(e : MessagesEvents)  {
        when(e) {
            is MessagesEvents.OnGetMyMessages ->getMessages(e.id)
        }
    }

    private fun getMessages(id: String) {
        viewModelScope.launch {
            messagesRepository.getUserWithMessages(id) {
                state = when(it) {
                    is UiState.Error -> state.copy(
                        isLoading = false,
                        errors = it.message
                    )
                    UiState.Loading -> state.copy(
                        isLoading = true,
                        errors = null
                    )
                    is UiState.Success ->state.copy(
                        isLoading = false,
                        errors = null,
                        userWithMessage = it.data
                    )
                }
            }
        }
    }
}