package org.marimon.sigc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black // Fondo de la barra en negro
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Inicio") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Red,     // Ícono rojo al seleccionar
                        unselectedIconColor = Color.White, // Ícono blanco por defecto
                        selectedTextColor = Color.Red,     // Texto rojo al seleccionar
                        unselectedTextColor = Color.White, // Texto blanco por defecto
                        indicatorColor = Color.Transparent // Sin burbuja de selección
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Registrar") },
                    label = { Text("Registrar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Red,
                        unselectedIconColor = Color.White,
                        selectedTextColor = Color.Red,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
                    label = { Text("Ajustes") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Red,
                        unselectedIconColor = Color.White,
                        selectedTextColor = Color.Red,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen()
                1 -> RegistrarProductoScreen()
                2 -> AjustesScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Text("Pantalla de Inicio", color = Color.Black)
}



@Composable
fun AjustesScreen() {
    Text("Pantalla de Ajustes", color = Color.Black)
}
