package com.flysolo.etrikedriver.screens.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flysolo.etrike.screens.main.view_trip.ViewTripScreen
import com.flysolo.etrikedriver.screens.main.bottom_nav.view_trip.ViewTripViewModel
import com.flysolo.etrikedriver.screens.main.bottom_nav.profile.ProfileViewModel
import com.flysolo.etrikedriver.config.AppRouter
import com.flysolo.etrikedriver.models.users.User
import com.flysolo.etrikedriver.screens.main.bottom_nav.activity.ActivityScreen
import com.flysolo.etrikedriver.screens.main.bottom_nav.home.HomeEvents
import com.flysolo.etrikedriver.screens.main.bottom_nav.home.HomeScreen
import com.flysolo.etrikedriver.screens.main.bottom_nav.home.HomeViewModel
import com.flysolo.etrikedriver.screens.main.bottom_nav.profile.ProfileEvents
import com.flysolo.etrikedriver.screens.main.bottom_nav.profile.ProfileScreen
import com.flysolo.etrikedriver.screens.main.bottom_nav.trips.TripEvents
import com.flysolo.etrikedriver.screens.main.bottom_nav.trips.TripScreen
import com.flysolo.etrikedriver.screens.main.bottom_nav.trips.TripViewModel
import com.flysolo.etrikedriver.screens.main.conversation.ConversationEvents
import com.flysolo.etrikedriver.screens.main.conversation.ConversationScreen
import com.flysolo.etrikedriver.screens.main.conversation.ConversationViewModel
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
            route = AppRouter.TRIPS.route
        ) {
            val viewmodel = hiltViewModel<TripViewModel>()
            viewmodel.events(TripEvents.OnSetUser(user))
            TripScreen(
                state = viewmodel.state,
                events = viewmodel::events,
                navHostController = navHostController
            )
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


        composable(
            route = AppRouter.VIEWTRIP.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionID = backStackEntry.arguments?.getString("id") ?: ""

            val viewModel = hiltViewModel<ViewTripViewModel>()
            ViewTripScreen(
                transactionID = transactionID,
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController
            )
        }


        composable(
            route = AppRouter.CONVERSATION.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val driverID = backStackEntry.arguments?.getString("id") ?: ""
            val viewModel = hiltViewModel<ConversationViewModel>()
            viewModel.events(ConversationEvents.OnSetUser(user))
            ConversationScreen(
                passengerID = driverID,
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController
            )
        }
    }



}