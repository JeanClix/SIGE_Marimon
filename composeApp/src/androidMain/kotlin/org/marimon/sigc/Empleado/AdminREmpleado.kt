package org.marimon.sigc.Empleado

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.marimon.sigc.viewmodel.EmpleadoViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import org.marimon.sigc.model.Empleado
import org.marimon.sigc.model.EmpleadoCreate
import org.marimon.sigc.model.Area

@Preview
@Composable
fun AdminREmpleadoPreview() {
    AdminREmpleadoApp()
}

@Composable
fun AdminREmpleadoApp(
    currentRoute: String = "user",
    onNavigate: (String) -> Unit = {}
) {
    val empleadoViewModel = remember { EmpleadoViewModel() }
    LaunchedEffect(Unit) { 
        empleadoViewModel.cargarEmpleados()
        empleadoViewModel.cargarAreas()
    }
    val empleados: List<Empleado> = empleadoViewModel.empleados
    val areas = empleadoViewModel.areas
    _root_ide_package_.org.marimon.sigc.AdminScreenLayout(
        title = "Registro de Empleados",
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) {
        EmpleadoListScreen(
            empleados = empleados,
            areas = areas,
            empleadoViewModel = empleadoViewModel
        )
    }
}

@Composable
fun EmpleadoListScreen(
    empleados: List<Empleado>, 
    areas: List<Area>,
    empleadoViewModel: EmpleadoViewModel
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear empleado")
            }
            empleados.forEach { empleado ->
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
                        // Imagen del empleado con Coil
                        if (empleado.imagenUrl != null && empleado.imagenUrl!!.isNotBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(empleado.imagenUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Foto de ${empleado.nombre}",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(50.dp),
                                tint = Color.LightGray
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(empleado.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(empleado.emailCorporativo, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                            Text(empleado.areaNombre, style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (empleado.activo) "Activo" else "Inactivo",
                            color = if (empleado.activo) Color(0xFF388E3C) else Color.Red,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
        if (showDialog) {
            CrearEmpleadoDialog(
                areas = areas,
                onDismiss = { showDialog = false },
                onConfirm = { nombre, email, areaId, urlImagen ->
                    val nuevoEmpleado = EmpleadoCreate(
                        nombre = nombre,
                        emailCorporativo = email,
                        areaId = areaId,
                        imagenUrl = urlImagen,
                        activo = true
                    )
                    empleadoViewModel.crearEmpleado(
                        empleado = nuevoEmpleado,
                        onSuccess = {
                            showDialog = false
                            mensaje = "✅ Empleado creado exitosamente"
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
                kotlinx.coroutines.delay(3000)
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