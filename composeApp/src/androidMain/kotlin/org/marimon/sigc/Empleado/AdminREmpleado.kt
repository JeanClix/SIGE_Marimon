package org.marimon.sigc.Empleado

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

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
    var showEditDialog by remember { mutableStateOf(false) }
    var empleadoAEditar by remember { mutableStateOf<Empleado?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showSuccessCreateDialog by remember { mutableStateOf(false) }
    var empleadoAEliminar by remember { mutableStateOf<Empleado?>(null) }
    var empleadoCreado by remember { mutableStateOf<Empleado?>(null) }
    var mensaje by remember { mutableStateOf("") }
    var mostrarMensaje by remember { mutableStateOf(false) }
    var esCreacion by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Botón Crear empleado (fijo arriba)
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Text("Crear empleado", color = Color.White)
            }

            // Lista con scroll
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(empleados) { empleado ->
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
                            Column {
                                Text(
                                    text = if (empleado.activo) "Activo" else "Inactivo",
                                    color = if (empleado.activo) Color(0xFF388E3C) else Color.Red,
                                    style = MaterialTheme.typography.labelMedium
                                )

                                if (empleado.activo) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                empleadoAEditar = empleado
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

                                        IconButton(
                                            onClick = {
                                                empleadoAEliminar = empleado
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
                            }
                        }
                    }
                }
            }
        }

        // Diálogo crear empleado
        if (showDialog) {
            CrearEmpleadoDialog(
                areas = areas,
                onDismiss = { showDialog = false },
                onConfirm = { nombre, email, areaId, urlImagen, password ->
                    val nuevoEmpleado = EmpleadoCreate(
                        nombre = nombre,
                        emailCorporativo = email,
                        areaId = areaId,
                        imagenUrl = urlImagen,
                        activo = true,
                        password = password
                    )
                    empleadoViewModel.crearEmpleado(
                        empleado = nuevoEmpleado,
                        onSuccess = {
                            showDialog = false
                            esCreacion = true
                            // Crear un empleado temporal para mostrar
                            empleadoCreado = Empleado(
                                id = 0,
                                nombre = nombre,
                                emailCorporativo = email,
                                areaId = areaId,
                                areaNombre = areas.find { it.id == areaId }?.nombre ?: "",
                                activo = true,
                                imagenUrl = urlImagen,
                                password = password
                            )
                            showSuccessCreateDialog = true
                        },
                        onError = { error ->
                            mensaje = "❌ Error: $error"
                            mostrarMensaje = true
                        }
                    )
                }
            )
        }

        // Diálogo editar empleado
        if (showEditDialog && empleadoAEditar != null) {
            EditarEmpleadoDialog(
                empleado = empleadoAEditar!!,
                areas = areas,
                onDismiss = {
                    showEditDialog = false
                    empleadoAEditar = null
                },
                onConfirm = { empleadoEditado ->
                    empleadoViewModel.editarEmpleado(
                        empleado = empleadoEditado,
                        onSuccess = {
                            showEditDialog = false
                            esCreacion = false
                            empleadoCreado = empleadoEditado
                            empleadoAEditar = null
                            showSuccessCreateDialog = true
                        },
                        onError = { error ->
                            mensaje = "❌ Error: $error"
                            mostrarMensaje = true
                        }
                    )
                }
            )
        }

        // Diálogo confirmar eliminación optimizado
        if (showDeleteDialog && empleadoAEliminar != null) {
            DialogoConfirmarEliminacion(
                empleado = empleadoAEliminar!!,
                onConfirmar = {
                    empleadoViewModel.eliminarEmpleado(
                        empleadoAEliminar!!.id,
                        onSuccess = {
                            showDeleteDialog = false
                            showSuccessDialog = true
                        },
                        onError = { error ->
                            mensaje = "❌ Error: $error"
                            mostrarMensaje = true
                        }
                    )
                },
                onCancelar = {
                    showDeleteDialog = false
                    empleadoAEliminar = null
                }
            )
        }

        // Diálogo de éxito al eliminar optimizado
        if (showSuccessDialog) {
            DialogoEliminacionExitosa {
                showSuccessDialog = false
                empleadoAEliminar = null
            }
        }

        // Diálogo de éxito al crear/actualizar empleado optimizado
        if (showSuccessCreateDialog && empleadoCreado != null) {
            DialogoExitoEmpleado(
                empleado = empleadoCreado!!,
                esCreacion = esCreacion,
                onDismiss = {
                    showSuccessCreateDialog = false
                    empleadoCreado = null
                }
            )
        }

        if (mostrarMensaje) {
            LaunchedEffect(mostrarMensaje) {
                kotlinx.coroutines.delay(3000)
                mostrarMensaje = false
            }
            MensajeEstado(
                mensaje = mensaje,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}