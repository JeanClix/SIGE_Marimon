package org.marimon.sigc.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
actual fun SimpleImageUploader(
    currentImageUrl: String?,
    onImageUploaded: (String?) -> Unit,
    modifier: Modifier
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color(0xFFFAFAFA)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📷 Imagen del producto",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF37474F)
            )
            
            OutlinedTextField(
                value = currentImageUrl ?: "",
                onValueChange = onImageUploaded,
                label = { Text("URL de imagen (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("https://...") }
            )
            
            Text(
                text = "En Web puedes ingresar la URL directamente",
                fontSize = 10.sp,
                color = Color(0xFF757575)
            )
        }
    }
}
