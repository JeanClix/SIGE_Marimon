package org.marimon.sigc.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.marimon.sigc.viewmodel.AuthViewModel

// Colores
private val RedPure = Color(0xFFFF0000) // Rojo puro
private val BackgroundDark = Color(0xFF000000) // Fondo negro
private val CardBackground = Color(0xFFFFFFFF) // Tarjeta blanca
private val TextPrimary = Color(0xFF000000) // Texto negro
private val TextSecondary = Color(0xFF666666) // Texto gris
private val InputBorder = Color(0xFFE5E5E5) // Borde gris claro
private val TextFooter = Color(0xFF666666) // Color específico para el footer dentro de la tarjeta

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onForgotPassword: () -> Unit = {}
) {
    // CAMBIO 1: Inicializar los estados como cadenas vacías para que el placeholder funcione correctamente.
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var rememberAccess by remember { mutableStateOf(false) }

    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    val isLoading = authState is org.marimon.sigc.data.model.AuthResult.Loading
    val errorMessage = (authState as? org.marimon.sigc.data.model.AuthResult.Error)?.message

    // Simplified validation for prototype visual match
    val emailValid = remember(email) { email.isNotBlank() }
    val passwordValid = remember(password) { password.isNotBlank() }
    val formValid = emailValid && passwordValid && !isLoading

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) onLoginSuccess()
    }

    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Placeholder for the Car Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color.Red.copy(alpha = 0.5f)) // Visual placeholder for the image area
            )
            // End of Car Image Placeholder

            Spacer(modifier = Modifier.height(20.dp))

            // Tarjeta blanca (Card)
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Título
                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RedPure,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)
                    )

                    // Correo
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Correo",
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            // El texto "ventas@marimon.com" permanece como placeholder
                            placeholder = { Text("ventas@marimon.com", color = TextSecondary.copy(alpha = 0.7f)) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RedPure,
                                unfocusedBorderColor = InputBorder,
                                cursorColor = RedPure,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                            ),
                            isError = email.isNotBlank() && !isValidEmail(email.trim())
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contraseña
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Contraseña",
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            // El texto "********" permanece como placeholder
                            placeholder = { Text("********", color = TextSecondary.copy(alpha = 0.7f)) },
                            trailingIcon = {
                                TextButton(
                                    onClick = { showPassword = !showPassword },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text(
                                        text = if (showPassword) "👁️" else "🔒",
                                        color = RedPure.copy(alpha = 0.6f),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            },
                            singleLine = true,
                            visualTransformation = if (showPassword) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RedPure,
                                unfocusedBorderColor = InputBorder,
                                cursorColor = RedPure,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                            ),
                            isError = password.isNotBlank() && password.length < 6
                        )
                    }

                    // Olvidaste tu contraseña - CENTRADO
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿Olvidaste tu contraseña? Ingresa ",
                            color = TextPrimary,
                            fontSize = 12.sp
                        )
                        TextButton(
                            onClick = onForgotPassword,
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text(
                                text = "aquí",
                                color = RedPure,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Recordar acceso
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberAccess,
                            onCheckedChange = { rememberAccess = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = RedPure,
                                uncheckedColor = TextSecondary,
                                checkmarkColor = Color.White
                            )
                        )
                        Text("Recordar acceso", color = TextPrimary, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón Iniciar
                    Button(
                        onClick = {
                            focusManager.clearFocus(force = true)
                            authViewModel.login(email.trim(), password)
                        },
                        enabled = formValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RedPure,
                            contentColor = Color.White,
                            disabledContainerColor = RedPure.copy(alpha = 0.4f),
                            disabledContentColor = Color.White.copy(alpha = 0.8f)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Iniciar", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        }
                    }

                    // Error
                    AnimatedVisibility(visible = !errorMessage.isNullOrBlank()) {
                        Text(
                            text = errorMessage.orEmpty(),
                            color = RedPure,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// Función auxiliar para validar email (reemplaza Patterns.EMAIL_ADDRESS para multiplataforma)
private fun isValidEmail(email: String): Boolean {
    return email.contains("@") && email.contains(".") && email.length > 5
}
