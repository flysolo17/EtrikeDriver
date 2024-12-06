package com.flysolo.etrikedriver.config

sealed class AppRouter(
    val route : String
)  {
    data object AUTH : AppRouter(route = "auth")
    data object LOGIN : AppRouter(route = "login")
    data object FORGOT_PASSWORD : AppRouter(route = "forgot-password")
    data object VERIFICATION : AppRouter(route = "verification")
    data object MAIN : AppRouter(route = "main")


    //BOTTOM NAV
    data object HOME : AppRouter(route = "home")
    data object QUEUE : AppRouter(route = "queue")
    data object ACTIVITY : AppRouter(route = "activity")
    data object PROFILE : AppRouter(route = "profile")
}