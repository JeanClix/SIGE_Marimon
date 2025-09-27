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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import android.content.Intent
import org.marimon.sigc.navigation.AppNavigation
import org.marimon.sigc.viewmodel.AuthViewModel
import org.marimon.sigc.data.model.User
import org.marimon.sigc.data.model.UserRole

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            val authViewModel = remember { AuthViewModel() }
            val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
            val currentUser by authViewModel.currentUser.collectAsState()
            
            LaunchedEffect(Unit) {
                val userId = intent.getStringExtra("user_id")
                val userEmail = intent.getStringExtra("user_email")
                val userName = intent.getStringExtra("user_name")
                val userRole = intent.getStringExtra("user_role")
                
                println("DEBUG: MainActivity - Datos recibidos del LoginActivity:")
                println("DEBUG: MainActivity - userId: $userId")
                println("DEBUG: MainActivity - userEmail: $userEmail")
                println("DEBUG: MainActivity - userName: $userName")
                println("DEBUG: MainActivity - userRole: $userRole")
                
                if (userId != null && userEmail != null && userName != null && userRole != null) {
                    val user = User(
                        id = userId,
                        username = userName,
                        email = userEmail,
                        firstName = userName,
                        lastName = "",
                        role = if (userRole == "ADMIN") UserRole.ADMIN else UserRole.EMPLOYEE,
                        createdAt = null,
                        updatedAt = null
                    )
                    
                    println("DEBUG: MainActivity - Usuario creado: $user")
                    
                    authViewModel.setLoggedInUser(user)
                }
            }
            
            // Manejar logout - volver a LoginActivity
            LaunchedEffect(isLoggedIn) {
                // Solo ejecutar logout si el usuario estaba logueado y ahora no lo está
                // Evitar ejecutar en el estado inicial cuando isLoggedIn es false
                if (!isLoggedIn && currentUser != null) {
                    println("DEBUG: MainActivity - Usuario deslogueado, volviendo a LoginActivity")
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            
            AppNavigation(authViewModel = authViewModel)
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
