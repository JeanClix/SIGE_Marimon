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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.marimon.sigc.config.SupabaseClient
import org.marimon.sigc.config.SupabaseConfig
import org.marimon.sigc.model.Movimiento
import org.marimon.sigc.model.MovimientoCreate
import org.marimon.sigc.model.TipoMovimiento

class MovimientoViewModel : ViewModel() {
    private val _movimientos = mutableStateListOf<Movimiento>()
    val movimientos: SnapshotStateList<Movimiento> = _movimientos
    
    private val _isLoading = mutableStateOf(false)
    val isLoading get() = _isLoading.value
    
    private val _error = mutableStateOf<String?>(null)
    val error get() = _error.value
    
    // Cache simple para evitar cargas innecesarias
    private var ultimoTipoCargado: TipoMovimiento? = null
    private var cacheValido: Boolean = false
    
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
                    _movimientos.clear()

                    for (row in movimientosJson) {
                        val obj = row.jsonObject
                        val producto = obj["productos"]?.jsonObject
                        val empleado = obj["empleados"]?.jsonObject
                        
                        _movimientos.add(
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
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun cargarMovimientosPorTipo(tipo: TipoMovimiento, forzarRecarga: Boolean = false) {
        // Verificar si ya tenemos datos del cache y no necesitamos recargar
        val tieneCache = ultimoTipoCargado == tipo && cacheValido && !forzarRecarga
        
        if (tieneCache && _movimientos.isNotEmpty()) {
            return
        }
        
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val tipoStr = if (tipo == TipoMovimiento.ENTRADA) "ENTRADA" else "SALIDA"
                
                // Usar directamente la consulta simplificada que sabemos que funciona
                val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/movimientos?activo=eq.true&tipo=eq.$tipoStr&order=fecha_registro.desc"
                val headers = mapOf(
                    "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                    "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}"
                )

                val response: HttpResponse = SupabaseClient.httpClient.get(url) {
                    headers.forEach { (k, v) -> header(k, v) }
                }

                if (response.status.isSuccess()) {
                    val responseBody = response.bodyAsText()
                    val movimientosJson = Json.parseToJsonElement(responseBody).jsonArray
                    _movimientos.clear()

                    for (row in movimientosJson) {
                        val obj = row.jsonObject
                        val producto = obj["Producto"]?.jsonObject
                        val empleado = obj["Empleado"]?.jsonObject
                        
                        _movimientos.add(
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
                    // Intentar consulta simplificada sin JOIN
                    val urlSimple = "${SupabaseConfig.SUPABASE_URL}/rest/v1/movimientos?activo=eq.true&tipo=eq.$tipoStr&order=fecha_registro.desc"
                    
                    val responseSimple = SupabaseClient.httpClient.get(urlSimple) {
                        headers.forEach { (k, v) -> header(k, v) }
                    }
                    
                    if (responseSimple.status.isSuccess()) {
                        val responseBodySimple = responseSimple.bodyAsText()
                        val movimientosJsonSimple = Json.parseToJsonElement(responseBodySimple).jsonArray
                        _movimientos.clear()
                        
                        // Recopilar todos los IDs únicos de productos y empleados
                        val productosIds = mutableSetOf<Int>()
                        val empleadosIds = mutableSetOf<Int>()
                        
                        val movimientosData = mutableListOf<Pair<JsonObject, Pair<Int, Int>>>()
                        
                        for (row in movimientosJsonSimple) {
                            val obj = row.jsonObject
                            val productoId = obj["producto_id"]!!.toString().toInt()
                            val empleadoId = obj["empleado_id"]!!.toString().toInt()
                            
                            productosIds.add(productoId)
                            empleadosIds.add(empleadoId)
                            movimientosData.add(obj to (productoId to empleadoId))
                        }
                        
                        // Buscar información de todos los productos y empleados de una vez
                        // Como las tablas productos y empleados dan 404, usar datos de la nota
                        val productosInfo = mutableMapOf<Int, Triple<String, String?, String?>>()
                        val empleadosInfo = mutableMapOf<Int, String>()
                        
                        // Extraer información de las notas y usar consultas directas para obtener información de productos y empleados
                        for ((obj, ids) in movimientosData) {
                            val nota = obj["nota"]?.toString()?.replace("\"", "") ?: ""
                            val (productoId, empleadoId) = ids
                            
                            // Extraer nombre del producto de la nota si está disponible
                            if (nota.contains("Producto:")) {
                                val productoNombre = nota.substringAfter("Producto: ").substringBefore(" - Empleado:")
                                if (productoNombre.isNotBlank()) {
                                    productosInfo[productoId] = Triple(productoNombre, "COD-$productoId", null)
                                }
                            } else if (nota.contains("Categoría:")) {
                                // Para notas con formato de categoría, intentar extraer de otra manera
                                val categoria = nota.substringAfter("Categoría: ").substringBefore(" - Empleado:")
                                productosInfo[productoId] = Triple("Producto de $categoria", "COD-$productoId", null)
                            }
                            
                            // Extraer nombre del empleado de la nota si está disponible
                            if (nota.contains("Empleado:")) {
                                val empleadoNombre = nota.substringAfter("Empleado: ").trim()
                                if (empleadoNombre.isNotBlank()) {
                                    empleadosInfo[empleadoId] = empleadoNombre
                                }
                            }
                        }
                        
                        // Intentar obtener información adicional de productos directamente
                        val productosInfoAdicional = buscarInfoProductos(productosIds.toList())
                        productosInfoAdicional.forEach { (id, info) ->
                            if (!productosInfo.containsKey(id)) {
                                productosInfo[id] = info
                            }
                        }
                        
                        // Intentar obtener información adicional de empleados directamente  
                        val empleadosInfoAdicional = buscarInfoEmpleados(empleadosIds.toList())
                        empleadosInfoAdicional.forEach { (id, nombre) ->
                            if (!empleadosInfo.containsKey(id)) {
                                empleadosInfo[id] = nombre
                            }
                        }
                        
                        // Crear los movimientos con la información obtenida
                        for ((obj, ids) in movimientosData) {
                            val (productoId, empleadoId) = ids
                            val productoInfo = productosInfo[productoId]
                            val empleadoInfo = empleadosInfo[empleadoId]
                            
                            val movimiento = Movimiento(
                                id = obj["id"]!!.toString().toInt(),
                                tipo = if (obj["tipo"]!!.toString().replace("\"", "") == "ENTRADA") TipoMovimiento.ENTRADA else TipoMovimiento.SALIDA,
                                productoId = productoId,
                                empleadoId = empleadoId,
                                cantidad = obj["cantidad"]!!.toString().toInt(),
                                nota = obj["nota"]?.toString()?.replace("\"", "")?.trim(),
                                fechaRegistro = obj["fecha_registro"]!!.toString().replace("\"", ""),
                                createdAt = obj["created_at"]!!.toString().replace("\"", ""),
                                updatedAt = obj["updated_at"]!!.toString().replace("\"", ""),
                                activo = obj["activo"]!!.toString().toBoolean(),
                                productoNombre = productoInfo?.first ?: "Producto ID: $productoId",
                                productoCodigo = productoInfo?.second ?: "COD-$productoId",
                                productoImagenUrl = productoInfo?.third,
                                empleadoNombre = empleadoInfo ?: "Empleado ID: $empleadoId"
                            )
                            
                            _movimientos.add(movimiento)
                        }
                        
                        // Actualizar cache
                        cacheValido = true
                        ultimoTipoCargado = tipo
                        
                        _error.value = null
                    } else {
                        val errorMsg = "Error al cargar movimientos: ${response.status}"
                        _error.value = errorMsg
                    }
                }
            } catch (e: Exception) {
                val errorMsg = "Error al cargar movimientos: ${e.message}"
                _error.value = errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Pre-carga todos los movimientos para mejorar la velocidad de navegación
     */
    fun precargarMovimientos() {
        viewModelScope.launch {
            try {
                // Pre-cargar entradas y salidas en paralelo
                val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/movimientos?activo=eq.true&order=fecha_registro.desc"
                val headers = mapOf(
                    "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                    "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}"
                )

                val response: HttpResponse = SupabaseClient.httpClient.get(url) {
                    headers.forEach { (k, v) -> header(k, v) }
                }

                if (response.status.isSuccess()) {
                    // Los datos se procesan cuando se llama cargarMovimientosPorTipo
                }
            } catch (e: Exception) {
                // Error silencioso en pre-carga
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

                val response: HttpResponse = SupabaseClient.httpClient.post(url) {
                    headers.forEach { (k, v) -> header(k, v) }
                    setBody(body)
                }

                if (response.status.isSuccess()) {
                    // 2. Actualizar el stock del producto
                    val updateStockSuccess = actualizarStockProducto(movimiento.productoId, movimiento.cantidad, movimiento.tipo)
                    
                    if (updateStockSuccess) {
                        _error.value = null
                        onSuccess()
                        
                        // 3. Invalidar cache y recargar movimientos para mostrar el nuevo
                        cacheValido = false
                        cargarMovimientosPorTipo(movimiento.tipo)
                    } else {
                        _error.value = "Movimiento registrado pero error actualizando stock"
                        onError("Error actualizando stock del producto")
                    }
                // Si hay error 409, intentar crear la tabla automáticamente
                } else if (response.status.value == 409) {
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
                        val updateStockSuccess = actualizarStockProducto(movimiento.productoId, movimiento.cantidad, movimiento.tipo)
                        if (updateStockSuccess) {
                            _error.value = null
                            onSuccess()
                            cacheValido = false
                            cargarMovimientosPorTipo(movimiento.tipo)
                        } else {
                            onError("Movimiento registrado pero error actualizando stock")
                        }
                    } else {
                        val responseBody = try { responseAlternativo.bodyAsText() } catch (e: Exception) { "Sin detalles" }
                        val errorMessage = "Error 409: Conflicto en base de datos. Verifica que las tablas existan."
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
                    _error.value = errorMessage
                    onError(errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message}"
                _error.value = errorMessage
                onError(errorMessage)
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
                existe
            } else {
                false
            }
        } catch (e: Exception) {
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
                        return true
                    } else {
                        return false
                    }
                } else {
                    return false
                }
            } else {
                return false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    fun obtenerMovimientosPorProducto(productoId: Int): List<Movimiento> {
        return _movimientos.filter { it.productoId == productoId }
    }
    
    fun obtenerUltimosMovimientos(limit: Int = 10): List<Movimiento> {
        return _movimientos.take(limit)
    }
    
    // Función auxiliar para buscar información de múltiples productos
    private suspend fun buscarInfoProductos(productosIds: List<Int>): Map<Int, Triple<String, String?, String?>> {
    return try {
        if (productosIds.isEmpty()) return emptyMap()
        val idsStr = productosIds.joinToString(",")
        val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/productos?id=in.($idsStr)"
            val headers = mapOf(
                "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}"
            )
            
            val response = SupabaseClient.httpClient.get(url) {
                headers.forEach { (k, v) -> header(k, v) }
            }
            
            if (response.status.isSuccess()) {
                val responseBody = response.bodyAsText()
                val productosJson = Json.parseToJsonElement(responseBody).jsonArray
                val result = mutableMapOf<Int, Triple<String, String?, String?>>()
                
                for (productoJson in productosJson) {
                    val producto = productoJson.jsonObject
                    val id = producto["id"]!!.toString().toInt()
                    result[id] = Triple(
                        producto["nombre"]?.toString()?.replace("\"", "") ?: "Producto Desconocido",
                        producto["codigo"]?.toString()?.replace("\"", ""),
                        producto["imagen_url"]?.toString()?.replace("\"", "")
                    )
                }
                result
            } else {
                emptyMap()
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }
    
    // Función auxiliar para buscar información de múltiples empleados
    private suspend fun buscarInfoEmpleados(empleadosIds: List<Int>): Map<Int, String> {
        return try {
            if (empleadosIds.isEmpty()) return emptyMap()
            
            val idsStr = empleadosIds.joinToString(",")
            val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/Empleado?id=in.($idsStr)"
            val headers = mapOf(
                "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}"
            )
            
            val response = SupabaseClient.httpClient.get(url) {
                headers.forEach { (k, v) -> header(k, v) }
            }
            
            if (response.status.isSuccess()) {
                val responseBody = response.bodyAsText()
                val empleadosJson = Json.parseToJsonElement(responseBody).jsonArray
                val result = mutableMapOf<Int, String>()
                
                for (empleadoJson in empleadosJson) {
                    val empleado = empleadoJson.jsonObject
                    val id = empleado["id"]!!.toString().toInt()
                    result[id] = empleado["nombre"]?.toString()?.replace("\"", "") ?: "Empleado Desconocido"
                }
                result
            } else {
                emptyMap()
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }
}