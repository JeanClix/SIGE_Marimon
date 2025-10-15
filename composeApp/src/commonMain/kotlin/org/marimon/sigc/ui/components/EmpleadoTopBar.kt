package org.marimon.sigc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.marimon.sigc.model.Empleado

// --- Colores ---
private val RedMarimon = Color(0xFFFF0000)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoTopBar(
    empleado: Empleado,
    title: String,
    onNavigateBack: (() -> Unit)? = null,
    showNotifications: Boolean = true,
    onNotificationClick: (() -> Unit)? = null
) {
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
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        empleado.nombre.firstOrNull()?.uppercase() ?: "E",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Información del empleado con título
                Column {
                    Text(
                        title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        empleado.areaNombre,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        },
        navigationIcon = {
            // Botón de retroceso (opcional)
            onNavigateBack?.let { backAction ->
                IconButton(onClick = backAction) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "←",
                            fontSize = 18.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        actions = {
            // Icono de notificación
            if (showNotifications) {
                IconButton(onClick = { onNotificationClick?.invoke() }) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "🔔",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = RedMarimon
        )
    )
}

@Composable
fun EmpleadoBottomNavigationBar(
    selectedIndex: Int = 1, // Por defecto selecciona "Reportes"
    onHomeClick: () -> Unit = {},
    onReportsClick: () -> Unit = {},
    onDocumentsClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = Color.Black,
        modifier = Modifier.height(60.dp)
    ) {
        NavigationBarItem(
            icon = { 
                Text("🏠", fontSize = 20.sp, color = if (selectedIndex == 0) RedMarimon else Color.White)
            },
            selected = selectedIndex == 0,
            onClick = onHomeClick,
            label = null
        )
        NavigationBarItem(
            icon = { 
                Text("📊", fontSize = 20.sp, color = if (selectedIndex == 1) RedMarimon else Color.White)
            },
            selected = selectedIndex == 1,
            onClick = onReportsClick,
            label = null
        )
        NavigationBarItem(
            icon = { 
                Text("📄", fontSize = 20.sp, color = if (selectedIndex == 2) RedMarimon else Color.White)
            },
            selected = selectedIndex == 2,
            onClick = onDocumentsClick,
            label = null
        )
    }
}