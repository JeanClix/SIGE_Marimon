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
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.layout.element.Div
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.font.PdfFont
import org.marimon.sigc.model.Transaccion
import java.io.File
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

            val fileName = "${transaccion.tipoComprobante.valor}_${String.format("%08d", transaccion.id)}_${transaccion.dniRuc}.pdf"
            val pdfFile = File(downloadsDir, fileName)

            val pdfWriter = PdfWriter(pdfFile)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            val font = PdfFontFactory.createFont()
            val boldFont = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD)

            // Título de la empresa
            val title = Paragraph("AUTOMOTRIZ MARIMON")
                .setFont(boldFont)
                .setFontSize(20f)
                .setFontColor(ColorConstants.RED)
            document.add(title)

            // Información de la empresa
            val companyInfo = Paragraph()
                .setFont(font)
                .setFontSize(10f)
                .add("RUC: 20123456789\n")
                .add("Av. Los Olivos 123, Lima, Perú\n")
                .add("Teléfono: (01) 456-7890 | Email: ventas@marimon.com")
            document.add(companyInfo)

            document.add(Paragraph("\n"))

            // Información del comprobante
            val comprobanteInfo = Paragraph()
                .setFont(boldFont)
                .setFontSize(14f)
                .add("${transaccion.tipoComprobante.descripcion}\n")
                .add("N° ${String.format("%08d", transaccion.id)}\n")
                .add("Fecha: ${transaccion.fechaEmision}")
            document.add(comprobanteInfo)

            document.add(Paragraph("\n"))

            // Datos del cliente
            val clientInfo = Paragraph()
                .setFont(boldFont)
                .setFontSize(12f)
                .add("DATOS DEL CLIENTE:\n")
                .setFont(font)
                .add("${if (transaccion.tipoComprobante.valor == "FACTURA") "RUC" else "DNI"}: ${transaccion.dniRuc}\n")
                .add("Nombre: ${transaccion.nombreCliente}\n")
                .add("Dirección: ${transaccion.direccion}\n")
                .add("Email: ${transaccion.correoElectronico}")
            document.add(clientInfo)

            document.add(Paragraph("\n"))

            // Tabla de productos
            val table = Table(4)
            table.setWidth(500f)

            table.addHeaderCell(Cell().add(Paragraph("Descripción").setFont(boldFont)))
            table.addHeaderCell(Cell().add(Paragraph("Cantidad").setFont(boldFont)))
            table.addHeaderCell(Cell().add(Paragraph("Precio Unit.").setFont(boldFont)))
            table.addHeaderCell(Cell().add(Paragraph("Importe").setFont(boldFont)))

            val subtotal = transaccion.precio * transaccion.cantidad
            table.addCell(Cell().add(Paragraph("${transaccion.productoNombre ?: "Producto"} (${transaccion.productoCodigo ?: "N/A"})")))
            table.addCell(Cell().add(Paragraph(transaccion.cantidad.toString())))
            table.addCell(Cell().add(Paragraph("S/ ${String.format("%.2f", transaccion.precio)}")))
            table.addCell(Cell().add(Paragraph("S/ ${String.format("%.2f", subtotal)}")))

            document.add(table)

            document.add(Paragraph("\n"))

            // Totales
            val igv = subtotal * 0.18
            val total = subtotal + igv

            val totalsDiv = Div()
                .add(Paragraph("Subtotal: S/ ${String.format("%.2f", subtotal)}").setFont(font))
                .add(Paragraph("IGV (18%): S/ ${String.format("%.2f", igv)}").setFont(font))
                .add(Paragraph("TOTAL: S/ ${String.format("%.2f", total)}").setFont(boldFont).setFontSize(14f))

            document.add(totalsDiv)

            document.add(Paragraph("\n"))

            // Información adicional
            val additionalInfo = Paragraph()
                .setFont(font)
                .setFontSize(10f)
                .add("Método de Pago: ${transaccion.metodoPago}\n")
                .add("Emitido por: ${transaccion.empleadoNombre ?: "Empleado"}\n")
                .add("Fecha de impresión: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date())}\n")

            if (!transaccion.observaciones.isNullOrBlank()) {
                additionalInfo.add("Observaciones: ${transaccion.observaciones}\n")
            }

            additionalInfo.add("¡Gracias por su compra!")
            document.add(additionalInfo)

            document.close()

            // Notificar al sistema que se creó un nuevo archivo
            val intent = android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val uri = android.net.Uri.fromFile(pdfFile)
            intent.data = uri
            context.sendBroadcast(intent)

            Log.d("PDFGeneratorReal", "PDF generado exitosamente: ${pdfFile.absolutePath}")
            Log.d("PDFGeneratorReal", "Archivo existe: ${pdfFile.exists()}")
            Log.d("PDFGeneratorReal", "Tamaño del archivo: ${pdfFile.length()} bytes")
            
            pdfFile.absolutePath

        } catch (e: IOException) {
            Log.e("PDFGeneratorReal", "Error generando PDF: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("PDFGeneratorReal", "Error inesperado: ${e.message}")
            null
        }
    }
}