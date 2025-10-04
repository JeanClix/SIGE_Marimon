package org.marimon.sigc.ui.components

import android.content.Context
import android.net.Uri
import android.util.Log
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
        Log.d("ImageUploader", "Image picker result: $uri")
        uri?.let { selectedUri ->
            Log.d("ImageUploader", "Selected URI: $selectedUri")
            uploading = true
            
            scope.launch {
                try {
                    Log.d("ImageUploader", "Starting image upload...")
                    val uploadedUrl = storageManager.subirImagenProducto(selectedUri, context)
                    Log.d("ImageUploader", "Upload completed. URL: $uploadedUrl")
                    uploading = false
                    onImageUploaded(uploadedUrl)
                } catch (e: Exception) {
                    Log.e("ImageUploader", "Error uploading image", e)
                    uploading = false
                    onImageUploaded(null)
                }
            }
        } ?: run {
            Log.d("ImageUploader", "No image selected or selection cancelled")
        }
    }
    
    // Diseño minimalista más simple y limpio
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color(0xFFFAFAFA)
        ),
        border = if (currentImageUrl.isNullOrBlank()) {
            BorderStroke(1.dp, Color(0xFFE0E0E0))
        } else {
            BorderStroke(1.dp, Color(0xFF4CAF50))
        }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Label simple y elegante
            Text(
                text = "📷 Imagen del producto",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF37474F)
            )
            
            if (currentImageUrl.isNullOrBlank()) {
                // Sin imagen - diseño simple y minimalista
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ninguna imagen seleccionada",
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                    
                    Button(
                        onClick = {
                            Log.d("ImageUploader", "Add image button clicked")
                            if (!uploading) {
                                Log.d("ImageUploader", "Launching image picker...")
                                imagePickerLauncher.launch("image/*")
                            } else {
                                Log.d("ImageUploader", "Upload in progress, ignoring click")
                            }
                        },
                        enabled = !uploading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF333333)
                        ),
                        modifier = Modifier.height(28.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        if (uploading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(10.dp),
                                color = Color.White,
                                strokeWidth = 1.dp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Subiendo...",
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        } else {
                            Text(
                                text = "+ Agregar",
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            } else {
                // Con imagen - diseño simple y clean
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "✓",
                            fontSize = 12.sp,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = "Imagen agregada",
                            fontSize = 12.sp,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // Botones pequeños y discretos
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón Cambiar (más pequeño)
                        OutlinedButton(
                            onClick = {
                                Log.d("ImageUploader", "Change image button clicked")
                                if (!uploading) {
                                    Log.d("ImageUploader", "Launching image picker for change...")
                                    imagePickerLauncher.launch("image/*")
                                } else {
                                    Log.d("ImageUploader", "Upload in progress, ignoring change click")
                                }
                            },
                            enabled = !uploading,
                            modifier = Modifier.height(24.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF666666)
                            ),
                            border = BorderStroke(0.5.dp, Color(0xFF999999))
                        ) {
                            Text(
                                text = "Cambiar",
                                fontSize = 9.sp
                            )
                        }
                        
                        // Botón Quitar (más pequeño)
                        OutlinedButton(
                            onClick = {
                                if (!uploading) {
                                    onImageUploaded(null)
                                }
                            },
                            enabled = !uploading,
                            modifier = Modifier.height(24.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF999999)
                            ),
                            border = BorderStroke(0.5.dp, Color(0xFF999999))
                        ) {
                            Text(
                                text = "Quitar",
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}