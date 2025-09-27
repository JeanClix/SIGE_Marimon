package org.marimon.sigc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import org.marimon.sigc.data.model.User
import org.marimon.sigc.viewmodel.AuthViewModel
import org.marimon.sigc.viewmodel.ProductoViewModel
import org.marimon.sigc.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDashboard(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val productoViewModel = remember { ProductoViewModel() }
    
    // Cargar productos al iniciar la pantalla
    LaunchedEffect(Unit) {
        productoViewModel.cargarProductos()
    }
    
    println("DEBUG: EmployeeDashboard - Usuario actual: $currentUser")
    println("DEBUG: EmployeeDashboard - Rol: ${currentUser?.role}")
    
    Scaffold(
        topBar = {
            // Barra superior personalizada con foto y saludo
            EmployeeTopBar(
                user = currentUser,
                onLogout = {
                    println("DEBUG: EmployeeDashboard - Botón cerrar sesión presionado")
                    authViewModel.logout()
                    // No llamar onLogout() aquí, dejar que AppNavigation maneje la navegación
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filtro de productos
            ProductFilterSection()
            
            // Lista de productos en dos columnas (desde BD)
            ProductGridSection(productos = productoViewModel.productos)
        }
    }
}

@Composable
fun EmployeeTopBar(
    user: User?,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto del empleado (izquierda)
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = androidx.compose.foundation.shape.CircleShape
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👤",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Saludo con nombre del empleado (centro)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Hola, ${user?.firstName ?: "Empleado"}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Bienvenido al sistema",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            // Botón de cerrar sesión (derecha)
            TextButton(onClick = onLogout) {
                Text(
                    text = "Cerrar Sesión",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun ProductFilterSection() {
    var searchQuery by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Filtrar Productos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar productos...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Text("🔍", style = MaterialTheme.typography.bodyLarge)
                }
            )
        }
    }
}

@Composable
fun ProductGridSection(productos: List<Producto>) {
    // Filtrar solo productos activos
    val productosActivos = productos.filter { it.activo }
    
    if (productosActivos.isEmpty()) {
        // Mostrar mensaje si no hay productos
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📦",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No hay productos disponibles",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Los productos aparecerán aquí cuando estén disponibles",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
        return
    }
    
    // Dividir productos en pares para crear filas de dos columnas
    val productPairs = productosActivos.chunked(2)
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(productPairs) { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Primera columna
                ProductCard(
                    producto = pair[0],
                    modifier = Modifier.weight(1f)
                )
                
                // Segunda columna (si existe)
                if (pair.size > 1) {
                    ProductCard(
                        producto = pair[1],
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    // Espacio vacío si solo hay un producto en la fila
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    producto: Producto,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Código del producto
            Text(
                text = "Código: ${producto.codigo}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Nombre del producto
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Descripción del producto
            if (!producto.descripcion.isNullOrBlank()) {
                Text(
                    text = producto.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            // Precio y cantidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$${producto.precio}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Stock: ${producto.cantidad}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
