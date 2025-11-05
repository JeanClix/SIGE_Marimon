package org.marimon.sigc

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

data class NavItem(val route: String, val iconRes: Int)

@Composable
fun CustomBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavItem("home", R.drawable.home),
        NavItem(Routes.PROFILE, R.drawable.user),  // Cambiar a profile
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
                            Routes.PROFILE -> "Perfil"
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
fun TopAdminBar(
    onNotificationsClick: () -> Unit = {},
    navController: androidx.navigation.NavHostController? = null
) {
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

            // Ícono de notificaciones (campana)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        // Si hay navController, navegar al perfil
                        if (navController != null) {
                            navController.navigate(Routes.PROFILE)
                        } else {
                            // Si no, usar el callback proporcionado
                            onNotificationsClick()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notificaciones",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )

                // Badge rojo con número de notificaciones (opcional)
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color.Yellow, shape = CircleShape)
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "3",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE53E3E),
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AdminScreenLayout(
    title: String,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    content: @Composable () -> Unit
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
            TopAdminBar()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))

                content()
            }
        }
    }
}
