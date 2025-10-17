package org.marimon.sigc.ui.screens.empleado.productos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.marimon.sigc.model.Empleado
import org.marimon.sigc.model.Movimiento
import org.marimon.sigc.model.Producto
import org.marimon.sigc.ui.components.EmpleadoTopBar
import org.marimon.sigc.ui.components.ProductImage
import org.marimon.sigc.ui.components.MovimientoCard
import org.marimon.sigc.ui.components.MovimientoDetailModal
import org.marimon.sigc.ui.components.MovimientoEmptyState
import org.marimon.sigc.ui.components.PaginationControls
import org.marimon.sigc.ui.components.rememberPaginationState
import org.marimon.sigc.ui.components.filterMovimientos
import org.marimon.sigc.ui.components.MovimientoFilters
import org.marimon.sigc.ui.components.FilterButton
import org.marimon.sigc.ui.components.MovimientoFiltersModal
import org.marimon.sigc.ui.components.modals.RegistroSalidaModal
import org.marimon.sigc.viewmodel.ProductoViewModel
import org.marimon.sigc.viewmodel.MovimientoViewModel
import org.marimon.sigc.model.MovimientoCreate
import org.marimon.sigc.model.TipoMovimiento

// --- Colores y Estilos ---
private val RedMarimon = Color(0xFFFF0000)
private val BackgroundColor = Color(0xFFF2F2F7)
private val CardBackgroundColor = Color.White
private val TextPrimaryColor = Color.Black
private val TextSecondaryColor = Color.Gray
private val RSalida = Color(0xFFFF383C)
private val BorderColor = Color(0xFFE0E0E0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalidaProductosScreen(
    empleado: Empleado,
    onNavigateBack: () -> Unit
) {
    var filters by remember { mutableStateOf(MovimientoFilters()) }
    var showRegistroDialog by remember { mutableStateOf(false) }
    var showRegistroForm by remember { mutableStateOf(false) }
    var showFiltersModal by remember { mutableStateOf(false) }
    var showDetailModal by remember { mutableStateOf(false) }
    var selectedMovimiento by remember { mutableStateOf<Movimiento?>(null) }
    var currentPage by remember { mutableIntStateOf(1) }
    
    // ViewModels
    val movimientoViewModel = remember { MovimientoViewModel() }
    val movimientos = movimientoViewModel.movimientos
    
    // Cargar movimientos de salida al inicializar
    LaunchedEffect(Unit) {
        movimientoViewModel.cargarMovimientosPorTipo(TipoMovimiento.SALIDA)
    }
    
    // Obtener lista de productos únicos para el filtro
    val availableProducts = remember(movimientos.toList()) {
        movimientos.mapNotNull { it.productoNombre }.distinct().sorted()
    }
    
    // Obtener lista de categorías únicas (extraídas de las notas)
    val availableCategories = remember(movimientos.toList()) {
        movimientos.mapNotNull { movimiento ->
            movimiento.nota?.let { nota ->
                if (nota.contains("Categoría:")) {
                    nota.substringAfter("Categoría: ").substringBefore(" -").trim()
                } else null
            }
        }.distinct().sorted()
    }
    
    // Calcular número de filtros activos (excluyendo búsqueda)
    val activeFiltersCount = remember(filters) {
        var count = 0
        if (filters.selectedProducto != null) count++
        if (filters.selectedCategoria != null) count++
        if (filters.fechaDesde != null) count++
        if (filters.fechaHasta != null) count++
        if (filters.tipo != null) count++
        count
    }
    
    // Filtrar movimientos por filtros y ordenar cronológicamente (más nuevos primero)
    val movimientosFiltrados = remember(movimientos.toList(), filters) {
        val filtrados = filterMovimientos(movimientos.toList(), filters)
        // Ordenar por fecha de registro, más nuevos primero
        val result = filtrados.sortedByDescending { it.fechaRegistro }
        println("[SalidaScreen] Movimientos filtrados: ${result.size}, Total: ${movimientos.size}")
        result.forEach { movimiento ->
            println("[SalidaScreen] Movimiento: ID=${movimiento.id}, Producto=${movimiento.productoNombre}, Cantidad=${movimiento.cantidad}")
        }
        result
    }
    
    // Estado de paginación
    val paginationState = rememberPaginationState(
        items = movimientosFiltrados,
        itemsPerPage = 6,
        currentPage = currentPage
    )
    
    // Ajustar página actual si es mayor al total de páginas
    LaunchedEffect(paginationState.totalPages) {
        if (currentPage > paginationState.totalPages) {
            currentPage = maxOf(1, paginationState.totalPages)
        }
    }

    Scaffold(
        topBar = {
            EmpleadoTopBar(
                empleado = empleado,
                title = "Salidas de Inventario",
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Header con título y botón registrar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Salidas",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryColor
                )
                
                Button(
                    onClick = { showRegistroForm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = RSalida),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("📤 Registrar", color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
            
            // Barra de búsqueda y botón de filtros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = filters.searchText,
                    onValueChange = { 
                        filters = filters.copy(searchText = it)
                        currentPage = 1
                    },
                    label = { Text("Buscar movimiento...") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RSalida,
                        focusedLabelColor = RSalida
                    ),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        if (filters.searchText.isNotEmpty()) {
                            IconButton(onClick = { 
                                filters = filters.copy(searchText = "")
                                currentPage = 1
                            }) {
                                Text("✕", fontSize = 18.sp, color = TextSecondaryColor)
                            }
                        }
                    }
                )
                
                FilterButton(
                    onClick = { showFiltersModal = true },
                    activeFiltersCount = activeFiltersCount,
                    accentColor = RSalida
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Grid de movimientos dinámico
            if (paginationState.itemsInCurrentPage.isEmpty() && movimientos.isEmpty()) {
                MovimientoEmptyState(
                    emoji = "📤",
                    title = "No hay salidas registradas",
                    subtitle = "Registra la primera salida de productos",
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
            } else if (paginationState.itemsInCurrentPage.isEmpty() && filters.searchText.isNotBlank()) {
                MovimientoEmptyState(
                    emoji = "🔍",
                    title = "No se encontraron salidas",
                    subtitle = "Intenta con otros términos de búsqueda o ajusta los filtros",
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
            } else if (paginationState.itemsInCurrentPage.isEmpty()) {
                MovimientoEmptyState(
                    emoji = "🔍",
                    title = "No se encontraron resultados",
                    subtitle = "Intenta ajustar los filtros aplicados",
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(paginationState.itemsInCurrentPage) { movimiento ->
                        MovimientoCard(
                            movimiento = movimiento,
                            onClick = {
                                selectedMovimiento = movimiento
                                showDetailModal = true
                            }
                        )
                    }
                }
            }
            
            // Paginación
            PaginationControls(
                currentPage = currentPage,
                totalPages = paginationState.totalPages,
                onPageChange = { currentPage = it }
            )
        }
        
        // Modal de formulario de registro
        if (showRegistroForm) {
            RegistroSalidaModal(
                onDismiss = { showRegistroForm = false },
                onRegistrar = {
                    showRegistroForm = false
                    showRegistroDialog = true
                    // Recargar movimientos después de registrar
                    movimientoViewModel.cargarMovimientosPorTipo(TipoMovimiento.SALIDA)
                },
                movimientoViewModel = movimientoViewModel,
                empleado = empleado
            )
        }
        
        // Modal de filtros
        if (showFiltersModal) {
            MovimientoFiltersModal(
                filters = filters,
                onFiltersChange = { newFilters ->
                    filters = newFilters
                    currentPage = 1
                },
                onDismiss = { showFiltersModal = false },
                availableProducts = availableProducts,
                availableCategories = availableCategories,
                showTipoFilter = false,
                accentColor = RSalida
            )
        }
        
        // Modal de detalle del movimiento
        if (showDetailModal && selectedMovimiento != null) {
            MovimientoDetailModal(
                movimiento = selectedMovimiento!!,
                onDismiss = { 
                    showDetailModal = false
                    selectedMovimiento = null
                }
            )
        }
        
        // Diálogo de confirmación de registro exitoso
        if (showRegistroDialog) {
            AlertDialog(
                onDismissRequest = { showRegistroDialog = false },
                title = {
                    Text("Salida Registrada", fontWeight = FontWeight.Bold)
                },
                text = {
                    Text("La salida de producto se ha registrado correctamente.")
                },
                confirmButton = {
                    Button(
                        onClick = { showRegistroDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = RSalida)
                    ) {
                        Text("Aceptar", color = Color.White)
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

@Composable
private fun ProductoSalidaCard(
    producto: Producto,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .border(2.dp, BorderColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        onClick = onSelect
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen del producto
            ProductImage(
                imageUrl = producto.imagenUrl,
                productName = producto.nombre,
                modifier = Modifier.size(60.dp),
                fallbackEmoji = "📦"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Código del producto
            Text(
                producto.codigo,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryColor,
                fontWeight = FontWeight.Medium
            )
            
            // Precio
            Text(
                "S/ ${producto.precio}",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryColor,
                fontWeight = FontWeight.Bold
            )
            
            // Nombre del producto
            Text(
                producto.nombre,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimaryColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Stock
            Text(
                "Stock: ${producto.cantidad}",
                style = MaterialTheme.typography.bodySmall,
                color = if (producto.cantidad > 10) Color(0xFF34C759) else RedMarimon,
                fontWeight = FontWeight.Medium
            )
        }
    }
}