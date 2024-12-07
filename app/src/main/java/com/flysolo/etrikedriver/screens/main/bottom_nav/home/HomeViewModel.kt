package com.flysolo.etrikedriver.screens.main.bottom_nav.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrikedriver.models.transactions.Transactions
import com.flysolo.etrikedriver.repository.auth.AuthRepository
import com.flysolo.etrikedriver.repository.transactions.TransactionRepository
import com.flysolo.etrikedriver.utils.UiState
import com.flysolo.etrikedriver.utils.getLatLngFromAddress
import com.flysolo.etrikedriver.utils.shortToast
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository : AuthRepository,
    private val transactionsRepository: TransactionRepository
) : ViewModel() {
    var state by mutableStateOf(HomeState())
    fun events(e : HomeEvents) {
        when(e) {
            is HomeEvents.OnSetUser -> {
                state = state.copy(user = e.user)
            }
            is HomeEvents.OnGetOngoingTrips -> getTransactions(driverID = e.driverID)
            is HomeEvents.OnPickup -> pickup(e.transactions,e.context)
        }
    }

    private fun pickup(transactions: Transactions, context: Context) {
        viewModelScope.launch {
            transactionsRepository.gotoPickupLocation(transactions.id!!).onSuccess {
                context.shortToast("Trip status updated")
                val pickup = transactions
                    .rideDetails
                    ?.routes
                    ?.firstOrNull()
                    ?.legs
                    ?.firstOrNull()
                    ?.start_address
                    ?.getLatLngFromAddress(context)
                    ?: LatLng(0.00,0.00)

                val gmmIntentUri = Uri.parse("google.navigation:q=${pickup.latitude},${pickup.longitude}.&mode=d")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                    setPackage("com.google.android.apps.maps")
                }
                context.startActivity(mapIntent)
            }
        }
    }

    private fun getTransactions(driverID : String) {
        viewModelScope.launch {
            transactionsRepository.getMyOnGoingTransactions(driverID) {
                state= when(it) {
                    is UiState.Error ->state.copy(
                        isGettingTransactions = false,
                        errors = it.message
                    )
                    UiState.Loading ->state.copy(
                        isGettingTransactions = true,
                        errors = null
                    )
                    is UiState.Success -> state.copy(
                        isGettingTransactions = false,
                        errors = null,
                        ongoingTrips = it.data
                    )
                }
            }
        }
    }
}