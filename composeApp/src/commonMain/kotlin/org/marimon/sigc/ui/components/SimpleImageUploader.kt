package org.marimon.sigc.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun SimpleImageUploader(
    currentImageUrl: String?,
    onImageUploaded: (String?) -> Unit,
    modifier: Modifier
)
