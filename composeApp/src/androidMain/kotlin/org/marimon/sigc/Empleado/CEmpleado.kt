package org.marimon.sigc.Empleado

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import org.marimon.sigc.model.Area
import org.marimon.sigc.model.Empleado
import org.marimon.sigc.storage.SupabaseStorageManager

@Composable
fun CrearEmpleadoDialog(
    areas: List<Area>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String?, String?) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var areaSeleccionada by remember { mutableStateOf(if (areas.isNotEmpty()) areas.first() else null) }
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imagenState = rememberImagenEmpleadoState(scope = scope)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { imagenState.subirImagen(it, context) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    areaSeleccionada?.let { area ->
                        onConfirm(nombre, email, area.id, imagenState.imagenUrl, password.ifBlank { null })
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = !imagenState.subiendo && nombre.isNotBlank() && email.isNotBlank() && 
                         areaSeleccionada != null && 
                         (password.isBlank() || password.length >= 6)
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
            Text("Crear Nuevo Empleado", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Imagen usando el componente optimizado
                ImagenEmpleado(
                    imagenUrl = imagenState.imagenUrl,
                    imagenUri = imagenState.imagenUri,
                    subiendo = imagenState.subiendo,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Button(
                    onClick = { launcher.launch("image/*") }, 
                    modifier = Modifier.padding(bottom = 16.dp), 
                    enabled = !imagenState.subiendo
                ) {
                    Text(if (imagenState.subiendo) "Subiendo..." else "Seleccionar imagen")
                }

                // Mensaje de estado usando el componente optimizado
                MensajeEstado(mensaje = imagenState.mensajeEstado)

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email corporativo") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(
                                text = if (passwordVisible) "👁️" else "🔒",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                

                
                // Selector de área
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = areaSeleccionada?.nombre ?: "Seleccionar área",
                        onValueChange = {},
                        label = { Text("Área de trabajo") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
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

@Composable
fun EditarEmpleadoDialog(
    empleado: Empleado,
    areas: List<Area>,
    onDismiss: () -> Unit,
    onConfirm: (Empleado) -> Unit
) {
    var nombre by remember { mutableStateOf(empleado.nombre) }
    var email by remember { mutableStateOf(empleado.emailCorporativo) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var areaSeleccionada by remember { mutableStateOf(areas.find { it.id == empleado.areaId } ?: areas.firstOrNull()) }
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imagenState = rememberImagenEmpleadoState(urlInicial = empleado.imagenUrl, scope = scope)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { imagenState.subirImagen(it, context) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    areaSeleccionada?.let { area ->
                        val empleadoEditado = Empleado(
                            id = empleado.id,
                            nombre = nombre,
                            emailCorporativo = email,
                            areaId = area.id,
                            areaNombre = area.nombre,
                            imagenUrl = imagenState.imagenUrl,
                            activo = empleado.activo,
                            password = password.ifBlank { empleado.password }
                        )
                        onConfirm(empleadoEditado)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = !imagenState.subiendo && nombre.isNotBlank() && email.isNotBlank() && 
                         areaSeleccionada != null && 
                         (password.isBlank() || (password == confirmPassword && password.length >= 6))
            ) {
                Text("Actualizar", color = Color.White)
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
                // Imagen usando el componente optimizado
                ImagenEmpleado(
                    imagenUrl = imagenState.imagenUrl,
                    imagenUri = imagenState.imagenUri,
                    subiendo = imagenState.subiendo,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Button(
                    onClick = { launcher.launch("image/*") }, 
                    modifier = Modifier.padding(bottom = 16.dp), 
                    enabled = !imagenState.subiendo
                ) {
                    Text(if (imagenState.subiendo) "Subiendo..." else "Cambiar Imagen")
                }

                // Mensaje de estado usando el componente optimizado
                MensajeEstado(mensaje = imagenState.mensajeEstado)

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email corporativo") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Nueva contraseña (opcional)") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(
                                text = if (passwordVisible) "👁️" else "🔒",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    },
                    placeholder = { Text("Dejar vacío para mantener actual") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                
                if (password.isNotBlank()) {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar nueva contraseña") },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Text(
                                    text = if (confirmPasswordVisible) "👁️" else "🔒",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        },
                        isError = password != confirmPassword,
                        supportingText = {
                            if (password != confirmPassword) {
                                Text(
                                    text = "Las contraseñas no coinciden",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }
                
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = areaSeleccionada?.nombre ?: "Seleccionar área",
                        onValueChange = {},
                        label = { Text("Área de trabajo") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
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