package org.marimon.sigc.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
actual fun ProductImage(
    imageUrl: String?,
    productName: String,
    modifier: Modifier,
    fallbackEmoji: String
) {
    if (!imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Foto de $productName",
            modifier = modifier,
            contentScale = ContentScale.Crop,
            onError = {
                println("DEBUG: Error cargando imagen: $imageUrl")
            }
        )
    } else {
        ProductImageFallback(
            productName = productName,
            modifier = modifier,
            fallbackEmoji = fallbackEmoji,
            hasImage = false
        )
    }
}
