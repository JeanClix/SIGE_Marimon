package org.marimon.sigc.services

import android.content.Context
import android.os.Environment
import android.util.Log
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.layout.element.Div
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.font.PdfFont
import org.marimon.sigc.model.Transaccion
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object PDFGeneratorReal {
    
    fun generatePDF(
        context: Context,
        transaccion: Transaccion
    ): String? {
        return try {
            // Crear directorio de descargas
            val downloadsDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "Marimon"
            )
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            
            // Crear archivo HTML (por ahora, hasta que iText7 funcione)
            val fileName = "${transaccion.tipoComprobante.valor}_${String.format("%08d", transaccion.id)}_${transaccion.dniRuc}.html"
            val htmlFile = File(downloadsDir, fileName)
            
            // Generar contenido HTML
            val subtotal = transaccion.precio * transaccion.cantidad
            val igv = subtotal * 0.18
            val total = subtotal + igv
            
            val htmlContent = """
<!DOCTYPE html>
<html>
<head>
    <title>${transaccion.tipoComprobante.descripcion}</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; color: #333; }
        .header { text-align: center; margin-bottom: 30px; }
        .header h1 { color: #FF0000; margin: 0; }
        .header p { margin: 5px 0; }
        .info-box { border: 1px solid #eee; padding: 15px; margin-bottom: 20px; background-color: #f9f9f9; }
        .info-box div { margin-bottom: 10px; }
        .details-table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
        .details-table th, .details-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        .details-table th { background-color: #FF0000; color: white; }
        .totals { text-align: right; margin-top: 20px; }
        .totals div { margin-bottom: 5px; }
        .footer { text-align: center; margin-top: 40px; font-size: 0.9em; color: #777; }
    </style>
</head>
<body>
    <div class="header">
        <h1>AUTOMOTRIZ MARIMON</h1>
        <p>RUC: 20123456789</p>
        <p>Dirección: Av. Los Olivos 123, Lima, Perú</p>
        <p>Teléfono: (01) 456-7890 | Email: ventas@marimon.com</p>
    </div>

    <div class="info-box">
        <div>
            <strong>${transaccion.tipoComprobante.descripcion}</strong><br>
            N° ${String.format("%08d", transaccion.id)}<br>
            Fecha: ${transaccion.fechaEmision}
        </div>
        <div>
            Emitido por: ${transaccion.empleadoNombre ?: "Empleado"}<br>
            Fecha de impresión: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date())}
        </div>
    </div>

    <div class="client-info">
        <strong>DATOS DEL CLIENTE:</strong><br>
        ${if (transaccion.tipoComprobante.valor == "FACTURA") "RUC" else "DNI"}: ${transaccion.dniRuc}<br>
        Nombre: ${transaccion.nombreCliente}<br>
        Dirección: ${transaccion.direccion}<br>
        Email: ${transaccion.correoElectronico}
    </div>

    <table class="details-table">
        <thead>
            <tr>
                <th>Descripción</th>
                <th>Cantidad</th>
                <th>Precio Unit.</th>
                <th>Importe</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>${transaccion.productoNombre ?: "Producto Desconocido"} (${transaccion.productoCodigo ?: "N/A"})</td>
                <td>${transaccion.cantidad}</td>
                <td>S/ ${String.format("%.2f", transaccion.precio)}</td>
                <td>S/ ${String.format("%.2f", subtotal)}</td>
            </tr>
        </tbody>
    </table>

    <div class="totals">
        <div>Subtotal: S/ ${String.format("%.2f", subtotal)}</div>
        <div>IGV (18%): S/ ${String.format("%.2f", igv)}</div>
        <div><strong>TOTAL: S/ ${String.format("%.2f", total)}</strong></div>
    </div>

    <div class="footer">
        <p>Método de Pago: ${transaccion.metodoPago}</p>
        ${if (!transaccion.observaciones.isNullOrBlank()) "<p>Observaciones: ${transaccion.observaciones}</p>" else ""}
        <p>¡Gracias por su compra!</p>
    </div>
</body>
</html>
            """.trimIndent()
            
            // Escribir archivo
            val fileWriter = FileWriter(htmlFile)
            fileWriter.write(htmlContent)
            fileWriter.close()
            
            Log.d("PDFGeneratorReal", "PDF generado exitosamente: ${htmlFile.absolutePath}")
            htmlFile.absolutePath
            
        } catch (e: IOException) {
            Log.e("PDFGeneratorReal", "Error generando PDF: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("PDFGeneratorReal", "Error inesperado: ${e.message}")
            null
        }
    }
}