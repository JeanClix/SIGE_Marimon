package org.marimon.sigc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            NavigationHost()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    AppAndroid()
}

data class PanelItem(val label: String, val iconRes: Int, val route: String)

@Composable
fun PanelAdministrativo(onNavigate: (String) -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TopAdminBar()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Panel Administrativo",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))

            val items = listOf(
                PanelItem("Registro de empleados", R.drawable.r_empleado, Routes.EMPLOYEES),
                PanelItem("Reporte de Ventas", R.drawable.r_ventas, Routes.HOME), // Temporalmente va a home
                PanelItem("Reporte de Productos", R.drawable.r_producto, Routes.PRODUCTS),
                PanelItem("Dashboard KPI", R.drawable.kpi, Routes.KPI)
            )

            for (row in 0..1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    for (col in 0..1) {
                        val item = items[row * 2 + col]
                        val imageSize = if (item.label == "Registro de empleados") 70.dp else 58.dp
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                                .clickable { onNavigate(item.route) },
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = item.iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(imageSize)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppAndroid(
    currentRoute: String = "home",
    onNavigate: (String) -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            CustomBottomNavBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            PanelAdministrativo(onNavigate = onNavigate)
        }
    }
}
