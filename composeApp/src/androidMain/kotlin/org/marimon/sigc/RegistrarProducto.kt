package org.marimon.sigc

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun RegistrarProductoScreen() {
    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var especificaciones by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    var errorMessage by remember { mutableStateOf("") }
    var registroExitoso by remember { mutableStateOf(false) }

    // Lanzador para abrir la galería
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registrar Producto",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.Red
        )

        // Campos de texto
        OutlinedTextField(value = codigo, onValueChange = { codigo = it }, label = { Text("Código de Producto") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del producto") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), maxLines = 2)
        OutlinedTextField(value = especificaciones, onValueChange = { especificaciones = it }, label = { Text("Especificaciones") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio (S/)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = cantidad, onValueChange = { cantidad = it }, label = { Text("Cantidad") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

        // Botón para seleccionar imagen
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Subir Imagen", color = Color.White)
        }

        // Mostrar imagen seleccionada
        imagenUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Imagen del producto",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
        }

        // Mostrar error si existe
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
        }

        // Botón Confirmar
        Button(
            onClick = {
                if (codigo.isBlank() || nombre.isBlank() || descripcion.isBlank() ||
                    especificaciones.isBlank() || categoria.isBlank() || precio.isBlank() || cantidad.isBlank()
                ) {
                    errorMessage = "⚠️ Todos los campos son obligatorios"
                    registroExitoso = false
                } else if (precio.toDoubleOrNull() == null || cantidad.toIntOrNull() == null) {
                    errorMessage = "⚠️ Precio debe ser numérico y Cantidad un número entero"
                    registroExitoso = false
                } else {
                    errorMessage = ""
                    registroExitoso = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Confirmar", color = Color.White)
        }

        // Mensaje de confirmación con diseño
        if (registroExitoso) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✅ Registro exitoso", color = Color.Red, style = MaterialTheme.typography.headlineSmall)
                    Text("El producto $nombre fue registrado correctamente.", color = Color.White)
                }
            }
        }
    }
}
