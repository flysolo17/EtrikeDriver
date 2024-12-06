package com.flysolo.etrikedriver.repository.franchise

import com.flysolo.etrikedriver.models.franchise.Franchise

interface FranchiseRepository {

    suspend fun getFranchises( driverID : String) : Result<List<Franchise>>
}