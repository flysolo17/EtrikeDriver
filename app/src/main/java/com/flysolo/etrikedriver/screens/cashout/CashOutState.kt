package com.flysolo.etrikedriver.screens.cashout

import com.flysolo.etrikedriver.models.wallet.Wallet
import com.flysolo.etrikedriver.ui.theme.TextFieldData


data class CashOutState(
    val isLoading : Boolean = false,
    val recipientType : RecipientType = RecipientType.EMAIL,
    val amount : TextFieldData = TextFieldData(),
    val receiver : TextFieldData = TextFieldData(),
    val wallet : Wallet ? = null,
    val errors : String ? = null,
    val confirm : ConfirmCashOut = ConfirmCashOut()
)


data class ConfirmCashOut(
    val isLoading: Boolean = false,
    val success : String ? = null,
    val error : String ? = null
)


enum class RecipientType {
    PHONE,
    EMAIL,
    PAYPAL_ID
}