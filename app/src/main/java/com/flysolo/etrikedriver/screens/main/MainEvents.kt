package com.flysolo.etrikedriver.screens.main



sealed interface MainEvents {
    data object OnGetCurrentUser : MainEvents
    data class OnGetFranchises(val driverID : String) : MainEvents
}