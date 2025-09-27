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
import org.marimon.sigc.model.Empleado
import org.marimon.sigc.model.EmpleadoCreate
import org.marimon.sigc.model.Area

class EmpleadoViewModel : ViewModel() {
    val empleados: SnapshotStateList<Empleado> = mutableStateListOf()
    val areas: SnapshotStateList<Area> = mutableStateListOf()

    fun cargarEmpleados() {
        viewModelScope.launch {
            try {
                val urlEmpleados = "${SupabaseConfig.SUPABASE_URL}/rest/v1/Empleado?select=*"
                val urlAreas = "${SupabaseConfig.SUPABASE_URL}/rest/v1/areas?select=id,nombre"
                val headers = mapOf(
                    "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                    "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}"
                )
                val empleadosResponse: HttpResponse = SupabaseClient.httpClient.get(urlEmpleados) {
                    headers.forEach { (k, v) -> header(k, v) }
                }
                val areasResponse: HttpResponse = SupabaseClient.httpClient.get(urlAreas) {
                    headers.forEach { (k, v) -> header(k, v) }
                }
                val empleadosJson = Json.parseToJsonElement(empleadosResponse.bodyAsText()).jsonArray
                val areasJson = Json.parseToJsonElement(areasResponse.bodyAsText()).jsonArray
                val areasMap = areasJson.associateBy { it.jsonObject["id"]!!.toString().toInt() }
                empleados.clear()
                for (row in empleadosJson) {
                    val obj = row.jsonObject
                    val areaId = obj["area_id"]!!.toString().toInt()
                    val areaNombre = areasMap[areaId]?.jsonObject?.get("nombre")?.toString()?.replace('"',' ')?.trim() ?: ""
                    empleados.add(
                        Empleado(
                            id = obj["id"]!!.toString().toInt(),
                            nombre = obj["nombre"]!!.toString().replace("\"", "").trim(),
                            emailCorporativo = obj["email_corporativo"]!!.toString().replace("\"", "").trim(),
                            areaId = areaId,
                            areaNombre = areaNombre,
                            activo = obj["activo"]!!.toString().toBoolean(),
                            imagenUrl = obj["imagen_url"]?.toString()?.replace("\"", "")?.trim()
                        )
                    )
                }
            } catch (e: Exception) {
                println("Error cargando empleados: ${e.message}")
            }
        }
    }

    fun cargarAreas() {
        viewModelScope.launch {
            try {
                val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/areas?select=*&activo=eq.true&order=nombre.asc"
                val headers = mapOf(
                    "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                    "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}"
                )
                
                val response: HttpResponse = SupabaseClient.httpClient.get(url) {
                    headers.forEach { (k, v) -> header(k, v) }
                }
                
                val areasJson = Json.parseToJsonElement(response.bodyAsText()).jsonArray
                areas.clear()
                
                for (row in areasJson) {
                    val obj = row.jsonObject
                    areas.add(
                        Area(
                            id = obj["id"]!!.toString().toInt(),
                            nombre = obj["nombre"]!!.toString().replace("\"", "").trim(),
                            descripcion = obj["descripcion"]?.toString()?.replace("\"", "")?.trim(),
                            activo = obj["activo"]!!.toString().toBoolean()
                        )
                    )
                }
            } catch (e: Exception) {
                println("Error cargando áreas: ${e.message}")
            }
        }
    }

    fun crearEmpleado(empleado: EmpleadoCreate, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/Empleado"
                val headers = mapOf(
                    "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                    "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}",
                    "Content-Type" to "application/json"
                )
                
                val body = """
                    {
                        "nombre": "${empleado.nombre}",
                        "email_corporativo": "${empleado.emailCorporativo}",
                        "area_id": ${empleado.areaId},
                        "imagen_url": ${if (empleado.imagenUrl != null) "\"${empleado.imagenUrl}\"" else "null"},
                        "activo": ${empleado.activo}
                    }
                """.trimIndent()

                val response: HttpResponse = SupabaseClient.httpClient.post(url) {
                    headers.forEach { (k, v) -> header(k, v) }
                    setBody(body)
                }

                if (response.status.isSuccess()) {
                    onSuccess()
                    cargarEmpleados()
                } else {
                    onError("Error al crear empleado: ${response.status}")
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    fun editarEmpleado(empleado: Empleado, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/Empleado?id=eq.${empleado.id}"
                val headers = mapOf(
                    "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                    "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}",
                    "Content-Type" to "application/json"
                )
                
                val body = """
                    {
                        "nombre": "${empleado.nombre}",
                        "email_corporativo": "${empleado.emailCorporativo}",
                        "area_id": ${empleado.areaId},
                        "imagen_url": ${if (empleado.imagenUrl != null) "\"${empleado.imagenUrl}\"" else "null"},
                        "activo": ${empleado.activo}
                    }
                """.trimIndent()

                val response: HttpResponse = SupabaseClient.httpClient.patch(url) {
                    headers.forEach { (k, v) -> header(k, v) }
                    setBody(body)
                }

                if (response.status.isSuccess()) {
                    onSuccess()
                    cargarEmpleados()
                } else {
                    onError("Error al actualizar empleado: ${response.status}")
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    fun eliminarEmpleado(empleadoId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/Empleado?id=eq.$empleadoId"
                val headers = mapOf(
                    "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                    "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}",
                    "Content-Type" to "application/json"
                )
                
                // Soft delete: marcar como inactivo en lugar de eliminar físicamente
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
                    cargarEmpleados()
                } else {
                    onError("Error al eliminar empleado: ${response.status}")
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }
}
