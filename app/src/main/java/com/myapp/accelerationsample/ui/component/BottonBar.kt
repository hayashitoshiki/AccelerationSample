package com.myapp.accelerationsample.ui.component

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.myapp.accelerationsample.ui.Screens

/**
 * Bottom Bar
 *
 * @param navController ナビゲーションAPI
 */
@Composable
fun BottomBar(navController: NavHostController) {
    BottomNavigation {
        val tab = listOf(
            Screens.Group.HOME,
            Screens.Group.INFO,
            Screens.Group.SETTING
        )
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        tab.forEach { group ->
            BottomNavigationItem(
                icon = { Icon(group.imgRes, contentDescription = null) },
                label = { Text(text = stringResource(id = group.title)) },
                selected = Screens.findGroupByRoute(currentRoute) == group,
                alwaysShowLabel = true,
                onClick = {
                    if (Screens.findGroupByRoute(currentRoute) != group) {
                        navController.navigate(group.route)
                    }
                }
            )
        }
    }
}