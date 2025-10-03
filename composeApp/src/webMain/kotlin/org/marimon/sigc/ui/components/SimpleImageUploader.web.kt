package org.marimon.sigc.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
actual fun SimpleImageUploader(
    currentImageUrl: String?,
    onImageUploaded: (String?) -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📷 Imagen del Producto",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333)
            )
            
            OutlinedTextField(
                value = currentImageUrl ?: "",
                onValueChange = onImageUploaded,
                label = { Text("URL de imagen") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("https://...") }
            )
            
            Text(
                text = "💡 En Web puedes ingresar la URL directamente",
                fontSize = 11.sp,
                color = Color(0xFF666666)
            )
        }
    }
}
