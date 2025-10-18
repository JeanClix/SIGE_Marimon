package org.marimon.sigc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import org.marimon.sigc.ui.icons.MarimonIcons
import org.marimon.sigc.viewmodel.AuthViewModel

// Colores Marimon
private val RedPure = Color(0xFFFF0000) // Rojo puro
private val BackgroundDark = Color(0xFF000000) // Fondo negro
private val CardBackground = Color(0xFFFFFFFF) // Tarjeta blanca
private val TextPrimary = Color(0xFF000000) // Texto negro
private val TextSecondary = Color(0xFF666666) // Texto gris
private val InputBorder = Color(0xFFE5E5E5) // Borde gris claro
private val TextFooter = Color(0xFF666666) // Color específico para el footer

private val BackgroundGradient = listOf(
    Color(0xFF333333), // Gris oscuro
    BackgroundDark     // Negro
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoScreen(
    empleado: Empleado,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    onNavigateToAutopartes: () -> Unit = { },
    onNavigateToEySAutopartes: () -> Unit = { },
    onNavigateToTransaccion: () -> Unit = { }
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
                            .clip(RoundedCornerShape(50))
                            .background(RedPure),
                        contentAlignment = Alignment.Center
                    ) {
                        // TODO: Reemplazar con imagen del empleado cuando esté disponible
                        /*
                        AsyncImage(
                            model = empleado.imagenUrl,
                            contentDescription = "Avatar ${empleado.nombre}",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(50)),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.placeholder_avatar),
                            error = painterResource(R.drawable.placeholder_avatar)
                        )
                        */
                        
                        // Texto temporal mientras no hay imagen
                        Text(
                            text = empleado.nombre.firstOrNull()?.uppercase() ?: "E",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
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
                containerColor = RedPure
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
                        containerColor = CardBackground
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
                            color = RedPure,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Empleado Marimon",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            // Opciones de navegación para empleados
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Módulos Disponibles",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = RedPure,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        // Administrar Autopartes
                        NavigationOption(
                            icon = MarimonIcons.Tools,
                            title = "Administrar Autopartes",
                            description = "Gestionar inventario de repuestos y autopartes",
                            enabled = true,
                            onItemClick = { 
                                println("DEBUG: Click en Administrar Autopartes")
                                onNavigateToAutopartes()
                                println("DEBUG: onNavigateToAutopartes() ejecutado")
                            }
                        )

                        //Administrar Entradas y salidas
                        NavigationOption(
                            icon = "📦",
                            title = "Movimiento de Inventario",
                            description = "Registrar entradas y salidas de productos",
                            enabled = true,
                            onItemClick = onNavigateToEySAutopartes
                        )


                        // Emitir comprobante de pago
                        NavigationOption(
                            icon = "📋",
                            title = "Emitir comprobante de pago",
                            description = "Registrar transacciones y generar comprobantes",
                            enabled = true,
                            onItemClick = onNavigateToTransaccion
                        )
                        
                        NavigationOption(
                            icon = "👥",
                            title = "Atención al Cliente",
                            description = "Gestionar solicitudes y consultas",
                            enabled = false,
                            onItemClick = { }
                        )
                        
                        NavigationOption(
                            icon = "📋",
                            title = "Órdenes de Trabajo",
                            description = "Crear y gestionar órdenes de servicio",
                            enabled = false,
                            onItemClick = { }
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
                        containerColor = CardBackground
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Mi Información",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = RedPure,
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
            

            
            // Información del sistema
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = RedPure.copy(alpha = 0.1f)
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
                            color = RedPure,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "Automotriz Marimon - Versión Empleado",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
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
            containerColor = if (enabled) RedPure.copy(alpha = 0.1f) 
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
                color = if (enabled) RedPure else Color.Gray
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

@Composable
private fun NavigationOption(
    icon: String,
    title: String,
    description: String,
    enabled: Boolean,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) RedPure.copy(alpha = 0.05f) 
                           else Color.Gray.copy(alpha = 0.1f)
        ),
        onClick = { 
            println("DEBUG: Card clicked, enabled = $enabled")
            if (enabled) {
                println("DEBUG: Ejecutando onItemClick")
                onItemClick()
            } else {
                println("DEBUG: Card deshabilitado, no se ejecuta onItemClick")
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icono con fondo circular
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (enabled) RedPure.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 20.sp
                )
            }
            
            // Contenido
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (enabled) TextPrimary else Color.Gray
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled) TextSecondary else Color.Gray.copy(alpha = 0.6f)
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
            
            // Flecha de navegación
            if (enabled) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = RedPure,
                            shape = RoundedCornerShape(50)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "→",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
