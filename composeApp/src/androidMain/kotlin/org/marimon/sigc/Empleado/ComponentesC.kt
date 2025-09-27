package org.marimon.sigc.Empleado

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.marimon.sigc.model.Area

/**
 * Componente reutilizable para campos de texto estándar
 */
@Composable
fun CampoTextoEmpleado(
    valor: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        enabled = enabled,
        singleLine = singleLine
    )
}

/**
 * Componente reutilizable para campos de contraseña
 */
@Composable
fun CampoContrasenaEmpleado(
    valor: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                Text(
                    text = if (passwordVisible) "👁️" else "🔒",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder) } } else null,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        enabled = enabled,
        singleLine = true
    )
}

/**
 * Componente reutilizable para selector de área
 */
@Composable
fun SelectorAreaEmpleado(
    areas: List<Area>,
    areaSeleccionada: Area?,
    onAreaSeleccionada: (Area) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = areaSeleccionada?.nombre ?: "Seleccionar área",
            onValueChange = {},
            label = { Text("Área de trabajo") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            enabled = enabled,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            areas.forEach { area ->
                DropdownMenuItem(
                    text = { Text(area.nombre) },
                    onClick = {
                        onAreaSeleccionada(area)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Componente reutilizable para la sección de imagen del empleado
 */
@Composable
fun SeccionImagenEmpleado(
    imagenState: ImagenEmpleadoState,
    textoBoton: String = "Seleccionar imagen",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { imagenState.subirImagen(it, context) }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Imagen
        ImagenEmpleado(
            imagenUrl = imagenState.imagenUrl,
            imagenUri = imagenState.imagenUri,
            subiendo = imagenState.subiendo,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Botón de selección
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.padding(bottom = 16.dp),
            enabled = !imagenState.subiendo,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFDC143C)
            )
        ) {
            Text(
                text = if (imagenState.subiendo) "Subiendo..." else textoBoton,
                color = Color.White
            )
        }

        // Mensaje de estado
        MensajeEstado(mensaje = imagenState.mensajeEstado)
    }
}

/**
 * Componente reutilizable para botones de acción de diálogos
 */
@Composable
fun BotonesAccionDialog(
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    textoConfirmar: String = "Confirmar",
    textoCancelar: String = "Cancelar",
    habilitado: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onCancelar,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFDC143C)
            )
        ) {
            Text(textoCancelar, color = Color.White)
        }

        Button(
            onClick = onConfirmar,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            enabled = habilitado
        ) {
            Text(textoConfirmar, color = Color.White)
        }
    }
}

/**
 * Componente para confirmación de contraseña con validación
 */
@Composable
fun CampoConfirmacionContrasena(
    password: String,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    if (password.isNotBlank()) {
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirmar nueva contraseña") },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Text(
                        text = if (confirmPasswordVisible) "👁️" else "🔒",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            isError = password != confirmPassword,
            supportingText = {
                if (password != confirmPassword) {
                    Text(
                        text = "Las contraseñas no coinciden",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            singleLine = true
        )
    }
}

/**
 * Componente reutilizable para formulario base de empleado
 */
@Composable
fun FormularioBaseEmpleado(
    nombre: String,
    onNombreChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    areas: List<Area>,
    areaSeleccionada: Area?,
    onAreaSeleccionada: (Area) -> Unit,
    imagenState: ImagenEmpleadoState,
    textoBotonImagen: String = "Seleccionar imagen",
    camposAdicionales: @Composable ColumnScope.() -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Sección de imagen
        SeccionImagenEmpleado(
            imagenState = imagenState,
            textoBoton = textoBotonImagen
        )

        // Campo nombre
        CampoTextoEmpleado(
            valor = nombre,
            onValueChange = onNombreChange,
            label = "Nombre completo"
        )

        // Campo email
        CampoTextoEmpleado(
            valor = email,
            onValueChange = onEmailChange,
            label = "Email corporativo"
        )

        // Campos adicionales (contraseñas, etc.)
        camposAdicionales()

        // Selector de área
        SelectorAreaEmpleado(
            areas = areas,
            areaSeleccionada = areaSeleccionada,
            onAreaSeleccionada = onAreaSeleccionada
        )
    }
}