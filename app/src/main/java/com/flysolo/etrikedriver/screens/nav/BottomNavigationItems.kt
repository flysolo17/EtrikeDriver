package com.flysolo.etrikedriver.screens.nav

import androidx.annotation.DrawableRes
import com.flysolo.etrikedriver.R
import com.flysolo.etrikedriver.config.AppRouter


data class BottomNavigationItems(
    val label : String,
    @DrawableRes val selectedIcon : Int,
    @DrawableRes val unselectedIcon : Int,
    val hasNews : Boolean,
    val badgeCount : Int? = null,
    val route : String
) {
    companion object {
        val BOTTOM_NAV = listOf(
            BottomNavigationItems(
                label = "Home",
                selectedIcon = R.drawable.ic_home_filled,
                unselectedIcon = R.drawable.ic_home_outlined,
                hasNews = false,
                route = AppRouter.HOME.route
            ),
            BottomNavigationItems(
                label = "Activity",
                selectedIcon = R.drawable.ic_activities_filled,
                unselectedIcon = R.drawable.ic_activities_outline,
                hasNews = false,
                route = AppRouter.ACTIVITY.route
            ),
            BottomNavigationItems(
                label = "Profile",
                selectedIcon = R.drawable.ic_profile_filled,
                unselectedIcon = R.drawable.ic_person_outline,
                hasNews = false,
                route = AppRouter.PROFILE.route
            ),
        )
    }
}