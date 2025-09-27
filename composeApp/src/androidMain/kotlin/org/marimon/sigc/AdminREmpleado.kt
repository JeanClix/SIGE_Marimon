package org.marimon.sigc

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.marimon.sigc.viewmodel.EmpleadoViewModel
import org.marimon.sigc.config.SupabaseConfig
import org.marimon.sigc.config.SupabaseClient
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.*
import sigc.composeapp.generated.resources.Res
import org.marimon.sigc.model.Empleado
import org.marimon.sigc.model.EmpleadoCreate
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

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
    AdminScreenLayout(
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
    areas: List<org.marimon.sigc.model.Area>,
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
                        if (empleado.imagenUrl != null) {
                            // Aquí podrías cargar la imagen usando Coil o similar
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = Color.Gray
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
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
            EmpleadoDialog(
                areas = areas,
                onDismiss = { showDialog = false },
                onConfirm = { nombre, email, areaId, urlImagen ->
                    val nuevoEmpleado = org.marimon.sigc.model.EmpleadoCreate(
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
                            mensaje = "Empleado creado exitosamente"
                            mostrarMensaje = true
                        },
                        onError = { error ->
                            mensaje = error
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

@Composable
fun EmpleadoDialog(
    areas: List<org.marimon.sigc.model.Area>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String?) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var areaSeleccionada by remember { mutableStateOf(if (areas.isNotEmpty()) areas.first() else null) }
    var expanded by remember { mutableStateOf(false) }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var imagenUrl by remember { mutableStateOf<String?>(null) }
    var subiendo by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imagenUri = uri
        if (uri != null) {
            subiendo = true
            try {
                val storageRef = FirebaseStorage.getInstance().reference.child("empleados/${System.currentTimeMillis()}.jpg")
                storageRef.putFile(uri)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { url ->
                            imagenUrl = url.toString()
                            subiendo = false
                        }
                    }
                    .addOnFailureListener {
                        subiendo = false
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                subiendo = false
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    areaSeleccionada?.let { area ->
                        onConfirm(nombre, email, area.id, imagenUrl)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = !subiendo && nombre.isNotBlank() && email.isNotBlank() && areaSeleccionada != null
            ) {
                Text("Confirmar", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))) {
                Text("Cancelar", color = Color.White)
            }
        },
        title = {
            Text("Editar Empleado", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Imagen seleccionada o placeholder
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .padding(bottom = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (imagenUri != null) {


                        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(90.dp), tint = Color.Gray)
                    } else {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(90.dp), tint = Color.LightGray)
                    }
                }
                Button(onClick = { launcher.launch("image/*") }, modifier = Modifier.padding(bottom = 16.dp), enabled = !subiendo) {
                    Text(if (subiendo) "Subiendo..." else "Subir imagen")
                }
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email corporativo") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = areaSeleccionada?.nombre ?: "Seleccionar área",
                        onValueChange = {},
                        label = { Text("Área") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        areas.forEach { area ->
                            DropdownMenuItem(
                                text = { Text(area.nombre) },
                                onClick = {
                                    areaSeleccionada = area
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color.White
    )
}
