package com.flysolo.etrikedriver.screens.main.bottom_nav.profile.view_bookings

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrikedriver.models.transactions.TransactionStatus
import com.flysolo.etrikedriver.repository.transactions.TransactionRepository
import com.flysolo.etrikedriver.utils.UiState

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


@HiltViewModel
class ViewBookingViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    var state by mutableStateOf(ViewBookingState())

    fun events(e : ViewBookingEvents) {
        when(e) {
            is ViewBookingEvents.OnGetAllBookings -> getAllBookings(e.driverID)
            is ViewBookingEvents.OnSelectTab -> selectTab(e.tab,e.index)
        }
    }

    private fun selectTab(tab: TransactionStatus, index: Int) {
        val allTransactions = state.transactions
        val filteredTransactions = allTransactions.filter { it.status == tab }

        state = state.copy(
            filteredTransactions = filteredTransactions,
            selectedTab = index
        )
    }



    private fun getAllBookings(driverID: String) {
        viewModelScope.launch {
            transactionRepository.getAllTransactions(driverID) {
                state = when(it) {
                    is UiState.Error -> state.copy(
                        isLoading = false,
                        errors = it.message,

                    )
                    UiState.Loading -> state.copy(
                        isLoading = true,
                        errors = null
                    )
                    is UiState.Success -> state.copy(
                        errors = null,
                        isLoading = false,
                        transactions = it.data
                    )
                }
            }
        }
    }

}