package org.marimon.sigc

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.marimon.sigc.navigation.AppNavigation
import org.marimon.sigc.viewmodel.AuthViewModel

@Composable
fun App() {
    MaterialTheme {
        val authViewModel = remember { AuthViewModel() }
        AppNavigation(authViewModel = authViewModel)
    }
}