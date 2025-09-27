package org.marimon.sigc.Empleado

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.net.Uri
import android.content.Context
import org.marimon.sigc.storage.SupabaseStorageManager
import org.marimon.sigc.model.Empleado

/**
 * Estado para el manejo de imágenes de empleados
 */
@Composable
fun rememberImagenEmpleadoState(
    urlInicial: String? = null,
    scope: CoroutineScope = rememberCoroutineScope()
): ImagenEmpleadoState {
    return remember {
        ImagenEmpleadoState(
            urlInicial = urlInicial,
            scope = scope
        )
    }
}

class ImagenEmpleadoState(
    urlInicial: String? = null,
    private val scope: CoroutineScope
) {
    var imagenUri by mutableStateOf<Uri?>(null)
    var imagenUrl by mutableStateOf<String?>(urlInicial)
    var subiendo by mutableStateOf(false)
    var mensajeEstado by mutableStateOf("")
    
    private val storageManager = SupabaseStorageManager()

    fun subirImagen(uri: Uri, context: Context) {
        imagenUri = uri
        subiendo = true
        mensajeEstado = "☁️ Subiendo imagen a Supabase..."
        
        scope.launch {
            try {
                val urlPublica = storageManager.subirImagen(uri, context)
                if (urlPublica != null) {
                    imagenUrl = urlPublica
                    mensajeEstado = "✅ Imagen subida a Supabase"
                } else {
                    // Fallback: guardar localmente
                    guardarImagenLocal(uri, context)
                }
            } catch (e: Exception) {
                mensajeEstado = "❌ Error: ${e.localizedMessage}"
                // Intentar guardar localmente como fallback
                guardarImagenLocal(uri, context)
            } finally {
                subiendo = false
            }
        }
    }
    
    private suspend fun guardarImagenLocal(uri: Uri, context: Context) {
        try {
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
            
            imagenUrl = localFile.absolutePath
            mensajeEstado = "⚠️ Guardado localmente"
        } catch (e: Exception) {
            mensajeEstado = "❌ Error guardando imagen"
        }
    }
}

/**
 * Composable reutilizable para mostrar la imagen de un empleado
 */
@Composable
fun ImagenEmpleado(
    imagenUrl: String?,
    imagenUri: Uri? = null,
    subiendo: Boolean = false,
    size: androidx.compose.ui.unit.Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        when {
            // Imagen desde URL (Supabase)
            imagenUrl != null && imagenUrl.startsWith("http") -> {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imagenUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Imagen de empleado",
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            // Imagen desde path local (fallback)
            imagenUrl != null && !imagenUrl.startsWith("http") -> {
                val localFile = java.io.File(imagenUrl)
                if (localFile.exists()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(localFile)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen de empleado (local)",
                        modifier = Modifier
                            .size(size)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Si el archivo local no existe, mostrar placeholder
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(size),
                        tint = Color.LightGray
                    )
                }
            }
            // Imagen desde URI local (durante selección)
            imagenUri != null -> {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imagenUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            // Placeholder
            else -> {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(size),
                    tint = Color.LightGray
                )
            }
        }
        
        // Indicador de carga
        if (subiendo) {
            Box(
                modifier = Modifier
                    .size(size)
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
}

/**
 * Composable para mostrar mensajes de estado
 */
@Composable
fun MensajeEstado(
    mensaje: String,
    modifier: Modifier = Modifier
) {
    if (mensaje.isNotBlank()) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    mensaje.contains("✅") -> Color(0xFF4CAF50)
                    mensaje.contains("❌") || mensaje.contains("Error") -> Color(0xFFE53935)
                    else -> Color(0xFF2196F3)
                }
            )
        ) {
            Text(
                text = mensaje,
                modifier = Modifier.padding(12.dp),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * Componente reutilizable para iconos de diálogos
 */
@Composable
fun IconoDialog(
    icono: @Composable () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        icono()
    }
}

/**
 * Componente base para estructura de diálogos de confirmación
 */
@Composable
fun EstructuraDialogoConfirmacion(
    icono: @Composable () -> Unit,
    colorIcono: Color,
    titulo: String,
    mensaje: String,
    contenidoAdicional: @Composable ColumnScope.() -> Unit = {},
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    textoConfirmar: String = "Confirmar",
    textoCancelar: String = "Cancelar",
    habilitado: Boolean = true
) {
    Dialog(onDismissRequest = onCancelar) {
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
                IconoDialog(
                    icono = icono,
                    backgroundColor = colorIcono
                )

                Text(
                    text = titulo,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = mensaje,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                contenidoAdicional()

                BotonesAccionDialog(
                    onConfirmar = onConfirmar,
                    onCancelar = onCancelar,
                    textoConfirmar = textoConfirmar,
                    textoCancelar = textoCancelar,
                    habilitado = habilitado
                )
            }
        }
    }
}

/**
 * Diálogo de confirmación para eliminar empleado
 */
@Composable
fun DialogoConfirmarEliminacion(
    empleado: Empleado,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    EstructuraDialogoConfirmacion(
        icono = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        },
        colorIcono = Color(0xFFDC143C),
        titulo = "¿Eliminar Empleado?",
        mensaje = "¿Estás seguro que deseas eliminar a ${empleado.nombre}?",
        contenidoAdicional = {
            Text(
                text = "Esta acción no se puede deshacer",
                fontSize = 12.sp,
                color = Color(0xFFDC143C),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        },
        onConfirmar = onConfirmar,
        onCancelar = onCancelar,
        textoConfirmar = "Eliminar",
        textoCancelar = "Cancelar"
    )
}

/**
 * Diálogo de confirmación para actualizar empleado
 */
@Composable
fun DialogoConfirmarActualizacion(
    empleado: Empleado,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    EstructuraDialogoConfirmacion(
        icono = {
            Text("✓", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        },
        colorIcono = Color(0xFFDC143C),
        titulo = "¿Actualizar Empleado?",
        mensaje = "¿Estás seguro que deseas actualizar los datos de ${empleado.nombre}?",
        contenidoAdicional = {
            // Mostrar datos del empleado a actualizar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImagenEmpleado(
                    imagenUrl = empleado.imagenUrl,
                    size = 50.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = empleado.nombre,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = empleado.emailCorporativo,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = empleado.areaNombre,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Text(
                text = "Los cambios se aplicarán inmediatamente",
                fontSize = 12.sp,
                color = Color(0xFFDC143C),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        },
        onConfirmar = onConfirmar,
        onCancelar = onCancelar,
        textoConfirmar = "Aplicar",
        textoCancelar = "Cancelar"
    )
}

/**
 * Diálogo de éxito para operaciones de empleado
 */
@Composable
fun DialogoExitoEmpleado(
    empleado: Empleado,
    esCreacion: Boolean,
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        onDismiss()
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
                    text = if (esCreacion) "¡Empleado Creado!" else "¡Éxito!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (esCreacion)
                        "El empleado se ha creado correctamente en el sistema"
                    else
                        "Los Datos del Empleado se Editaron Exitosamente",
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
                    ImagenEmpleado(
                        imagenUrl = empleado.imagenUrl,
                        size = 50.dp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = empleado.nombre,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = empleado.areaNombre,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Salir", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

/**
 * Diálogo de éxito para eliminación
 */
@Composable
fun DialogoEliminacionExitosa(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                        .background(Color(0xFFDC143C), CircleShape),
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
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Aceptar", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}