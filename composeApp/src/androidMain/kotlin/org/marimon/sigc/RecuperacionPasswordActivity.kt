package org.marimon.sigc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.marimon.sigc.ui.screens.recuperacion.RecuperarPasswordScreen
import org.marimon.sigc.ui.screens.recuperacion.IngresarCodigoScreen
import org.marimon.sigc.ui.screens.recuperacion.CambiarPasswordScreen
import org.marimon.sigc.viewmodel.RecuperacionPasswordViewModel

/**
 * Actividad dedicada al flujo de recuperación de contraseña
 * Maneja las 3 pantallas: Solicitar código, Verificar código, Cambiar contraseña
 */
class RecuperacionPasswordActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val viewModel = remember { RecuperacionPasswordViewModel() }

                NavHost(
                    navController = navController,
                    startDestination = "recuperar_password"
                ) {
                    // Pantalla 1: Recuperar Contraseña
                    composable("recuperar_password") {
                        RecuperarPasswordScreen(
                            viewModel = viewModel,
                            onNavigateToVerificar = {
                                navController.navigate("ingresar_codigo")
                            },
                            onNavigateBack = {
                                finish() // Volver al login
                            }
                        )
                    }

                    // Pantalla 2: Ingresar Código
                    composable("ingresar_codigo") {
                        IngresarCodigoScreen(
                            viewModel = viewModel,
                            onNavigateToCambiarPassword = {
                                navController.navigate("cambiar_password")
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // Pantalla 3: Cambiar Contraseña
                    composable("cambiar_password") {
                        CambiarPasswordScreen(
                            viewModel = viewModel,
                            onPasswordCambiado = {
                                // Contraseña cambiada exitosamente
                                // Volver al login
                                finish()
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
