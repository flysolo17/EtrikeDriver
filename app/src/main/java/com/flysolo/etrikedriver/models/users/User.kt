package com.flysolo.etrikedriver.models.users

import java.util.Date

const val USER_COLLECTION  = "users"
data class User(
    var id: String ? = null,
    val name: String? = null,
    val phone : String? = null,
    val email: String? = null,
    val profile: String? = null,
    val active : Boolean = false,
    val location: Location? = null,
    val type : UserType ? = UserType.PASSENGER,
    val createdAt : Date = Date()
)
data class Location(
    val latitude : Long,
    val longitude  : Long
)
enum class UserType {
    PASSENGER, DRIVER
}
