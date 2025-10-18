package org.marimon.sigc.ui.components.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.marimon.sigc.model.Producto
import org.marimon.sigc.model.Empleado
import org.marimon.sigc.ui.components.AutoCompleteProductField
import org.marimon.sigc.viewmodel.ProductoViewModel
import org.marimon.sigc.viewmodel.MovimientoViewModel
import org.marimon.sigc.model.MovimientoCreate
import org.marimon.sigc.model.TipoMovimiento

// --- Colores y Estilos ---
private val TextPrimaryColor = Color.Black
private val RSalida = Color(0xFFFF383C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroSalidaModal(
    onDismiss: () -> Unit,
    onRegistrar: () -> Unit,
    movimientoViewModel: MovimientoViewModel,
    empleado: Empleado,
    onNavigateToAutopartes: () -> Unit = {}
) {
    var cantidad by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf<Producto?>(null) }
    var searchText by remember { mutableStateOf("") }
    var showCreateProductDialog by remember { mutableStateOf(false) }
    var newProductName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    // ViewModels
    val productoViewModel = remember { ProductoViewModel() }
    val productos = productoViewModel.productos

    // Cargar productos al inicializar
    LaunchedEffect(Unit) {
        productoViewModel.cargarProductos()
    }

    // Función para registrar salida
    fun registrarSalida() {
        errorMessage = ""

        // Validaciones
        if (selectedProduct == null || selectedProduct?.id == 0) {
            errorMessage = "Debe seleccionar un producto válido"
            return
        }

        if (cantidad.isBlank() || cantidad.toIntOrNull() == null || cantidad.toInt() <= 0) {
            errorMessage = "La cantidad debe ser un número mayor a 0"
            return
        }

        // Validar stock disponible
        val cantidadSalida = cantidad.toInt()
        if (cantidadSalida > selectedProduct!!.cantidad) {
            errorMessage = "No hay suficiente stock. Disponible: ${selectedProduct!!.cantidad}"
            return
        }

        // Crear movimiento
        val movimiento = MovimientoCreate(
            tipo = TipoMovimiento.SALIDA,
            productoId = selectedProduct!!.id,
            empleadoId = empleado.id, // Usar el ID del empleado logueado
            cantidad = cantidadSalida,
            nota = "Salida registrada - Producto: ${selectedProduct!!.nombre} - Empleado: ${empleado.nombre}"
        )

        // Registrar en el ViewModel con callbacks
        movimientoViewModel.registrarMovimiento(
            movimiento = movimiento,
            onSuccess = {
                successMessage = "Se han registrado ${cantidadSalida} unidades del ${selectedProduct!!.nombre} en Inventario"
                onRegistrar()
            },
            onError = { error ->
                errorMessage = error
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Registrar Salida",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryColor
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mensaje de error
                if (errorMessage.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "⚠️ $errorMessage",
                            color = Color(0xFFD32F2F),
                            modifier = Modifier.padding(12.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Selector de Producto con Autocompletado
                Text(
                    "Producto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryColor
                )

                AutoCompleteProductField(
                    productos = productos.filter { it.cantidad > 0 }, // Solo productos con stock
                    selectedProduct = selectedProduct,
                    onProductSelected = {
                        selectedProduct = it
                        // Limpiar cantidad si el producto cambió
                        if (cantidad.toIntOrNull() ?: 0 > (it?.cantidad ?: 0)) {
                            cantidad = ""
                        }
                        errorMessage = ""
                    },
                    onCreateNewProduct = { productName ->
                        // Cerrar el modal y navegar a AutopartesScreen
                        onDismiss()
                        onNavigateToAutopartes()
                    },
                    searchText = searchText,
                    onSearchTextChange = { searchText = it },
                    focusedBorderColor = RSalida,
                    label = "Buscar producto con stock...",
                    placeholder = "Escribe el nombre o código del producto"
                )                // Advertencia si el producto tiene poco stock
                if (selectedProduct != null && selectedProduct?.cantidad != null && selectedProduct!!.cantidad <= 5) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF3E0)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "⚠️ Advertencia: Este producto tiene stock bajo (${selectedProduct!!.cantidad} unidades)",
                            color = Color(0xFFE65100),
                            modifier = Modifier.padding(12.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Cantidad
                Text(
                    "Cantidad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryColor
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) {
                                val newCantidad = it.toIntOrNull() ?: 0
                                if (selectedProduct == null || newCantidad <= selectedProduct!!.cantidad) {
                                    cantidad = it
                                    errorMessage = ""
                                } else {
                                    errorMessage = "No hay suficiente stock. Disponible: ${selectedProduct!!.cantidad}"
                                }
                            }
                        },
                        label = { Text("Ingresar Cantidad") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RSalida
                        ),
                        shape = RoundedCornerShape(12.dp),
                        isError = errorMessage.contains("cantidad") || errorMessage.contains("stock"),
                        supportingText = {
                            if (selectedProduct != null && selectedProduct?.cantidad != null) {
                                Text(
                                    "Stock disponible: ${selectedProduct!!.cantidad}",
                                    color = if (selectedProduct!!.cantidad > 5) Color.Gray else Color(0xFFE65100)
                                )
                            }
                        }
                    )

                    // Botones + y -
                    IconButton(
                        onClick = {
                            val current = cantidad.toIntOrNull() ?: 0
                            if (current > 0) {
                                cantidad = (current - 1).toString()
                                errorMessage = ""
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.LightGray, CircleShape)
                    ) {
                        Text("−", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = {
                            val current = cantidad.toIntOrNull() ?: 0
                            val maxStock = selectedProduct?.cantidad ?: 0
                            if (current < maxStock) {
                                cantidad = (current + 1).toString()
                                errorMessage = ""
                            } else {
                                errorMessage = "No hay suficiente stock. Disponible: $maxStock"
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (selectedProduct != null && (cantidad.toIntOrNull() ?: 0) < selectedProduct!!.cantidad)
                                    RSalida else Color.Gray,
                                CircleShape
                            ),
                        enabled = selectedProduct != null && (cantidad.toIntOrNull() ?: 0) < selectedProduct!!.cantidad
                    ) {
                        Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // Información adicional si hay producto seleccionado
                if (selectedProduct != null && selectedProduct?.id != 0) {
                    Text(
                        "💡 Tip: El stock de este producto se reducirá automáticamente después del registro.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar", color = Color.Black)
                }

                Button(
                    onClick = { registrarSalida() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedProduct?.id != 0 && cantidad.isNotEmpty() && errorMessage.isEmpty())
                            RSalida else Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = selectedProduct?.id != 0 && cantidad.isNotEmpty() && errorMessage.isEmpty()
                ) {
                    Text("📤 Registrar", color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
        },
        dismissButton = {},
        containerColor = Color.White
    )

    // Diálogo para crear nuevo producto
    if (showCreateProductDialog) {
        AlertDialog(
            onDismissRequest = {
                showCreateProductDialog = false
                newProductName = ""
            },
            title = { Text("Crear Nuevo Producto") },
            text = {
                Column {
                    Text("¿Deseas crear el producto \"$newProductName\"?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Nota: Para registrar salidas, el producto debe tener stock. Es recomendable crear el producto y registrar una entrada primero.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // TODO: Implementar creación rápida de producto
                        showCreateProductDialog = false
                        errorMessage = "Función de creación rápida en desarrollo. Por favor, crea el producto desde la gestión de productos."
                    }
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateProductDialog = false
                        newProductName = ""
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}