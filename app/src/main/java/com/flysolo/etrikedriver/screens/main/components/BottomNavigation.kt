package com.flysolo.etrikedriver.screens.main.components

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.flysolo.etrikedriver.screens.nav.BottomNavigationItems


@Composable
fun BottomNavigation(
    modifier: Modifier = Modifier,
    items: List<BottomNavigationItems>,
    navBackStackEntry : NavBackStackEntry?,
    navHostController: NavHostController
) {

    val currentRoute = navBackStackEntry?.destination
    val bottomBarDestination = items.any { it.route == currentRoute?.route }
    BottomAppBar(containerColor = Color.Transparent) {
        items.forEachIndexed { index, destinations ->
            val isSelected = (currentRoute?.hierarchy?.any {
                it.route == destinations.route
            } == true).also {
                NavigationBarItem(
                    label = { Text(text = destinations.label)},
                    selected = it,
                    onClick = {
                        navHostController.navigate(destinations.route) {
                            popUpTo(navHostController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }, icon = {
                        BadgedBox(badge = {
                            if (destinations.badgeCount != null) {
                                Badge {
                                    Text(text = destinations.badgeCount.toString())
                                }
                            } else if (destinations.hasNews) {
                                Badge()
                            }
                        }) {
                            if (it) {
                                Icon(painter = painterResource(id = destinations.selectedIcon), contentDescription = destinations.route)
                            } else {
                                Icon(painter = painterResource(id = destinations.unselectedIcon), contentDescription = destinations.route)
                            }
                        }
                    })
            }
        }
    }
}