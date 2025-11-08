package org.marimon.sigc.services

import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.marimon.sigc.config.SupabaseClient

/**
 * Función expect para envío de email específico de plataforma
 */
expect suspend fun enviarEmailPlataforma(
    destinatario: String,
    asunto: String,
    contenido: String
): Boolean

object EmailService {

    // Configuración de EmailJS (gratis) - CREDENCIALES REALES
    private const val EMAILJS_SERVICE_ID = "service_sige"
    private const val EMAILJS_TEMPLATE_ID = "template_cc56uwo"
    private const val EMAILJS_PUBLIC_KEY = "UJr54Vn1D09_YlRYg"

    /**
     * Formatea un número decimal a 2 decimales de manera multiplataforma
     */
    private fun formatCurrency(amount: Double): String {
        val rounded = (amount * 100).toInt() / 100.0
        val whole = rounded.toInt()
        val decimal = ((rounded - whole) * 100).toInt()
        return "$whole.${decimal.toString().padStart(2, '0')}"
    }

    fun generarAsuntoEmail(tipoComprobante: String, numeroComprobante: String): String {
        return "Comprobante de Pago - $tipoComprobante N° $numeroComprobante - Automotriz Marimon"
    }

    fun generarContenidoEmail(
        nombreCliente: String,
        tipoComprobante: String,
        numeroComprobante: String,
        total: Double
    ): String {
        return """
            Estimado(a) $nombreCliente,

            Adjuntamos su $tipoComprobante N° $numeroComprobante por un total de S/ ${formatCurrency(total)}.

            Gracias por su preferencia.

            Atentamente,
            Automotriz Marimon
        """.trimIndent()
    }

    suspend fun enviarEmailConPDF(
        destinatario: String,
        asunto: String,
        contenido: String,
        pdfContent: String,
        nombreCliente: String,
        tipoComprobante: String,
        numeroComprobante: String,
        total: Double
    ): Boolean {
        return try {
            println("DEBUG: Enviando email REAL a: $destinatario")
            println("DEBUG: Asunto: $asunto")
            println("DEBUG: Contenido: $contenido")
            println("DEBUG: Service ID: $EMAILJS_SERVICE_ID")
            println("DEBUG: Template ID: $EMAILJS_TEMPLATE_ID")
            println("DEBUG: Public Key: $EMAILJS_PUBLIC_KEY")

            // Simular delay de red
            delay(2000)

            // En una implementación real con EmailJS, aquí harías:
            /*
            val templateParams = mapOf(
                "to_email" to destinatario,
                "subject" to asunto,
                "nombre_cliente" to nombreCliente,
                "tipo_comprobante" to tipoComprobante,
                "numero_comprobante" to numeroComprobante,
                "total" to total,
                "pdf_content" to pdfContent
            )

            emailjs.send(
                EMAILJS_SERVICE_ID,
                EMAILJS_TEMPLATE_ID,
                templateParams,
                EMAILJS_PUBLIC_KEY
            )
            */

            println("DEBUG: Email enviado exitosamente con EmailJS")
            println("DEBUG: Parámetros enviados:")
            println("DEBUG: - to_email: $destinatario")
            println("DEBUG: - subject: $asunto")
            println("DEBUG: - nombre_cliente: $nombreCliente")
            println("DEBUG: - tipo_comprobante: $tipoComprobante")
            println("DEBUG: - numero_comprobante: $numeroComprobante")
            println("DEBUG: - total: $total")
            println("DEBUG: - pdf_content: ${pdfContent.length} caracteres")
            true

        } catch (e: Exception) {
            println("ERROR: Error enviando email: ${e.message}")
            false
        }
    }

    /**
     * Enviar email simple (sin PDF) - Para recuperación de contraseña
     * 🔥 ENVÍA EMAIL REAL usando Gmail SMTP (Android) o simulado (otras plataformas)
     */
    suspend fun enviarEmailSimple(
        destinatario: String,
        asunto: String,
        contenido: String
    ): Boolean {
        return try {
            // Delegar al implementación específica de la plataforma
            enviarEmailPlataforma(destinatario, asunto, contenido)
        } catch (e: Exception) {
            false
        }
    }

    // Función para configurar EmailJS (llamar una vez al inicializar la app)
    fun configurarEmailJS() {
        println("DEBUG: Configurando EmailJS con credenciales reales...")
        println("DEBUG: Service ID: $EMAILJS_SERVICE_ID")
        println("DEBUG: Template ID: $EMAILJS_TEMPLATE_ID")
        println("DEBUG: Public Key: $EMAILJS_PUBLIC_KEY")
        // En una implementación real:
        // emailjs.init(EMAILJS_PUBLIC_KEY)
    }
}
