package com.flysolo.etrikedriver.screens.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.flysolo.etrikedriver.screens.main.bottom_nav.profile.ProfileViewModel
import com.flysolo.etrikedriver.config.AppRouter
import com.flysolo.etrikedriver.models.users.User
import com.flysolo.etrikedriver.screens.main.bottom_nav.activity.ActivityScreen
import com.flysolo.etrikedriver.screens.main.bottom_nav.home.HomeEvents
import com.flysolo.etrikedriver.screens.main.bottom_nav.home.HomeScreen
import com.flysolo.etrikedriver.screens.main.bottom_nav.home.HomeViewModel
import com.flysolo.etrikedriver.screens.main.bottom_nav.profile.ProfileEvents
import com.flysolo.etrikedriver.screens.main.bottom_nav.profile.ProfileScreen
import com.flysolo.etrikedriver.screens.main.queue.QueueEvents
import com.flysolo.etrikedriver.screens.main.queue.QueueScreen
import com.flysolo.etrikedriver.screens.main.queue.QueueViewModel

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    user: User?,
) {
    NavHost(navController = navHostController, startDestination = AppRouter.HOME.route) {
        composable(
            route = AppRouter.HOME.route
        ) {
            val viewmodel = hiltViewModel<HomeViewModel>()
            viewmodel.events(HomeEvents.OnSetUser(user))
            HomeScreen(
                state = viewmodel.state,
                events = viewmodel::events,
                navHostController = navHostController
            )
        }

        composable(
            route = AppRouter.QUEUE.route
        ) {
            val viewmodel = hiltViewModel<QueueViewModel>()
            viewmodel.events(QueueEvents.OnSetUser(user))
            QueueScreen(
                state = viewmodel.state,
                events = viewmodel::events,
                navHostController = navHostController
            )
        }
        composable(
            route = AppRouter.ACTIVITY.route
        ) {
            ActivityScreen()
        }
        composable(
            route = AppRouter.PROFILE.route
        ) {
            val viewModel = hiltViewModel<ProfileViewModel>()
            viewModel.events(ProfileEvents.OnSetUser(user))
            ProfileScreen(
                state = viewModel.state,
                events = viewModel::events,
            )
        }

    }
}