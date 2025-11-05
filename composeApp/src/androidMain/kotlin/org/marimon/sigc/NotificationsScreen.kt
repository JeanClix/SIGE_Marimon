package org.marimon.sigc

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Modelo de notificación
data class NotificationItem(
    val id: String,
    val type: String, // "producto_agregado", "stock_bajo", "perfil_actualizado", "empleado_eliminado"
    val title: String,
    val description: String,
    val timestamp: String,
    val isRead: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit = {}
) {
    // Estado mutable para las notificaciones
    var notificationsList by remember {
        mutableStateOf(listOf(
            NotificationItem(
                id = "1",
                type = "producto_agregado",
                title = "📦 Producto Agregado",
                description = "Nuevo producto 'Aceite Premium 5L' ha sido agregado al inventario",
                timestamp = "Hace 2 horas",
                isRead = false
            ),
            NotificationItem(
                id = "2",
                type = "stock_bajo",
                title = "⚠️ Stock Bajo",
                description = "El producto 'Aceite Estándar 1L' tiene solo 3 unidades disponibles",
                timestamp = "Hace 1 hora",
                isRead = false
            ),
            NotificationItem(
                id = "3",
                type = "perfil_actualizado",
                title = "👤 Perfil Actualizado",
                description = "Tu perfil de administrador ha sido actualizado correctamente",
                timestamp = "Hace 30 minutos",
                isRead = true
            ),
            NotificationItem(
                id = "4",
                type = "empleado_eliminado",
                title = "👨‍💼 Empleado Eliminado",
                description = "El empleado 'Juan Pérez' ha sido removido del sistema",
                timestamp = "Hace 15 minutos",
                isRead = true
            ),
            NotificationItem(
                id = "5",
                type = "stock_bajo",
                title = "⚠️ Stock Bajo",
                description = "El producto 'Filtro de Aire' tiene bajo inventario",
                timestamp = "Hace 5 minutos",
                isRead = false
            )
        ))
    }

    // Función para marcar como leído
    fun markAsRead(id: String) {
        notificationsList = notificationsList.map { notification ->
            if (notification.id == id) {
                notification.copy(isRead = true)
            } else {
                notification
            }
        }
    }

    // Función para eliminar notificación
    fun deleteNotification(id: String) {
        notificationsList = notificationsList.filter { it.id != id }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE53E3E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filtros (opcionales)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = true,
                    onClick = { },
                    label = { Text("Todas") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text("No leídas") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Lista de notificaciones
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(notificationsList) { notification ->
                    NotificationCard(
                        notification = notification,
                        onMarkAsRead = { markAsRead(notification.id) },
                        onDelete = { deleteNotification(notification.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationItem,
    onMarkAsRead: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    // Definir color según el tipo de notificación
    val (iconColor, iconEmoji) = when (notification.type) {
        "producto_agregado" -> Color(0xFF4CAF50) to "📦"
        "stock_bajo" -> Color(0xFFFFC107) to "⚠️"
        "perfil_actualizado" -> Color(0xFF2196F3) to "👤"
        "empleado_eliminado" -> Color(0xFFE53E3E) to "👨‍💼"
        else -> Color(0xFF9C27B0) to "📌"
    }

    // Badge rojo en la esquina si no está leído
    val badgeColor = if (!notification.isRead) Color(0xFFE53E3E) else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                // Al hacer click en la tarjeta, marcar como leído
                if (!notification.isRead) {
                    onMarkAsRead()
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono circular de color
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(iconColor.copy(alpha = 0.15f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Texto emoji o icono
                Text(
                    text = iconEmoji,
                    style = MaterialTheme.typography.headlineSmall
                )

                // Badge rojo en la esquina
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(badgeColor, shape = CircleShape)
                        .align(Alignment.TopEnd)
                )
            }

            // Contenido de la notificación
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = notification.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 2
                    )
                }

                Text(
                    text = notification.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF999999)
                )
            }

            // Botón de eliminar (X)
            IconButton(
                onClick = { onDelete() },
                modifier = Modifier.size(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(0xFFE53E3E).copy(alpha = 0.1f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFE53E3E),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
