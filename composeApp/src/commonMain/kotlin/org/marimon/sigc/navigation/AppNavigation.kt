package org.marimon.sigc.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.marimon.sigc.ui.screens.HomeScreen
import org.marimon.sigc.ui.screens.EmployeeDashboard
import org.marimon.sigc.data.model.UserRole
import org.marimon.sigc.viewmodel.AuthViewModel
import androidx.compose.material3.Text

@Composable
fun AppNavigation(authViewModel: AuthViewModel) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    println("DEBUG: AppNavigation - isLoggedIn: $isLoggedIn")
    println("DEBUG: AppNavigation - currentUser: $currentUser")
    println("DEBUG: AppNavigation - user role: ${currentUser?.role}")
    
    if (isLoggedIn) {
        // Redirigir según el rol del usuario
        when (currentUser?.role) {
            UserRole.ADMIN -> {
                println("DEBUG: AppNavigation - Redirigiendo a HomeScreen (ADMIN)")
                HomeScreen(
                    authViewModel = authViewModel,
                    onLogout = {
                        // El logout se maneja en el ViewModel
                    }
                )
            }
            UserRole.EMPLOYEE -> {
                println("DEBUG: AppNavigation - Redirigiendo a EmployeeDashboard (EMPLOYEE)")
                EmployeeDashboard(
                    authViewModel = authViewModel,
                    onLogout = {
                        // El logout se maneja en el ViewModel
                    }
                )
            }
            null -> {
                println("DEBUG: AppNavigation - Usuario null, esto no debería pasar")
                // Si no hay usuario, esto no debería pasar ya que LoginActivity maneja el login
                Text("Error: Usuario null")
            }
        }
    } else {
        println("DEBUG: AppNavigation - No está logueado, esto no debería pasar")
        // Esto no debería pasar ya que LoginActivity maneja el login
        Text("Error: No está logueado")
    }
}
