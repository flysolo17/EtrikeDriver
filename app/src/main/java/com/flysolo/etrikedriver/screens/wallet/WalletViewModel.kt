package com.flysolo.etrikedriver.screens.wallet

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrikedriver.repository.wallet.WalletRepository

import com.flysolo.etrikedriver.screens.wallet.WalletEvents
import com.flysolo.etrikedriver.screens.wallet.WalletState
import com.flysolo.etrikedriver.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {
    var state by mutableStateOf(WalletState())
        private set

    fun events(e : WalletEvents) {
        when(e) {
            is WalletEvents.OnGetMyWallet -> getWallet(e.id)
            is WalletEvents.OnGetWalletHistory -> getHistory(e.id)
        }
    }

    private fun getHistory(id: String) {
        viewModelScope.launch {
            walletRepository.getActivity(id) {
                val current = state.activity
                state = when(it) {
                    is UiState.Error -> state.copy(
                        activity = current.copy(
                            isLoading = false,
                            errors = it.message
                        )
                    )
                    UiState.Loading -> state.copy(
                        activity = current.copy(
                            isLoading = true,
                            errors = null
                        )
                    )
                    is UiState.Success -> state.copy(
                        activity = current.copy(
                            isLoading = false,
                            errors = null,
                            data = it.data
                        )
                    )
                }
            }
        }
    }

    private fun getWallet(id: String) {
        viewModelScope.launch {
            walletRepository.getMyWallet(id) {
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
                        wallet = it.data
                    )
                }
            }
        }
    }
}