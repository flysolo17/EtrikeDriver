package com.flysolo.etrikedriver.screens.main.bottom_nav.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.flysolo.etrikedriver.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository : AuthRepository
) : ViewModel() {
    var state by mutableStateOf(HomeState())
    fun events(e : HomeEvents) {
        when(e) {
            is HomeEvents.OnSetUser -> state = state.copy(user = e.user)
        }
    }
}