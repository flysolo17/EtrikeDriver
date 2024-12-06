package com.flysolo.etrikedriver.repository.directions


import com.flysolo.etrikedriver.models.directions.GooglePlacesInfo
import com.flysolo.etrikedriver.repository.directions.DirectionsRepository
import com.flysolo.etrikedriver.services.GoogleDirectionsService


class DirectionsRepositoryImpl(
    private val googleDirectionsService: GoogleDirectionsService
): DirectionsRepository {
    override suspend fun getDirections(
        origin: String,
        destination: String
    ): Result<GooglePlacesInfo> {
        return try {
            val response = googleDirectionsService.getDirections(
                origin = origin,
                destination = destination,
                key = ""
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}