package org.marimon.sigc.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.marimon.sigc.model.Movimiento
import org.marimon.sigc.model.TipoMovimiento

// --- Colores Compartidos ---
private val CardBackgroundColor = Color.White
private val TextPrimaryColor = Color.Black
private val TextSecondaryColor = Color.Gray
private val REntrada = Color(0xFFFF383C)
private val RSalida = Color(0xFFFF383C)

/**
 * Función utilitaria para formatear fechas ISO a formato legible
 */
fun formatFecha(fechaISO: String): String {
    return try {
        val partes = fechaISO.split("T")
        if (partes.size >= 2) {
            val fecha = partes[0]
            val hora = partes[1].substring(0, 5) // Solo HH:MM
            "$fecha $hora"
        } else {
            fechaISO
        }
    } catch (e: Exception) {
        fechaISO
    }
}

/**
 * Configuración de tema para movimientos
 */
data class MovimientoTheme(
    val color: Color,
    val emoji: String,
    val signo: String
)

/**
 * Componente unificado para mostrar tarjetas de movimientos
 */
@Composable
fun MovimientoCard(
    movimiento: Movimiento,
    theme: MovimientoTheme = when (movimiento.tipo) {
        TipoMovimiento.ENTRADA -> MovimientoTheme(
            color = Color(0xFF4CAF50),
            emoji = "📦",
            signo = "+"
        )
        TipoMovimiento.SALIDA -> MovimientoTheme(
            color = RSalida,
            emoji = "📤",
            signo = "-"
        )
    }
) {
    val borderColor = if (movimiento.cantidad > 10) theme.color else theme.color.copy(alpha = 0.6f)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen del producto
            ProductImage(
                imageUrl = movimiento.productoImagenUrl,
                productName = movimiento.productoNombre ?: "Producto",
                modifier = Modifier.size(60.dp),
                fallbackEmoji = theme.emoji
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Código del producto
            Text(
                movimiento.productoCodigo ?: "N/A",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryColor,
                fontWeight = FontWeight.Medium
            )
            
            // Cantidad con signo
            Text(
                "${theme.signo}${movimiento.cantidad}",
                style = MaterialTheme.typography.titleLarge,
                color = theme.color,
                fontWeight = FontWeight.Bold
            )
            
            // Nombre del producto
            Text(
                movimiento.productoNombre ?: "Producto desconocido",
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimaryColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Fecha de registro
            Text(
                formatFecha(movimiento.fechaRegistro),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Componente para mostrar estados vacíos personalizados
 */
@Composable
fun MovimientoEmptyState(
    emoji: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                emoji,
                fontSize = 48.sp
            )
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondaryColor,
                textAlign = TextAlign.Center
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Componente de paginación reutilizable
 */
@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (totalPages > 1) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (currentPage > 1) onPageChange(currentPage - 1) },
                enabled = currentPage > 1
            ) {
                Text("◀", color = if (currentPage > 1) TextPrimaryColor else TextSecondaryColor)
            }
            
            Text(
                "$currentPage de $totalPages",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimaryColor,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            IconButton(
                onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) },
                enabled = currentPage < totalPages
            ) {
                Text("▶", color = if (currentPage < totalPages) TextPrimaryColor else TextSecondaryColor)
            }
        }
    }
}

/**
 * Lógica de paginación reutilizable
 */
data class PaginationState<T>(
    val items: List<T>,
    val currentPage: Int,
    val totalPages: Int,
    val itemsInCurrentPage: List<T>
)

/**
 * Hook para manejar paginación
 */
@Composable
fun <T> rememberPaginationState(
    items: List<T>,
    itemsPerPage: Int = 6,
    currentPage: Int
): PaginationState<T> {
    return remember(items, currentPage) {
        val totalPages = if (items.isNotEmpty()) {
            (items.size + itemsPerPage - 1) / itemsPerPage
        } else {
            1
        }
        
        val startIndex = (currentPage - 1) * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, items.size)
        val itemsInCurrentPage = if (items.isNotEmpty()) {
            items.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        
        PaginationState(
            items = items,
            currentPage = currentPage,
            totalPages = totalPages,
            itemsInCurrentPage = itemsInCurrentPage
        )
    }
}

/**
 * Función para filtrar movimientos por búsqueda
 */
fun filterMovimientos(movimientos: List<Movimiento>, searchText: String): List<Movimiento> {
    return if (searchText.isBlank()) {
        movimientos
    } else {
        movimientos.filter { movimiento ->
            movimiento.productoNombre?.contains(searchText, ignoreCase = true) == true ||
            movimiento.productoCodigo?.contains(searchText, ignoreCase = true) == true ||
            movimiento.nota?.contains(searchText, ignoreCase = true) == true
        }
    }
}