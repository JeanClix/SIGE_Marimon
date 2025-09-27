package org.marimon.sigc.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.marimon.sigc.ui.screens.HomeScreen
import org.marimon.sigc.ui.screens.EmployeeDashboard
import org.marimon.sigc.data.model.UserRole
import org.marimon.sigc.viewmodel.AuthViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState

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
                println("DEBUG: AppNavigation - Usuario null, mostrando LoginScreen")
                // Si no hay usuario, mostrar login
                SimpleLoginScreen(authViewModel = authViewModel)
            }
        }
    } else {
        println("DEBUG: AppNavigation - No está logueado, mostrando LoginScreen")
        SimpleLoginScreen(authViewModel = authViewModel)
    }
}

@Composable
fun SimpleLoginScreen(authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val authState by authViewModel.authState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SIGE Marimon",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Sistema de Gestión Empresarial",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        authViewModel.clearError()
                    },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        authViewModel.clearError()
                    },
                    label = { Text("Contraseña") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                when (val currentAuthState = authState) {
                    is org.marimon.sigc.data.model.AuthResult.Error -> {
                        if (currentAuthState.message.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Text(
                                    text = currentAuthState.message,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    else -> { /* No mostrar nada para otros estados */ }
                }
                
                Button(
                    onClick = {
                        authViewModel.login(email, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = authState !is org.marimon.sigc.data.model.AuthResult.Loading
                ) {
                    when (authState) {
                        is org.marimon.sigc.data.model.AuthResult.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        else -> {
                            Text(
                                text = "Iniciar Sesión",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Credenciales de Prueba:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "👤 Admin (Supabase Auth): admin@sige.com / admin123",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "👨‍💼 Empleado (Tabla Empleado): jose@sige.com / jose",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
