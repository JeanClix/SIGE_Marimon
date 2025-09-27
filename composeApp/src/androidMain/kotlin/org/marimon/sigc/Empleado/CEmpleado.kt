package org.marimon.sigc.Empleado

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import org.marimon.sigc.storage.SupabaseStorageManager
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import org.marimon.sigc.model.Area
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.background
import androidx.compose.material3.CircularProgressIndicator

@Composable
fun CrearEmpleadoDialog(
    areas: List<Area>,
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
    var mensajeEstado by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val storageManager = remember { SupabaseStorageManager() }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imagenUri = uri
        if (uri != null) {
            subiendo = true
            mensajeEstado = "☁️ Subiendo imagen a Supabase..."
            
            // Usar Supabase Storage
            scope.launch {
                try {
                    val urlPublica = storageManager.subirImagen(uri, context)
                    if (urlPublica != null) {
                        imagenUrl = urlPublica
                        mensajeEstado = "✅ Imagen subida a Supabase"
                    } else {
                        // Fallback: usar almacenamiento local si Supabase falla
                        try {
                            // Guardar archivo localmente
                            val timestamp = System.currentTimeMillis()
                            val fileName = "empleado_$timestamp.jpg"
                            val filesDir = context.filesDir
                            val empleadosDir = java.io.File(filesDir, "empleados")
                            if (!empleadosDir.exists()) {
                                empleadosDir.mkdirs()
                            }
                            
                            val localFile = java.io.File(empleadosDir, fileName)
                            val inputStream = context.contentResolver.openInputStream(uri)
                            val outputStream = localFile.outputStream()
                            
                            inputStream?.use { input ->
                                outputStream.use { output ->
                                    input.copyTo(output)
                                }
                            }
                            
                            val localPath = localFile.absolutePath
                            imagenUrl = localPath
                            mensajeEstado = "⚠️ Guardado localmente"
                        } catch (e: Exception) {
                            mensajeEstado = "❌ Error guardando imagen"
                        }
                    }
                } catch (e: Exception) {
                    mensajeEstado = "❌ Error: ${e.localizedMessage}"
                } finally {
                    subiendo = false
                }
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
            Text("Crear Nuevo Empleado", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Imagen seleccionada o placeholder
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .padding(bottom = 8.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        // Si hay URL de Supabase, mostrar imagen desde la nube
                        imagenUrl != null && imagenUrl!!.startsWith("http") -> {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imagenUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Imagen de empleado",
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        // Si hay URI local, mostrar imagen local
                        imagenUri != null -> {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imagenUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Imagen seleccionada",
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        // Placeholder cuando no hay imagen
                        else -> {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(90.dp),
                                tint = Color.LightGray
                            )
                        }
                    }
                    
                    // Indicador de carga superpuesto
                    if (subiendo) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }
                
                Button(
                    onClick = { launcher.launch("image/*") }, 
                    modifier = Modifier.padding(bottom = 16.dp), 
                    enabled = !subiendo
                ) {
                    Text(if (subiendo) "Subiendo..." else "Seleccionar imagen")
                }

                // Mostrar mensajes de estado si hay alguno
                if (mensajeEstado.isNotBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (mensajeEstado.contains("✅")) {
                                Color(0xFF4CAF50)
                            } else if (mensajeEstado.contains("❌") || mensajeEstado.contains("Error")) {
                                Color(0xFFE53935)
                            } else {
                                Color(0xFF2196F3)
                            }
                        )
                    ) {
                        Text(
                            text = mensajeEstado,
                            modifier = Modifier.padding(12.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

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