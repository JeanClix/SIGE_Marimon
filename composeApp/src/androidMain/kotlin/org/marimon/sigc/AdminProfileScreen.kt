package org.marimon.sigc

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AdminProfileScreen(
    onNotificationsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    // Datos del administrador (en un caso real vendrían de una base de datos o estado global)
    val adminName = "Marko Orihuela"
    val adminEmail = "marko.orihuela@marimon.com"
    val notificationCount = 3

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header rojo
        Surface(
            color = Color(0xFFE53E3E),
            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Avatar
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(80.dp)
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "Avatar del administrador",
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nombre
                Text(
                    text = adminName,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rol
                Text(
                    text = "Administrador",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta de información
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Email
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE53E3E).copy(alpha = 0.1f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "Email",
                            tint = Color(0xFFE53E3E),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Correo Electrónico",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = adminEmail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Divider(modifier = Modifier.fillMaxWidth(), color = Color.LightGray)

                // Notificaciones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNotificationsClick() }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0xFFFFC107).copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(24.dp)
                        )

                        // Badge
                        if (notificationCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color(0xFFE53E3E), shape = CircleShape)
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = notificationCount.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Notificaciones",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "Ver mis notificaciones",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_media_next),
                        contentDescription = "Ir a notificaciones",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón de cerrar sesión
        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE53E3E)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Filled.ExitToApp,
                    contentDescription = "Cerrar sesión",
                    tint = Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = "Cerrar Sesión",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
