package org.marimon.sigc.services

import kotlinx.coroutines.delay

/**
 * Implementación iOS para envío de emails
 * Por ahora simulada
 */
actual suspend fun enviarEmailPlataforma(
    destinatario: String,
    asunto: String,
    contenido: String
): Boolean {
    println("📧 [iOS] Simulando envío de email a: $destinatario")
    delay(1500)
    println("✅ [iOS] Email simulado enviado")
    return true
}
