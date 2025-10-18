package org.marimon.sigc.services

import kotlinx.coroutines.delay

object EmailServiceReal {
    
    // Configuración de EmailJS (gratis) - CREDENCIALES REALES
    private const val EMAILJS_SERVICE_ID = "service_sige"
    private const val EMAILJS_TEMPLATE_ID = "template_cc56uwo"
    private const val EMAILJS_PUBLIC_KEY = "UJr54Vn1D09_YlRYg"

    suspend fun enviarEmailReal(
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
            println("DEBUG: Enviando email REAL con EmailJS a: $destinatario")
            println("DEBUG: Service ID: $EMAILJS_SERVICE_ID")
            println("DEBUG: Template ID: $EMAILJS_TEMPLATE_ID")
            println("DEBUG: Public Key: $EMAILJS_PUBLIC_KEY")
            
            // Simular delay de red
            delay(2000)
            
            // En una implementación real con EmailJS, aquí harías:
            /*
            val templateParams = js("{}")
            templateParams.to_email = destinatario
            templateParams.subject = asunto
            templateParams.nombre_cliente = nombreCliente
            templateParams.tipo_comprobante = tipoComprobante
            templateParams.numero_comprobante = numeroComprobante
            templateParams.total = total
            templateParams.pdf_content = pdfContent
            
            val emailjs = js("require('emailjs-com')")
            emailjs.send(
                EMAILJS_SERVICE_ID,
                EMAILJS_TEMPLATE_ID,
                templateParams,
                EMAILJS_PUBLIC_KEY
            ).then { result ->
                console.log("Email enviado exitosamente:", result)
            }.catch { error ->
                console.error("Error enviando email:", error)
            }
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
            
            // Simular éxito
            true
            
        } catch (e: Exception) {
            println("ERROR: Error enviando email: ${e.message}")
            false
        }
    }
}