package org.marimon.sigc.ui.components

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.marimon.sigc.ui.icons.MarimonIcons
import org.marimon.sigc.storage.SupabaseStorageManager

@Composable
actual fun ImageUploader(
    imageUrl: String?,
    onImageSelected: (String?) -> Unit,
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
            // Subir imagen a Supabase Storage usando corrutinas
            scope.launch {
                try {
                    val result = storageManager.subirImagenProducto(selectedUri, context)
                    uploading = false
                    onImageSelected(result)
                } catch (e: Exception) {
                    uploading = false
                    println("Error subiendo imagen: ${e.message}")
                    onImageSelected(null)
                }
            }
        }
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (imageUrl != null) {
            // Mostrar imagen cargada
            ProductImage(
                imageUrl = imageUrl,
                productName = "Imagen del producto",
                modifier = Modifier.size(120.dp)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    imagePickerLauncher.launch("image/*")
                },
                enabled = !uploading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
                modifier = Modifier.weight(1f)
            ) {
                if (uploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (uploading) "Subiendo..." else "${MarimonIcons.Camera} Seleccionar",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            
            if (imageUrl != null) {
                Button(
                    onClick = { onImageSelected(null) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF999999)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Eliminar",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
        
        if (uploading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFF0000)
            )
        }
    }
}
