package org.marimon.sigc.ui.components

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


@Composable
actual fun SimpleImageUploader(
    currentImageUrl: String?,
    onImageUploaded: (String?) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var uploading by remember { mutableStateOf(false) }
    
    val client = OkHttpClient.Builder()
        .build()
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            uploading = true
            
            scope.launch {
                try {
                    val uploadedUrl = uploadImageToSupabase(selectedUri, context, client)
                    uploading = false
                    onImageUploaded(uploadedUrl)
                } catch (e: Exception) {
                    uploading = false
                    println("Error subiendo imagen: ${e.message}")
                    onImageUploaded(null)
                }
            }
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        border = if (currentImageUrl != null) BorderStroke(2.dp, Color(0xFF4CAF50)) 
                else BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📷 Imagen del Producto",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333)
                )
                
                if (uploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFFFF0000),
                        strokeWidth = 2.dp
                    )
                }
            }
            
            if (currentImageUrl != null) {
                // Mostrar imagen cargada
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✅ Imagen cargada exitosamente",
                            fontSize = 14.sp,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = currentImageUrl.takeLast(30),
                            fontSize = 10.sp,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    // Cambiar imagen
                                    imagePickerLauncher.launch("image/*")
                                },
                                enabled = !uploading,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "🔄 Cambiar",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Button(
                                onClick = {
                                    onImageUploaded(null)
                                },
                                enabled = !uploading,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF999999)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "❌ Quitar",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // Sin imagen
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (!uploading) {
                                imagePickerLauncher.launch("image/*")
                            }
                        },
                        enabled = !uploading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uploading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color(0xFF333333),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = if (uploading) "Subiendo..." else "📷 Seleccionar Imagen",
                            color = Color(0xFF333333),
                            fontSize = 14.sp
                        )
                    }
                    
                    Text(
                        text = "💡 Puedes seleccionar una imagen desde tu galería",
                        fontSize = 11.sp,
                        color = Color(0xFF666666),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

suspend fun uploadImageToSupabase(uri: Uri, context: Context, client: OkHttpClient): String? {
    return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis()
            val fileName = "producto_$timestamp.jpg"

            // Obtener bytes de la imagen
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("No se puede abrir el archivo")

            val imageBytes = inputStream.readBytes()
            inputStream.close()

            // Usar FormData multipart
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    fileName,
                    imageBytes.toRequestBody("image/jpeg".toMediaType())
                )
                .build()

            val request = Request.Builder()
                .url("https://toothspciydsgevyxkol.supabase.co/storage/v1/object/productos-imagenes/$fileName")
                .post(requestBody)
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRvb3Roc3BjaXlkc2dldnl4a29sIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1ODk0NjgxMSwiZXhwIjoyMDc0NTIyODExfQ.k5k5V01RszFVjA94NdS_drlrXvG-5m50t7B_hGFQMDY")
                .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRvb3Roc3BjaXlkc2dldnl4a29sIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1ODk0NjgxMSwiZXhwIjoyMDc0NTIyODExfQ.k5k5V01RszFVjA94NdS_drlrXvG-5m50t7B_hGFQMDY")
                .build()

            // Ejecutar request
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    // Generar URL pública
                    val publicUrl = "https://toothspciydsgevyxkol.supabase.co/storage/v1/object/public/productos-imagenes/$fileName"
                    println("✅ Imagen subida exitosamente: $publicUrl")
                    publicUrl
                } else {
                    println("❌ Error subiendo imagen: ${response.code}")
                    null
                }
            }

        } catch (e: Exception) {
            println("❌ Error subiendo imagen: ${e.message}")
            null
        }
    }
}
