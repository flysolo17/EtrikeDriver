package com.flysolo.etrikedriver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flysolo.etrikedriver.screens.main.MainScreen
import com.flysolo.etrikedriver.screens.main.MainViewModel
import com.flysolo.etrikedriver.config.AppRouter
import com.flysolo.etrikedriver.screens.nav.authNavGraph
import com.flysolo.etrikedriver.ui.theme.EtrikeDriverTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EtrikeDriverTheme {
                val windowSize = calculateWindowSizeClass(activity = this)
                EtrikeApp(windowSize)
            }
        }
    }
}

@Composable
fun EtrikeApp(
    windowSizeClass: WindowSizeClass
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppRouter.AUTH.route) {
        authNavGraph(navController)
        composable(
            route = AppRouter.MAIN.route
        ) {
            val viewModel = hiltViewModel<MainViewModel>()
            MainScreen(
                state =viewModel.state,
                events = viewModel::events,
                mainNavHostController = navController
            )
        }

    }
}