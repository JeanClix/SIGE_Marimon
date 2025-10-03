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
import org.marimon.sigc.storage.SupabaseStorageManager


@Composable
actual fun SimpleImageUploader(
    currentImageUrl: String?,
    onImageUploaded: (String?) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var uploading by remember { mutableStateOf(false) }
    val storageManager = remember { SupabaseStorageManager() }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            uploading = true
            
            scope.launch {
                try {
                    val uploadedUrl = storageManager.subirImagenProducto(selectedUri, context)
                    uploading = false
                    onImageUploaded(uploadedUrl)
                } catch (e: Exception) {
                    uploading = false
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
