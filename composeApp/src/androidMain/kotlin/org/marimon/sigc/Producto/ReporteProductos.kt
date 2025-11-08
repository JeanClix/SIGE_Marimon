package org.marimon.sigc.Producto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.marimon.sigc.model.Producto
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

@Composable
fun ReporteProductosDialog(
    productos: List<Producto>,
    onDismiss: () -> Unit,
    onRefresh: (onComplete: () -> Unit) -> Unit
) {
    var ordenarPorStock by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    // Timeout de seguridad para resetear el estado de carga
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            kotlinx.coroutines.delay(5000) // 5 segundos de timeout
            isRefreshing = false
        }
    }

    // Ordenar productos por stock si el filtro está activo
    val productosFiltrados = remember(ordenarPorStock, productos) {
        if (ordenarPorStock) {
            productos.sortedByDescending { it.cantidad }
        } else {
            productos
        }
    }

    // Calcular estadísticas reales con productos filtrados
    val productosActivos = productosFiltrados.filter { it.activo }
    val productosInactivos = productosFiltrados.filter { !it.activo }
    val totalStock = productosActivos.sumOf { it.cantidad }
    val totalValor = productosActivos.sumOf { it.precio * it.cantidad }

    // Producto destacado: cambia según el filtro
    val productoDestacado = if (ordenarPorStock) {
        // Si está ordenado, tomar el primero (mayor stock > 0)
        productosActivos.firstOrNull { it.cantidad > 0 }
    } else {
        // Si no está ordenado, tomar el de menor stock > 0
        productosActivos.filter { it.cantidad > 0 }.minByOrNull { it.cantidad }
    }

    val porcentajeDestacado = if (totalStock > 0 && productoDestacado != null) {
        ((productoDestacado.cantidad.toFloat() / totalStock) * 100).toInt()
    } else 0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header con fondo rojo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE53935))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color.White
                            )
                        }

                        Text(
                            text = "Reporte de Productos",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Spacer para balancear el layout
                        Spacer(modifier = Modifier.width(48.dp))
                    }
                }

                // Botón de Ordenar por Stock
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { ordenarPorStock = !ordenarPorStock },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (ordenarPorStock) Color(0xFF2196F3) else Color.Gray
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Ordenar",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (ordenarPorStock) "Mayor Stock ↓" else "Ordenar Stock")
                    }

                    Button(
                        onClick = {
                            isRefreshing = true
                            onRefresh {
                                isRefreshing = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        ),
                        enabled = !isRefreshing
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Actualizar",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isRefreshing) "Actualizando..." else "Actualizar")
                    }
                }

                // Gráfico de barras
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Header del producto destacado
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (ordenarPorStock) Color(0xFF2196F3) else Color(0xFFFF9800),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFFFA726), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📦", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (ordenarPorStock) "📈 Mayor:" else "📉 Menor:",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                    Text(
                                        text = productoDestacado?.nombre ?: "Sin productos",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "Stock: ${productoDestacado?.cantidad ?: 0}",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Gráfico de barras con productos filtrados
                        BarChartProductos(productos = productosFiltrados)
                    }
                }

                // Gráfico de torta
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Distribución de Stock",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Etiqueta de estadísticas
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFE53935), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Total Stock: $totalStock",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF4CAF50), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Valor: S/ ${String.format("%.2f", totalValor)}",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Información de filtrado
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF2196F3), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Activos: ${productosActivos.size}",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF757575), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Inactivos: ${productosInactivos.size}",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Mensaje si no hay inactivos
                            if (productosInactivos.isEmpty()) {
                                Text(
                                    text = "✓ Todos los productos están activos",
                                    fontSize = 11.sp,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Gráfico de torta con productos filtrados
                        PieChartProductos(productos = productosFiltrados)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Información adicional
                        Text(
                            text = "📦 Análisis de inventario actual",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BarChartProductos(productos: List<Producto>) {
    // Filtrar solo productos activos
    val productosActivos = productos.filter { it.activo }

    if (productosActivos.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay datos para mostrar", color = Color.Gray)
        }
    } else {
        // Tomar los primeros 8 productos activos (ya vienen ordenados si se activó el filtro)
        val productosParaGrafico = productosActivos.take(8)
        val maxCantidad = productosParaGrafico.maxOfOrNull { it.cantidad } ?: 1

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Gráfico de barras
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                productosParaGrafico.forEachIndexed { index, producto ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        val barHeightPercentage = (producto.cantidad.toFloat() / maxCantidad)

                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .fillMaxHeight(barHeightPercentage)
                                .background(
                                    color = when {
                                        index == 0 -> Color(0xFFE53935) // Rojo
                                        index % 2 == 0 -> Color(0xFF2196F3) // Azul
                                        else -> Color(0xFF424242) // Gris oscuro
                                    },
                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                    }
                }
            }

            // Etiquetas del eje X (nombres abreviados)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                productosParaGrafico.forEach { producto ->
                    Text(
                        text = producto.nombre.take(6),
                        fontSize = 9.sp,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }

            // Mostrar cantidades
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                productosParaGrafico.forEach { producto ->
                    Text(
                        text = "${producto.cantidad}",
                        fontSize = 10.sp,
                        color = Color(0xFF2196F3),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun PieChartProductos(productos: List<Producto>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        if (productos.isEmpty()) {
            Text("No hay datos para mostrar", color = Color.Gray)
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Calcular totales para el gráfico de torta (basado en conteo de productos, no stock)
                val productosActivos = productos.filter { it.activo == true }
                val productosInactivos = productos.filter { it.activo == false }
                val totalProductos = productos.size

                val activePercentage = if (totalProductos > 0) (productosActivos.size.toFloat() / totalProductos) else 0f
                val inactivePercentage = if (totalProductos > 0) (productosInactivos.size.toFloat() / totalProductos) else 0f

                val totalStockActivos = productosActivos.sumOf { it.cantidad }
                val totalStockInactivos = productosInactivos.sumOf { it.cantidad }

                Canvas(
                    modifier = Modifier
                        .size(180.dp)
                ) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val radius = size.minDimension / 2

                    var startAngle = -90f

                    // Dibujar siempre, incluso si es 100% activos o 100% inactivos
                    if (activePercentage > 0) {
                        // Segmento de productos activos (rojo)
                        val activeSweepAngle = 360f * activePercentage
                        drawArc(
                            color = Color(0xFFE53935),
                            startAngle = startAngle,
                            sweepAngle = if (activeSweepAngle > 0) activeSweepAngle else 0.1f,
                            useCenter = true,
                            topLeft = Offset(centerX - radius, centerY - radius),
                            size = Size(radius * 2, radius * 2)
                        )
                        startAngle += activeSweepAngle
                    }

                    if (inactivePercentage > 0) {
                        // Segmento de productos inactivos (gris oscuro)
                        val inactiveSweepAngle = 360f * inactivePercentage
                        drawArc(
                            color = Color(0xFF424242),
                            startAngle = startAngle,
                            sweepAngle = if (inactiveSweepAngle > 0) inactiveSweepAngle else 0.1f,
                            useCenter = true,
                            topLeft = Offset(centerX - radius, centerY - radius),
                            size = Size(radius * 2, radius * 2)
                        )
                    }

                    // Círculo interior blanco para efecto de dona
                    drawCircle(
                        color = Color.White,
                        radius = radius * 0.6f,
                        center = Offset(centerX, centerY)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Leyenda con cantidades reales
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Siempre mostrar ambos porcentajes
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        LegendItem(
                            color = Color(0xFFE53935),
                            label = "Activos: ${String.format("%.1f", activePercentage * 100)}% (${productosActivos.size} productos)"
                        )
                        LegendItem(
                            color = Color(0xFF424242),
                            label = "Inactivos: ${String.format("%.1f", inactivePercentage * 100)}% (${productosInactivos.size} productos)"
                        )
                    }

                    // Mostrar stocks
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Stock Activo: $totalStockActivos",
                            fontSize = 11.sp,
                            color = Color(0xFFE53935),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Stock Inactivo: $totalStockInactivos",
                            fontSize = 11.sp,
                            color = Color(0xFF424242),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Mensaje de estado
                    if (productosInactivos.isEmpty()) {
                        Text(
                            text = "✓ Todos los productos están activos",
                            fontSize = 11.sp,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "⚠️ Hay ${productosInactivos.size} producto(s) inactivo(s)",
                            fontSize = 11.sp,
                            color = Color(0xFFFF9800),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}


