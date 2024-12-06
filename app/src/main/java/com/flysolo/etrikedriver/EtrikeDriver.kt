package com.flysolo.etrikedriver

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel



@HiltAndroidApp
class EtrikeDriver  : Application() {
    override fun onCreate() {
        super.onCreate()
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext,BuildConfig.MAPS_API_KEY)
        }
        val placesClient = Places.createClient(this)
    }
}