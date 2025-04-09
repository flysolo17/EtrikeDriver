package com.flysolo.etrikedriver.screens.cashout



sealed interface CashOutEvents {
    data class OnGetWallet(
        val id : String
    ) : CashOutEvents

    data object OnConfirm : CashOutEvents

    data class OnAmountChange(
        val amount : String
    ) : CashOutEvents
    data class OnPaypalAccountChange(
        val receiver : String
    ) : CashOutEvents

    data class OnRecipientChange(
        val recipientType: RecipientType
    ) :CashOutEvents
}