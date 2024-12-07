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
    data object TRIPS : AppRouter(route = "trips")
    data object ACTIVITY : AppRouter(route = "activity")
    data object PROFILE : AppRouter(route = "profile")


    //others
    data object VIEWTRIP : AppRouter(route = "transaction/{id}") {
        fun navigate(id: String): String {
            return "transaction/$id"
        }
    }

    data object RIDE : AppRouter(route = "ride")

    data object CONVERSATION : AppRouter(route = "conversation/{id}") {
        fun navigate(id: String): String {
            return "conversation/$id"
        }
    }
}