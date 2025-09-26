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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.graphics.ColorFilter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppAndroid()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    AppAndroid()
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
fun TopAdminBar() {
    Surface(
        color = Color(0xFFE53E3E),
        shape = MaterialTheme.shapes.large.copy(
            topStart = CornerSize(0.dp),
            topEnd = CornerSize(0.dp),
            bottomStart = CornerSize(32.dp),
            bottomEnd = CornerSize(32.dp)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar circular
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(48.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "Avatar",
                        modifier = Modifier.size(44.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Hola!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Administrador",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

data class PanelItem(val label: String, val iconRes: Int)

@Composable
fun PanelAdministrativo() {
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
                PanelItem("Registro de empleados", R.drawable.r_empleado),
                PanelItem("Reporte de Ventas", R.drawable.r_ventas),
                PanelItem("Reporte de Productos", R.drawable.r_producto),
                PanelItem("Dashboard KPI", R.drawable.kpi)
            )
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
}

@Composable
fun AppAndroid() {
    Scaffold(
        bottomBar = {
            CustomBottomNavBar(
                currentRoute = "home",
                onNavigate = {  }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            PanelAdministrativo()
        }
    }
}
