package org.marimon.sigc.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
actual fun ProductImage(
    imageUrl: String?,
    productName: String,
    modifier: Modifier,
    fallbackEmoji: String
) {
    // Por ahora usamos fallback en iOS hasta implementar carga de imágenes nativa
    ProductImageFallback(
        productName = productName,
        modifier = modifier,
        fallbackEmoji = fallbackEmoji,
        hasImage = !imageUrl.isNullOrBlank()
    )
}
