package org.marimon.sigc

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Preview
@Composable
fun AdminRProductoPreview() {
    AdminRProductoApp()
}

@Composable
fun AdminRProductoApp(
    currentRoute: String = "circulo",
    onNavigate: (String) -> Unit = {}
) {
    AdminScreenLayout(
        title = "Reporte de Productos",
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Listado de Productos",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Inventario, stock, categorías, etc.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
