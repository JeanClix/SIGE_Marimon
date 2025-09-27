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
                println("DEBUG: AppNavigation - Redirigiendo a Panel Administrativo (ADMIN)")
                AdminPanel(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanel(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
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
                        // No llamar onLogout() aquí, dejar que AppNavigation maneje la navegación
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
                UserInfoCard(user = currentUser)
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
                AdminMenuItemCard(
                    title = menuItem.title,
                    description = menuItem.description,
                    emoji = menuItem.emoji,
                    onClick = { /* TODO: Implementar navegación */ }
                )
            }
        }
    }
}

@Composable
fun UserInfoCard(user: User?) {
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
                    text = "Bienvenido, ${user?.firstName ?: "Administrador"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = user?.email ?: "admin@sige.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = "Rol: ${user?.role?.name ?: "ADMIN"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun AdminMenuItemCard(
    title: String,
    description: String,
    emoji: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
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
