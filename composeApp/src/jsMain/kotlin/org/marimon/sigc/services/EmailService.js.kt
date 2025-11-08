package org.marimon.sigc.services

import kotlinx.coroutines.delay

/**
 * Implementación JS/Web para envío de emails
 * Podría usar EmailJS directamente aquí en el futuro
 */
actual suspend fun enviarEmailPlataforma(
    destinatario: String,
    asunto: String,
    contenido: String
): Boolean {
    println("📧 [JS] Simulando envío de email a: $destinatario")
    delay(1500)
    println("✅ [JS] Email simulado enviado")
    return true
}
