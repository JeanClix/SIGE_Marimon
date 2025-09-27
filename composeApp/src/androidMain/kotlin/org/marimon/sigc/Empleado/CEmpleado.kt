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
    var areaSeleccionada by remember { mutableStateOf(if (areas.isNotEmpty()) areas.first() else null) }

    val scope = rememberCoroutineScope()
    val imagenState = rememberImagenEmpleadoState(scope = scope)

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
            FormularioBaseEmpleado(
                nombre = nombre,
                onNombreChange = { nombre = it },
                email = email,
                onEmailChange = { email = it },
                areas = areas,
                areaSeleccionada = areaSeleccionada,
                onAreaSeleccionada = { areaSeleccionada = it },
                imagenState = imagenState,
                textoBotonImagen = "Seleccionar imagen",
                camposAdicionales = {
                    CampoContrasenaEmpleado(
                        valor = password,
                        onValueChange = { password = it },
                        label = "Contraseña"
                    )
                }
            )
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
    var areaSeleccionada by remember { mutableStateOf(areas.find { it.id == empleado.areaId } ?: areas.firstOrNull()) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var empleadoParaActualizar by remember { mutableStateOf<Empleado?>(null) }

    val scope = rememberCoroutineScope()
    val imagenState = rememberImagenEmpleadoState(urlInicial = empleado.imagenUrl, scope = scope)

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
                        empleadoParaActualizar = empleadoEditado
                        mostrarConfirmacion = true
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
            FormularioBaseEmpleado(
                nombre = nombre,
                onNombreChange = { nombre = it },
                email = email,
                onEmailChange = { email = it },
                areas = areas,
                areaSeleccionada = areaSeleccionada,
                onAreaSeleccionada = { areaSeleccionada = it },
                imagenState = imagenState,
                textoBotonImagen = "Cambiar Imagen",
                camposAdicionales = {
                    CampoContrasenaEmpleado(
                        valor = password,
                        onValueChange = { password = it },
                        label = "Nueva contraseña (opcional)",
                        placeholder = "Dejar vacío para mantener actual"
                    )

                    CampoConfirmacionContrasena(
                        password = password,
                        confirmPassword = confirmPassword,
                        onConfirmPasswordChange = { confirmPassword = it }
                    )
                }
            )
        },
        containerColor = Color.White
    )

    // Modal de confirmación para actualizar
    if (mostrarConfirmacion && empleadoParaActualizar != null) {
        DialogoConfirmarActualizacion(
            empleado = empleadoParaActualizar!!,
            onConfirmar = {
                mostrarConfirmacion = false
                onConfirm(empleadoParaActualizar!!)
            },
            onCancelar = {
                mostrarConfirmacion = false
                empleadoParaActualizar = null
            }
        )
    }
}