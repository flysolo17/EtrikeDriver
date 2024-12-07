package com.flysolo.etrikedriver.screens.main.bottom_nav.trips

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrikedriver.repository.transactions.TransactionRepository
import com.flysolo.etrikedriver.utils.UiState

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TripViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    var state by mutableStateOf(TripState())

    fun events(e: TripEvents) {
        when (e) {
            is TripEvents.OnGetTrips -> getTrips(e.id)
            is TripEvents.OnSetUser -> {
                state = state.copy(
                    user = e.user
                )

            }
        }
    }

    private fun getTrips(id: String) {
        viewModelScope.launch {
            transactionRepository.getAllTransactions(id) {
                state = when(it) {
                    is UiState.Error -> state.copy(
                        isLoading = false,
                        errors = it.message
                    )
                    UiState.Loading -> state.copy(
                        isLoading = true,
                        errors = null
                    )
                    is UiState.Success -> state.copy(
                        isLoading = false,
                        errors = null,
                        trips = it.data
                    )
                }
            }
        }
    }
}
