package com.flysolo.etrikedriver.screens.cashout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrikedriver.models.wallet.Amount
import com.flysolo.etrikedriver.models.wallet.CashOutRequest
import com.flysolo.etrikedriver.repository.wallet.WalletRepository
import com.flysolo.etrikedriver.screens.cashout.utils.isValidEmail
import com.flysolo.etrikedriver.screens.cashout.utils.isValidPayPalID
import com.flysolo.etrikedriver.screens.cashout.utils.isValidPhone
import com.flysolo.etrikedriver.screens.cashout.utils.validateAccount
import com.flysolo.etrikedriver.ui.theme.TextFieldData
import com.flysolo.etrikedriver.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CashOutViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {
    var state by mutableStateOf(CashOutState())

    fun events(e: CashOutEvents) {
        when (e) {
            CashOutEvents.OnConfirm -> confirm()
            is CashOutEvents.OnGetWallet -> getWallet(e.id)
            is CashOutEvents.OnAmountChange -> amountChange(e.amount)
            is CashOutEvents.OnPaypalAccountChange -> accountChange(e.receiver)
            is CashOutEvents.OnRecipientChange -> recipientChange(e.recipientType)
        }
    }

    private fun recipientChange(recipientType: RecipientType) {
        // Keep the current account value
        val currentAccount = state.receiver.value

        // Revalidate the account based on the new recipient type
        val updatedAccountState = validateAccount(currentAccount, recipientType)

        state = state.copy(
            recipientType = recipientType,
            receiver = updatedAccountState // Update the receiver with the revalidated state
        )
    }
    private fun accountChange(account: String) {
        val recipientType = state.recipientType
        val currentAccountState = validateAccount(account, recipientType)

        state = state.copy(receiver = currentAccountState)
    }

    private fun amountChange(amount: String) {
        val parsedAmount = amount.toDoubleOrNull()
        val max = state.wallet?.amount ?: 0.00 // Get the wallet balance

        val currentAmountState = when {
            parsedAmount == null -> TextFieldData(value = amount, hasError = true, errorMessage = "Invalid amount")
            parsedAmount < 500 -> TextFieldData(value = amount, hasError = true, errorMessage = "Minimum amount is 500")
            parsedAmount > max -> TextFieldData(value = amount, hasError = true, errorMessage = "Amount exceeds wallet balance")
            else -> TextFieldData(value = amount, hasError = false, errorMessage = null)
        }

        state = state.copy(amount = currentAmountState)
    }



    private fun confirm() {
        val cashOutRequest = CashOutRequest(
            amount = Amount(state.amount.value),
            receiver = state.receiver.value,
            recipientType = state.recipientType,
            walletID = state.wallet?.id
        )

        viewModelScope.launch {
            state = state.copy(confirm = ConfirmCashOut(isLoading = true))
            walletRepository.cashOut(cashOutRequest)
                .onSuccess { state = state.copy(confirm = ConfirmCashOut(isLoading = false, success = it)) }
                .onFailure { state = state.copy(confirm = ConfirmCashOut(isLoading = false, error = it.localizedMessage)) }
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
                        wallet = it.data,
                        receiver = state.receiver.copy(
                            value = it.data?.email ?: ""
                        )
                    )
                }
            }
        }
    }
}