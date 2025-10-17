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
private val REntrada = Color(0xFFFF383C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroEntradaModal(
    onDismiss: () -> Unit,
    onRegistrar: () -> Unit,
    movimientoViewModel: MovimientoViewModel,
    empleado: Empleado
) {
    var cantidad by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf<Producto?>(null) }
    var searchText by remember { mutableStateOf("") }
    var expandedCategoria by remember { mutableStateOf(false) }
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
    
    val categorias = listOf("Filtros", "Frenos", "Motor", "Suspensión", "Eléctrico")
    
    // Función para registrar entrada
    fun registrarEntrada() {
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
        
        // Crear movimiento
        val movimiento = MovimientoCreate(
            tipo = TipoMovimiento.ENTRADA,
            productoId = selectedProduct!!.id,
            empleadoId = empleado.id, // Usar el ID del empleado logueado
            cantidad = cantidad.toInt(),
            nota = "Entrada registrada - Producto: ${selectedProduct!!.nombre} - Empleado: ${empleado.nombre}"
        )
        
        // Registrar en el ViewModel con callbacks
        movimientoViewModel.registrarMovimiento(
            movimiento = movimiento,
            onSuccess = {
                successMessage = "Se han registrado ${cantidad.toInt()} unidades del ${selectedProduct!!.nombre} en Inventario"
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
                "Registrar Entrada",
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
                
                // Categoría
                Text(
                    "Categoría",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryColor
                )
                
                ExposedDropdownMenuBox(
                    expanded = expandedCategoria,
                    onExpandedChange = { expandedCategoria = !expandedCategoria }
                ) {
                    OutlinedTextField(
                        value = categoria.ifEmpty { "Seleccionar Categoría" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Seleccionar Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = REntrada
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategoria,
                        onDismissRequest = { expandedCategoria = false }
                    ) {
                        categorias.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    categoria = cat
                                    expandedCategoria = false
                                }
                            )
                        }
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
                    productos = productos,
                    selectedProduct = selectedProduct,
                    onProductSelected = { selectedProduct = it },
                    onCreateNewProduct = { productName ->
                        newProductName = productName
                        showCreateProductDialog = true
                    },
                    searchText = searchText,
                    onSearchTextChange = { searchText = it },
                    focusedBorderColor = REntrada,
                    label = "Buscar producto...",
                    placeholder = "Escribe el nombre o código del producto"
                )
                
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
                                cantidad = it
                                errorMessage = "" // Limpiar error al cambiar
                            }
                        },
                        label = { Text("Ingresar Cantidad") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = REntrada
                        ),
                        shape = RoundedCornerShape(12.dp),
                        isError = errorMessage.contains("cantidad")
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
                            cantidad = (current + 1).toString()
                            errorMessage = ""
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(REntrada, CircleShape)
                    ) {
                        Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                
                // Información adicional si hay producto seleccionado
                if (selectedProduct != null && selectedProduct?.id != 0) {
                    Text(
                        "💡 Tip: El stock actual de este producto se incrementará automáticamente.",
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
                    onClick = { registrarEntrada() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedProduct?.id != 0 && categoria.isNotEmpty() && cantidad.isNotEmpty()) 
                            REntrada else Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = selectedProduct?.id != 0 && categoria.isNotEmpty() && cantidad.isNotEmpty()
                ) {
                    Text("📦 Registrar", color = Color.White, fontWeight = FontWeight.Medium)
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
                        "Nota: Se creará con información básica. Podrás editarlo después desde la gestión de productos.",
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