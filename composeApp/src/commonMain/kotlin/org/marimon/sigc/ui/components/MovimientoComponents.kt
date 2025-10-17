package org.marimon.sigc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
    onClick: (() -> Unit)? = null,
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onClick?.invoke() }
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
 * Modal para mostrar el detalle completo de un movimiento
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientoDetailModal(
    movimiento: Movimiento,
    onDismiss: () -> Unit
) {
    val theme = when (movimiento.tipo) {
        TipoMovimiento.ENTRADA -> MovimientoTheme(
            color = Color(0xFF4CAF50),
            emoji = "📦",
            signo = "+"
        )
        TipoMovimiento.SALIDA -> MovimientoTheme(
            color = Color(0xFFFF383C),
            emoji = "📤",
            signo = "-"
        )
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(theme.emoji, fontSize = 24.sp)
                    Text(
                        when (movimiento.tipo) {
                            TipoMovimiento.ENTRADA -> "Detalle de Entrada"
                            TipoMovimiento.SALIDA -> "Detalle de Salida"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Text("✕", fontSize = 20.sp, color = TextSecondaryColor)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Imagen del producto (centrada y más grande)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ProductImage(
                        imageUrl = movimiento.productoImagenUrl,
                        productName = movimiento.productoNombre ?: "Producto",
                        modifier = Modifier.size(100.dp),
                        fallbackEmoji = theme.emoji
                    )
                }
                
                // Información del producto
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // ID del movimiento
                        DetailRow(
                            label = "ID Movimiento",
                            value = "#${movimiento.id}",
                            icon = "🔖"
                        )
                        
                        Divider(color = Color(0xFFE0E0E0))
                        
                        // Código del producto
                        DetailRow(
                            label = "Código",
                            value = movimiento.productoCodigo ?: "N/A",
                            icon = "📋"
                        )
                        
                        // Nombre del producto
                        DetailRow(
                            label = "Producto",
                            value = movimiento.productoNombre ?: "Producto desconocido",
                            icon = "📦"
                        )
                        
                        Divider(color = Color(0xFFE0E0E0))
                        
                        // Cantidad
                        DetailRow(
                            label = "Cantidad",
                            value = "${theme.signo}${movimiento.cantidad} unidades",
                            valueColor = theme.color,
                            icon = "📊",
                            bold = true
                        )
                        
                        // Tipo
                        DetailRow(
                            label = "Tipo",
                            value = when (movimiento.tipo) {
                                TipoMovimiento.ENTRADA -> "Entrada de inventario"
                                TipoMovimiento.SALIDA -> "Salida de inventario"
                            },
                            valueColor = theme.color,
                            icon = theme.emoji
                        )
                        
                        Divider(color = Color(0xFFE0E0E0))
                        
                        // Empleado
                        if (movimiento.empleadoNombre != null) {
                            DetailRow(
                                label = "Registrado por",
                                value = movimiento.empleadoNombre,
                                icon = "👤"
                            )
                        }
                        
                        // Fecha de registro
                        DetailRow(
                            label = "Fecha de registro",
                            value = formatFechaDetallada(movimiento.fechaRegistro),
                            icon = "📅"
                        )
                        
                        // Nota (si existe)
                        if (!movimiento.nota.isNullOrBlank()) {
                            Divider(color = Color(0xFFE0E0E0))
                            
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("📝", fontSize = 16.sp)
                                    Text(
                                        "Nota",
                                        fontSize = 12.sp,
                                        color = TextSecondaryColor,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    movimiento.nota,
                                    fontSize = 14.sp,
                                    color = TextPrimaryColor,
                                    modifier = Modifier.padding(start = 24.dp)
                                )
                            }
                        }
                    }
                }
                
                // Estado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (movimiento.activo) Color(0xFF4CAF50) else Color(0xFFFF5252),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            if (movimiento.activo) "✓ Activo" else "✕ Inactivo",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = theme.color),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cerrar", color = Color.White)
            }
        },
        dismissButton = {},
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Componente auxiliar para mostrar filas de detalle
 */
@Composable
private fun DetailRow(
    label: String,
    value: String,
    icon: String = "",
    valueColor: Color = TextPrimaryColor,
    bold: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon.isNotEmpty()) {
                Text(icon, fontSize = 16.sp)
            }
            Text(
                label,
                fontSize = 12.sp,
                color = TextSecondaryColor,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            value,
            fontSize = 14.sp,
            color = valueColor,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(start = if (icon.isNotEmpty()) 24.dp else 0.dp)
        )
    }
}

/**
 * Función para formatear fecha con más detalle
 */
