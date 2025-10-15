package org.marimon.sigc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import org.marimon.sigc.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteProductField(
    productos: List<Producto>,
    selectedProduct: Producto?,
    onProductSelected: (Producto) -> Unit,
    onCreateNewProduct: (String) -> Unit,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    isLoading: Boolean = false,
    focusedBorderColor: Color = Color(0xFFFF383C),
    modifier: Modifier = Modifier,
    label: String = "Buscar producto...",
    placeholder: String = "Escribe el nombre o código del producto"
) {
    var expanded by remember { mutableStateOf(false) }
    
    // Filtrar productos por búsqueda
    val filteredProducts = remember(productos, searchText) {
        if (searchText.isBlank()) {
            productos.take(10) // Mostrar solo los primeros 10 si no hay búsqueda
        } else {
            productos.filter { producto ->
                producto.nombre.contains(searchText, ignoreCase = true) ||
                producto.codigo.contains(searchText, ignoreCase = true)
            }.take(10) // Limitar a 10 resultados
        }
    }
    
    // Expandir automáticamente cuando hay texto y hay resultados
    LaunchedEffect(searchText, filteredProducts) {
        expanded = searchText.isNotBlank() && filteredProducts.isNotEmpty()
    }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedProduct?.nombre ?: searchText,
                onValueChange = { newValue ->
                    onSearchTextChange(newValue)
                    // Si se seleccionó un producto y el usuario empieza a escribir, limpiar selección
                    if (selectedProduct != null && newValue != selectedProduct.nombre) {
                        onProductSelected(Producto(
                            id = 0, codigo = "", nombre = "", descripcion = null,
                            especificaciones = null, precio = 0.0, cantidad = 0,
                            imagenUrl = null, activo = true
                        ))
                    }
                },
                label = { Text(label) },
                placeholder = { Text(placeholder) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = focusedBorderColor,
                    focusedLabelColor = focusedBorderColor
                ),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = focusedBorderColor
                            )
                        }
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                },
                readOnly = selectedProduct != null
            )

            // Menú desplegable con productos
            if (expanded && (filteredProducts.isNotEmpty() || searchText.isNotBlank())) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .exposedDropdownSize()
                        .heightIn(max = 300.dp),
                    properties = PopupProperties(focusable = false)
                ) {
                    // Mostrar productos filtrados
                    filteredProducts.forEach { producto ->
                        ProductDropdownItem(
                            producto = producto,
                            onClick = {
                                onProductSelected(producto)
                                onSearchTextChange(producto.nombre)
                                expanded = false
                            }
                        )
                    }
                    
                    // Opción para crear nuevo producto si no hay coincidencias exactas y hay texto
                    if (searchText.isNotBlank() && filteredProducts.none { it.nombre.equals(searchText, ignoreCase = true) }) {
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        "✨ Crear nuevo producto",
                                        fontWeight = FontWeight.Bold,
                                        color = focusedBorderColor
                                    )
                                    Text(
                                        "\"$searchText\"",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            },
                            onClick = {
                                onCreateNewProduct(searchText)
                                expanded = false
                            },
                            leadingIcon = {
                                Text("➕", fontSize = 16.sp)
                            }
                        )
                    }
                    
                    // Mensaje si no hay resultados
                    if (filteredProducts.isEmpty() && searchText.isNotBlank()) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "No se encontraron productos",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            },
                            onClick = { },
                            enabled = false
                        )
                    }
                }
            }
        }
        
        // Mostrar información del producto seleccionado
        if (selectedProduct != null && selectedProduct.id != 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = focusedBorderColor.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "✅ Producto seleccionado",
                            fontSize = 12.sp,
                            color = focusedBorderColor,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${selectedProduct.codigo} - ${selectedProduct.nombre}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Stock actual: ${selectedProduct.cantidad} | S/ ${selectedProduct.precio}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            onProductSelected(Producto(
                                id = 0, codigo = "", nombre = "", descripcion = null,
                                especificaciones = null, precio = 0.0, cantidad = 0,
                                imagenUrl = null, activo = true
                            ))
                            onSearchTextChange("")
                        }
                    ) {
                        Text("✖", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductDropdownItem(
    producto: Producto,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Column {
                Text(
                    producto.nombre,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        producto.codigo,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        "Stock: ${producto.cantidad}",
                        fontSize = 12.sp,
                        color = if (producto.cantidad > 10) Color(0xFF4CAF50) else Color(0xFFFF5722)
                    )
                }
            }
        },
        onClick = onClick,
        leadingIcon = {
            ProductImage(
                imageUrl = producto.imagenUrl,
                productName = producto.nombre,
                modifier = Modifier.size(32.dp),
                fallbackEmoji = "📦"
            )
        }
    )
}

/**
 * Función utilitaria para validar si un producto existe en la lista
 */
fun validateProductExists(productos: List<Producto>, searchText: String): Producto? {
    return productos.find { producto ->
        producto.nombre.equals(searchText, ignoreCase = true) ||
        producto.codigo.equals(searchText, ignoreCase = true)
    }
}

/**
 * Función utilitaria para obtener productos más populares (por stock o uso frecuente)
 */
fun getPopularProducts(productos: List<Producto>, limit: Int = 5): List<Producto> {
    return productos
        .filter { it.cantidad > 0 }
        .sortedByDescending { it.cantidad }
        .take(limit)
}