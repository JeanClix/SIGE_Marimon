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
                            esCreacion = true
                            // Crear un empleado temporal para mostrar
                            empleadoCreado = Empleado(
                                id = 0,
                                nombre = nombre,
                                emailCorporativo = email,
                                areaId = areaId,
                                areaNombre = areas.find { it.id == areaId }?.nombre ?: "",
                                activo = true,
                                imagenUrl = urlImagen
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

        // Diálogo confirmar eliminación
        if (showDeleteDialog && empleadoAEliminar != null) {
            Dialog(onDismissRequest = { showDeleteDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color(0xFFE91E63), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "¿Eliminar Empleado?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Esta acción no se puede deshacer. El empleado será eliminado permanentemente del sistema.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (empleadoAEliminar!!.imagenUrl != null && empleadoAEliminar!!.imagenUrl!!.isNotBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(empleadoAEliminar!!.imagenUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Foto empleado",
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

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = empleadoAEliminar!!.nombre,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = empleadoAEliminar!!.areaNombre,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    showDeleteDialog = false
                                    empleadoAEliminar = null
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Cancelar", color = Color.White, fontSize = 16.sp)
                            }

                            Button(
                                onClick = {
                                    empleadoAEliminar?.let { empleado ->
                                        empleadoViewModel.eliminarEmpleado(
                                            empleadoId = empleado.id,
                                            onSuccess = {
                                                showDeleteDialog = false
                                                showSuccessDialog = true
                                            },
                                            onError = { error ->
                                                println("Error eliminando empleado: $error")
                                                showDeleteDialog = false
                                                mensaje = "❌ Error: $error"
                                                mostrarMensaje = true
                                            }
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE91E63)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Eliminar", color = Color.White, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }

        // Diálogo de éxito al eliminar
        if (showSuccessDialog) {
            Dialog(onDismissRequest = { showSuccessDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color(0xFFE91E63), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminado",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Text(
                            text = "¡Empleado Eliminado!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "El empleado ha sido eliminado exitosamente",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = {
                                showSuccessDialog = false
                                empleadoAEliminar = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE91E63)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Aceptar", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // Diálogo de éxito al crear/actualizar empleado
        if (showSuccessCreateDialog && empleadoCreado != null) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000)
                showSuccessCreateDialog = false
                empleadoCreado = null
            }

            Dialog(onDismissRequest = { }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color.Black, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✓", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = if (esCreacion) "¡Empleado Creado!" else "¡Datos Actualizados!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = if (esCreacion)
                                "El empleado se ha creado correctamente en el sistema"
                            else
                                "Los datos del empleado se han actualizado correctamente en el sistema",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (empleadoCreado!!.imagenUrl != null && empleadoCreado!!.imagenUrl!!.isNotBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(empleadoCreado!!.imagenUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Foto empleado",
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

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = empleadoCreado!!.nombre,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = empleadoCreado!!.areaNombre,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
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