private fun formatFechaDetallada(fechaISO: String): String {
    return try {
        val partes = fechaISO.split("T")
        if (partes.size >= 2) {
            val fecha = partes[0]
            val hora = partes[1].substring(0, 8) // HH:MM:SS
            
            // Formato: DD/MM/YYYY a las HH:MM:SS
            val fechaPartes = fecha.split("-")
            if (fechaPartes.size == 3) {
                val (year, month, day) = fechaPartes
                "$day/$month/$year a las $hora"
            } else {
                "$fecha $hora"
            }
        } else {
            fechaISO
        }
    } catch (e: Exception) {
        fechaISO
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
 * Data class para manejar los filtros de movimientos
 */
data class MovimientoFilters(
    val searchText: String = "",
    val selectedProducto: String? = null,
    val selectedCategoria: String? = null,
    val fechaDesde: String? = null,
    val fechaHasta: String? = null,
    val tipo: TipoMovimiento? = null
)

/**
 * Botón compacto para abrir el modal de filtros
 */
@Composable
fun FilterButton(
    onClick: () -> Unit,
    activeFiltersCount: Int = 0,
    accentColor: Color = Color(0xFFFF383C),
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (activeFiltersCount > 0) accentColor else TextSecondaryColor
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (activeFiltersCount > 0) accentColor else Color(0xFFE0E0E0)
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔧", fontSize = 18.sp)
            Text(
                "Filtros",
                fontWeight = if (activeFiltersCount > 0) FontWeight.Bold else FontWeight.Normal
            )
            if (activeFiltersCount > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(accentColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "$activeFiltersCount",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Modal de filtros para movimientos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientoFiltersModal(
    filters: MovimientoFilters,
    onFiltersChange: (MovimientoFilters) -> Unit,
    onDismiss: () -> Unit,
    availableProducts: List<String>,
    availableCategories: List<String>,
    showTipoFilter: Boolean = true,
    accentColor: Color = Color(0xFFFF383C)
) {
    var localFilters by remember { mutableStateOf(filters) }
    var expanded by remember { mutableStateOf(false) }
    var categoriaExpanded by remember { mutableStateOf(false) }
    var tipoExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var datePickerType by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "🔧 Filtros",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Text("✕", fontSize = 20.sp, color = TextSecondaryColor)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Filtro por producto
                Text("Producto", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimaryColor)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = localFilters.selectedProducto ?: "Todos los productos",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            focusedLabelColor = accentColor
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos los productos") },
                            onClick = {
                                localFilters = localFilters.copy(selectedProducto = null)
                                expanded = false
                            }
                        )
                        availableProducts.forEach { producto ->
                            DropdownMenuItem(
                                text = { Text(producto, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                onClick = {
                                    localFilters = localFilters.copy(selectedProducto = producto)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                // Filtro por categoría
                Text("Categoría", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimaryColor)
                ExposedDropdownMenuBox(
                    expanded = categoriaExpanded,
                    onExpandedChange = { categoriaExpanded = it }
                ) {
                    OutlinedTextField(
                        value = localFilters.selectedCategoria ?: "Todas las categorías",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            focusedLabelColor = accentColor
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    ExposedDropdownMenu(
                        expanded = categoriaExpanded,
                        onDismissRequest = { categoriaExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas las categorías") },
                            onClick = {
                                localFilters = localFilters.copy(selectedCategoria = null)
                                categoriaExpanded = false
                            }
                        )
                        availableCategories.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria) },
                                onClick = {
                                    localFilters = localFilters.copy(selectedCategoria = categoria)
                                    categoriaExpanded = false
                                }
                            )
                        }
                    }
                }
                
                // Filtro por tipo (si está habilitado)
                if (showTipoFilter) {
                    Text("Tipo de Movimiento", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimaryColor)
                    ExposedDropdownMenuBox(
                        expanded = tipoExpanded,
                        onExpandedChange = { tipoExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = when (localFilters.tipo) {
                                TipoMovimiento.ENTRADA -> "Entradas"
                                TipoMovimiento.SALIDA -> "Salidas"
                                null -> "Todos"
                            },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                focusedLabelColor = accentColor
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        
                        ExposedDropdownMenu(
                            expanded = tipoExpanded,
                            onDismissRequest = { tipoExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Todos") },
                                onClick = {
                                    localFilters = localFilters.copy(tipo = null)
                                    tipoExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Entradas") },
                                onClick = {
                                    localFilters = localFilters.copy(tipo = TipoMovimiento.ENTRADA)
                                    tipoExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Salidas") },
                                onClick = {
                                    localFilters = localFilters.copy(tipo = TipoMovimiento.SALIDA)
                                    tipoExpanded = false
                                }
                            )
                        }
                    }
                }
                
                // Filtros de fecha
                Text("Rango de Fechas", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimaryColor)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Fecha desde
                    OutlinedButton(
                        onClick = {
                            datePickerType = "desde"
                            showDatePicker = true
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (localFilters.fechaDesde != null) accentColor else TextSecondaryColor
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text("Desde", fontSize = 10.sp, color = TextSecondaryColor)
                            Text(
                                localFilters.fechaDesde ?: "Seleccionar",
                                fontSize = 11.sp,
                                fontWeight = if (localFilters.fechaDesde != null) FontWeight.Medium else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                    
                    // Fecha hasta
                    OutlinedButton(
                        onClick = {
                            datePickerType = "hasta"
                            showDatePicker = true
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (localFilters.fechaHasta != null) accentColor else TextSecondaryColor
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text("Hasta", fontSize = 10.sp, color = TextSecondaryColor)
                            Text(
                                localFilters.fechaHasta ?: "Seleccionar",
                                fontSize = 11.sp,
                                fontWeight = if (localFilters.fechaHasta != null) FontWeight.Medium else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Limpiar
                OutlinedButton(
                    onClick = {
                        localFilters = MovimientoFilters(searchText = localFilters.searchText)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Limpiar")
                }
                
                // Botón Aplicar
                Button(
                    onClick = {
                        onFiltersChange(localFilters)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Aplicar", color = Color.White)
                }
            }
        },
        dismissButton = {},
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
    
    // Date picker dialog
    if (showDatePicker && datePickerType != null) {
        var inputDate by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("Seleccionar fecha ${if (datePickerType == "desde") "desde" else "hasta"}") },
            text = {
                Column {
                    Text(
                        "Ingresa la fecha en formato YYYY-MM-DD",
                        fontSize = 12.sp,
                        color = TextSecondaryColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputDate,
                        onValueChange = { inputDate = it },
                        label = { Text("Fecha") },
                        placeholder = { Text("2024-01-15") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputDate.isNotBlank()) {
                            if (datePickerType == "desde") {
                                localFilters = localFilters.copy(fechaDesde = inputDate)
                            } else {
                                localFilters = localFilters.copy(fechaHasta = inputDate)
                            }
                        }
                        showDatePicker = false
                        inputDate = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showDatePicker = false
                    inputDate = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Función mejorada para filtrar movimientos con múltiples criterios
 */
fun filterMovimientos(
    movimientos: List<Movimiento>,
    filters: MovimientoFilters
): List<Movimiento> {
    return movimientos.filter { movimiento ->
        // Filtro por texto de búsqueda
        val matchesSearch = if (filters.searchText.isBlank()) {
            true
        } else {
            movimiento.productoNombre?.contains(filters.searchText, ignoreCase = true) == true ||
            movimiento.productoCodigo?.contains(filters.searchText, ignoreCase = true) == true ||
            movimiento.nota?.contains(filters.searchText, ignoreCase = true) == true
        }
        
        // Filtro por producto
        val matchesProducto = if (filters.selectedProducto == null) {
            true
        } else {
            movimiento.productoNombre == filters.selectedProducto
        }
        
        // Filtro por categoría (extraer de la nota)
        val matchesCategoria = if (filters.selectedCategoria == null) {
            true
        } else {
            movimiento.nota?.contains(filters.selectedCategoria, ignoreCase = true) == true
        }
        
        // Filtro por tipo
        val matchesTipo = if (filters.tipo == null) {
            true
        } else {
            movimiento.tipo == filters.tipo
        }
        
        // Filtro por fecha desde
        val matchesFechaDesde = if (filters.fechaDesde == null) {
            true
        } else {
            try {
                val fechaMovimiento = movimiento.fechaRegistro.split("T")[0]
                fechaMovimiento >= filters.fechaDesde
            } catch (e: Exception) {
                true
            }
        }
        
        // Filtro por fecha hasta
        val matchesFechaHasta = if (filters.fechaHasta == null) {
            true
        } else {
            try {
                val fechaMovimiento = movimiento.fechaRegistro.split("T")[0]
                fechaMovimiento <= filters.fechaHasta
            } catch (e: Exception) {
                true
            }
        }
        
        matchesSearch && matchesProducto && matchesCategoria && matchesTipo && matchesFechaDesde && matchesFechaHasta
    }
}

/**
 * Función legacy para mantener compatibilidad
 */
fun filterMovimientos(movimientos: List<Movimiento>, searchText: String): List<Movimiento> {
    return filterMovimientos(movimientos, MovimientoFilters(searchText = searchText))
}