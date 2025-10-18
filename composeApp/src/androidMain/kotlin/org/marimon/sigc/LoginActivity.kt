package org.marimon.sigc

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import org.marimon.sigc.ui.screens.LoginScreen
import org.marimon.sigc.ui.screens.EmpleadoScreen
import org.marimon.sigc.ui.screens.empleado.AutopartesScreen
import org.marimon.sigc.ui.screens.empleado.EySAutopartesScreen
import org.marimon.sigc.ui.screens.empleado.productos.EntradaProductosScreen
import org.marimon.sigc.ui.screens.empleado.productos.SalidaProductosScreen
import org.marimon.sigc.ui.screens.empleado.TransaccionScreen
import org.marimon.sigc.services.PDFService
import org.marimon.sigc.services.PDFServiceManager
import org.marimon.sigc.viewmodel.AuthViewModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar el servicio de PDF
        PDFServiceManager.initialize(PDFService(this))
        
        setContent {
            MaterialTheme {
                val authViewModel = remember { AuthViewModel() }
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                val userType by authViewModel.userType.collectAsState()
                val currentEmpleado by authViewModel.currentEmpleado.collectAsState()

                // Estado para controlar la navegación dentro del empleado
                var currentScreen by remember { mutableStateOf("empleado") }

                // Log del estado actual
                LaunchedEffect(currentScreen) {
                    println("DEBUG: currentScreen cambió a: $currentScreen")
                }

                when {
                    !isLoggedIn -> {
                        LoginScreen(
                            authViewModel = authViewModel,
                            onLoginSuccess = {
                                // La navegación se maneja automáticamente por el estado
                            }
                        )
                    }
                    userType == "admin" -> {
                        // Navegar a MainActivity para administradores
                        LaunchedEffect(Unit) {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    userType == "empleado" && currentEmpleado != null -> {
                        // Navegación para empleados
                        when (currentScreen) {
                            "empleado" -> {
                                println("DEBUG: Mostrando EmpleadoScreen")
                                EmpleadoScreen(
                                    empleado = currentEmpleado!!,
                                    authViewModel = authViewModel,
                                    onLogout = {
                                        // El logout ya se maneja en el ViewModel
                                        // La UI se actualizará automáticamente
                                    },
                                    onNavigateToAutopartes = {
                                        println("DEBUG: onNavigateToAutopartes callback ejecutado")
                                        currentScreen = "autopartes"
                                        println("DEBUG: currentScreen establecido a: autopartes")
                                    },
                                    onNavigateToEySAutopartes = {
                                        println("DEBUG: onNavigateToEySAutopartes callback ejecutado")
                                        currentScreen = "eys_autopartes"
                                        println("DEBUG: currentScreen establecido a: eys_autopartes")
                                    },
                                    onNavigateToTransaccion = {
                                        println("DEBUG: onNavigateToTransaccion callback ejecutado")
                                        currentScreen = "transaccion"
                                        println("DEBUG: currentScreen establecido a: transaccion")
                                    }
                                )
                            }
                            "autopartes" -> {
                                println("DEBUG: Mostrando AutopartesScreen")
                                AutopartesScreen(
                                    empleado = currentEmpleado!!,
                                    authViewModel = authViewModel,
                                    onBack = {
                                        println("DEBUG: onBack callback ejecutado")
                                        currentScreen = "empleado"
                                        println("DEBUG: currentScreen establecido a: empleado")
                                    }
                                )
                            }
                            "eys_autopartes" -> {
                                println("DEBUG: Mostrando EySAutopartesScreen")
                                EySAutopartesScreen(
                                    empleado = currentEmpleado!!,
                                    onNavigateBack = {
                                        println("DEBUG: onNavigateBack (EyS) callback ejecutado")
                                        currentScreen = "empleado"
                                        println("DEBUG: currentScreen establecido a: empleado")
                                    },
                                    onNavigateToEntrada = {
                                        println("DEBUG: Navegando a entrada de productos")
                                        currentScreen = "entrada_productos"
                                    },
                                    onNavigateToSalida = {
                                        println("DEBUG: Navegando a salida de productos")
                                        currentScreen = "salida_productos"
                                    }
                                )
                            }
                            "entrada_productos" -> {
                                println("DEBUG: Mostrando EntradaProductosScreen")
                                EntradaProductosScreen(
                                    empleado = currentEmpleado!!,
                                    onNavigateBack = {
                                        println("DEBUG: onNavigateBack (Entrada) callback ejecutado")
                                        currentScreen = "eys_autopartes"
                                        println("DEBUG: currentScreen establecido a: eys_autopartes")
                                    },
                                    onNavigateToAutopartes = {
                                        println("DEBUG: Navegando a autopartes desde entrada")
                                        currentScreen = "autopartes"
                                    }
                                )
                            }
                            "salida_productos" -> {
                                println("DEBUG: Mostrando SalidaProductosScreen")
                                SalidaProductosScreen(
                                    empleado = currentEmpleado!!,
                                    onNavigateBack = {
                                        println("DEBUG: onNavigateBack (Salida) callback ejecutado")
                                        currentScreen = "eys_autopartes"
                                        println("DEBUG: currentScreen establecido a: eys_autopartes")
                                    },
                                    onNavigateToAutopartes = {
                                        println("DEBUG: Navegando a autopartes desde salida")
                                        currentScreen = "autopartes"
                                    }
                                )
                            }
                            "transaccion" -> {
                                println("DEBUG: Mostrando TransaccionScreen")
                                TransaccionScreen(
                                    empleado = currentEmpleado!!,
                                    onNavigateBack = {
                                        println("DEBUG: onNavigateBack (Transaccion) callback ejecutado")
                                        currentScreen = "empleado"
                                        println("DEBUG: currentScreen establecido a: empleado")
                                    },
                                    onSuccess = { message ->
                                        println("DEBUG: Transacción exitosa: $message")
                                        // Aquí se podría mostrar un diálogo de éxito o navegar de vuelta
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}