package org.marimon.sigc.services

/**
 * Implementación Android para envío de emails
 * Usa Gmail SMTP directamente
 */
actual suspend fun enviarEmailPlataforma(
    destinatario: String,
    asunto: String,
    contenido: String
): Boolean {
    return GmailSender.enviarEmail(destinatario, asunto, contenido)
}
