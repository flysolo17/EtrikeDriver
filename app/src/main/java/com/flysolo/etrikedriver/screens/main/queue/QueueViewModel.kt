package com.flysolo.etrikedriver.screens.main.queue

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrikedriver.repository.franchise.FranchiseRepository
import com.flysolo.etrikedriver.repository.transactions.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class QueueViewModel @Inject constructor(
    private val transactionRepository : TransactionRepository,
    private val franchiseRepository: FranchiseRepository
) : ViewModel() {
    var state by mutableStateOf(QueueState())
    init {
        events(QueueEvents.OnGetPendingTransactions)
    }
    fun events(e : QueueEvents) {
        when(e) {
            QueueEvents.OnGetPendingTransactions -> getPendingTransactions()
            is QueueEvents.OnSetUser -> state = state.copy(
                user = e.user
            )

            is QueueEvents.OnSetCurrentLocation -> state = state.copy(
                currentPosition = e.latLng
            )

            is QueueEvents.AcceptTransaction -> acceptTransaction(
                e.transactionID,
                e.driverID,
                e.franchiseID
            )
            is QueueEvents.OnGetFranchise ->getFranchise(e.userID)
            is QueueEvents.OnSelectTransction -> state = state.copy(
                selectedTransaction = e.transactionWithUser
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
                events(QueueEvents.OnSelectTransction(null))
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
            state = state.copy(isLoading = true)
            transactionRepository.getAllPendingTransaction()  {
                it.onSuccess {
                    val filteredTransactions = it.filter {e-> e.transactions.scheduleDate == null }
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


private const val REQUEST_LOCATION_PERMISSION = 1