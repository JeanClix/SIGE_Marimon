package org.marimon.sigc.ui.screens.empleado

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.marimon.sigc.model.Empleado
import org.marimon.sigc.model.Producto
import org.marimon.sigc.model.Transaccion
import org.marimon.sigc.model.TransaccionCreate
import org.marimon.sigc.model.MetodoPago
import org.marimon.sigc.model.TipoComprobante
import org.marimon.sigc.viewmodel.TransaccionViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

// Colores Marimon
private val RedPure = Color(0xFFFF0000)
private val BackgroundDark = Color(0xFFFFFFFF)

private val CardBackground = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF000000)
private val TextSecondary = Color(0xFF666666)
private val InputBorder = Color(0xFFE5E5E5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransaccionScreen(
    empleado: Empleado,
    onNavigateBack: () -> Unit,
    onSuccess: (String) -> Unit = {},
    context: Any? = null // Contexto para Android
) {
    val transaccionViewModel = remember { TransaccionViewModel() }
    val productos = transaccionViewModel.productos
    val isLoading = transaccionViewModel.isLoading
    val error = transaccionViewModel.error
    
    // Estados del formulario
    var dniRuc by remember { mutableStateOf("") }
    var nombreCliente by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var correoElectronico by remember { mutableStateOf("") }
    var fechaEmision by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()) }
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var precio by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var metodoPago by remember { mutableStateOf(MetodoPago.EFECTIVO) }
    var observaciones by remember { mutableStateOf("") }
    
    // Estados de UI
    var showProductoDropdown by remember { mutableStateOf(false) }
    var showMetodoPagoDropdown by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var transaccionGenerada by remember { mutableStateOf("") }
    var transaccionParaEmail by remember { mutableStateOf<Transaccion?>(null) }
    
            // Cargar productos al inicializar
            LaunchedEffect(Unit) {
                transaccionViewModel.cargarProductos()
            }
    
    // Enviar email cuando se registre la transacción
    LaunchedEffect(transaccionParaEmail) {
        transaccionParaEmail?.let { transaccion ->
            transaccionViewModel.enviarEmailConPDF(
                transaccion = transaccion,
                onSuccess = {
                    transaccionGenerada = "Transacción registrada y email enviado exitosamente"
                    showSuccessDialog = true
                },
                onError = { error ->
                    transaccionGenerada = "Transacción registrada pero error enviando email: $error"
                    showSuccessDialog = true
                }
            )
        }
    }
    
    // Validaciones
    val dniRucValid = dniRuc.length == 8 || dniRuc.length == 11
    val nombreClienteValid = nombreCliente.isNotBlank()
    val direccionValid = direccion.isNotBlank()
    val correoValid = correoElectronico.contains("@") && correoElectronico.contains(".")
    val productoValid = productoSeleccionado != null
    val precioValid = precio.toDoubleOrNull() != null && precio.toDoubleOrNull()!! > 0
    val cantidadValid = cantidad.toIntOrNull() != null && cantidad.toIntOrNull()!! > 0
    
    val formValid = dniRucValid && nombreClienteValid && direccionValid && 
                   correoValid && productoValid && precioValid && cantidadValid
    
    // Determinar tipo de comprobante
    val tipoComprobante = if (dniRuc.length == 8) TipoComprobante.BOLETA else TipoComprobante.FACTURA
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "Emitir Comprobante",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                TextButton(onClick = onNavigateBack) {
                    Text(
                        text = "←",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = RedPure
            )
        )
        
        // Contenido del formulario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Información del tipo de comprobante
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (dniRuc.length == 8) "📄" else "🧾",
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (dniRuc.length == 8) "Boleta de Venta" else "Factura",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = RedPure
                    )
                    Text(
                        text = if (dniRuc.length == 8) "DNI: $dniRuc" else "RUC: $dniRuc",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
            
            // Campo DNI/RUC
            OutlinedTextField(
                value = dniRuc,
                onValueChange = { dniRuc = it.filter { char -> char.isDigit() } },
                label = { Text("DNI/RUC") },
                placeholder = { Text("8 dígitos (DNI) o 11 dígitos (RUC)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = dniRuc.isNotEmpty() && !dniRucValid,
                supportingText = if (dniRuc.isNotEmpty() && !dniRucValid) {
                    { Text("DNI debe tener 8 dígitos o RUC 11 dígitos") }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RedPure,
                    unfocusedBorderColor = InputBorder
                )
            )
            
            // Campo Nombre del Cliente
            OutlinedTextField(
                value = nombreCliente,
                onValueChange = { nombreCliente = it },
                label = { Text("Nombre del Cliente") },
                placeholder = { Text("Ingrese el nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombreCliente.isNotEmpty() && !nombreClienteValid,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RedPure,
                    unfocusedBorderColor = InputBorder
                )
            )
            
            // Campo Dirección
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                placeholder = { Text("Ingrese la dirección del cliente") },
                modifier = Modifier.fillMaxWidth(),
                isError = direccion.isNotEmpty() && !direccionValid,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RedPure,
                    unfocusedBorderColor = InputBorder
                )
            )
            
            // Campo Correo Electrónico
            OutlinedTextField(
                value = correoElectronico,
                onValueChange = { correoElectronico = it },
                label = { Text("Correo Electrónico") },
                placeholder = { Text("cliente@ejemplo.com") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = correoElectronico.isNotEmpty() && !correoValid,
                supportingText = if (correoElectronico.isNotEmpty() && !correoValid) {
                    { Text("Ingrese un correo válido") }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RedPure,
                    unfocusedBorderColor = InputBorder
                )
            )
            
            // Campo Fecha de Emisión
            OutlinedTextField(
                value = fechaEmision,
                onValueChange = { fechaEmision = it },
                label = { Text("Fecha de Emisión") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RedPure,
                    unfocusedBorderColor = InputBorder
                )
            )
            
            // Campo Producto (Combo Box)
                            ExposedDropdownMenuBox(
                                expanded = showProductoDropdown,
                                onExpandedChange = { showProductoDropdown = !showProductoDropdown }
                            ) {
                OutlinedTextField(
                    value = productoSeleccionado?.nombre ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Producto") },
                    placeholder = { Text("Seleccione un producto") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = showProductoDropdown
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPure,
                        unfocusedBorderColor = InputBorder
                    )
                )
                
                                ExposedDropdownMenu(
                                    expanded = showProductoDropdown,
                                    onDismissRequest = { showProductoDropdown = false }
                                ) {
                                    if (productos.isEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text("No hay productos disponibles") },
                                            onClick = { showProductoDropdown = false }
                                        )
                                    } else {
                                        productos.forEach { producto ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text("${producto.nombre} - S/ ${producto.precio}")
                                                },
                                                onClick = {
                                                    productoSeleccionado = producto
                                                    precio = producto.precio.toString()
                                                    showProductoDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
            }
            
            // Campo Precio
            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio (S/)") },
                placeholder = { Text("0.00") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = precio.isNotEmpty() && !precioValid,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RedPure,
                    unfocusedBorderColor = InputBorder
                )
            )
            
            // Campo Cantidad
            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text("Cantidad") },
                placeholder = { Text("1") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = cantidad.isNotEmpty() && !cantidadValid,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RedPure,
                    unfocusedBorderColor = InputBorder
                )
            )
            
            // Campo Método de Pago (Combo Box)
            ExposedDropdownMenuBox(
                expanded = showMetodoPagoDropdown,
                onExpandedChange = { showMetodoPagoDropdown = !showMetodoPagoDropdown }
            ) {
                OutlinedTextField(
                    value = metodoPago.descripcion,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Método de Pago") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = showMetodoPagoDropdown
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPure,
                        unfocusedBorderColor = InputBorder
                    )
                )
                
                ExposedDropdownMenu(
                    expanded = showMetodoPagoDropdown,
                    onDismissRequest = { showMetodoPagoDropdown = false }
                ) {
                    MetodoPago.values().forEach { metodo ->
                        DropdownMenuItem(
                            text = { Text(metodo.descripcion) },
                            onClick = {
                                metodoPago = metodo
                                showMetodoPagoDropdown = false
                            }
                        )
                    }
                }
            }
            
            // Campo Observaciones
            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones") },
                placeholder = { Text("Observaciones adicionales (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RedPure,
                    unfocusedBorderColor = InputBorder
                )
            )
            
            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TextSecondary
                    )
                ) {
                    Text("Cancelar")
                }
                
                                Button(
                                    onClick = {
                                        val transaccion = TransaccionCreate(
                                            dniRuc = dniRuc,
                                            nombreCliente = nombreCliente,
                                            direccion = direccion,
                                            correoElectronico = correoElectronico,
                                            fechaEmision = fechaEmision,
                                            productoId = productoSeleccionado!!.id,
                                            precio = precio.toDouble(),
                                            cantidad = cantidad.toInt(),
                                            metodoPago = metodoPago.valor,
                                            observaciones = observaciones.ifBlank { null },
                                            empleadoId = empleado.id
                                        )

                                        transaccionViewModel.registrarTransaccion(
                                            transaccion = transaccion,
                                            onSuccess = {
                                                // Crear objeto Transaccion para el email
                                                val producto = productoSeleccionado!!
                                                val transaccionCompleta = Transaccion(
                                                    id = 1, // ID temporal, en producción vendría de la BD
                                                    dniRuc = transaccion.dniRuc,
                                                    nombreCliente = transaccion.nombreCliente,
                                                    direccion = transaccion.direccion,
                                                    correoElectronico = transaccion.correoElectronico,
                                                    fechaEmision = transaccion.fechaEmision,
                                                    productoId = transaccion.productoId,
                                                    precio = transaccion.precio,
                                                    cantidad = transaccion.cantidad,
                                                    metodoPago = transaccion.metodoPago,
                                                    observaciones = transaccion.observaciones,
                                                    empleadoId = transaccion.empleadoId,
                                                    tipoComprobante = if (transaccion.dniRuc.length == 8) TipoComprobante.BOLETA else TipoComprobante.FACTURA,
                                                    activo = true,
                                                    fechaCreacion = null,
                                                    fechaActualizacion = null,
                                                    productoNombre = producto.nombre,
                                                    productoCodigo = producto.codigo,
                                                    empleadoNombre = empleado.nombre
                                                )

                                                // Usar PDFDownloader para descarga y email automáticos
                                                if (context != null) {
                                                    // Llamar a la función de Android específica
                                                    try {
                                                        val pdfDownloaderClass = Class.forName("org.marimon.sigc.services.PDFDownloader")
                                                        val downloadMethod = pdfDownloaderClass.getMethod("downloadPDFWithEmail", 
                                                            android.content.Context::class.java,
                                                            org.marimon.sigc.model.Transaccion::class.java,
                                                            kotlin.jvm.functions.Function0::class.java,
                                                            kotlin.jvm.functions.Function1::class.java
                                                        )
                                                        
                                                        downloadMethod.invoke(null, context, transaccionCompleta,
                                                            { // onSuccess
                                                                transaccionGenerada = "Transacción registrada, PDF descargado y email enviado exitosamente"
                                                                showSuccessDialog = true
                                                            },
                                                            { error: String -> // onError
                                                                transaccionGenerada = "Transacción registrada pero error: $error"
                                                                showSuccessDialog = true
                                                            }
                                                        )
                                                    } catch (e: Exception) {
                                                        println("ERROR: No se pudo usar PDFDownloader: ${e.message}")
                                                        transaccionGenerada = "Transacción registrada pero error en PDF/Email: ${e.message}"
                                                        showSuccessDialog = true
                                                    }
                                                } else {
                                                    // Fallback si no hay contexto
                                                    transaccionGenerada = "Transacción registrada exitosamente"
                                                    showSuccessDialog = true
                                                }
                                            },
                                            onError = { error ->
                                                transaccionGenerada = error
                                                showSuccessDialog = true
                                            }
                                        )
                                    },
                    modifier = Modifier.weight(1f),
                    enabled = formValid && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedPure
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Generar")
                    }
                }
            }
            
            // Mostrar error si existe
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFFD32F2F)
                    )
                }
            }
        }
    }
    
    // Dialog de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Text(
                    text = if (transaccionGenerada.contains("Error")) "Error" else "¡Éxito!",
                    color = if (transaccionGenerada.contains("Error")) Color(0xFFD32F2F) else RedPure
                )
            },
            text = {
                Text(transaccionGenerada)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        if (!transaccionGenerada.contains("Error")) {
                            onSuccess(transaccionGenerada)
                        }
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = if (!transaccionGenerada.contains("Error")) {
                {
                    TextButton(
                        onClick = {
                            showSuccessDialog = false
                            // Generar y descargar PDF real
                            val producto = productoSeleccionado!!
                            val transaccionCompleta = Transaccion(
                                id = 1,
                                dniRuc = dniRuc,
                                nombreCliente = nombreCliente,
                                direccion = direccion,
                                correoElectronico = correoElectronico,
                                fechaEmision = fechaEmision,
                                productoId = producto.id,
                                precio = precio.toDouble(),
                                cantidad = cantidad.toInt(),
                                metodoPago = metodoPago.valor,
                                observaciones = observaciones.ifBlank { null },
                                empleadoId = empleado.id,
                                tipoComprobante = if (dniRuc.length == 8) TipoComprobante.BOLETA else TipoComprobante.FACTURA,
                                activo = true,
                                fechaCreacion = null,
                                fechaActualizacion = null,
                                productoNombre = producto.nombre,
                                productoCodigo = producto.codigo,
                                empleadoNombre = empleado.nombre
                            )
                            
                            val pdfContent = transaccionViewModel.generarPDF(transaccionCompleta)
                            val fileName = "${transaccionCompleta.tipoComprobante.valor}_${String.format("%08d", transaccionCompleta.id)}_${transaccionCompleta.dniRuc}"
                            
                            // Descargar PDF automáticamente usando corrutina
                            if (context != null) {
                                kotlinx.coroutines.GlobalScope.launch {
                                    try {
                                        val pdfDownloaderClass = Class.forName("org.marimon.sigc.services.PDFDownloader")
                                        
                                        // Para funciones suspend, necesitamos usar getDeclaredMethod
                                        val downloadMethod = pdfDownloaderClass.getDeclaredMethod("downloadPDF", 
                                            android.content.Context::class.java,
                                            org.marimon.sigc.model.Transaccion::class.java
                                        )
                                        
                                        // Llamar el método suspend
                                        downloadMethod.invoke(null, context, transaccionCompleta)
                                        println("PDF descargado automáticamente")
                                    } catch (e: Exception) {
                                        println("ERROR: No se pudo descargar PDF: ${e.message}")
                                        e.printStackTrace()
                                    }
                                }
                            } else {
                                println("No hay contexto disponible para descargar PDF")
                            }
                        }
                    ) {
                        Text("Descargar PDF")
                    }
                }
            } else null
        )
    }
}
