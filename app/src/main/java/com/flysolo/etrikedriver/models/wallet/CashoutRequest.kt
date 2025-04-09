package com.flysolo.etrikedriver.models.wallet

import com.flysolo.etrikedriver.screens.cashout.RecipientType
import java.util.Date


data class CashOutRequest(
    var id : String ? = null,
    val walletID : String ? = null,
    val amount : Amount  = Amount(),
    val status : CashOutStatus =CashOutStatus.PENDING,
    val recipientType: RecipientType = RecipientType.EMAIL,
    val receiver : String ? = null,
    val createdAt : Date = Date(),
    val updatedAt : Date = Date()
)
data class Amount(
    val value : String = "0.00",
    val currency : String = "PHP"
)

enum class CashOutStatus {
    PENDING,       // Request is created but not yet processed
    PROCESSING,    // Request is being processed
    COMPLETED,     // Request has been successfully processed
    FAILED,        // Request was rejected or encountered an error
    CANCELLED      // Request was cancelled by the user or admin
}