package org.marimon.sigc.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.marimon.sigc.model.Transaccion
import java.io.File

/**
 * Implementación Android específica para servicios de PDF
 */
actual class PDFService(private val context: Context) {
    
    actual suspend fun downloadPDF(transaccion: Transaccion): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("PDFService", "Iniciando descarga de PDF para transacción: ${transaccion.id}")
            
            // Generar PDF real usando PDFGeneratorReal
            val pdfFilePath = PDFGeneratorReal.generatePDF(context, transaccion)
            
            if (pdfFilePath != null) {
                Log.d("PDFService", "PDF generado exitosamente: $pdfFilePath")
                
                // Abrir el archivo PDF con una aplicación externa
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    val uri = Uri.fromFile(File(pdfFilePath))
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                context.startActivity(intent)
                Log.d("PDFService", "PDF abierto con aplicación externa")
                true
            } else {
                Log.e("PDFService", "Error generando PDF")
                false
            }
            
        } catch (e: Exception) {
            Log.e("PDFService", "Error inesperado: ${e.message}", e)
            false
        }
    }
    
    actual suspend fun downloadPDFWithEmail(transaccion: Transaccion, onSuccess: () -> Unit, onError: (String) -> Unit): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("PDFService", "Iniciando descarga de PDF con email para transacción: ${transaccion.id}")
            
            // Generar PDF real
            val pdfFilePath = PDFGeneratorReal.generatePDF(context, transaccion)
            
            if (pdfFilePath != null) {
                Log.d("PDFService", "PDF generado exitosamente: $pdfFilePath")
                
                // Enviar email con PDF adjunto
                val subject = "Comprobante de Pago - ${transaccion.tipoComprobante.descripcion} N° ${String.format("%08d", transaccion.id)} - Automotriz Marimon"
                val body = """
                    Estimado(a) ${transaccion.nombreCliente},
                    
                    Adjuntamos su ${transaccion.tipoComprobante.descripcion} N° ${String.format("%08d", transaccion.id)} por un total de S/ ${String.format("%.2f", transaccion.precio * transaccion.cantidad)}.
                    
                    Gracias por su preferencia.
                    
                    Atentamente,
                    Automotriz Marimon
                """.trimIndent()
                
                EmailServiceReal.sendEmailWithPDF(
                    context = context,
                    toEmail = transaccion.correoElectronico,
                    subject = subject,
                    body = body,
                    pdfFilePath = pdfFilePath,
                    onSuccess = {
                        Log.d("PDFService", "Email enviado exitosamente")
                        onSuccess()
                    },
                    onError = { error ->
                        Log.e("PDFService", "Error enviando email: $error")
                        onError(error)
                    }
                )
                
                // Abrir el archivo PDF con una aplicación externa
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    val uri = Uri.fromFile(File(pdfFilePath))
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                context.startActivity(intent)
                Log.d("PDFService", "PDF abierto con aplicación externa")
                true
                
            } else {
                onError("Error generando PDF")
                false
            }
            
        } catch (e: Exception) {
            Log.e("PDFService", "Error inesperado: ${e.message}", e)
            onError("Error inesperado: ${e.message}")
            false
        }
    }
}
