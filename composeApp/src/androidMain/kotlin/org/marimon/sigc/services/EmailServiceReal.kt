package org.marimon.sigc.services

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Properties
import javax.mail.*
import javax.mail.internet.*

object EmailServiceReal {
    
    suspend fun sendEmailWithPDF(
        context: Context,
        toEmail: String,
        subject: String,
        body: String,
        pdfFilePath: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) = withContext(Dispatchers.IO) {
        try {
            // Cargar configuración
            EmailConfig.loadConfig(context)
            
            // Verificar si está configurado
            if (!EmailConfig.isConfigured()) {
                onError("Email no configurado. Configura email_config.properties")
                return@withContext
            }
            
            // Configurar propiedades SMTP
            val properties = Properties().apply {
                put("mail.smtp.host", EmailConfig.getSmtpHost())
                put("mail.smtp.port", EmailConfig.getSmtpPort())
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.ssl.trust", EmailConfig.getSmtpHost())
            }
            
            // Crear sesión
            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(EmailConfig.getFromEmail(), EmailConfig.getFromPassword())
                }
            })
            
            // Crear mensaje
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(EmailConfig.getFromEmail()))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                setSubject(subject)
                
                // Crear multipart para adjuntar PDF
                val multipart = MimeMultipart()
                
                // Cuerpo del mensaje
                val textPart = MimeBodyPart().apply {
                    setText(body, "utf-8")
                }
                multipart.addBodyPart(textPart)
                
                // Adjuntar PDF
                val pdfFile = File(pdfFilePath)
                if (pdfFile.exists()) {
                    val pdfPart = MimeBodyPart().apply {
                        attachFile(pdfFile)
                        fileName = pdfFile.name
                    }
                    multipart.addBodyPart(pdfPart)
                }
                
                setContent(multipart)
            }
            
            // Enviar email
            Transport.send(message)
            
            Log.d("EmailServiceReal", "Email enviado exitosamente a: $toEmail")
            onSuccess()
            
        } catch (e: MessagingException) {
            val errorMsg = "Error enviando email: ${e.message}"
            Log.e("EmailServiceReal", errorMsg)
            onError(errorMsg)
        } catch (e: Exception) {
            val errorMsg = "Error inesperado: ${e.message}"
            Log.e("EmailServiceReal", errorMsg)
            onError(errorMsg)
        }
    }
    
    fun sendEmailSimple(
        context: Context,
        toEmail: String,
        subject: String,
        body: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        try {
            // Cargar configuración
            EmailConfig.loadConfig(context)
            
            // Verificar si está configurado
            if (!EmailConfig.isConfigured()) {
                onError("Email no configurado. Configura email_config.properties")
                return
            }
            
            // Configurar propiedades SMTP
            val properties = Properties().apply {
                put("mail.smtp.host", EmailConfig.getSmtpHost())
                put("mail.smtp.port", EmailConfig.getSmtpPort())
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.ssl.trust", EmailConfig.getSmtpHost())
            }
            
            // Crear sesión
            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(EmailConfig.getFromEmail(), EmailConfig.getFromPassword())
                }
            })
            
            // Crear mensaje
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(EmailConfig.getFromEmail()))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                setSubject(subject)
                setText(body, "utf-8")
            }
            
            // Enviar email
            Transport.send(message)
            
            Log.d("EmailServiceReal", "Email enviado exitosamente a: $toEmail")
            onSuccess()
            
        } catch (e: MessagingException) {
            val errorMsg = "Error enviando email: ${e.message}"
            Log.e("EmailServiceReal", errorMsg)
            onError(errorMsg)
        } catch (e: Exception) {
            val errorMsg = "Error inesperado: ${e.message}"
            Log.e("EmailServiceReal", errorMsg)
            onError(errorMsg)
        }
    }
}