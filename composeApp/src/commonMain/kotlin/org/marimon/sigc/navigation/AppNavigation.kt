package org.marimon.sigc.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.marimon.sigc.ui.screens.HomeScreen
import org.marimon.sigc.ui.screens.LoginScreen
import org.marimon.sigc.ui.screens.EmployeeScreen
import org.marimon.sigc.data.model.UserRole
import org.marimon.sigc.viewmodel.AuthViewModel

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
                HomeScreen(
                    authViewModel = authViewModel,
                    onLogout = {
                        // El logout se maneja en el ViewModel
                    }
                )
            }
            UserRole.EMPLOYEE -> {
                EmployeeScreen(
                    authViewModel = authViewModel,
                    onLogout = {
                        // El logout se maneja en el ViewModel
                    }
                )
            }
            null -> {
                // Si no hay usuario, mostrar login
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        // El estado de login se maneja en el ViewModel
                    }
                )
            }
        }
    } else {
        LoginScreen(
            authViewModel = authViewModel,
            onLoginSuccess = {
                // El estado de login se maneja en el ViewModel
            }
        )
    }
}
