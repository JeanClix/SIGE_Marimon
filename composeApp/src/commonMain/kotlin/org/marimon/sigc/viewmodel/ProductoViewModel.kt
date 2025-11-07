package org.marimon.sigc.viewmodel

import androidx.compose.runtime.mutableStateListOf
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
import org.marimon.sigc.model.Producto
import org.marimon.sigc.model.ProductoCreate

class ProductoViewModel : ViewModel() {
    val productos: SnapshotStateList<Producto> = mutableStateListOf()

    fun cargarProductos() {
        viewModelScope.launch {
            try {
                // CORREGIDO: Traer TODOS los productos (activos e inactivos) para que el reporte funcione correctamente
                val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/productos?select=*&order=fecha_creacion.desc"
                val headers = mapOf(
                    "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                    "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}"
                )

                val response: HttpResponse = SupabaseClient.httpClient.get(url) {
                    headers.forEach { (k, v) -> header(k, v) }
                }

                val productosJson = Json.parseToJsonElement(response.bodyAsText()).jsonArray
                productos.clear()

                for (row in productosJson) {
                    val obj = row.jsonObject
                    productos.add(
                        Producto(
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
                    )
                }
            } catch (e: Exception) {
                println("Error cargando productos: ${e.message}")
            }
        }
    }

    fun crearProducto(producto: ProductoCreate, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/productos"
                val headers = mapOf(
                    "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                    "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}",
                    "Content-Type" to "application/json"
                )

                val body = """
                    {
                        "codigo": "${producto.codigo}",
                        "nombre": "${producto.nombre}",
                        "descripcion": ${if (producto.descripcion != null) "\"${producto.descripcion}\"" else "null"},
                        "especificaciones": ${if (producto.especificaciones != null) "\"${producto.especificaciones}\"" else "null"},
                        "precio": ${producto.precio},
                        "cantidad": ${producto.cantidad},
                        "imagen_url": ${if (producto.imagenUrl != null) "\"${producto.imagenUrl}\"" else "null"},
                        "activo": ${producto.activo}
                    }
                """.trimIndent()

                val response: HttpResponse = SupabaseClient.httpClient.post(url) {
                    headers.forEach { (k, v) -> header(k, v) }
                    setBody(body)
                }

                if (response.status.isSuccess()) {
                    println("✅ Producto creado exitosamente")
                    onSuccess()
                    cargarProductos() // Recargar la lista
                } else {
                    val errorMessage = "Error al crear producto: ${response.status}"
                    println("❌ $errorMessage")
                    onError(errorMessage)
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    fun editarProducto(producto: Producto, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/productos?id=eq.${producto.id}"
                val headers = mapOf(
                    "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                    "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}",
                    "Content-Type" to "application/json"
                )

                val body = """
                    {
                        "codigo": "${producto.codigo}",
                        "nombre": "${producto.nombre}",
                        "descripcion": ${if (producto.descripcion != null) "\"${producto.descripcion}\"" else "null"},
                        "especificaciones": ${if (producto.especificaciones != null) "\"${producto.especificaciones}\"" else "null"},
                        "precio": ${producto.precio},
                        "cantidad": ${producto.cantidad},
                        "imagen_url": ${if (producto.imagenUrl != null) "\"${producto.imagenUrl}\"" else "null"},
                        "activo": ${producto.activo}
                    }
                """.trimIndent()

                val response: HttpResponse = SupabaseClient.httpClient.patch(url) {
                    headers.forEach { (k, v) -> header(k, v) }
                    setBody(body)
                }

                if (response.status.isSuccess()) {
                    onSuccess()
                    cargarProductos()
                } else {
                    onError("Error al actualizar producto: ${response.status}")
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    fun eliminarProducto(productoId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/productos?id=eq.$productoId"
                val headers = mapOf(
                    "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                    "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}",
                    "Content-Type" to "application/json"
                )

                // Soft delete: marcar como inactivo en lugar de eliminar fÃ­sicamente
                val body = """
                    {
                        "activo": false
                    }
                """.trimIndent()

                val response: HttpResponse = SupabaseClient.httpClient.patch(url) {
                    headers.forEach { (k, v) -> header(k, v) }
                    setBody(body)
                }

                if (response.status.isSuccess()) {
                    onSuccess()
                    cargarProductos()
                } else {
                    onError("Error al eliminar producto: ${response.status}")
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    /**
     * Actualiza el stock de un producto sumando una cantidad adicional
     * Usado cuando se registra una entrada de inventario
     */
    fun actualizarStockProducto(
        productoId: Int,
        cantidadAdicional: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 1. Obtener el producto actual para conocer su stock
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
                        val productoObj = productosJson[0].jsonObject
                        val cantidadActual = productoObj["cantidad"]!!.toString().toInt()
                        val nuevaCantidad = cantidadActual + cantidadAdicional

                        println("📦 Actualizando stock - Producto ID: $productoId, Stock actual: $cantidadActual, Adicional: +$cantidadAdicional, Nuevo stock: $nuevaCantidad")

                        // 2. Actualizar la cantidad del producto
                        val updateUrl = "${SupabaseConfig.SUPABASE_URL}/rest/v1/productos?id=eq.$productoId"
                        val updateHeaders = headers + ("Content-Type" to "application/json")

                        val updateBody = """
                            {
                                "cantidad": $nuevaCantidad
                            }
                        """.trimIndent()

                        val updateResponse: HttpResponse = SupabaseClient.httpClient.patch(updateUrl) {
                            updateHeaders.forEach { (k, v) -> header(k, v) }
                            setBody(updateBody)
                        }

                        if (updateResponse.status.isSuccess()) {
                            println("✅ Stock actualizado exitosamente: $cantidadActual -> $nuevaCantidad")
                            onSuccess()
                            // Recargar productos para actualizar la lista
                            cargarProductos()
                        } else {
                            val errorMsg = "Error al actualizar stock: ${updateResponse.status}"
                            println("❌ $errorMsg")
                            onError(errorMsg)
                        }
                    } else {
                        val errorMsg = "Producto no encontrado"
                        println("❌ $errorMsg")
                        onError(errorMsg)
                    }
                } else {
                    val errorMsg = "Error al obtener producto: ${getResponse.status}"
                    println("❌ $errorMsg")
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error: ${e.message}"
                println("❌ $errorMsg")
                onError(errorMsg)
            }
        }
    }
}
