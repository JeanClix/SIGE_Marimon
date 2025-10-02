package org.marimon.sigc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.marimon.sigc.model.Empleado
import org.marimon.sigc.viewmodel.AuthViewModel

// Colores para empleados
private val EmpleadoBlue = Color(0xFF1976D2) // Azul corporativo
private val EmpleadoLightBlue = Color(0xFF42A5F5) // Azul claro
private val BackgroundGradient = listOf(
    Color(0xFFF5F5F5), // Gris muy claro
    Color(0xFFE3F2FD)  // Azul muy claro
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoScreen(
    empleado: Empleado,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(BackgroundGradient)
            )
    ) {
        // Top Bar con información del empleado
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar del empleado
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(EmpleadoBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👤",
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Hola, ${empleado.nombre.split(" ").firstOrNull() ?: empleado.nombre}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = empleado.areaNombre,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            },
            actions = {
                TextButton(
                    onClick = {
                        authViewModel.logout()
                        onLogout()
                    }
                ) {
                    Text(
                        text = "🚪",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = EmpleadoBlue
            )
        )
        
        // Contenido principal
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de bienvenida
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "💼",
                            fontSize = 48.sp
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "¡Bienvenido al Sistema!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmpleadoBlue,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Acceso de Empleado - SIGE Marimon",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            // Información del empleado
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Mi Información",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = EmpleadoBlue,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        InfoRow(label = "Nombre", value = empleado.nombre)
                        InfoRow(label = "Email", value = empleado.emailCorporativo)
                        InfoRow(label = "Área", value = empleado.areaNombre)
                        InfoRow(label = "ID Empleado", value = empleado.id.toString())
                        InfoRow(
                            label = "Estado", 
                            value = if (empleado.activo) "Activo" else "Inactivo",
                            valueColor = if (empleado.activo) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                    }
                }
            }
            
            // Funciones disponibles para empleados
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Funciones Disponibles",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = EmpleadoBlue,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        // Lista de funciones que puede realizar un empleado
                        FunctionItem(
                            title = "Consultar Productos",
                            description = "Ver catálogo de productos disponibles",
                            enabled = true
                        )
                        
                        FunctionItem(
                            title = "Registrar Ventas",
                            description = "Crear y gestionar registros de ventas",
                            enabled = true
                        )
                        
                        FunctionItem(
                            title = "Ver Reportes",
                            description = "Consultar reportes de mi área",
                            enabled = true
                        )
                        
                        FunctionItem(
                            title = "Actualizar Perfil",
                            description = "Modificar información personal",
                            enabled = false
                        )
                    }
                }
            }
            
            // Información del sistema
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = EmpleadoLightBlue.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Sistema de Gestión Empresarial",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = EmpleadoBlue,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "Automotriz Marimon - Versión Empleado",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun FunctionItem(
    title: String,
    description: String,
    enabled: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) EmpleadoLightBlue.copy(alpha = 0.1f) 
                           else Color.Gray.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (enabled) EmpleadoBlue else Color.Gray
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) Color.Gray else Color.Gray.copy(alpha = 0.6f)
            )
            if (!enabled) {
                Text(
                    text = "Próximamente disponible",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray.copy(alpha = 0.7f),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}
