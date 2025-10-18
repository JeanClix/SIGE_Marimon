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

object PDFDownloaderSimple {
    
    suspend fun downloadPDF(
        context: Context,
        pdfContent: String,
        fileName: String
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
            
            // Crear archivo HTML
            val htmlFile = File(downloadsDir, "$fileName.html")
            val fileWriter = FileWriter(htmlFile)
            fileWriter.write(pdfContent)
            fileWriter.close()
            
            Log.d("PDFDownloaderSimple", "PDF guardado en: ${htmlFile.absolutePath}")
            
            // Abrir el archivo con el navegador
            val intent = Intent(Intent.ACTION_VIEW).apply {
                val uri = Uri.fromFile(htmlFile)
                setDataAndType(uri, "text/html")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(intent)
            
            Log.d("PDFDownloaderSimple", "PDF abierto en el navegador")
            
        } catch (e: IOException) {
            Log.e("PDFDownloaderSimple", "Error guardando PDF: ${e.message}")
        } catch (e: Exception) {
            Log.e("PDFDownloaderSimple", "Error inesperado: ${e.message}")
        }
    }
}
