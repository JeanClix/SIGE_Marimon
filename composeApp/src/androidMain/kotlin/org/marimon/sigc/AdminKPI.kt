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
fun AdminKPIPreview() {
    AdminKPIApp()
}

@Composable
fun AdminKPIApp(
    currentRoute: String = "grafico",
    onNavigate: (String) -> Unit = {}
) {
    AdminScreenLayout(
        title = "Dashboard KPI",
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
                    text = "Contenido KPI",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Aquí van tus métricas y gráficos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
