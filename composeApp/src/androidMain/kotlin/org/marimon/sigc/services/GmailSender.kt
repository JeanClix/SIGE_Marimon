package org.marimon.sigc.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.marimon.sigc.BuildConfig
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * Servicio para enviar emails usando Gmail SMTP
 * 🔥 ENVÍO REAL usando JavaMail API
 */
object GmailSender {

    // Credenciales obtenidas desde BuildConfig
    private val GMAIL_USERNAME: String
        get() = BuildConfig.GMAIL_USERNAME

    private val GMAIL_APP_PASSWORD: String
        get() = BuildConfig.GMAIL_APP_PASSWORD

    /**
     * Envía un email usando Gmail SMTP
     */
    suspend fun enviarEmail(
        destinatario: String,
        asunto: String,
        contenido: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // Configuración de Gmail SMTP
            val props = Properties().apply {
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.port", "587")
                put("mail.smtp.ssl.protocols", "TLSv1.2")
            }

            // Crear sesión con autenticación
            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(GMAIL_USERNAME, GMAIL_APP_PASSWORD.replace(" ", ""))
                }
            })

            // Crear mensaje
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(GMAIL_USERNAME, "Automotriz Marimon - SIGE"))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario))
                subject = asunto
                setText(contenido, "UTF-8")
            }

            // Enviar
            Transport.send(message)
            true
        } catch (e: Exception) {
            // imprimir stacktrace para diagnóstico, sin logs adicionales
            e.printStackTrace()
            false
        }
    }
}
