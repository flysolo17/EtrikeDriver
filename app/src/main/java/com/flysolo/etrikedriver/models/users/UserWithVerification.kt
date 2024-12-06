package com.flysolo.etrikedriver.models.users

data class UserWithVerification(
    val user: User,
    val isVerified : Boolean,
)