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

object AndroidPDFDownloader {
    
    suspend fun downloadPDF(
        context: Context,
        pdfContent: String,
        fileName: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            // Crear directorio de descargas si no existe
            val downloadsDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "Marimon"
            )
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            
            // Crear archivo HTML (que se puede abrir como PDF en el navegador)
            val htmlFile = File(downloadsDir, "$fileName.html")
            val fileWriter = FileWriter(htmlFile)
            fileWriter.write(pdfContent)
            fileWriter.close()
            
            Log.d("AndroidPDFDownloader", "PDF guardado en: ${htmlFile.absolutePath}")
            
            // Abrir el archivo con el navegador
            val intent = Intent(Intent.ACTION_VIEW).apply {
                val uri = Uri.fromFile(htmlFile)
                setDataAndType(uri, "text/html")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(intent)
            
            onSuccess("PDF guardado y abierto: ${htmlFile.absolutePath}")
            
        } catch (e: IOException) {
            Log.e("AndroidPDFDownloader", "Error guardando PDF: ${e.message}")
            onError("Error guardando PDF: ${e.message}")
        } catch (e: Exception) {
            Log.e("AndroidPDFDownloader", "Error inesperado: ${e.message}")
            onError("Error inesperado: ${e.message}")
        }
    }
    
    fun sharePDF(
        context: Context,
        pdfContent: String,
        fileName: String
    ) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/html"
                putExtra(Intent.EXTRA_TEXT, pdfContent)
                putExtra(Intent.EXTRA_SUBJECT, fileName)
            }
            
            val shareIntent = Intent.createChooser(intent, "Compartir PDF")
            context.startActivity(shareIntent)
            
        } catch (e: Exception) {
            Log.e("AndroidPDFDownloader", "Error compartiendo PDF: ${e.message}")
        }
    }
}