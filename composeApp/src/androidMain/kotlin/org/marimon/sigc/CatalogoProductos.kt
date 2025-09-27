package org.marimon.sigc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoProductosScreen(onNavigateToRegister: () -> Unit = {}) {
    var searchText by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var productoAEliminar by remember { mutableStateOf("") }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        Dialog(onDismissRequest = { showDeleteDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "¿Quiere Eliminar?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE91E63),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Seguro que desea eliminar esta acción?",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { showDeleteDialog = false },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE91E63)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Cancelar", color = Color.White, fontSize = 16.sp)
                        }

                        Button(
                            onClick = {
                                showDeleteDialog = false
                                showSuccessDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Confirmar", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }

    // Diálogo de éxito al eliminar
    if (showSuccessDialog) {
        Dialog(onDismissRequest = { showSuccessDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color(0xFFE91E63), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminado",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Text(
                        text = "¡Producto Eliminado!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Producto Eliminado Exitosamente",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = { showSuccessDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE91E63)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Salir", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header centrado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título con botón Registrar centrados
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Autopartes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onNavigateToRegister,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE91E63)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_input_add),
                        contentDescription = "Registrar",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Registrar", color = Color.White, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de búsqueda
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Buscar Producto", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_sort_by_size),
                        contentDescription = "Filtros",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid de productos centrado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            ProductCard(
                emoji = "🛢️",
                precio = "S/ 99",
                nombre = "Aceite PC",
                stock = "Stock: 5",
                onDelete = {
                    productoAEliminar = "Aceite PC"
                    showDeleteDialog = true
                }
            )

            ProductCard(
                emoji = "🛞",
                precio = "S/ 199",
                nombre = "Llanta",
                stock = "Stock: 8",
                onDelete = {
                    productoAEliminar = "Llanta"
                    showDeleteDialog = true
                }
            )
        }
    }
}

@Composable
fun ProductCard(
    emoji: String,
    precio: String,
    nombre: String,
    stock: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.width(155.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 56.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = precio,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = nombre,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = stock,
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Editar", color = Color.White, fontSize = 10.sp)
                }

                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE91E63)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Borrar",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Borrar", color = Color.White, fontSize = 10.sp)
                }
            }
        }
    }
}