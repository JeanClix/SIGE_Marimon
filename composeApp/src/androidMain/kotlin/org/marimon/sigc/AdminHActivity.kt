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
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ColorFilter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

data class NavItem(val route: String, val iconRes: Int)

@Composable
fun CustomBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavItem("home", R.drawable.home),
        NavItem("user", R.drawable.user),
        NavItem("circulo", R.drawable.circulo),
        NavItem("grafico", R.drawable.grafico)
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Black,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 0.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            navItems.forEach { item ->
                val isSelected = currentRoute == item.route
                val iconColor = if (isSelected) Color(0xFFE53E3E) else Color.Gray

                IconButton(
                    onClick = { onNavigate(item.route) },
                    modifier = Modifier
                        .size(48.dp)
                        .then(
                            if (isSelected) {
                                Modifier.shadow(
                                    elevation = 4.dp,
                                    shape = CircleShape,
                                    spotColor = Color(0xFFE53E3E).copy(alpha = 0.3f)
                                )
                            } else Modifier
                        )
                ) {
                    Image(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(iconColor)
                    )
                }
            }
        }
    }
}

@Composable
fun PanelAdministrativo() {
    val items = listOf(
        PanelItem("Registro de empleados", R.drawable.r_empleado),
        PanelItem("Reporte de Ventas", R.drawable.r_ventas),
        PanelItem("Reporte de Productos", R.drawable.r_producto),
        PanelItem("Dashboard KPI", R.drawable.kpi)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Panel Administrativo",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        for (row in 0..1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (col in 0..1) {
                    val item = items[row * 2 + col]
                    val imageSize = if (item.label == "Registro de empleados") 80.dp else 58.dp
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp),
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


data class PanelItem(val label: String, val iconRes: Int)

@Composable
fun App() {
    Scaffold(
        bottomBar = {
            CustomBottomNavBar(
                currentRoute = "home",
                onNavigate = {  }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            PanelAdministrativo()
        }
    }
}
