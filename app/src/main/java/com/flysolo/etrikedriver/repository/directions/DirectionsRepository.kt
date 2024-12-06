package com.flysolo.etrikedriver.repository.directions

import com.flysolo.etrikedriver.models.directions.GooglePlacesInfo


interface DirectionsRepository {

    suspend fun getDirections(
        origin : String,
        destination : String,
    ) : Result<GooglePlacesInfo>
}