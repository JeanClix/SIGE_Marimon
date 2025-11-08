package org.marimon.sigc

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.marimon.sigc.viewmodel.ProductoViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import org.marimon.sigc.model.Producto
import org.marimon.sigc.model.ProductoCreate
import org.marimon.sigc.Producto.CrearProductoDialog
import org.marimon.sigc.Producto.EditarProductoDialog
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import org.marimon.sigc.Producto.ReporteProductosDialog
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Preview
@Composable
fun AdminRProductoPreview() {
    AdminRProductoApp()
}

@Composable
fun AdminRProductoApp(
    currentRoute: String = "circulo",
    onNavigate: (String) -> Unit = {}
) {
    val productoViewModel = remember { ProductoViewModel() }
    LaunchedEffect(Unit) {
        productoViewModel.cargarProductos()
    }
    // Obtener TODOS los productos (activos e inactivos) desde la BD
    val todosLosProductos: List<Producto> = productoViewModel.productos
    // Filtrar solo los activos para mostrar en la lista
    val productosActivos = todosLosProductos.filter { it.activo }

    AdminScreenLayout(
        title = "Registro de Productos",
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) {
        ProductoListScreen(
            productos = productosActivos,
            todosLosProductos = todosLosProductos, // Pasar todos para el reporte
            productoViewModel = productoViewModel
        )
    }
}

@Composable
fun ProductoListScreen(
    productos: List<Producto>, // Solo productos activos para mostrar en la lista
    todosLosProductos: List<Producto>, // TODOS los productos (activos e inactivos) para el reporte
    productoViewModel: ProductoViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var productoAEditar by remember { mutableStateOf<Producto?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }
    var mensaje by remember { mutableStateOf("") }
    var mostrarMensaje by remember { mutableStateOf(false) }
    var showReporte by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { showReporte = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text("Visualizar Reporte", color = Color.White)
            }

            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Text("Registrar", color = Color.White)
            }

            productos.forEach { producto ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Imagen del producto
                        if (producto.imagenUrl != null && producto.imagenUrl!!.isNotBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(producto.imagenUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Foto de ${producto.nombre}",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = Color.LightGray
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = producto.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Código: ${producto.codigo}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = producto.descripcion ?: "Sin descripción",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                color = if (producto.descripcion != null) Color.Unspecified else Color.Gray
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "S/ ${String.format("%.2f", producto.precio)}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color(0xFF4CAF50)
                                )
                                Text(
                                    text = "Stock: ${producto.cantidad}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (producto.cantidad > 10) Color(0xFF4CAF50) else Color(0xFFFF9800)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Botones de acción
                        if (producto.activo) {
                            Column {
                                Text(
                                    text = if (producto.activo) "Activo" else "Inactivo",
                                    color = if (producto.activo) Color(0xFF388E3C) else Color.Red,
                                    style = MaterialTheme.typography.labelMedium
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    // Botón editar
                                    IconButton(
                                        onClick = {
                                            productoAEditar = producto
                                            showEditDialog = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = Color(0xFF2196F3),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    // Botón eliminar
                                    IconButton(
                                        onClick = {
                                            productoAEliminar = producto
                                            showDeleteDialog = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = Color(0xFFE53935),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "Inactivo",
                                color = Color.Red,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {
            CrearProductoDialog(
                onDismiss = { showDialog = false },
                onConfirm = { codigo, nombre, descripcion, especificaciones, precio, cantidad, urlImagen ->
                    val nuevoProducto = ProductoCreate(
                        codigo = codigo,
                        nombre = nombre,
                        descripcion = descripcion,
                        especificaciones = especificaciones,
                        precio = precio,
                        cantidad = cantidad,
                        imagenUrl = urlImagen,
                        activo = true
                    )
                    productoViewModel.crearProducto(
                        producto = nuevoProducto,
                        onSuccess = {
                            showDialog = false
                            mensaje = "✅ Producto creado exitosamente"
                            mostrarMensaje = true
                        },
                        onError = { error ->
                            mensaje = "❌ Error: $error"
                            mostrarMensaje = true
                        }
                    )
                }
            )
        }

        // Diálogo editar producto
        if (showEditDialog && productoAEditar != null) {
            EditarProductoDialog(
                producto = productoAEditar!!,
                onDismiss = {
                    showEditDialog = false
                    productoAEditar = null
                },
                onConfirm = { productoEditado ->
                    productoViewModel.editarProducto(
                        producto = productoEditado,
                        onSuccess = {
                            showEditDialog = false
                            productoAEditar = null
                            mensaje = "✅ Producto actualizado exitosamente"
                            mostrarMensaje = true
                        },
                        onError = { error ->
                            mensaje = "❌ Error: $error"
                            mostrarMensaje = true
                        }
                    )
                }
            )
        }

        // Diálogo confirmar eliminación
        if (showDeleteDialog && productoAEliminar != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    productoAEliminar = null
                },
                title = { Text("Confirmar eliminación") },
                text = {
                    Text("¿Estás seguro de que deseas eliminar ${productoAEliminar!!.nombre}?\n\nEsta acción marcará el producto como inactivo.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            productoViewModel.eliminarProducto(
                                productoId = productoAEliminar!!.id,
                                onSuccess = {
                                    showDeleteDialog = false
                                    productoAEliminar = null
                                    mensaje = "✅ Producto eliminado exitosamente"
                                    mostrarMensaje = true
                                },
                                onError = { error ->
                                    mensaje = "❌ Error: $error"
                                    mostrarMensaje = true
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                    ) {
                        Text("Eliminar", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                            productoAEliminar = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Cancelar", color = Color.White)
                    }
                }
            )
        }

        if (mostrarMensaje) {
            LaunchedEffect(mostrarMensaje) {
                delay(3000)
                mostrarMensaje = false
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (mensaje.contains("exitosamente")) Color(0xFF4CAF50) else Color(0xFFE53935)
                )
            ) {
                Text(
                    text = mensaje,
                    modifier = Modifier.padding(16.dp),
                    color = Color.White
                )
            }
        }

        if (showReporte) {
            ReporteProductosDialog(
                productos = todosLosProductos, // Usar TODOS los productos para el reporte
                onDismiss = { showReporte = false },
                onRefresh = { onComplete ->
                    // Recargar los productos desde la base de datos
                    productoViewModel.cargarProductos {
                        onComplete()
                    }
                }
            )
        }
    }
}
