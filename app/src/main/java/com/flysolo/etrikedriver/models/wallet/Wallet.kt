package com.flysolo.etrikedriver.models.wallet

import java.util.Date


data class Wallet(
    val id : String ? = null,
    val amount : Double = 0.00,
    val phone : String ? = null,
    val email : String ? = null,
    val name : String ? = null,
    val createdAt : Date = Date(),
    val updatedAt : Date = Date()
)