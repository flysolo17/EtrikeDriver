package com.flysolo.etrikedriver.screens.view_trip

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.flysolo.etrikedriver.repository.transactions.TransactionRepository
import com.flysolo.etrikedriver.screens.view_trip.ViewTripEvents
import com.flysolo.etrikedriver.screens.view_trip.ViewTripState
import com.flysolo.etrikedriver.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ViewTripViewModel @Inject constructor(
    private val transactionRepository : TransactionRepository
) : ViewModel() {
    var state by mutableStateOf(ViewTripState())
    fun events(e : ViewTripEvents) {
        when(e) {
            is ViewTripEvents.OnViewTrip -> getTransaction(e.transactionID)

        }
    }




    private fun getTransaction(transactionID: String) {
        viewModelScope.launch {
            transactionRepository.viewTripInfo(transactionID) {
                state = when (it) {
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
                        passenger = it.data.passenger,
                        driver = it.data.driver,
                        transactions = it.data.transactions
                    )
                }

            }
        }
    }
}