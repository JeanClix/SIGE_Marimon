package org.marimon.sigc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import org.marimon.sigc.data.model.User
import org.marimon.sigc.data.model.UserRole
import org.marimon.sigc.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    
    println("DEBUG: EmployeeScreen - Usuario actual: $currentUser")
    println("DEBUG: EmployeeScreen - Rol: ${currentUser?.role}")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SIGE Marimon - Empleado",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    TextButton(onClick = { 
                        println("DEBUG: EmployeeScreen - Botón cerrar sesión presionado")
                        authViewModel.logout()
                        onLogout()
                    }) {
                        Text("Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
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
            // Información del empleado
            item {
                println("DEBUG: EmployeeScreen - Mostrando información del empleado")
                EmployeeInfoCard(user = currentUser)
            }
            
            // Saludo personalizado al empleado
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Emoji de saludo
                        Text(
                            text = "👋",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.size(64.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Saludo personalizado
                        Text(
                            text = "¡Hola, ${currentUser?.firstName ?: "Empleado"}!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Mensaje de bienvenida
                        Text(
                            text = "Bienvenido al Sistema de Gestión Empresarial",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Descripción
                        Text(
                            text = "Aquí tienes acceso a todas las herramientas y funciones disponibles para tu rol de empleado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Indicador de rol
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                text = "👤 Rol: Empleado",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
            
            // Menú de opciones para empleados
            item {
                Text(
                    text = "Opciones Disponibles",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(getEmployeeMenuItems()) { menuItem ->
                EmployeeMenuItemCard(
                    title = menuItem.title,
                    description = menuItem.description,
                    emoji = menuItem.emoji,
                    onClick = { /* TODO: Implementar navegación específica para empleados */ }
                )
            }
        }
    }
}

@Composable
fun EmployeeInfoCard(user: User?) {
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
            // Avatar del empleado
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = androidx.compose.foundation.shape.CircleShape
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👤",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Información Personal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Nombre: ${user?.firstName ?: "N/A"} ${user?.lastName ?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Text(
                    text = "Email: ${user?.email ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Text(
                    text = "ID: ${user?.id ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun EmployeeMenuItemCard(
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

data class EmployeeMenuItem(
    val title: String,
    val description: String,
    val emoji: String
)

fun getEmployeeMenuItems(): List<EmployeeMenuItem> {
    return listOf(
        EmployeeMenuItem(
            title = "Mi Perfil",
            description = "Ver y editar mi información personal",
            emoji = "👤"
        ),
        EmployeeMenuItem(
            title = "Mis Tareas",
            description = "Ver las tareas asignadas",
            emoji = "📋"
        ),
        EmployeeMenuItem(
            title = "Reportes",
            description = "Generar reportes de mi trabajo",
            emoji = "📊"
        ),
        EmployeeMenuItem(
            title = "Contacto",
            description = "Información de contacto y soporte",
            emoji = "📞"
        )
    )
}
