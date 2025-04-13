package com.flysolo.etrikedriver.config

sealed class AppRouter(
    val route : String
)  {


    data object AUTH : AppRouter(route = "auth")
    data object LOGIN : AppRouter(route = "login")
    data object FORGOT_PASSWORD : AppRouter(route = "forgot-password")
    data object VERIFICATION : AppRouter(route = "verification")
    data object MAIN : AppRouter(route = "main")

    data object PIN : AppRouter(route = "pin")
    data object PHONE : AppRouter(route = "phone")

    //BOTTOM NAV
    data object HOME : AppRouter(route = "home")
    data object QUEUE : AppRouter(route = "queue")
    data object BOOKINGS : AppRouter(route = "bookings")
    data object TRIPS : AppRouter(route = "trips")
    data object ACTIVITY : AppRouter(route = "activity")
    data object PROFILE : AppRouter(route = "profile")
    data object CHANGE_PASSWORD : AppRouter(route = "change-password")
    data object EDIT_PROFILE : AppRouter(route = "edit-profile")


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

    data object MESSAGES : AppRouter(route = "messages/{id}") {
        fun navigate(id: String): String {
            return "messages/$id"
        }
    }

    data object SECURITY_SETTINGS : AppRouter(route = "security-settings")
    data object CREATE_PIN : AppRouter(route = "create-pin")


    data object WALLET : AppRouter(route = "wallet/{id}") {
        fun navigate(id: String): String {
            return "wallet/$id"
        }
    }

    data object CASHOUT : AppRouter(route = "cashout/{id}") {
        fun navigate(id: String): String {
            return "cashout/$id"
        }
    }


    data object VIEW_BOOKINGS : AppRouter(route = "view-bookings/{uid}") {
        fun navigate(uid: String): String {
            return "view-bookings/$uid"
        }
    }

    data object RECENT_ACTIVITIES : AppRouter(route = "recent-activities/{uid}") {
        fun navigate(uid: String): String {
            return "recent-activities/$uid"
        }
    }
}