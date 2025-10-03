package org.marimon.sigc.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ImageUploader(
    imageUrl: String?,
    onImageSelected: (String?) -> Unit,
    modifier: Modifier
)
