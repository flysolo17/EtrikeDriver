package com.flysolo.etrikedriver.screens.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flysolo.etrike.screens.main.create_biometric.CreateBiometricViewModel
import com.flysolo.etrike.screens.main.security.SecuritySettingsViewModel
import com.flysolo.etrike.screens.main.view_trip.ViewTripScreen
import com.flysolo.etrikedriver.screens.main.bottom_nav.view_trip.ViewTripViewModel
import com.flysolo.etrikedriver.screens.main.bottom_nav.profile.ProfileViewModel
import com.flysolo.etrikedriver.config.AppRouter
import com.flysolo.etrikedriver.models.users.User
import com.flysolo.etrikedriver.screens.auth.change_password.ChangePasswordScreen
import com.flysolo.etrikedriver.screens.auth.change_password.ChangePasswordViewModel
import com.flysolo.etrikedriver.screens.auth.edit_profile.EditProfileEvents
import com.flysolo.etrikedriver.screens.auth.edit_profile.EditProfileScreen
import com.flysolo.etrikedriver.screens.auth.edit_profile.EditProfileViewModel
import com.flysolo.etrikedriver.screens.auth.phone.PhoneEvents
import com.flysolo.etrikedriver.screens.auth.phone.PhoneScreen
import com.flysolo.etrikedriver.screens.auth.phone.PhoneViewModel
import com.flysolo.etrikedriver.screens.cashout.CashOutScreen
import com.flysolo.etrikedriver.screens.cashout.CashOutViewModel
import com.flysolo.etrikedriver.screens.main.booking.BookingEvents
import com.flysolo.etrikedriver.screens.main.booking.BookingScreen
import com.flysolo.etrikedriver.screens.main.booking.BookingViewModel
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
import com.flysolo.etrikedriver.screens.main.create_biometric.CreateBiometricEvents
import com.flysolo.etrikedriver.screens.main.create_biometric.CreateBiometricsScreen
import com.flysolo.etrikedriver.screens.main.messages.MessagesScreen
import com.flysolo.etrikedriver.screens.main.messages.MessagesViewModel
import com.flysolo.etrikedriver.screens.main.queue.QueueEvents
import com.flysolo.etrikedriver.screens.main.queue.QueueScreen
import com.flysolo.etrikedriver.screens.main.queue.QueueViewModel
import com.flysolo.etrikedriver.screens.main.security.SecuritySettingsScreen
import com.flysolo.etrikedriver.screens.wallet.WalletScreen
import com.flysolo.etrikedriver.screens.wallet.WalletViewModel

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    mainNavHostController: NavHostController,
    navHostController: NavHostController,
    user: User?,
) {
    NavHost(navController = navHostController, startDestination = AppRouter.HOME.route) {
        composable(
            route = AppRouter.HOME.route
        ) {
            val viewmodel = hiltViewModel<HomeViewModel>()
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
            route = AppRouter.BOOKINGS.route
        ) {
            val viewmodel = hiltViewModel<BookingViewModel>()
            viewmodel.events(BookingEvents.OnSetUser(user))
            BookingScreen(
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
            ProfileScreen(
                state = viewModel.state,
                events = viewModel::events,
                mainNavHostController = mainNavHostController,
                navHostController = navHostController
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

        composable(
            route = AppRouter.CHANGE_PASSWORD.route
        ) {
            val viewModel = hiltViewModel<ChangePasswordViewModel>()
            ChangePasswordScreen(
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController,
            )
        }

        composable(
            route = AppRouter.EDIT_PROFILE.route
        ) {
            val viewModel = hiltViewModel<EditProfileViewModel>()
            viewModel.events(EditProfileEvents.OnSetUser(user))
            EditProfileScreen(
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController,
            )
        }



        composable(
            route = AppRouter.MESSAGES.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->

            val myID = backStackEntry.arguments?.getString("id") ?: ""
            val viewModel = hiltViewModel<MessagesViewModel>()
            MessagesScreen(
                id = myID,
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController
            )
        }


        composable(route = AppRouter.SECURITY_SETTINGS.route) {
            val  viewModel = hiltViewModel<SecuritySettingsViewModel>()
            SecuritySettingsScreen(
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController,
            )
        }

        composable(
            route = AppRouter.CREATE_PIN.route
        ) {
            val  viewModel = hiltViewModel<CreateBiometricViewModel>()
            viewModel.events(CreateBiometricEvents.OnSetUser(user))
            CreateBiometricsScreen(
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController,
            )
        }
        composable(route = AppRouter.PHONE.route) {
            val  viewModel = hiltViewModel<PhoneViewModel>()
            viewModel.events(PhoneEvents.OnSetUser(user))
            PhoneScreen(
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController,
            )
        }

        composable(
            route = AppRouter.WALLET.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->

            val myID = backStackEntry.arguments?.getString("id") ?: ""
            val viewModel = hiltViewModel<WalletViewModel>()
            WalletScreen(
                id = myID,
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController
            )
        }

        composable(
            route = AppRouter.CASHOUT.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->

            val myID = backStackEntry.arguments?.getString("id") ?: ""
            val viewModel = hiltViewModel<CashOutViewModel>()
            CashOutScreen(
                id = myID,
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController
            )
        }
    }
}