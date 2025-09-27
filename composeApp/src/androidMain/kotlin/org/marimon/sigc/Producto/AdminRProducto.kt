package org.marimon.sigc

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay

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
    val productos: List<Producto> = productoViewModel.productos
    
    AdminScreenLayout(
        title = "Registro de Productos",
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) {
        ProductoListScreen(
            productos = productos,
            productoViewModel = productoViewModel
        )
    }
}

@Composable
fun ProductoListScreen(
    productos: List<Producto>,
    productoViewModel: ProductoViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var mostrarMensaje by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Text("Registrar Producto", color = Color.White)
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
    }
}
