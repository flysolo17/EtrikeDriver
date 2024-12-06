package com.flysolo.etrikedriver.screens.auth.verification

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.flysolo.etrikedriver.repository.auth.AuthRepository
import com.flysolo.etrikedriver.utils.shortToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val authRepository : AuthRepository
) : ViewModel() {
    var state by mutableStateOf(VerificationState())
    private var timerJob: Job? = null
    init {
        events(VerificationEvents.OnListenToUserVerification)
        events(VerificationEvents.OnGetCurrentUser)
    }

    fun events(e : VerificationEvents) {
        when(e) {
            VerificationEvents.OnListenToUserVerification -> listenToUserVerification()
            is VerificationEvents.OnSendVerification -> sendVerificationEmail(e.context)
            VerificationEvents.OnGetCurrentUser -> getUsers()
        }
    }

    private fun getUsers() {
        viewModelScope.launch {
            authRepository.getCurrentUser().onSuccess {
                state = state.copy(users = it?.user , isVerified = it?.isVerified ?: false)
            }

        }
    }

    private fun sendVerificationEmail(context : Context) {
        viewModelScope.launch {

            state = state.copy(isLoading = true)
            authRepository.sendEmailVerification().onSuccess {
                context.shortToast(it)
                startTimer()
            }.onFailure {
                context.shortToast(it.localizedMessage.toString())
            }

        }
    }
    private fun listenToUserVerification() {
        viewModelScope.launch {
            while (true) {
                delay(3000) // Poll every 3 seconds
                val result = authRepository.listenToUserEmailVerification()
                if (result.isSuccess) {
                    val isVerified = result.getOrNull() ?: false
                    if (isVerified) {
                        state = state.copy(isVerified = true)
                        break // Stop the loop once the email is verified
                    }
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Error checking email verification."
                    state = state.copy(errors = errorMessage)
                }
            }
        }
    }


    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (i in 60 downTo 0) {
                delay(1000L)
                state = state.copy(timer = i)
            }
        }
    }


}