package org.marimon.sigc.ui.screens.recuperacion

import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.marimon.sigc.model.RecuperacionResult
import org.marimon.sigc.viewmodel.RecuperacionPasswordViewModel

/**
 * PANTALLA 1: Recuperar Contraseña
 * Usuario ingresa su email corporativo
 */
@Composable
fun RecuperarPasswordScreen(
    viewModel: RecuperacionPasswordViewModel,
    onNavigateToVerificar: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    val state by viewModel.solicitarState.collectAsState()

    var mostrarExito by remember { mutableStateOf(false) }

    // Observar cambios de estado
    LaunchedEffect(state) {
        when (state) {
            is RecuperacionResult.Success -> {
                val mensaje = (state as RecuperacionResult.Success).mensaje
                if (mensaje.isNotEmpty()) {
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
                text = "Recuperar Contraseña",
                color = Color(0xFFEF5350),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtítulo
            Text(
                text = "Te enviaremos un código de 5 dígitos a tu email\ncorporativo para verificar tu identidad.",
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Campo de email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Corporativo", color = Color.Gray) },
                placeholder = { Text("ejemplo@marimon.com", color = Color.Gray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

            Spacer(modifier = Modifier.height(24.dp))

            // Botón continuar
            Button(
                onClick = {
                    if (email.isNotBlank()) {
                        viewModel.solicitarRecuperacion(email)
                    }
                },
                enabled = email.isNotBlank() && state !is RecuperacionResult.Loading,
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
                    Text("Continuar", fontSize = 16.sp)
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
                        .background(Color(0xFFEF5350), RoundedCornerShape(2.dp))
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
                        .background(Color.Gray, RoundedCornerShape(2.dp))
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
            val mensaje = (state as? RecuperacionResult.Success)?.mensaje ?: ""
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Código Enviado", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        mensaje.ifEmpty {
                            "Hemos enviado un código de 6 dígitos a tu email corporativo.\n\n" +
                            "El código expirará en 15 minutos."
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            mostrarExito = false
                            viewModel.limpiarEstados()
                            onNavigateToVerificar()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF5350)
                        )
                    ) {
                        Text("Continuar")
                    }
                },
                containerColor = Color.White
            )
        }
    }
}
