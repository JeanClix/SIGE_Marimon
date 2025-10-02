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
import org.marimon.sigc.viewmodel.AuthViewModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                        }
                    }
                }
            }
        }
    }
}
