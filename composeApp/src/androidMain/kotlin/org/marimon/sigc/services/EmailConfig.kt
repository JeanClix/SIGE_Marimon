package org.marimon.sigc.services

import android.content.Context
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.util.Properties

object EmailConfig {
    
    private var properties: Properties? = null
    
    fun loadConfig(context: Context) {
        try {
            val inputStream: InputStream = context.assets.open("email_config.properties")
            properties = Properties().apply {
                load(inputStream)
            }
            inputStream.close()
            Log.d("EmailConfig", "Configuración de email cargada exitosamente")
        } catch (e: IOException) {
            Log.e("EmailConfig", "Error cargando configuración: ${e.message}")
            // Usar valores por defecto si no se puede cargar el archivo
            properties = Properties().apply {
                setProperty("SMTP_HOST", "smtp.gmail.com")
                setProperty("SMTP_PORT", "587")
                setProperty("FROM_EMAIL", "tu-email@gmail.com")
                setProperty("FROM_PASSWORD", "tu-app-password")
            }
        }
    }
    
    fun getSmtpHost(): String {
        return properties?.getProperty("SMTP_HOST") ?: "smtp.gmail.com"
    }
    
    fun getSmtpPort(): String {
        return properties?.getProperty("SMTP_PORT") ?: "587"
    }
    
    fun getFromEmail(): String {
        return properties?.getProperty("FROM_EMAIL") ?: "tu-email@gmail.com"
    }
    
    fun getFromPassword(): String {
        return properties?.getProperty("FROM_PASSWORD") ?: "tu-app-password"
    }
    
    fun isConfigured(): Boolean {
        val email = getFromEmail()
        val password = getFromPassword()
        return email != "tu-email@gmail.com" && password != "tu-app-password"
    }
}
