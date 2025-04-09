package com.flysolo.etrikedriver.screens.main.booking

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrikedriver.repository.franchise.FranchiseRepository
import com.flysolo.etrikedriver.repository.transactions.TransactionRepository
import com.flysolo.etrikedriver.screens.main.queue.QueueEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BookingViewModel @Inject constructor(
    private val transactionRepository : TransactionRepository,
    private val franchiseRepository: FranchiseRepository
) : ViewModel() {
    var state by mutableStateOf(BookingState())
    init {
        events(BookingEvents.OnGetPendingTransactions)
    }
    fun events(e : BookingEvents) {
        when(e) {
            is BookingEvents.AcceptTransaction -> acceptTransaction(e.transactionID,e.driverID,
                e.franchiseID)
            is BookingEvents.OnGetFranchise -> getFranchise(e.userID)
            BookingEvents.OnGetPendingTransactions -> getPendingTransactions()
            is BookingEvents.OnSelectTransction -> state = state.copy(
                selectedTransaction = e.transactionWithUser
            )
            is BookingEvents.OnSetCurrentLocation ->  state = state.copy(
                currentPosition = e.latLng
            )
            is BookingEvents.OnSetUser ->  state = state.copy(
                user = e.user
            )
        }
    }

    private fun acceptTransaction(transactionID: String, driverID: String, franchiseID: String) {
        viewModelScope.launch {
            state = state.copy(
                isAccepting = true
            )
            transactionRepository.acceptTransaction(
                transactionID,
                driverID, franchiseID
            ).onSuccess {
                state = state.copy(
                    isAccepting = false,
                    isAccepted = it
                )
                events(BookingEvents.OnSelectTransction(null))
            }.onFailure {
                state = state.copy(
                    isAccepting = false,
                    errors = it.message
                )
            }
        }
    }

    private fun getFranchise(userID: String) {
        viewModelScope.launch {
            franchiseRepository.getFranchises(userID).onSuccess {
                state = state.copy(
                    franchise = it[0]
                )
            }
        }
    }

    private fun getPendingTransactions() {
        viewModelScope.launch {
            Log.d("Booking","Getting bookings")
            state = state.copy(isLoading = true)
            delay(1000)

            transactionRepository.getAllPendingTransaction()  {
                it.onSuccess {
                    Log.d("Booking",it.toString())
                    val filteredTransactions = it.filter {e-> e.transactions.scheduleDate != null }
                    Log.d("Booking",filteredTransactions.toString())
                    state = state.copy(
                        isLoading = false,
                        transactionWithUser = filteredTransactions
                    )
                }.onFailure {
                    state = state.copy(
                        isLoading = false,
                        errors = it.message
                    )
                }
            }

        }
    }
}