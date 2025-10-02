package org.marimon.sigc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
expect fun ProductImage(
    imageUrl: String?,
    productName: String,
    modifier: Modifier = Modifier,
    fallbackEmoji: String = "🔧"
)

@Composable
fun ProductImageFallback(
    productName: String,
    modifier: Modifier = Modifier,
    fallbackEmoji: String = "🔧",
    hasImage: Boolean = false
) {
    Box(
        modifier = modifier
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (hasImage) {
            // Mostrar emoji con indicador de que hay imagen disponible
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = fallbackEmoji,
                    fontSize = 32.sp
                )
                Text(
                    text = "📷",
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
            }
        } else {
            // Fallback emoji si no hay imagen
            Text(
                text = fallbackEmoji,
                fontSize = 40.sp
            )
        }
    }
}
