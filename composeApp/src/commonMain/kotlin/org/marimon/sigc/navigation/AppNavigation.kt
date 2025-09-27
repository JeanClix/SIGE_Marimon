package org.marimon.sigc.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.marimon.sigc.ui.screens.EmployeeDashboard
import org.marimon.sigc.data.model.UserRole
import org.marimon.sigc.viewmodel.AuthViewModel
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.collectAsState
import org.marimon.sigc.data.model.User
import org.marimon.sigc.AppAndroid
import org.marimon.sigc.PanelAdministrativo
import org.marimon.sigc.Routes
import org.marimon.sigc.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

@Composable
fun AppNavigation(authViewModel: AuthViewModel) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    println("DEBUG: AppNavigation - isLoggedIn: $isLoggedIn")
    println("DEBUG: AppNavigation - currentUser: $currentUser")
    println("DEBUG: AppNavigation - user role: ${currentUser?.role}")
    
    if (isLoggedIn) {
        // Redirigir según el rol del usuario
        when (currentUser?.role) {
            UserRole.ADMIN -> {
                println("DEBUG: AppNavigation - Redirigiendo a Vista Administrativa Original (ADMIN)")
                // Usar la vista original del administrador con barra de navegación inferior
                AppAndroid(
                    currentRoute = "home",
                    onNavigate = { route ->
                        println("DEBUG: AppNavigation - Navegando a: $route")
                        // TODO: Implementar navegación entre módulos
                    }
                )
            }
            UserRole.EMPLOYEE -> {
                println("DEBUG: AppNavigation - Redirigiendo a EmployeeDashboard (EMPLOYEE)")
                EmployeeDashboard(
            authViewModel = authViewModel,
            onLogout = {
                // El logout se maneja en el ViewModel
            }
        )
            }
            null -> {
                println("DEBUG: AppNavigation - Usuario null, esto no debería pasar")
                // Si no hay usuario, esto no debería pasar ya que LoginActivity maneja el login
                Text("Error: Usuario null")
            }
        }
    } else {
        println("DEBUG: AppNavigation - No está logueado, redirigiendo a LoginActivity")
        // Cuando no está logueado, mostrar mensaje de redirección
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Redirigiendo al login...",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
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
                .padding(vertical = 4.dp),
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
                        contentDescription = when(item.route) {
                            "home" -> "Inicio"
                            "user" -> "Empleados"
                            "circulo" -> "Productos"
                            "grafico" -> "Gráficos"
                            else -> null
                        },
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

