package org.marimon.sigc

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Routes {
    const val HOME = "home"
    const val EMPLOYEES = "user"
    const val PRODUCTS = "circulo"
    const val KPI = "grafico"
}

@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            AdminMainScreen(navController = navController)
        }

        composable(Routes.EMPLOYEES) {
            AdminREmpleadoScreen(navController = navController)
        }

        composable(Routes.PRODUCTS) {
            AdminRProductoScreen(navController = navController)
        }

        composable(Routes.KPI) {
            AdminKPIScreen(navController = navController)
        }
    }
}

@Composable
fun AdminMainScreen(navController: NavHostController) {
    AppAndroid(
        currentRoute = Routes.HOME,
        onNavigate = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}

@Composable
fun AdminREmpleadoScreen(navController: NavHostController) {
    _root_ide_package_.org.marimon.sigc.Empleado.AdminREmpleadoApp(
        currentRoute = Routes.EMPLOYEES,
        onNavigate = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}

@Composable
fun AdminRProductoScreen(navController: NavHostController) {
    AdminRProductoApp(
        currentRoute = Routes.PRODUCTS,
        onNavigate = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}

@Composable
fun AdminKPIScreen(navController: NavHostController) {
    AdminKPIApp(
        currentRoute = Routes.KPI,
        onNavigate = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}
