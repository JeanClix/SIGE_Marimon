package org.marimon.sigc

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.marimon.sigc.ui.screens.recuperacion.RecuperarPasswordScreen
import org.marimon.sigc.ui.screens.recuperacion.IngresarCodigoScreen
import org.marimon.sigc.ui.screens.recuperacion.CambiarPasswordScreen
import org.marimon.sigc.viewmodel.RecuperacionPasswordViewModel

object Routes {
    const val HOME = "home"
    const val EMPLOYEES = "user"
    const val PRODUCTS = "circulo"
    const val KPI = "grafico"
    const val VENTAS = "ventas"
    const val RECUPERAR_PASSWORD = "recuperar_password"
    const val INGRESAR_CODIGO = "ingresar_codigo"
    const val CAMBIAR_PASSWORD = "cambiar_password"
}

@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController(),
    recuperacionViewModel: RecuperacionPasswordViewModel = RecuperacionPasswordViewModel()
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

        composable(Routes.VENTAS) {
            DashboardVentasScreen(navController = navController)
        }

        // Pantalla 1: Recuperar Contraseña
        composable(Routes.RECUPERAR_PASSWORD) {
            RecuperarPasswordScreen(
                viewModel = recuperacionViewModel,
                onNavigateToVerificar = {
                    navController.navigate(Routes.INGRESAR_CODIGO)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla 2: Ingresar Código
        composable(Routes.INGRESAR_CODIGO) {
            IngresarCodigoScreen(
                viewModel = recuperacionViewModel,
                onNavigateToCambiarPassword = {
                    navController.navigate(Routes.CAMBIAR_PASSWORD)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla 3: Cambiar Contraseña
        composable(Routes.CAMBIAR_PASSWORD) {
            CambiarPasswordScreen(
                viewModel = recuperacionViewModel,
                onPasswordCambiado = {
                    // Navegar de vuelta al login y limpiar todo el stack
                    navController.popBackStack(Routes.HOME, inclusive = false)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
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

@Composable
fun DashboardVentasScreen(navController: NavHostController) {
    DashboardVentasApp(
        currentRoute = Routes.VENTAS,
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
