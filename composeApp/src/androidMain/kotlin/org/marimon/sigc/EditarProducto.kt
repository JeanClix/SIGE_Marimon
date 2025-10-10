package org.marimon.sigc

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoScreen(onNavigateToHome: () -> Unit = {}) {
    var codigo by remember { mutableStateOf("P001") } // ejemplo precargado
    var nombre by remember { mutableStateOf("Silla Gamer") }
    var descripcion by remember { mutableStateOf("Silla ergonómica con soporte lumbar") }
    var especificaciones by remember { mutableStateOf("Color negro, material cuero sintético") }
    var precio by remember { mutableStateOf("499.90") }
    var cantidad by remember { mutableStateOf("10") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    var edicionExitosa by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
    }

    // Diálogo de éxito
    if (edicionExitosa) {
        Dialog(onDismissRequest = { edicionExitosa = false }) {
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
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Éxito",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Text(
                        text = "¡Se editó exitosamente!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "El producto se actualizó correctamente",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = {
                            edicionExitosa = false
                            onNavigateToHome()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF5252)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Salir", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF5252))
                .padding(vertical = 16.dp, horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavigateToHome() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Editar Producto",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // Contenido con scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = { Text("Código de Producto") },
                placeholder = { Text("Ingresar Código", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Producto") },
                placeholder = { Text("Ingresar Nombre", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                placeholder = { Text("Ingresar Descripción", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            OutlinedTextField(
                value = especificaciones,
                onValueChange = { especificaciones = it },
                label = { Text("Especificaciones") },
                placeholder = { Text("Ingresar Especificaciones", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth()
            )

            Text(text = "Imagen", fontSize = 14.sp, color = Color.Black)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    if (imagenUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imagenUri),
                            contentDescription = "Imagen seleccionada",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("📷", fontSize = 32.sp)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Cambiar Imagen", color = Color.White, fontSize = 14.sp)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio (S/)") },
                    placeholder = { Text("Ingresar Precio", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("Cantidad") },
                    placeholder = { Text("Ingresar Cantidad", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { onNavigateToHome() },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Cancelar", color = Color.White, fontSize = 16.sp)
                }

                Button(
                    onClick = {
                        when {
                            codigo.isBlank() || nombre.isBlank() || descripcion.isBlank() ||
                                    especificaciones.isBlank() || precio.isBlank() || cantidad.isBlank() -> {
                                errorMessage = "⚠️ Todos los campos son obligatorios"
                            }
                            precio.toDoubleOrNull() == null -> {
                                errorMessage = "⚠️ El precio debe ser un número válido"
                            }
                            cantidad.toIntOrNull() == null -> {
                                errorMessage = "⚠️ La cantidad debe ser un número entero"
                            }
                            else -> {
                                errorMessage = ""
                                edicionExitosa = true
                            }
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Guardar Cambios", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}
