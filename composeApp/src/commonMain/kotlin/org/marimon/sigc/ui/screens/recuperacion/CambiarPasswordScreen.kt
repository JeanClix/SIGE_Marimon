package org.marimon.sigc.ui.screens.recuperacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.marimon.sigc.model.RecuperacionResult
import org.marimon.sigc.viewmodel.RecuperacionPasswordViewModel

/**
 * PANTALLA 3: Cambiar Contraseña
 * Usuario ingresa nueva contraseña y confirmación
 */
@Composable
fun CambiarPasswordScreen(
    viewModel: RecuperacionPasswordViewModel,
    onPasswordCambiado: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nuevaPassword by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }
    var mostrarConfirmarPassword by remember { mutableStateOf(false) }

    val state by viewModel.cambiarState.collectAsState()
    var mostrarExito by remember { mutableStateOf(false) }

    // Validaciones en tiempo real
    val passwordsCoinciden = nuevaPassword == confirmarPassword && confirmarPassword.isNotEmpty()
    val longitudValida = nuevaPassword.length >= 6
    val formularioValido = passwordsCoinciden && longitudValida

    // Observar cambios de estado
    LaunchedEffect(state) {
        when (val currentState = state) {
            is RecuperacionResult.Success -> {
                // Solo mostrar éxito si el mensaje indica que la contraseña fue cambiada
                if (currentState.mensaje.contains("cambiada exitosamente", ignoreCase = true)) {
                    mostrarExito = true
                }
            }
            else -> {}
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Título
            Text(
                text = "Nueva Contraseña",
                color = Color(0xFFEF5350),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtítulo
            Text(
                text = "Crea una nueva contraseña segura.\nDebe tener al menos 6 caracteres.",
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Campo nueva contraseña
            OutlinedTextField(
                value = nuevaPassword,
                onValueChange = { nuevaPassword = it },
                label = { Text("Nueva Contraseña", color = Color.Gray) },
                placeholder = { Text("Mínimo 6 caracteres", color = Color.Gray) },
                singleLine = true,
                visualTransformation = if (mostrarPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { mostrarPassword = !mostrarPassword }) {
                        Text(
                            text = if (mostrarPassword) "Ocultar" else "Mostrar",
                            color = Color(0xFFEF5350),
                            fontSize = 12.sp
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFEF5350),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFEF5350)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Indicador de fortaleza
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(
                                color = when {
                                    nuevaPassword.isEmpty() -> Color.Gray
                                    nuevaPassword.length >= 6 + (index * 2) -> Color(0xFF4CAF50)
                                    else -> Color.Gray
                                },
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo confirmar contraseña
            OutlinedTextField(
                value = confirmarPassword,
                onValueChange = { confirmarPassword = it },
                label = { Text("Confirmar Contraseña", color = Color.Gray) },
                placeholder = { Text("Repite tu contraseña", color = Color.Gray) },
                singleLine = true,
                visualTransformation = if (mostrarConfirmarPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { mostrarConfirmarPassword = !mostrarConfirmarPassword }) {
                        Text(
                            text = if (mostrarConfirmarPassword) "Ocultar" else "Mostrar",
                            color = Color(0xFFEF5350),
                            fontSize = 12.sp
                        )
                    }
                },
                isError = confirmarPassword.isNotEmpty() && !passwordsCoinciden,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (passwordsCoinciden) Color(0xFF4CAF50) else Color(0xFFEF5350),
                    unfocusedBorderColor = Color.Gray,
                    errorBorderColor = Color(0xFFEF5350),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    errorTextColor = Color.White,
                    cursorColor = Color(0xFFEF5350)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )

            if (confirmarPassword.isNotEmpty() && !passwordsCoinciden) {
                Text(
                    text = "Las contraseñas no coinciden",
                    color = Color(0xFFEF5350),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Requisitos de contraseña
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Tu contraseña debe tener:",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    RequisitoItem("Al menos 6 caracteres", longitudValida)
                    RequisitoItem("Las contraseñas coinciden", passwordsCoinciden)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón cambiar contraseña
            Button(
                onClick = {
                    if (formularioValido) {
                        viewModel.cambiarPassword(nuevaPassword, confirmarPassword)
                    }
                },
                enabled = formularioValido && state !is RecuperacionResult.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF5350),
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (state is RecuperacionResult.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Cambiar Contraseña", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Indicadores de página
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.Gray, RoundedCornerShape(2.dp))
                )
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.Gray, RoundedCornerShape(2.dp))
                )
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color(0xFFEF5350), RoundedCornerShape(2.dp))
                )
            }
        }

        // Mostrar mensaje de error
        if (state is RecuperacionResult.Error) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                containerColor = Color(0xFFEF5350)
            ) {
                Text((state as RecuperacionResult.Error).mensaje)
            }
        }

        // Diálogo de éxito
        if (mostrarExito) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("¡Contraseña Cambiada!", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Tu contraseña ha sido actualizada exitosamente.\n\n" +
                        "Ya puedes iniciar sesión con tu nueva contraseña."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            mostrarExito = false
                            viewModel.limpiarEstados()
                            onPasswordCambiado()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF5350)
                        )
                    ) {
                        Text("Ir a Inicio de Sesión")
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

/**
 * Item de requisito con check
 */
@Composable
private fun RequisitoItem(
    texto: String,
    cumplido: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (cumplido) "✓" else "○",
            color = if (cumplido) Color(0xFF4CAF50) else Color.Gray,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = texto,
            color = if (cumplido) Color.White else Color.Gray,
            fontSize = 13.sp
        )
    }
}
