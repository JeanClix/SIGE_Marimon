package org.marimon.sigc.ui.screens.recuperacion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.clickable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import org.marimon.sigc.model.RecuperacionResult
import org.marimon.sigc.viewmodel.RecuperacionPasswordViewModel

/**
 * PANTALLA 2: Ingresar Código de Verificación
 * Usuario ingresa el código de 6 dígitos recibido por email
 */
@Composable
fun IngresarCodigoScreen(
    viewModel: RecuperacionPasswordViewModel,
    onNavigateToCambiarPassword: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var codigo by remember { mutableStateOf("") }
    val state by viewModel.verificarState.collectAsState()
    val email by viewModel.emailTemporal.collectAsState()

    var mostrarExito by remember { mutableStateOf(false) }

    // Observar cambios de estado
    LaunchedEffect(state) {
        when (val currentState = state) {
            is RecuperacionResult.Success -> {
                // Solo mostrar éxito si hay un mensaje y coincide con validación
                if (currentState.mensaje == "Código válido") {
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
                text = "Ingresar Código",
                color = Color(0xFFEF5350),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtítulo
            Text(
                text = "Ingresa el código de 6 dígitos que enviamos a:",
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email enmascarado
            Text(
                text = email ?: "",
                color = Color(0xFFEF5350),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Campo de código de 6 dígitos
            CodigoVerificacionInput(
                codigo = codigo,
                onCodigoChange = { nuevoCodigo ->
                    if (nuevoCodigo.length <= 6 && nuevoCodigo.all { it.isDigit() }) {
                        codigo = nuevoCodigo
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón verificar
            Button(
                onClick = {
                    if (codigo.length == 6) {
                        viewModel.verificarCodigo(codigo)
                    }
                },
                enabled = codigo.length == 6 && state !is RecuperacionResult.Loading,
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
                    Text("Verificar Código", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón reenviar código
            TextButton(
                onClick = {
                    viewModel.reenviarCodigo()
                },
                enabled = state !is RecuperacionResult.Loading
            ) {
                Text(
                    "¿No recibiste el código? Reenviar",
                    color = Color(0xFFEF5350),
                    fontSize = 14.sp
                )
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
                        .background(Color(0xFFEF5350), RoundedCornerShape(2.dp))
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
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Código Verificado", fontWeight = FontWeight.Bold) },
                text = { Text("El código es válido. Ahora puedes cambiar tu contraseña.") },
                confirmButton = {
                    Button(
                        onClick = {
                            mostrarExito = false
                            viewModel.limpiarEstados()
                            onNavigateToCambiarPassword()
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

/**
 * Input personalizado para código de 6 dígitos
 * Muestra 6 cajas visuales + campo de texto oculto
 */
@Composable
private fun CodigoVerificacionInput(
    codigo: String,
    onCodigoChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val focusRequester = remember { FocusRequester() }

        // Cajas visuales de 6 dígitos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { focusRequester.requestFocus() },
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            repeat(6) { index ->
                val digito = if (index < codigo.length) codigo[index].toString() else ""

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .border(
                            width = 2.dp,
                            color = if (digito.isNotEmpty()) Color(0xFFEF5350) else Color.Gray,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            color = Color(0xFF1A1A1A),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = digito,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Campo invisible y pequeño que captura el input del teclado
        BasicTextField(
            value = codigo,
            onValueChange = onCodigoChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(color = Color.Transparent, fontSize = 1.sp),
            modifier = Modifier
                .size(1.dp)
                .focusRequester(focusRequester)
        )
    }
}
