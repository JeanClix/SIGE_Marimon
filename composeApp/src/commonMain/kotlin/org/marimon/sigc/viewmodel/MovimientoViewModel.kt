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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.marimon.sigc.config.SupabaseClient
import org.marimon.sigc.config.SupabaseConfig
import org.marimon.sigc.model.Movimiento
import org.marimon.sigc.model.MovimientoCreate
import org.marimon.sigc.model.TipoMovimiento

class MovimientoViewModel : ViewModel() {
    val movimientos: SnapshotStateList<Movimiento> = mutableStateListOf()
    
    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading.value
    
    private val _error = mutableStateOf<String?>(null)
    val error = _error.value
    
    fun cargarMovimientos() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/movimientos?select=*,productos(nombre,codigo,imagen_url),empleados(nombre)&activo=eq.true&order=fecha_registro.desc"
                val headers = mapOf(
                    "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                    "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}"
                )

                val response: HttpResponse = SupabaseClient.httpClient.get(url) {
                    headers.forEach { (k, v) -> header(k, v) }
                }

                if (response.status.isSuccess()) {
                    val movimientosJson = Json.parseToJsonElement(response.bodyAsText()).jsonArray
                    movimientos.clear()

                    for (row in movimientosJson) {
                        val obj = row.jsonObject
                        val producto = obj["productos"]?.jsonObject
                        val empleado = obj["empleados"]?.jsonObject
                        
                        movimientos.add(
                            Movimiento(
                                id = obj["id"]!!.toString().toInt(),
                                tipo = if (obj["tipo"]!!.toString().replace("\"", "") == "ENTRADA") TipoMovimiento.ENTRADA else TipoMovimiento.SALIDA,
                                productoId = obj["producto_id"]!!.toString().toInt(),
                                empleadoId = obj["empleado_id"]!!.toString().toInt(),
                                cantidad = obj["cantidad"]!!.toString().toInt(),
                                nota = obj["nota"]?.toString()?.replace("\"", "")?.trim(),
                                fechaRegistro = obj["fecha_registro"]!!.toString().replace("\"", ""),
                                createdAt = obj["created_at"]!!.toString().replace("\"", ""),
                                updatedAt = obj["updated_at"]!!.toString().replace("\"", ""),
                                activo = obj["activo"]!!.toString().toBoolean(),
                                productoNombre = producto?.get("nombre")?.toString()?.replace("\"", ""),
                                productoCodigo = producto?.get("codigo")?.toString()?.replace("\"", ""),
                                productoImagenUrl = producto?.get("imagen_url")?.toString()?.replace("\"", ""),
                                empleadoNombre = empleado?.get("nombre")?.toString()?.replace("\"", "")
                            )
                        )
                    }
                    _error.value = null
                } else {
                    _error.value = "Error al cargar movimientos: ${response.status}"
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar movimientos: ${e.message}"
                println("❌ Error cargando movimientos: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun cargarMovimientosPorTipo(tipo: TipoMovimiento) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val tipoStr = if (tipo == TipoMovimiento.ENTRADA) "ENTRADA" else "SALIDA"
                val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/movimientos?select=*,productos(nombre,codigo,imagen_url),empleados(nombre)&activo=eq.true&tipo=eq.$tipoStr&order=fecha_registro.desc"
                val headers = mapOf(
                    "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                    "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}"
                )

                val response: HttpResponse = SupabaseClient.httpClient.get(url) {
                    headers.forEach { (k, v) -> header(k, v) }
                }

                if (response.status.isSuccess()) {
                    val movimientosJson = Json.parseToJsonElement(response.bodyAsText()).jsonArray
                    movimientos.clear()

                    for (row in movimientosJson) {
                        val obj = row.jsonObject
                        val producto = obj["productos"]?.jsonObject
                        val empleado = obj["empleados"]?.jsonObject
                        
                        movimientos.add(
                            Movimiento(
                                id = obj["id"]!!.toString().toInt(),
                                tipo = if (obj["tipo"]!!.toString().replace("\"", "") == "ENTRADA") TipoMovimiento.ENTRADA else TipoMovimiento.SALIDA,
                                productoId = obj["producto_id"]!!.toString().toInt(),
                                empleadoId = obj["empleado_id"]!!.toString().toInt(),
                                cantidad = obj["cantidad"]!!.toString().toInt(),
                                nota = obj["nota"]?.toString()?.replace("\"", "")?.trim(),
                                fechaRegistro = obj["fecha_registro"]!!.toString().replace("\"", ""),
                                createdAt = obj["created_at"]!!.toString().replace("\"", ""),
                                updatedAt = obj["updated_at"]!!.toString().replace("\"", ""),
                                activo = obj["activo"]!!.toString().toBoolean(),
                                productoNombre = producto?.get("nombre")?.toString()?.replace("\"", ""),
                                productoCodigo = producto?.get("codigo")?.toString()?.replace("\"", ""),
                                productoImagenUrl = producto?.get("imagen_url")?.toString()?.replace("\"", ""),
                                empleadoNombre = empleado?.get("nombre")?.toString()?.replace("\"", "")
                            )
                        )
                    }
                    _error.value = null
                } else {
                    _error.value = "Error al cargar movimientos: ${response.status}"
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar movimientos: ${e.message}"
                println("❌ Error cargando movimientos por tipo: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun registrarMovimiento(movimiento: MovimientoCreate, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}): Boolean {
        viewModelScope.launch {
            try {
                // 0. Verificar primero si el producto existe
                val productoExiste = verificarProductoExiste(movimiento.productoId)
                if (!productoExiste) {
                    val errorMessage = "El producto con ID ${movimiento.productoId} no existe"
                    _error.value = errorMessage
                    onError(errorMessage)
                    return@launch
                }

                // 1. Usar el empleado_id proporcionado del empleado logueado
                val empleadoIdSeguro = movimiento.empleadoId

                // 2. Registrar el movimiento en la base de datos
                val tipoStr = if (movimiento.tipo == TipoMovimiento.ENTRADA) "ENTRADA" else "SALIDA"
                val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/movimientos"
                val headers = mapOf(
                    "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                    "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}",
                    "Content-Type" to "application/json",
                    "Prefer" to "return=representation"
                )

                val body = """
                    {
                        "tipo": "$tipoStr",
                        "producto_id": ${movimiento.productoId},
                        "empleado_id": $empleadoIdSeguro,
                        "cantidad": ${movimiento.cantidad},
                        "nota": ${if (movimiento.nota != null) "\"${movimiento.nota?.replace("\"", "\\\"")}\"" else "null"},
                        "activo": true
                    }
                """.trimIndent()

                println("🔍 Enviando datos a Supabase:")
                println("   URL: $url")
                println("   Body: $body")

                val response: HttpResponse = SupabaseClient.httpClient.post(url) {
                    headers.forEach { (k, v) -> header(k, v) }
                    setBody(body)
                }

                if (response.status.isSuccess()) {
                    // 2. Actualizar el stock del producto
                    val updateStockSuccess = actualizarStockProducto(movimiento.productoId, movimiento.cantidad, movimiento.tipo)
                    
                    if (updateStockSuccess) {
                        println("✅ Movimiento registrado exitosamente")
                        _error.value = null
                        onSuccess()
                        
                        // 3. Recargar movimientos para mostrar el nuevo
                        cargarMovimientosPorTipo(movimiento.tipo)
                    } else {
                        _error.value = "Movimiento registrado pero error actualizando stock"
                        onError("Error actualizando stock del producto")
                    }
                // Si hay error 409, intentar crear la tabla automáticamente
                } else if (response.status.value == 409) {
                    println("❌ Error 409 - Posible problema de estructura de tabla")
                    println("🔄 Intentando registrar con estructura alternativa...")
                    
                    // Intentar con estructura mínima
                    val bodyMinimo = """
                        {
                            "tipo": "$tipoStr",
                            "producto_id": ${movimiento.productoId},
                            "empleado_id": $empleadoIdSeguro,
                            "cantidad": ${movimiento.cantidad}
                        }
                    """.trimIndent()
                    
                    val responseAlternativo: HttpResponse = SupabaseClient.httpClient.post(url) {
                        headers.forEach { (k, v) -> header(k, v) }
                        setBody(bodyMinimo)
                    }
                    
                    if (responseAlternativo.status.isSuccess()) {
                        println("✅ Registro exitoso con estructura mínima")
                        val updateStockSuccess = actualizarStockProducto(movimiento.productoId, movimiento.cantidad, movimiento.tipo)
                        if (updateStockSuccess) {
                            _error.value = null
                            onSuccess()
                            cargarMovimientosPorTipo(movimiento.tipo)
                        } else {
                            onError("Movimiento registrado pero error actualizando stock")
                        }
                    } else {
                        val responseBody = try { responseAlternativo.bodyAsText() } catch (e: Exception) { "Sin detalles" }
                        val errorMessage = "Error 409: Conflicto en base de datos. Verifica que las tablas existan."
                        println("❌ $errorMessage")
                        println("❌ Respuesta: $responseBody")
                        _error.value = errorMessage
                        onError(errorMessage)
                    }
                } else {
                    // Obtener más detalles del error
                    val responseBody = try { 
                        response.bodyAsText() 
                    } catch (e: Exception) { 
                        "No se pudo obtener el cuerpo de la respuesta" 
                    }
                    val errorMessage = "Error al registrar movimiento: ${response.status}"
                    println("❌ $errorMessage")
                    println("❌ Detalles del error: $responseBody")
                    println("❌ Datos enviados: $body")
                    _error.value = errorMessage
                    onError(errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message}"
                _error.value = errorMessage
                onError(errorMessage)
                println("❌ Error registrando movimiento: ${e.message}")
            }
        }
        return true // Retorna true inmediatamente, el resultado real se maneja en los callbacks
    }
    
    private suspend fun verificarProductoExiste(productoId: Int): Boolean {
        return try {
            val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/productos?id=eq.$productoId&select=id"
            val headers = mapOf(
                "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}"
            )

            val response: HttpResponse = SupabaseClient.httpClient.get(url) {
                headers.forEach { (k, v) -> header(k, v) }
            }

            if (response.status.isSuccess()) {
                val productos = Json.parseToJsonElement(response.bodyAsText()).jsonArray
                val existe = productos.isNotEmpty()
                println("🔍 Verificación producto ID $productoId: ${if (existe) "✅ Existe" else "❌ No existe"}")
                existe
            } else {
                println("❌ Error verificando producto: ${response.status}")
                false
            }
        } catch (e: Exception) {
            println("❌ Error verificando producto: ${e.message}")
            false
        }
    }
    
    private suspend fun actualizarStockProducto(productoId: Int, cantidad: Int, tipo: TipoMovimiento): Boolean {
        return try {
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
                        println("❌ Error: Stock insuficiente. Stock actual: $stockActual, cantidad solicitada: $cantidad")
                        return false
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
                        println("✅ Stock actualizado: $stockActual → $nuevoStock")
                        return true
                    } else {
                        println("❌ Error actualizando stock: ${updateResponse.status}")
                        return false
                    }
                } else {
                    println("❌ Producto no encontrado")
                    return false
                }
            } else {
                println("❌ Error obteniendo stock actual: ${getResponse.status}")
                return false
            }
        } catch (e: Exception) {
            println("❌ Error actualizando stock: ${e.message}")
            false
        }
    }
    
    fun obtenerMovimientosPorProducto(productoId: Int): List<Movimiento> {
        return movimientos.filter { it.productoId == productoId }
    }
    
    fun obtenerUltimosMovimientos(limit: Int = 10): List<Movimiento> {
        return movimientos.take(limit)
    }
}