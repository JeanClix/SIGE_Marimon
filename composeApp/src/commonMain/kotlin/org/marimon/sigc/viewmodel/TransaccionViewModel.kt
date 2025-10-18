package org.marimon.sigc.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.marimon.sigc.config.SupabaseClient
import org.marimon.sigc.config.SupabaseConfig
import org.marimon.sigc.model.Producto
import org.marimon.sigc.model.Transaccion
import org.marimon.sigc.model.TransaccionCreate
import org.marimon.sigc.model.TipoComprobante
import org.marimon.sigc.model.MetodoPago
import org.marimon.sigc.services.PDFGenerator
import org.marimon.sigc.services.EmailService
import org.marimon.sigc.model.MovimientoCreate
import org.marimon.sigc.model.TipoMovimiento
import org.marimon.sigc.model.generarMovimientoDesdeTransaccionCreate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TransaccionViewModel : ViewModel() {
    private val _transacciones = mutableStateListOf<Transaccion>()
    val transacciones: SnapshotStateList<Transaccion> = _transacciones
    
    private val _productos = mutableStateListOf<Producto>()
    val productos: SnapshotStateList<Producto> = _productos
    
    private val _isLoading = mutableStateOf(false)
    val isLoading get() = _isLoading.value
    
    private val _error = mutableStateOf<String?>(null)
    val error get() = _error.value
    
    private val _isGeneratingPDF = mutableStateOf(false)
    val isGeneratingPDF get() = _isGeneratingPDF.value
    
    private val _isSendingEmail = mutableStateOf(false)
    val isSendingEmail get() = _isSendingEmail.value
    
    fun cargarProductos() {
        _isLoading.value = true
        viewModelScope.launch { // Usar el dispatcher por defecto
            try {
                val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/productos?select=*&activo=eq.true&order=fecha_creacion.desc"
                println("DEBUG: URL de productos: $url")
                val headers = mapOf(
                    "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                    "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}"
                )

                val response: HttpResponse = SupabaseClient.httpClient.get(url) {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                }

                println("DEBUG: Status de respuesta: ${response.status}")
                println("DEBUG: Body de respuesta: ${response.bodyAsText()}")

                if (response.status.isSuccess()) {
                    val productosJson = Json.parseToJsonElement(response.bodyAsText()).jsonArray
                    println("DEBUG: Productos JSON array size: ${productosJson.size}")

                    val productosList = mutableListOf<Producto>()

                    for (row in productosJson) {
                        try {
                            val obj = row.jsonObject
                            val producto = Producto(
                                id = obj["id"]!!.toString().toInt(),
                                codigo = obj["codigo"]!!.toString().replace("\"", "").trim(),
                                nombre = obj["nombre"]!!.toString().replace("\"", "").trim(),
                                descripcion = obj["descripcion"]?.toString()?.replace("\"", "")?.trim(),
                                especificaciones = obj["especificaciones"]?.toString()?.replace("\"", "")?.trim(),
                                precio = obj["precio"]!!.toString().toDouble(),
                                cantidad = obj["cantidad"]!!.toString().toInt(),
                                imagenUrl = obj["imagen_url"]?.toString()?.replace("\"", "")?.trim(),
                                activo = obj["activo"]!!.toString().toBoolean()
                            )
                            productosList.add(producto)
                            println("DEBUG: Producto agregado: ${producto.nombre} (ID: ${producto.id}, Activo: ${producto.activo})")
                        } catch (e: Exception) {
                            println("ERROR: Error procesando producto: ${e.message}")
                        }
                    }

                    // Actualizar UI en el hilo principal
                    withContext(Dispatchers.Main) {
                        _productos.clear()
                        _productos.addAll(productosList)
                        println("DEBUG: Productos cargados exitosamente: ${_productos.size}")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _error.value = "Error al cargar productos: ${response.status}"
                    }
                    println("ERROR: Error al cargar productos: ${response.status}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _error.value = "Error de conexión: ${e.message}"
                }
                println("ERROR: Excepción al cargar productos: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun registrarTransaccion(
        transaccion: TransaccionCreate,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ): Boolean {
        _isLoading.value = true
        viewModelScope.launch { // Usar el dispatcher por defecto
            try {
                // Determinar tipo de comprobante basado en la longitud del DNI/RUC
                val tipoComprobante = if (transaccion.dniRuc.length == 8) {
                    TipoComprobante.BOLETA
                } else {
                    TipoComprobante.FACTURA
                }
                
                val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/transacciones"
                println("DEBUG: URL de transacciones: $url")
                val headers = mapOf(
                    "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                    "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}",
                    "Content-Type" to "application/json",
                    "Prefer" to "return=representation"
                )

                // Construir JSON de forma segura usando StringBuilder
                val body = buildString {
                    append("{")
                    append("\"dni_ruc\":\"${transaccion.dniRuc}\",")
                    append("\"nombre_cliente\":\"${transaccion.nombreCliente.trim().replace("\"", "\\\"")}\",")
                    append("\"direccion\":\"${transaccion.direccion.trim().replace("\"", "\\\"")}\",")
                    append("\"correo_electronico\":\"${transaccion.correoElectronico}\",")
                    append("\"fecha_emision\":\"${transaccion.fechaEmision}\",")
                    append("\"producto_id\":${transaccion.productoId},")
                    append("\"precio\":${transaccion.precio},")
                    append("\"cantidad\":${transaccion.cantidad},")
                    append("\"metodo_pago\":\"${transaccion.metodoPago}\",")
                    append("\"observaciones\":${if (transaccion.observaciones != null) "\"${transaccion.observaciones.replace("\"", "\\\"")}\"" else "null"},")
                    append("\"empleado_id\":${transaccion.empleadoId},")
                    append("\"tipo_comprobante\":\"${tipoComprobante.valor}\",")
                    append("\"activo\":true")
                    append("}")
                }
                
                println("DEBUG: Body de la transacción: $body")

                val response: HttpResponse = SupabaseClient.httpClient.post(url) {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                    setBody(body)
                }

                println("DEBUG: Status de respuesta transacción: ${response.status}")
                println("DEBUG: Body de respuesta transacción: ${response.bodyAsText()}")

                if (response.status.isSuccess()) {
                    println("DEBUG: Transacción registrada exitosamente")
                    
                    // Parsear la respuesta para obtener el ID de la transacción creada
                    val responseBody = response.bodyAsText()
                    val transaccionesJson = Json.parseToJsonElement(responseBody).jsonArray
                    
                    if (transaccionesJson.isNotEmpty()) {
                        val transaccionCreada = transaccionesJson[0].jsonObject
                        val transaccionId = transaccionCreada["id"]!!.toString().toInt()
                        
                        // Generar movimiento de salida automático
                        val movimientoSalida = generarMovimientoDesdeTransaccionCreate(transaccion, transaccionId)
                        registrarMovimientoSalida(movimientoSalida) { movimientoExito ->
                            if (movimientoExito) {
                                println("DEBUG: Movimiento de salida registrado exitosamente")
                            } else {
                                println("WARNING: Error al registrar movimiento de salida automático")
                            }
                        }
                    }
                    
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    val errorMessage = "Error al registrar transacción: ${response.status.value} - ${response.bodyAsText()}"
                    withContext(Dispatchers.Main) {
                        _error.value = errorMessage
                        onError(errorMessage)
                    }
                    println("ERROR: $errorMessage")
                }
            } catch (e: Exception) {
                val errorMessage = "Error de conexión: ${e.message}"
                withContext(Dispatchers.Main) {
                    _error.value = errorMessage
                    onError(errorMessage)
                }
                println("ERROR: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
        return true
    }
    
    fun generarPDF(transaccion: Transaccion): String {
        return PDFGenerator.generarComprobantePDF(transaccion)
    }
    
            suspend fun enviarEmailConPDF(
                transaccion: Transaccion,
                onSuccess: () -> Unit = {},
                onError: (String) -> Unit = {}
            ): Boolean {
                _isSendingEmail.value = true
                return try {
                    // Generar PDF
                    val pdfContent = PDFGenerator.generarComprobantePDF(transaccion)

                    // Generar contenido del email
                    val asunto = EmailService.generarAsuntoEmail(
                        transaccion.tipoComprobante.descripcion,
                        transaccion.id.toString().padStart(8, '0')
                    )
                    val contenido = EmailService.generarContenidoEmail(
                        transaccion.nombreCliente,
                        transaccion.tipoComprobante.descripcion,
                        transaccion.id.toString().padStart(8, '0'),
                        transaccion.precio * transaccion.cantidad
                    )

                    // Enviar email
                    val enviado = EmailService.enviarEmailConPDF(
                        destinatario = transaccion.correoElectronico,
                        asunto = asunto,
                        contenido = contenido,
                        pdfContent = pdfContent,
                        nombreCliente = transaccion.nombreCliente,
                        tipoComprobante = transaccion.tipoComprobante.descripcion,
                        numeroComprobante = transaccion.id.toString().padStart(8, '0'),
                        total = transaccion.precio * transaccion.cantidad
                    )

                    if (enviado) {
                        println("DEBUG: Email enviado exitosamente")
                        onSuccess()
                    } else {
                        onError("Error al enviar el email")
                    }

                    enviado
                } catch (e: Exception) {
                    onError("Error generando PDF o enviando email: ${e.message}")
                    false
                } finally {
                    _isSendingEmail.value = false
                }
            }
    
    fun limpiarError() {
        _error.value = null
    }
    
    /**
     * Registra un movimiento de salida en Supabase
     */
    private suspend fun registrarMovimientoSalida(
        movimiento: MovimientoCreate,
        onComplete: (Boolean) -> Unit
    ) {
        try {
            val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/movimientos"
            val headers = mapOf(
                "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}",
                "Content-Type" to "application/json",
                "Prefer" to "return=representation"
            )

            val body = buildString {
                append("{")
                append("\"tipo\":\"${movimiento.tipo.valor}\",")
                append("\"producto_id\":${movimiento.productoId},")
                append("\"empleado_id\":${movimiento.empleadoId},")
                append("\"cantidad\":${movimiento.cantidad},")
                append("\"nota\":${if (movimiento.nota != null) {
                    val notaLimpia = movimiento.nota
                        .replace("\\", "\\\\")  // Escapar backslashes
                        .replace("\"", "\\\"")  // Escapar comillas dobles
                        .replace("\t", "\\t")   // Escapar tabs
                        .replace("\n", "\\n")   // Escapar saltos de línea
                        .replace("\r", "\\r")   // Escapar retornos de carro
                        .trim()                 // Eliminar espacios al inicio y final
                    "\"$notaLimpia\""
                } else "null"},")
                append("\"transaccion_id\":${movimiento.transaccionId},")
                append("\"fecha_registro\":\"${Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())}\",")
                append("\"activo\":true")
                append("}")
            }

            println("DEBUG: Registrando movimiento de salida: $body")

            val response: HttpResponse = SupabaseClient.httpClient.post(url) {
                headers.forEach { (key, value) ->
                    header(key, value)
                }
                setBody(body)
            }

            println("DEBUG: Status movimiento: ${response.status}")
            println("DEBUG: Response movimiento: ${response.bodyAsText()}")

            val exito = response.status.isSuccess()
            
            // También actualizar el stock del producto
            if (exito) {
                actualizarStockProducto(movimiento.productoId, movimiento.cantidad, TipoMovimiento.SALIDA) { stockActualizado ->
                    if (stockActualizado) {
                        println("DEBUG: Stock actualizado exitosamente")
                    } else {
                        println("WARNING: Error al actualizar stock del producto")
                    }
                    onComplete(exito)
                }
            } else {
                onComplete(false)
            }
            
        } catch (e: Exception) {
            println("ERROR: Error registrando movimiento de salida: ${e.message}")
            onComplete(false)
        }
    }
    
    /**
     * Actualiza el stock de un producto después de un movimiento
     */
    private suspend fun actualizarStockProducto(
        productoId: Int,
        cantidad: Int,
        tipo: TipoMovimiento,
        onComplete: (Boolean) -> Unit
    ) {
        try {
            // Primero obtener el stock actual
            val getUrl = "${SupabaseConfig.SUPABASE_URL}/rest/v1/productos?id=eq.$productoId&select=cantidad"
            val headers = mapOf(
                "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}"
            )
            
            val getResponse: HttpResponse = SupabaseClient.httpClient.get(getUrl) {
                headers.forEach { (k, v) -> header(k, v) }
            }
            
            if (getResponse.status.isSuccess()) {
                val productosJson = Json.parseToJsonElement(getResponse.bodyAsText()).jsonArray
                if (productosJson.isNotEmpty()) {
                    val stockActual = productosJson[0].jsonObject["cantidad"]!!.toString().toInt()
                    
                    // Calcular nuevo stock
                    val nuevoStock = if (tipo == TipoMovimiento.ENTRADA) {
                        stockActual + cantidad
                    } else {
                        stockActual - cantidad
                    }
                    
                    // Validar que no quede en negativo para salidas
                    if (nuevoStock < 0) {
                        println("ERROR: Stock insuficiente. Stock actual: $stockActual, Cantidad solicitada: $cantidad")
                        onComplete(false)
                        return
                    }
                    
                    // Actualizar el stock
                    val updateUrl = "${SupabaseConfig.SUPABASE_URL}/rest/v1/productos?id=eq.$productoId"
                    val updateHeaders = mapOf(
                        "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                        "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}",
                        "Content-Type" to "application/json"
                    )
                    
                    val updateBody = """
                        {
                            "cantidad": $nuevoStock
                        }
                    """.trimIndent()
                    
                    val updateResponse: HttpResponse = SupabaseClient.httpClient.patch(updateUrl) {
                        updateHeaders.forEach { (k, v) -> header(k, v) }
                        setBody(updateBody)
                    }
                    
                    if (updateResponse.status.isSuccess()) {
                        println("DEBUG: Stock actualizado de $stockActual a $nuevoStock para producto $productoId")
                        onComplete(true)
                    } else {
                        println("ERROR: Error actualizando stock: ${updateResponse.status}")
                        onComplete(false)
                    }
                } else {
                    println("ERROR: Producto no encontrado: $productoId")
                    onComplete(false)
                }
            } else {
                println("ERROR: Error obteniendo stock actual: ${getResponse.status}")
                onComplete(false)
            }
        } catch (e: Exception) {
            println("ERROR: Error actualizando stock: ${e.message}")
            onComplete(false)
        }
    }
}
