package com.flysolo.etrikedriver.models.wallet


import com.flysolo.etrikedriver.utils.generateRandomString
import java.util.Date


data class WalletHistory(
    val id : String = generateRandomString(6),
    val walletID : String ? = null,
    val message : String ? = null,
    val amount : Double  = 0.00,
    val type : HistoryType = HistoryType.CREATED,
    val createdAt : Date = Date(),
)

enum class HistoryType {
    IN,
    OUT,
    CREATED,
}