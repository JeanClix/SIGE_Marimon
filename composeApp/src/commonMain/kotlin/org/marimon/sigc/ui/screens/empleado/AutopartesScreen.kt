package org.marimon.sigc.ui.screens.empleado

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import org.marimon.sigc.model.Empleado
import org.marimon.sigc.ui.components.ProductImage
import org.marimon.sigc.model.Producto
import org.marimon.sigc.ui.icons.MarimonIcons
import org.marimon.sigc.viewmodel.AuthViewModel
import org.marimon.sigc.viewmodel.ProductoViewModel
import org.marimon.sigc.model.ProductoCreate
import org.marimon.sigc.ui.components.SimpleImageUploader

// Colores Marimon
private val RedPure = Color(0xFFFF0000)
private val BackgroundLight = Color(0xFFF5F5F5)
private val CardBackground = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF000000)
private val TextSecondary = Color(0xFF666666)
private val BorderColor = Color(0xFFE0E0E0)
private val GreenStock = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutopartesScreen(
    empleado: Empleado,
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var currentPage by remember { mutableIntStateOf(1) }
    var showRegistroDialog by remember { mutableStateOf(false) }
    var showEditarDialog by remember { mutableStateOf(false) }
    var showEliminarDialog by remember { mutableStateOf(false) }
    var productoAEditar by remember { mutableStateOf<Producto?>(null) }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }
    
    // ViewModel para manejar productos
    val productoViewModel = remember { ProductoViewModel() }
    val productos = productoViewModel.productos
    
    // Cargar productos al inicializar
    LaunchedEffect(Unit) {
        println("DEBUG: Cargando productos desde la base de datos")
        productoViewModel.cargarProductos()
    }
    
    // Filtrar productos por búsqueda
    val productosFiltrados = remember(productos, searchText) {
        if (searchText.isBlank()) {
            productos
        } else {
            productos.filter { producto ->
                producto.nombre.contains(searchText, ignoreCase = true) ||
                producto.codigo.contains(searchText, ignoreCase = true) ||
                producto.descripcion?.contains(searchText, ignoreCase = true) == true
            }
        }
    }
    
    // Paginación
    val itemsPerPage = 6
    val totalPages = if (productosFiltrados.isNotEmpty()) {
        (productosFiltrados.size + itemsPerPage - 1) / itemsPerPage
    } else {
        1
    }
    
    // Ajustar página actual si es mayor al total de páginas
    LaunchedEffect(totalPages) {
        if (currentPage > totalPages) {
            currentPage = maxOf(1, totalPages)
        }
    }
    
    val startIndex = (currentPage - 1) * itemsPerPage
    val endIndex = minOf(startIndex + itemsPerPage, productosFiltrados.size)
    val productosEnPagina = if (productosFiltrados.isNotEmpty()) {
        productosFiltrados.subList(startIndex, endIndex)
    } else {
        emptyList()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    TextButton(
                        onClick = onBack
                    ) {
                        Text(
                            text = "${MarimonIcons.Back} Volver",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                },
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
                                .background(Color(0xFF333333)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = empleado.nombre.firstOrNull()?.toString() ?: "E",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        
                        Column {
                            Text(
                                text = "Hotel",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Text(
                                text = empleado.nombre,
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = empleado.areaNombre,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                actions = {
                    // Botón de notificaciones
                    IconButton(
                        onClick = { }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🔔",
                                fontSize = 20.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RedPure
                )
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header con título y botón registrar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Autopartes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Button(
                    onClick = { 
                        println("DEBUG: Abriendo diálogo de registro")
                        showRegistroDialog = true 
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedPure
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = MarimonIcons.Add,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Registrar",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            // Buscador
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = {
                    Text(
                        text = "Buscar Producto",
                        color = TextSecondary
                    )
                },
                trailingIcon = {
                    Text(
                        text = MarimonIcons.Search,
                        fontSize = 20.sp
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = BorderColor,
                    unfocusedBorderColor = BorderColor
                ),
                singleLine = true
            )
            
            // Grid de productos dinámico
            if (productosEnPagina.isEmpty() && productos.isEmpty()) {
                // Estado de carga
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = RedPure,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cargando productos...",
                            color = TextSecondary,
                            fontSize = 16.sp
                        )
                    }
                }
            } else if (productosEnPagina.isEmpty() && searchText.isNotBlank()) {
                // No hay resultados de búsqueda
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = MarimonIcons.Search,
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No se encontraron productos",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Intenta con otros términos de búsqueda",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(productosEnPagina.size) { index ->
                        val producto = productosEnPagina[index]
                        ProductCard(
                            producto = producto,
                            onEdit = { 
                                println("DEBUG: Editar producto ${producto.nombre}")
                                productoAEditar = producto
                                showEditarDialog = true
                            },
                            onDelete = { 
                                println("DEBUG: Eliminar producto ${producto.nombre}")
                                productoAEliminar = producto
                                showEliminarDialog = true
                            }
                        )
                    }
                }
            }
            
            // Paginación
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { if (currentPage > 1) currentPage-- }
                ) {
                    Text(
                        text = "◀",
                        fontSize = 20.sp,
                        color = if (currentPage > 1) TextPrimary else TextSecondary
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "$currentPage de $totalPages",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                TextButton(
                    onClick = { if (currentPage < totalPages) currentPage++ }
                ) {
                    Text(
                        text = "▶",
                        fontSize = 20.sp,
                        color = if (currentPage < totalPages) TextPrimary else TextSecondary
                    )
                }
            }
            
            // Diálogo de registro de productos
            if (showRegistroDialog) {
                ProductoRegistroDialog(
                    onDismiss = { 
                        println("DEBUG: Cerrando diálogo de registro")
                        showRegistroDialog = false 
                    },
                    onConfirmar = { productoCreate ->
                        println("DEBUG: Registrando producto: ${productoCreate.nombre}")
                        productoViewModel.crearProducto(
                            producto = productoCreate,
                            onSuccess = {
                                println("DEBUG: Producto registrado exitosamente")
                                showRegistroDialog = false
                            },
                            onError = { error ->
                                println("DEBUG: Error al registrar producto: $error")
                            }
                        )
                    }
                )
            }
            
            // Diálogo de edición de productos
            if (showEditarDialog && productoAEditar != null) {
                ProductoEditarDialog(
                    producto = productoAEditar!!,
                    onDismiss = { 
                        println("DEBUG: Cerrando diálogo de edición")
                        showEditarDialog = false
                        productoAEditar = null
                    },
                    onConfirmar = { productoEditado ->
                        println("DEBUG: Editando producto: ${productoEditado.nombre}")
                        productoViewModel.editarProducto(
                            producto = productoEditado,
                            onSuccess = {
                                println("DEBUG: Producto editado exitosamente")
                                showEditarDialog = false
                                productoAEditar = null
                            },
                            onError = { error ->
                                println("DEBUG: Error al editar producto: $error")
                            }
                        )
                    }
                )
            }
            
            // Diálogo de confirmación de eliminación
            if (showEliminarDialog && productoAEliminar != null) {
                ProductoEliminarDialog(
                    producto = productoAEliminar!!,
                    onDismiss = { 
                        println("DEBUG: Cerrando diálogo de eliminación")
                        showEliminarDialog = false
                        productoAEliminar = null
                    },
                    onConfirmar = { productoParaEliminar ->
                        println("DEBUG: Eliminando producto: ${productoParaEliminar.nombre}")
                        productoViewModel.eliminarProducto(
                            productoId = productoParaEliminar.id,
                            onSuccess = {
                                println("DEBUG: Producto eliminado exitosamente")
                                showEliminarDialog = false
                                productoAEliminar = null
                            },
                            onError = { error ->
                                println("DEBUG: Error al eliminar producto: $error")
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ProductCard(
    producto: Producto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // Determinar emoji basado en el nombre del producto
    val emoji = when {
        producto.nombre.contains("aceite", ignoreCase = true) -> MarimonIcons.Oil
        producto.nombre.contains("llanta", ignoreCase = true) -> MarimonIcons.Tire
        producto.nombre.contains("filtro", ignoreCase = true) -> MarimonIcons.Filter
        producto.nombre.contains("freno", ignoreCase = true) -> MarimonIcons.Brake
        producto.nombre.contains("motor", ignoreCase = true) -> MarimonIcons.Engine
        producto.nombre.contains("bateria", ignoreCase = true) -> MarimonIcons.Battery
        producto.nombre.contains("luz", ignoreCase = true) -> MarimonIcons.Light
        else -> MarimonIcons.Tools // Icono por defecto
    }
    
    // Determinar color del borde basado en stock
    val borderColor = if (producto.cantidad > 10) GreenStock else RedPure
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
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
                imageUrl = producto.imagenUrl,
                productName = producto.nombre,
                modifier = Modifier.size(80.dp),
                fallbackEmoji = emoji
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Precio
            Text(
                text = "S/ ${producto.precio}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            // Nombre del producto
            Text(
                text = producto.nombre,
                fontSize = 13.sp,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            // Stock
            Text(
                text = "Stock: ${producto.cantidad}",
                fontSize = 11.sp,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Botón Editar
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF333333)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text(
                        text = "Editar",
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }
                
                // Botón Eliminar
                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedPure
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text(
                        text = "Eliminar",
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductoRegistroDialog(
    onDismiss: () -> Unit,
    onConfirmar: (ProductoCreate) -> Unit
) {
    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var especificaciones by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = MarimonIcons.Add,
                    fontSize = 24.sp
                )
                Text(
                    text = "Registrar Nuevo Producto",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = codigo,
                    onValueChange = { codigo = it },
                    label = { Text("Código *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPure,
                        focusedLabelColor = RedPure
                    )
                )
                
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPure,
                        focusedLabelColor = RedPure
                    )
                )
                
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPure,
                        focusedLabelColor = RedPure
                    )
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RedPure,
                            focusedLabelColor = RedPure
                        )
                    )
                    
                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = { cantidad = it },
                        label = { Text("Stock *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RedPure,
                            focusedLabelColor = RedPure
                        )
                    )
                }
                
                // Sección para imagen usando SimpleImageUploader
                SimpleImageUploader(
                    currentImageUrl = imagenUrl,
                    onImageUploaded = { newImageUrl ->
                        imagenUrl = newImageUrl ?: ""
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (codigo.isNotBlank() && nombre.isNotBlank() && precio.isNotBlank() && cantidad.isNotBlank()) {
                        val precioDouble = precio.toDoubleOrNull() ?: 0.0
                        val cantidadInt = cantidad.toIntOrNull() ?: 0
                        
                        val nuevoProducto = ProductoCreate(
                            codigo = codigo.trim(),
                            nombre = nombre.trim(),
                            descripcion = if (descripcion.isNotBlank()) descripcion.trim() else null,
                            especificaciones = if (especificaciones.isNotBlank()) especificaciones.trim() else null,
                            precio = precioDouble,
                            cantidad = cantidadInt,
                            imagenUrl = if (imagenUrl.isNotBlank()) imagenUrl.trim() else null,
                            activo = true
                        )
                        
                        println("DEBUG: Creando producto con imagen: ${nuevoProducto.imagenUrl}")
                        onConfirmar(nuevoProducto)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedPure),
                enabled = codigo.isNotBlank() && nombre.isNotBlank() && precio.isNotBlank() && cantidad.isNotBlank()
            ) {
                Text(
                    text = "${MarimonIcons.Add} Registrar",
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    color = TextSecondary
                )
            }
        },
        containerColor = Color.White,
        titleContentColor = TextPrimary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoEditarDialog(
    producto: Producto,
    onDismiss: () -> Unit,
    onConfirmar: (Producto) -> Unit
) {
    var codigo by remember { mutableStateOf(producto.codigo) }
    var nombre by remember { mutableStateOf(producto.nombre) }
    var descripcion by remember { mutableStateOf(producto.descripcion ?: "") }
    var especificaciones by remember { mutableStateOf(producto.especificaciones ?: "") }
    var precio by remember { mutableStateOf(producto.precio.toString()) }
    var cantidad by remember { mutableStateOf(producto.cantidad.toString()) }
    var imagenUrl by remember { mutableStateOf(producto.imagenUrl ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = MarimonIcons.Edit,
                    fontSize = 24.sp
                )
                Text(
                    text = "Editar Producto",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Código y Nombre
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = codigo,
                        onValueChange = { codigo = it },
                        label = { Text("Código *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RedPure,
                            focusedLabelColor = RedPure
                        )
                    )
                    
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RedPure,
                            focusedLabelColor = RedPure
                        )
                    )
                }
                
                // Descripción
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPure,
                        focusedLabelColor = RedPure
                    )
                )
                
                // Especificaciones
                OutlinedTextField(
                    value = especificaciones,
                    onValueChange = { especificaciones = it },
                    label = { Text("Especificaciones") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPure,
                        focusedLabelColor = RedPure
                    )
                )
                
                // Precio y Stock
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RedPure,
                            focusedLabelColor = RedPure
                        )
                    )
                    
                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = { cantidad = it },
                        label = { Text("Stock *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RedPure,
                            focusedLabelColor = RedPure
                        )
                    )
                }
                
                // Sección para imagen usando SimpleImageUploader
                SimpleImageUploader(
                    currentImageUrl = imagenUrl,
                    onImageUploaded = { newImageUrl ->
                        imagenUrl = newImageUrl ?: ""
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val precioDouble = precio.toDoubleOrNull() ?: 0.0
                    val cantidadInt = cantidad.toIntOrNull() ?: 0
                    
                    val productoEditado = Producto(
                        id = producto.id,
                        codigo = codigo.trim(),
                        nombre = nombre.trim(),
                        descripcion = if (descripcion.isNotBlank()) descripcion.trim() else null,
                        especificaciones = if (especificaciones.isNotBlank()) especificaciones.trim() else null,
                        precio = precioDouble,
                        cantidad = cantidadInt,
                        imagenUrl = if (imagenUrl.isNotBlank()) imagenUrl.trim() else null,
                        activo = producto.activo
                    )
                    
                    println("DEBUG: Editando producto con imagen: ${productoEditado.imagenUrl}")
                    onConfirmar(productoEditado)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedPure),
                enabled = codigo.isNotBlank() && nombre.isNotBlank() && precio.isNotBlank() && cantidad.isNotBlank()
            ) {
                Text(
                    text = "${MarimonIcons.Edit} Actualizar",
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    color = TextSecondary
                )
            }
        },
        containerColor = Color.White,
        titleContentColor = TextPrimary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoEliminarDialog(
    producto: Producto,
    onDismiss: () -> Unit,
    onConfirmar: (Producto) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "⚠️",
                    fontSize = 24.sp
                )
                Text(
                    text = "Eliminar Producto",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "¿Estás seguro de que deseas eliminar este producto?",
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                
                // Información del producto
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = BackgroundLight
                    ),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "📦 ${producto.nombre}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Código: ${producto.codigo}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Precio: S/ ${producto.precio}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Stock: ${producto.cantidad}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
                
                Text(
                    text = "Esta acción marcará el producto como inactivo y no se podrá deshacer.",
                    fontSize = 12.sp,
                    color = RedPure,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmar(producto) },
                colors = ButtonDefaults.buttonColors(containerColor = RedPure)
            ) {
                Text(
                    text = "Eliminar",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    color = TextSecondary
                )
            }
        },
        containerColor = Color.White,
        titleContentColor = TextPrimary
    )
}