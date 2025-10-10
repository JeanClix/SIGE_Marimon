package org.marimon.sigc.Producto

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.background
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import java.io.File

@Composable
fun CrearProductoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String?, String?, Double, Int, String?) -> Unit
) {
    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var especificaciones by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
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

            scope.launch {
                try {
                    val urlPublica = storageManager.subirImagenProducto(uri, context)
                    if (urlPublica != null) {
                        imagenUrl = urlPublica
                        mensajeEstado = "✅ Imagen subida a Supabase"
                    } else {
                        // Fallback: usar almacenamiento local si Supabase falla
                        try {
                            val timestamp = System.currentTimeMillis()
                            val fileName = "producto_$timestamp.jpg"
                            val filesDir = context.filesDir
                            val productosDir = File(filesDir, "productos")
                            if (!productosDir.exists()) {
                                productosDir.mkdirs()
                            }

                            val localFile = File(productosDir, fileName)
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
                    val precioDouble = precio.toDoubleOrNull() ?: 0.0
                    val cantidadInt = cantidad.toIntOrNull() ?: 0
                    val desc = if (descripcion.isBlank()) null else descripcion
                    val espec = if (especificaciones.isBlank()) null else especificaciones
                    onConfirm(codigo, nombre, desc, espec, precioDouble, cantidadInt, imagenUrl)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = !subiendo && codigo.isNotBlank() && nombre.isNotBlank() &&
                        precio.isNotBlank() && cantidad.isNotBlank()
            ) {
                Text("Confirmar", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Text("Cancelar", color = Color.White)
            }
        },
        title = {
            Text("Registrar Producto", style = MaterialTheme.typography.titleLarge)
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
                                contentDescription = "Imagen de producto",
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
                    Text(if (subiendo) "Subiendo..." else "Subir Imagen")
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

                // Campos del formulario
                OutlinedTextField(
                    value = codigo,
                    onValueChange = { codigo = it },
                    label = { Text("Código del Producto *") },
                    placeholder = { Text("Ingresar Código") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    isError = codigo.isBlank()
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del Producto *") },
                    placeholder = { Text("Ingresar Nombre") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    isError = nombre.isBlank()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    placeholder = { Text("Ingresar Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    minLines = 2,
                    maxLines = 3
                )

                OutlinedTextField(
                    value = especificaciones,
                    onValueChange = { especificaciones = it },
                    label = { Text("Especificaciones (opcional)") },
                    placeholder = { Text("Ingresar Especificaciones") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    minLines = 2,
                    maxLines = 3
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio (S/) *") },
                        placeholder = { Text("0.00") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = precio.isBlank() || precio.toDoubleOrNull() == null
                    )

                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = { cantidad = it },
                        label = { Text("Cantidad *") },
                        placeholder = { Text("0") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = cantidad.isBlank() || cantidad.toIntOrNull() == null
                    )
                }
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun EditarProductoDialog(
    producto: org.marimon.sigc.model.Producto,
    onDismiss: () -> Unit,
    onConfirm: (org.marimon.sigc.model.Producto) -> Unit
) {
    var codigo by remember { mutableStateOf(producto.codigo) }
    var nombre by remember { mutableStateOf(producto.nombre) }
    var descripcion by remember { mutableStateOf(producto.descripcion ?: "") }
    var especificaciones by remember { mutableStateOf(producto.especificaciones ?: "") }
    var precio by remember { mutableStateOf(producto.precio.toString()) }
    var cantidad by remember { mutableStateOf(producto.cantidad.toString()) }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var imagenUrl by remember { mutableStateOf<String?>(producto.imagenUrl) }
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

            scope.launch {
                try {
                    val urlPublica = storageManager.subirImagenProducto(uri, context)
                    if (urlPublica != null) {
                        imagenUrl = urlPublica
                        mensajeEstado = "✅ Imagen subida a Supabase"
                    } else {
                        try {
                            val timestamp = System.currentTimeMillis()
                            val fileName = "producto_$timestamp.jpg"
                            val filesDir = context.filesDir
                            val productosDir = java.io.File(filesDir, "productos")
                            if (!productosDir.exists()) {
                                productosDir.mkdirs()
                            }

                            val localFile = java.io.File(productosDir, fileName)
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
                    val precioDouble = precio.toDoubleOrNull() ?: 0.0
                    val cantidadInt = cantidad.toIntOrNull() ?: 0
                    val desc = if (descripcion.isBlank()) null else descripcion
                    val espec = if (especificaciones.isBlank()) null else especificaciones

                    val productoEditado = org.marimon.sigc.model.Producto(
                        id = producto.id,
                        codigo = codigo,
                        nombre = nombre,
                        descripcion = desc,
                        especificaciones = espec,
                        precio = precioDouble,
                        cantidad = cantidadInt,
                        imagenUrl = imagenUrl,
                        activo = producto.activo
                    )
                    onConfirm(productoEditado)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = !subiendo && codigo.isNotBlank() && nombre.isNotBlank() &&
                        precio.isNotBlank() && cantidad.isNotBlank()
            ) {
                Text("Actualizar", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Text("Cancelar", color = Color.White)
            }
        },
        title = {
            Text("Editar Producto", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Imagen actual o nueva
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .padding(bottom = 8.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        imagenUrl != null && imagenUrl!!.startsWith("http") -> {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imagenUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Imagen de producto",
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
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
                        else -> {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(90.dp),
                                tint = Color.LightGray
                            )
                        }
                    }

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
                    Text(if (subiendo) "Subiendo..." else "Cambiar Imagen")
                }

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
                    value = codigo,
                    onValueChange = { codigo = it },
                    label = { Text("Código del Producto *") },
                    placeholder = { Text("Ingresar Código") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    isError = codigo.isBlank()
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del Producto *") },
                    placeholder = { Text("Ingresar Nombre") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    isError = nombre.isBlank()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    placeholder = { Text("Ingresar Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    minLines = 2,
                    maxLines = 3
                )

                OutlinedTextField(
                    value = especificaciones,
                    onValueChange = { especificaciones = it },
                    label = { Text("Especificaciones (opcional)") },
                    placeholder = { Text("Ingresar Especificaciones") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    minLines = 2,
                    maxLines = 3
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio (S/) *") },
                        placeholder = { Text("0.00") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = precio.isBlank() || precio.toDoubleOrNull() == null
                    )

                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = { cantidad = it },
                        label = { Text("Cantidad *") },
                        placeholder = { Text("0") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = cantidad.isBlank() || cantidad.toIntOrNull() == null
                    )
                }
            }
        },
        containerColor = Color.White
    )
}