package com.flysolo.etrikedriver.models.users

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

const val USER_COLLECTION  = "users"

@Parcelize
data class User(
    var id: String ? = null,
    val name: String? = null,
    val phone : String? = null,
    val email: String? = null,
    val profile: String? = null,
    val active : Boolean = false,
    val type : UserType ? = UserType.PASSENGER,
    val location : LocationSettings ? = LocationSettings(),
    val pin : Pin ? = Pin(),
    val createdAt : Date = Date()
) : Parcelable



@Parcelize
data class LocationSettings(
    val latitude : Double ? = null,
    val longitude : Double ? = null,
    val lastUpdated : Date = Date(),
    val enableTracking : Boolean = true,
) : Parcelable

@Parcelize
data class Pin(
    val pin : String ?  = null,
    val biometricEnabled : Boolean = false
) : Parcelable

enum class UserType {
    PASSENGER,DRIVER
}
