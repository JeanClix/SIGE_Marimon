package org.marimon.sigc.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.marimon.sigc.ui.screens.EmployeeDashboard
import org.marimon.sigc.data.model.UserRole
import org.marimon.sigc.viewmodel.AuthViewModel
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.collectAsState
import org.marimon.sigc.data.model.User
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

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
                println("DEBUG: AppNavigation - Redirigiendo a Vista Administrativa (ADMIN)")
                // Vista simple para administrador
                AdminSimpleView(authViewModel = authViewModel)
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
        println("DEBUG: AppNavigation - No está logueado, redirigiendo a LoginActivity")
        // Cuando no está logueado, mostrar mensaje de redirección
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Redirigiendo al login...",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun AdminSimpleView(authViewModel: AuthViewModel) {
    val currentUser by authViewModel.currentUser.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SIGE Marimon - Administrador",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    TextButton(onClick = { 
                        authViewModel.logout()
                    }) {
                        Text("Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Información del usuario
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👤",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.size(48.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = "Bienvenido, ${currentUser?.firstName ?: "Administrador"}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = currentUser?.email ?: "admin@sige.com",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Rol: ${currentUser?.role?.name ?: "ADMIN"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            
            // Menú de opciones administrativas
            item {
                Text(
                    text = "Panel Administrativo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(getAdminMenuItems()) { menuItem ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /* TODO: Implementar navegación */ },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = menuItem.emoji,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.size(32.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = menuItem.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = menuItem.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class AdminMenuItem(
    val title: String,
    val description: String,
    val emoji: String
)

fun getAdminMenuItems(): List<AdminMenuItem> {
    return listOf(
        AdminMenuItem(
            title = "Gestión de Empleados",
            description = "Administrar información de empleados",
            emoji = "👥"
        ),
        AdminMenuItem(
            title = "Gestión de Productos",
            description = "Administrar catálogo de productos",
            emoji = "📦"
        ),
        AdminMenuItem(
            title = "Reportes y KPI",
            description = "Ver métricas y reportes del sistema",
            emoji = "📊"
        ),
        AdminMenuItem(
            title = "Configuración",
            description = "Configurar parámetros del sistema",
            emoji = "⚙️"
        )
    )
}


