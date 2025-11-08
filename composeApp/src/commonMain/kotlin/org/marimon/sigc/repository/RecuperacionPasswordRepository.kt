package org.marimon.sigc.repository

import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.marimon.sigc.config.SupabaseClient
import org.marimon.sigc.config.SupabaseConfig
import org.marimon.sigc.model.Empleado
import org.marimon.sigc.model.RecuperacionCodigo
import org.marimon.sigc.services.EmailService
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes

/**
 * Repository para recuperación de contraseñas
 * 🔥 USA ALMACENAMIENTO TEMPORAL EN MEMORIA (sin base de datos)
 */
class RecuperacionPasswordRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    companion object {
        // 🔥 Almacenamiento temporal en memoria
        private val codigosTemporales = mutableMapOf<String, RecuperacionCodigo>()
        private const val EXPIRACION_MINUTOS = 15L
    }

    /**
     * Genera un código de 6 dígitos
     */
    private fun generarCodigo(): String {
        return (100000..999999).random().toString()
    }

    /**
     * Limpia códigos expirados del mapa
     */
    private fun limpiarCodigosExpirados() {
        val ahora = Clock.System.now()
        codigosTemporales.entries.removeIf { (_, codigo) ->
            val expiracion = Instant.parse(codigo.fechaExpiracion)
            ahora > expiracion
        }
    }

    /**
     * Solicita recuperación de contraseña
     * 🔥 ALMACENA EN MEMORIA (sin base de datos)
     * 1. Busca el empleado por email
     * 2. Genera código temporal
     * 3. Guarda en memoria
     * 4. Envía por email (simulado)
     */
    suspend fun solicitarRecuperacion(emailCorporativo: String): Pair<Boolean, String> {
        return withContext(Dispatchers.IO) {
            try {
                // Limpiar códigos expirados
                limpiarCodigosExpirados()

                // 1. Buscar empleado
                val empleado = buscarEmpleadoPorEmail(emailCorporativo)
                if (empleado == null) {
                    return@withContext Pair(false, "Email no registrado en el sistema")
                }

                // 2. Generar nuevo código
                val codigo = generarCodigo()
                val ahora = Clock.System.now()
                val expiracion = ahora + EXPIRACION_MINUTOS.minutes

                // 3. Guardar en memoria temporal (no en BD)
                val recuperacionCodigo = RecuperacionCodigo(
                    id = 0,
                    empleadoId = empleado.id,
                    codigo = codigo,
                    emailCorporativo = emailCorporativo,
                    fechaCreacion = ahora.toString(),
                    fechaExpiracion = expiracion.toString(),
                    usado = false,
                    activo = true
                )

                // Usar email como clave única
                codigosTemporales[emailCorporativo] = recuperacionCodigo

                // 4. Enviar email usando EmailService
                val emailEnviado = enviarCodigoPorEmail(
                    emailCorporativo = emailCorporativo,
                    nombreEmpleado = empleado.nombre,
                    codigo = codigo
                )

                if (emailEnviado) {
                    Pair(true, "Código de recuperación enviado a tu email corporativo.\n\nEl código expirará en 15 minutos.")
                } else {
                    Pair(false, "Error al enviar el email. Intenta nuevamente.")
                }            } catch (e: Exception) {
                println("❌ [RecuperacionRepo] Exception: ${e.message}")
                e.printStackTrace()
                Pair(false, "Error: ${e.message}")
            }
        }
    }

    /**
     * Verifica si el código es válido
     * 🔥 VERIFICA EN MEMORIA (sin base de datos)
     */
    suspend fun verificarCodigo(emailCorporativo: String, codigo: String): Pair<Boolean, String> {
        return withContext(Dispatchers.IO) {
            try {
                // Limpiar códigos expirados
                limpiarCodigosExpirados()

                // Buscar en memoria
                val recuperacion = codigosTemporales[emailCorporativo]

                if (recuperacion == null) {
                    return@withContext Pair(false, "No se encontró un código válido. Solicita uno nuevo.")
                }

                // Verificar si ya fue usado
                if (recuperacion.usado) {
                    return@withContext Pair(false, "Este código ya fue utilizado. Solicita uno nuevo.")
                }

                // Verificar código
                if (recuperacion.codigo != codigo) {
                    return@withContext Pair(false, "Código incorrecto")
                }

                // Verificar expiración
                val expiracion = Instant.parse(recuperacion.fechaExpiracion)
                val ahora = Clock.System.now()

                if (ahora > expiracion) {
                    // Eliminar código expirado
                    codigosTemporales.remove(emailCorporativo)
                    return@withContext Pair(false, "El código ha expirado. Solicita uno nuevo.")
                }

                Pair(true, "Código válido")

            } catch (e: Exception) {
                e.printStackTrace()
                Pair(false, "Error al verificar código")
            }
        }
    }

    /**
     * Cambia la contraseña del empleado
     * 🔥 MARCA EL CÓDIGO COMO USADO EN MEMORIA
     */
    suspend fun cambiarPassword(
        emailCorporativo: String,
        codigo: String,
        nuevaPassword: String
    ): Pair<Boolean, String> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Verificar código nuevamente
                val (codigoValido, _) = verificarCodigo(emailCorporativo, codigo)
                if (!codigoValido) {
                    return@withContext Pair(false, "Código no válido")
                }

                // 2. Buscar empleado
                val empleado = buscarEmpleadoPorEmail(emailCorporativo)
                if (empleado == null) {
                    return@withContext Pair(false, "Empleado no encontrado")
                }

                // 3. Actualizar contraseña en Supabase
                val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/Empleado?id=eq.${empleado.id}"
                val headers = mapOf(
                    "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                    "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}",
                    "Content-Type" to "application/json"
                )

                val body = """
                    {
                        "password": "$nuevaPassword"
                    }
                """.trimIndent()

                val response: HttpResponse = SupabaseClient.httpClient.patch(url) {
                    headers.forEach { (k, v) -> header(k, v) }
                    setBody(body)
                }

                if (response.status.value in 200..299) {
                    // 4. Marcar código como usado EN MEMORIA
                    val recuperacion = codigosTemporales[emailCorporativo]
                    if (recuperacion != null) {
                        codigosTemporales[emailCorporativo] = recuperacion.copy(usado = true)
                    }

                    Pair(true, "Contraseña cambiada exitosamente")
                } else {
                    Pair(false, "Error al cambiar contraseña")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Pair(false, "Error al cambiar contraseña")
            }
        }
    }

    // ==================== FUNCIONES AUXILIARES ====================

    private suspend fun buscarEmpleadoPorEmail(email: String): Empleado? {
        return try {
            val url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/Empleado?email_corporativo=eq.$email&activo=eq.true"
            val headers = mapOf(
                "apikey" to SupabaseConfig.SUPABASE_ANON_KEY,
                "Authorization" to "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}"
            )

            val response: HttpResponse = SupabaseClient.httpClient.get(url) {
                headers.forEach { (k, v) -> header(k, v) }
            }

            if (response.status.value in 200..299) {
                val responseBody = response.bodyAsText()
                val empleados = json.parseToJsonElement(responseBody).jsonArray

                if (empleados.isNotEmpty()) {
                    val obj = empleados[0].jsonObject
                    Empleado(
                        id = obj["id"]!!.jsonPrimitive.content.toInt(),
                        nombre = obj["nombre"]!!.jsonPrimitive.content,
                        emailCorporativo = obj["email_corporativo"]!!.jsonPrimitive.content,
                        areaId = obj["area_id"]!!.jsonPrimitive.content.toInt(),
                        areaNombre = "",
                        activo = true,
                        imagenUrl = obj["imagen_url"]?.jsonPrimitive?.content,
                        password = obj["password"]?.jsonPrimitive?.content
                    )
                } else null
            } else null

        } catch (e: Exception) {
            println("❌ Error buscando empleado: ${e.message}")
            null
        }
    }

    private suspend fun enviarCodigoPorEmail(
        emailCorporativo: String,
        nombreEmpleado: String,
        codigo: String
    ): Boolean {
        return try {
            val asunto = "Código de Recuperación - Automotriz Marimon"
            val contenido = """
                Hola $nombreEmpleado,

                Has solicitado recuperar tu contraseña en el sistema SIGE de Automotriz Marimon.

                Tu código de recuperación es: $codigo

                Este código expirará en 15 minutos.

                Si no solicitaste este cambio, ignora este mensaje.

                Saludos,
                Equipo de Automotriz Marimon
            """.trimIndent()

            // Enviar email
            EmailService.enviarEmailSimple(emailCorporativo, asunto, contenido)

        } catch (e: Exception) {
            false
        }
    }
}
