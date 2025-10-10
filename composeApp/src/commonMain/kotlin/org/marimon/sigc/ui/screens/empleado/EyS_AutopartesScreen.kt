package org.marimon.sigc.ui.screens.empleado

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Colores y Estilos ---
private val RedMarimon = Color(0xFFFF0000)
private val BackgroundColor = Color(0xFFF2F2F7)
private val CardBackgroundColor = Color.White
private val TextPrimaryColor = Color.Black
private val TextSecondaryColor = Color.Gray
private val GreenEntrada = Color(0xFF34C759)
private val BlueSalida = Color(0xFF007AFF)

// --- Datos de Ejemplo (Reemplazar con tu ViewModel) ---
data class Autoparte(val id: Int, val nombre: String, val stock: Int)
data class Movimiento(
    val id: Int,
    val autoparteNombre: String,
    val cantidad: Int,
    val tipo: String,
    val fecha: String
)

val autopartesDeEjemplo = listOf(
    Autoparte(1, "Filtro de Aceite X-123", 15),
    Autoparte(2, "Pastillas de Freno B-456", 8),
    Autoparte(3, "Bujía de Iridio Z-789", 50)
)
val movimientosDeEjemplo = listOf(
    Movimiento(1, "Filtro de Aceite X-123", 5, "ENTRADA", "10/10/2025 10:00 AM"),
    Movimiento(2, "Pastillas de Freno B-456", -2, "SALIDA", "10/10/2025 09:30 AM"),
    Movimiento(3, "Bujía de Iridio Z-789", 10, "ENTRADA", "09/10/2025 04:15 PM")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EySAutopartesScreen(onNavigateBack: () -> Unit) {
    var tipoMovimiento by remember { mutableStateOf("ENTRADA") } // ENTRADA o SALIDA
    var cantidad by remember { mutableStateOf("") }
    var nota by remember { mutableStateOf("") }
    val autopartes = remember { autopartesDeEjemplo }
    var selectedAutoparte by remember { mutableStateOf<Autoparte?>(null) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Movimiento de Inventario",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RedMarimon)
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Sección de Registro ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Registrar Nuevo Movimiento",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = RedMarimon
                        )
                        Spacer(Modifier.height(16.dp))

                        // Selector de Tipo (Entrada / Salida)
                        TipoMovimientoSelector(
                            selectedType = tipoMovimiento,
                            onTypeSelected = { tipoMovimiento = it }
                        )
                        Spacer(Modifier.height(16.dp))

                        // Selector de Autoparte
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedAutoparte?.nombre ?: "Seleccione una autoparte",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Autoparte") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = RedMarimon,
                                    unfocusedBorderColor = Color.LightGray
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                autopartes.forEach { autoparte ->
                                    DropdownMenuItem(
                                        text = { Text("${autoparte.nombre} (Stock: ${autoparte.stock})") },
                                        onClick = {
                                            selectedAutoparte = autoparte
                                            expanded = false
                                        }
                                    )
                                 }
                            }
                        }
                        Spacer(Modifier.height(16.dp))

                        // Campos de Cantidad y Nota
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = cantidad,
                                onValueChange = { cantidad = it.filter { char -> char.isDigit() } },
                                label = { Text("Cantidad") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedMarimon)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = nota,
                            onValueChange = { nota = it },
                            label = { Text("Nota / Justificación (Opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedMarimon)
                        )
                        Spacer(Modifier.height(20.dp))

                        // Botón de Registro
                        Button(
                            onClick = {
                                // TODO: Lógica para registrar el movimiento en el ViewModel
                                println("Registrando: $tipoMovimiento de $cantidad para ${selectedAutoparte?.nombre}")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RedMarimon)
                        ) {
                            Text(
                                "Registrar Movimiento",
                                modifier = Modifier.padding(8.dp),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // --- Sección de Historial ---
            item {
                Text(
                    "Historial de Movimientos Recientes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            items(movimientosDeEjemplo) { movimiento ->
                MovimientoItem(movimiento)
            }
        }
    }
}


@Composable
fun MovimientoItem(movimiento: Movimiento) {
    val isEntrada = movimiento.tipo == "ENTRADA"
    val colorTipo = if (isEntrada) GreenEntrada else BlueSalida
    val signo = if (isEntrada) "+" else ""

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colorTipo.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isEntrada) "E" else "S",
                    color = colorTipo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    movimiento.autoparteNombre,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryColor
                )
                Text(
                    movimiento.fecha,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryColor
                )
            }
            Text(
                text = "$signo${movimiento.cantidad}",
                color = colorTipo,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoMovimientoSelector(selectedType: String, onTypeSelected: (String) -> Unit) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        listOf("ENTRADA", "SALIDA").forEach { tipo ->
            val isSelected = selectedType == tipo
            val backgroundColor = when {
                isSelected && tipo == "ENTRADA" -> GreenEntrada.copy(alpha = 0.1f)
                isSelected && tipo == "SALIDA" -> BlueSalida.copy(alpha = 0.1f)
                else -> Color.LightGray.copy(alpha = 0.3f)
            }
            val textColor = when {
                isSelected && tipo == "ENTRADA" -> GreenEntrada
                isSelected && tipo == "SALIDA" -> BlueSalida
                else -> TextSecondaryColor
            }
            val borderColor = when {
                isSelected && tipo == "ENTRADA" -> GreenEntrada
                isSelected && tipo == "SALIDA" -> BlueSalida
                else -> Color.Gray.copy(alpha = 0.5f)
            }

            FilterChip(
                selected = isSelected,
                onClick = { onTypeSelected(tipo) },
                label = {
                    Text(
                        tipo,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = backgroundColor,
                    labelColor = textColor,
                    selectedContainerColor = backgroundColor,
                    selectedLabelColor = textColor
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = borderColor,
                    selectedBorderColor = borderColor,
                    borderWidth = 1.dp,
                    selectedBorderWidth = 1.5.dp,
                    disabledBorderColor = Color.Gray.copy(alpha = 0.5f),
                    disabledSelectedBorderColor = Color.Gray.copy(alpha = 0.5f)
                )
            )
        }
    }
}