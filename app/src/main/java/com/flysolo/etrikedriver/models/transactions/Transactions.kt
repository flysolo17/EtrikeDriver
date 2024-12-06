package com.flysolo.etrikedriver.models.transactions

import androidx.compose.ui.graphics.Color
import com.flysolo.etrikedriver.models.directions.GooglePlacesInfo

import java.util.Date


data class Transactions(
    var id : String ? = null,
    val passengerID : String ? = null,
    val driverID : String ? = null,
    val franchiseID : String ? = null,
    val status: TransactionStatus = TransactionStatus.PENDING,
    val rideDetails: GooglePlacesInfo? = null,
    val payment : Payment = Payment(),
    val note : String ?= null,
    val scheduleDate : Date ? = null,
    val createdAt : Date  = Date(),
    val updatedAt : Date = Date()
)


data class Payment(
    val id : String ? = null,
    val amount : Double = 0.00,
    val method : PaymentMethod? = null,
    val createdAt : Date = Date(),
    val updatedAt : Date  = Date(),
)

enum class PaymentMethod {
    WALLET,
    CASH
}

data class Location(
    val latitude : Double,
    val longitude  : Double
)


enum class TransactionStatus {
    PENDING,
    ACCEPTED,
    CONFIRMED,
    OTW,
    COMPLETED,
    CANCELLED,
    FAILED
}



fun TransactionStatus.toColor(): Color {
    return when (this) {
        TransactionStatus.PENDING -> Color(0xFFFFC107)  // Amber (bright yellow)
        TransactionStatus.CONFIRMED -> Color(0xFF4CAF50)  // Green (light and bright)
        TransactionStatus.COMPLETED -> Color(0xFF2196F3)  // Blue (bright and noticeable)
        TransactionStatus.CANCELLED -> Color(0xFFF44336)  // Red (vivid red)
        TransactionStatus.FAILED -> Color(0xFF9E9E9E)  // Grey (light grey for visibility)
        TransactionStatus.ACCEPTED -> Color(0xFF4CAF50)
        TransactionStatus.OTW -> Color(0xFFFFC107)
    }
}

