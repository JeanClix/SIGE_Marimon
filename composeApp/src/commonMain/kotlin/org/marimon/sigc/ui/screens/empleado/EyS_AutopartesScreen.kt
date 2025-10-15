package org.marimon.sigc.ui.screens.empleado

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.marimon.sigc.model.Empleado
import org.marimon.sigc.ui.components.EmpleadoTopBar

// --- Colores y Estilos ---
private val RedMarimon = Color(0xFFFF0000)
private val BackgroundColor = Color(0xFFF2F2F7)
private val CardBackgroundColor = Color.White
private val TextPrimaryColor = Color.Black
private val TextSecondaryColor = Color.Gray
private val BlackButton = Color(0xFF000000)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EySAutopartesScreen(
    empleado: Empleado,
    onNavigateBack: () -> Unit,
    onNavigateToEntrada: () -> Unit = {},
    onNavigateToSalida: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            EmpleadoTopBar(
                empleado = empleado,
                title = "Entradas y Salidas",
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Saludo personalizado
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "¡Hola! ${empleado.nombre}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryColor
                    )
                    Text(
                        empleado.areaNombre,
                        fontSize = 16.sp,
                        color = TextSecondaryColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            // Sección ENTRADA
            MovementCard(
                title = "ENTRADA",
                description = "Registra la llegada de nuevos productos comprados al inventario",
                iconEmoji = "📦",
                iconBackgroundColor = Color(0xFFFFF3E0),
                iconTint = Color(0xFFFF9800)
            )
            Spacer(modifier = Modifier.height(24.dp))
            // Sección SALIDA
            MovementCard(
                title = "SALIDA",
                description = "Controla la salida de productos por ventas",
                iconEmoji = "👤",
                iconBackgroundColor = Color(0xFFE3F2FD),
                iconTint = Color(0xFF2196F3)
            )
            Spacer(modifier = Modifier.weight(1f))
            
            // Botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateToEntrada,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BlackButton),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        "Entrada",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Button(
                    onClick = onNavigateToSalida,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RedMarimon),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        "Salida",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun MovementCard(
    title: String,
    description: String,
    iconEmoji: String,
    iconBackgroundColor: Color,
    iconTint: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono usando emoji
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    iconEmoji,
                    fontSize = 32.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Título
            Text(
                title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryColor
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Descripción
            Text(
                description,
                fontSize = 14.sp,
                color = TextSecondaryColor,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}