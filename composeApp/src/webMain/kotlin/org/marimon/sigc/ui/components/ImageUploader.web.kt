package org.marimon.sigc.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.marimon.sigc.ui.icons.MarimonIcons

@Composable
actual fun ImageUploader(
    imageUrl: String?,
    onImageSelected: (String?) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "${MarimonIcons.Camera} Selección de imagen disponible solo en Android",
            modifier = Modifier.padding(8.dp)
        )
        
        OutlinedTextField(
            value = imageUrl ?: "",
            onValueChange = onImageSelected,
            label = { Text("URL de imagen") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
