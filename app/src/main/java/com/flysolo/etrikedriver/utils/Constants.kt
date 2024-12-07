package com.flysolo.etrikedriver.utils

import java.util.Locale.*

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.round


import kotlin.math.round

fun calculateDistanceInKm(
    from: LatLng,
    to: LatLng
): Float {
    val fromLocation = Location("").apply {
        latitude = from.latitude
        longitude = from.longitude
    }

    val toLocation = Location("").apply {
        latitude = to.latitude
        longitude = to.longitude
    }

    val distanceInKm = fromLocation.distanceTo(toLocation) / 1000
    return round(distanceInKm * 100) / 100 // Round to 2 decimal places
}


fun Context.shortToast(message : String) {
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}

fun generateRandomString(length: Int = 15): String {
    val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    var result = ""

    for (i in 0 until length) {
        val randomIndex = (Math.random() * characters.length).toInt()
        result += characters[randomIndex]
    }

    return result
}


fun generateRandomNumberString(length: Int = 15): String {
    val characters = "0123456789"
    var result = ""

    for (i in 0 until length) {
        val randomIndex = (Math.random() * characters.length).toInt()
        result += characters[randomIndex]
    }

    return result
}

fun LatLng.getAddressFromLatLng(context: Context): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        val addresses: List<Address>? = geocoder.getFromLocation(this.latitude, this.longitude, 1)
        if (addresses?.isNotEmpty() == true) {
            val address: Address = addresses[0]
            // Return a formatted address or a specific field like address.getAddressLine(0)
            address.getAddressLine(0) ?: "Address not found"
        } else {
            "No address found"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "Error fetching address"
    }
}

fun String.getLatLngFromAddress(context: Context): LatLng? {
    if (this.isEmpty()) return null
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val locations = geocoder.getFromLocationName(this, 1)
        if (locations.isNullOrEmpty()) null
        else LatLng(locations[0].latitude, locations[0].longitude)
    } catch (e: Exception) {
        null
    }
}

fun Double.toPhp(): String {
    return "₱ %.2f".format(this)
}



fun Date.display(): String {
    val formatter = SimpleDateFormat("MMM dd, hh:mm aa", Locale.getDefault())
    return formatter.format(this)
}