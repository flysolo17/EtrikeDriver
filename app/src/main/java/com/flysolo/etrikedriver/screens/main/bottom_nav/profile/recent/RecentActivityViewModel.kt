package com.flysolo.etrikedriver.screens.main.bottom_nav.profile.recent

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrikedriver.repository.wallet.WalletRepository
import com.flysolo.etrikedriver.utils.UiState

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RecentActivityViewModel @Inject constructor(
    private  val walletRepository: WalletRepository
) : ViewModel() {
    var state by mutableStateOf(RecentActivityState())

    fun events(e : RecentActivityEvents) {
        when(e) {
            is RecentActivityEvents.OnGetActivities -> getActivities(e.walletID)
        }
    }

    private fun getActivities(walletID: String) {
        viewModelScope.launch {
            walletRepository.getActivity(walletID) {
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
                        activities = it.data
                    )
                }
            }
        }
    }
}