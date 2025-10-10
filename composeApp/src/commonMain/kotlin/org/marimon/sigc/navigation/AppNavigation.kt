package org.marimon.sigc.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.marimon.sigc.ui.screens.HomeScreen
import org.marimon.sigc.ui.screens.LoginScreen
import org.marimon.sigc.viewmodel.AuthViewModel

@Composable
fun AppNavigation(authViewModel: AuthViewModel) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    
    if (isLoggedIn) {
        HomeScreen(
            authViewModel = authViewModel,
            onLogout = {
                // El logout se maneja en el ViewModel
            }
        )
    } else {
        LoginScreen(
            authViewModel = authViewModel,
            onLoginSuccess = {
                // El estado de login se maneja en el ViewModel
            }
        )
    }
}
