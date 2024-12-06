package com.flysolo.etrikedriver.models.franchise

import java.util.Date


data class Franchise(
    val id: String? = null,
    val driverID: String? = null,
    val franchiseNumber: String? = null,
    val driverLicense: String? = null,
    val licenseNumber: String? = null,
    val status: FranchiseStatus? = null,
    val expiration: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class FranchiseStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    PENDING
}