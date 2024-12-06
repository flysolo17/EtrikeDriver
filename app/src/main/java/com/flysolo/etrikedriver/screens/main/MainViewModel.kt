package com.flysolo.etrikedriver.screens.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.flysolo.etrikedriver.repository.auth.AuthRepository
import com.flysolo.etrikedriver.repository.franchise.FranchiseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository : AuthRepository,
    private val franchiseRepository: FranchiseRepository
) : ViewModel() {
    var state by mutableStateOf(MainState())
    init {
        getUser()
    }
    fun events(e : MainEvents) {
        when(e) {
            MainEvents.OnGetCurrentUser -> getUser()
            is MainEvents.OnGetFranchises -> getFranchises(e.driverID)
        }
    }

    private fun getFranchises(driverID: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            franchiseRepository.getFranchises(driverID).onSuccess {
                state = state.copy(
                    isLoading = false,
                    errors = null,
                    franchises = it
                )
            }.onFailure {
                state = state.copy(
                    isLoading = false,
                    errors = it.localizedMessage?.toString(),
                )
            }
        }
    }

    private fun getUser() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            authRepository.getCurrentUser().onSuccess {
                state = state.copy(
                    isLoading = false,
                    errors = null,
                    user = it?.user
                )
                it?.user?.id?.let {id ->
                    events(MainEvents.OnGetFranchises(id))
                }
            }.onFailure {
                state = state.copy(
                    isLoading = false,
                    errors = it.localizedMessage?.toString(),
                )
            }
        }
    }
}