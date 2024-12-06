package com.flysolo.etrikedriver.services


import com.flysolo.etrikedriver.models.directions.GooglePlacesInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleDirectionsService {

    @GET("/maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin : String,
        @Query("destination") destination : String,
        @Query("key") key : String,
    ) : GooglePlacesInfo

    companion object {
        const val API = "https://maps.googleapis.com/"
    }
}


