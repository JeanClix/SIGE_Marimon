package org.marimon.sigc.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.io.IOException
import org.marimon.sigc.model.Transaccion

object PDFDownloader {
    
    suspend fun downloadPDF(
        context: Context,
        transaccion: Transaccion
    ) = withContext(Dispatchers.IO) {
        try {
            // Generar PDF real usando PDFGeneratorReal
            val pdfFilePath = PDFGeneratorReal.generatePDF(context, transaccion)
            
            if (pdfFilePath != null) {
                Log.d("PDFDownloader", "PDF generado exitosamente: $pdfFilePath")
                
                // Abrir el archivo PDF con una aplicación externa
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    val uri = Uri.fromFile(File(pdfFilePath))
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                context.startActivity(intent)
                
                Log.d("PDFDownloader", "PDF abierto con aplicación externa")
            } else {
                Log.e("PDFDownloader", "Error generando PDF")
            }
            
        } catch (e: Exception) {
            Log.e("PDFDownloader", "Error inesperado: ${e.message}")
        }
    }
    
    suspend fun downloadPDFWithEmail(
        context: Context,
        transaccion: Transaccion,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) = withContext(Dispatchers.IO) {
        try {
            // Generar PDF real
            val pdfFilePath = PDFGeneratorReal.generatePDF(context, transaccion)
            
            if (pdfFilePath != null) {
                Log.d("PDFDownloader", "PDF generado exitosamente: $pdfFilePath")
                
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
                        Log.d("PDFDownloader", "Email enviado exitosamente")
                        onSuccess()
                    },
                    onError = { error ->
                        Log.e("PDFDownloader", "Error enviando email: $error")
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
                
            } else {
                onError("Error generando PDF")
            }
            
        } catch (e: Exception) {
            Log.e("PDFDownloader", "Error inesperado: ${e.message}")
            onError("Error inesperado: ${e.message}")
        }
    }
    
    fun sharePDF(
        context: Context,
        pdfFilePath: String,
        fileName: String
    ) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(pdfFilePath)))
                putExtra(Intent.EXTRA_SUBJECT, fileName)
            }
            
            val shareIntent = Intent.createChooser(intent, "Compartir PDF")
            context.startActivity(shareIntent)
            
        } catch (e: Exception) {
            Log.e("PDFDownloader", "Error compartiendo PDF: ${e.message}")
        }
    }
}