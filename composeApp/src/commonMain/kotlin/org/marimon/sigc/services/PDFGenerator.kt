package org.marimon.sigc.services

import org.marimon.sigc.model.Transaccion
import org.marimon.sigc.model.TipoComprobante
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object PDFGenerator {
    
    fun generarComprobantePDF(transaccion: Transaccion): String {
        val fechaActual = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
        val subtotal = transaccion.precio * transaccion.cantidad
        val igv = subtotal * 0.18 // 18% IGV
        val total = subtotal + igv
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>${transaccion.tipoComprobante.descripcion}</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .header { text-align: center; border-bottom: 2px solid #FF0000; padding-bottom: 10px; margin-bottom: 20px; }
                    .company-name { font-size: 24px; font-weight: bold; color: #FF0000; }
                    .company-info { font-size: 12px; color: #666; margin-top: 5px; }
                    .comprobante-info { display: flex; justify-content: space-between; margin-bottom: 20px; }
                    .client-info { margin-bottom: 20px; }
                    .items-table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
                    .items-table th, .items-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                    .items-table th { background-color: #f2f2f2; }
                    .totals { text-align: right; margin-top: 20px; }
                    .total-line { margin: 5px 0; }
                    .grand-total { font-weight: bold; font-size: 18px; border-top: 2px solid #FF0000; padding-top: 10px; }
                    .footer { margin-top: 30px; text-align: center; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="header">
                    <div class="company-name">AUTOMOTRIZ MARIMON</div>
                    <div class="company-info">
                        RUC: 20123456789<br>
                        Av. Principal 123, Lima, Perú<br>
                        Tel: (01) 234-5678 | Email: info@marimon.com
                    </div>
                </div>
                
                <div class="comprobante-info">
                    <div>
                        <strong>${transaccion.tipoComprobante.descripcion}</strong><br>
                        N° ${transaccion.id}<br>
                        Fecha: ${transaccion.fechaEmision}
                    </div>
                    <div>
            Emitido por: ${transaccion.empleadoNombre ?: "Empleado"}<br>
            Fecha de impresión: $fechaActual
                    </div>
                </div>
                
                <div class="client-info">
                    <strong>DATOS DEL CLIENTE:</strong><br>
                    ${if (transaccion.tipoComprobante == TipoComprobante.FACTURA) "RUC" else "DNI"}: ${transaccion.dniRuc}<br>
                    Razón Social: ${transaccion.nombreCliente}<br>
                    Dirección: ${transaccion.direccion}<br>
                    Email: ${transaccion.correoElectronico}
                </div>
                
                <table class="items-table">
                    <thead>
                        <tr>
                            <th>Código</th>
                            <th>Descripción</th>
                            <th>Cantidad</th>
                            <th>Precio Unit.</th>
                            <th>Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>${transaccion.productoCodigo ?: "N/A"}</td>
                            <td>${transaccion.productoNombre ?: "Producto"}</td>
                            <td>${transaccion.cantidad}</td>
                            <td>S/ ${transaccion.precio}</td>
                            <td>S/ ${subtotal}</td>
                        </tr>
                    </tbody>
                </table>
                
                <div class="totals">
                    <div class="total-line">Subtotal: S/ ${subtotal}</div>
                    <div class="total-line">IGV (18%): S/ ${igv}</div>
                    <div class="total-line grand-total">TOTAL: S/ ${total}</div>
                </div>
                
                <div style="margin-top: 20px;">
                    <strong>Método de Pago:</strong> ${transaccion.metodoPago}<br>
                    ${if (!transaccion.observaciones.isNullOrBlank()) "<strong>Observaciones:</strong> ${transaccion.observaciones}" else ""}
                </div>
                
                <div class="footer">
                    <p>¡Gracias por su compra!</p>
                    <p>Este comprobante ha sido generado electrónicamente y no requiere firma.</p>
                    <p>Para consultas o reclamos, contacte con nosotros.</p>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
    
    fun generarPDFSimple(transaccion: Transaccion): String {
        val fechaActual = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
        // Versión simplificada para testing
        return """
            COMPROBANTE DE PAGO
            ==================
            
            Empresa: AUTOMOTRIZ MARIMON
            Tipo: ${transaccion.tipoComprobante.descripcion}
            Número: ${transaccion.id}
            Fecha: ${transaccion.fechaEmision}
            
            Cliente:
            ${if (transaccion.tipoComprobante == TipoComprobante.FACTURA) "RUC" else "DNI"}: ${transaccion.dniRuc}
            Nombre: ${transaccion.nombreCliente}
            Dirección: ${transaccion.direccion}
            Email: ${transaccion.correoElectronico}
            
            Producto:
            ${transaccion.productoNombre ?: "Producto"}
            Cantidad: ${transaccion.cantidad}
            Precio: S/ ${transaccion.precio}
            Total: S/ ${transaccion.precio * transaccion.cantidad}
            
            Método de Pago: ${transaccion.metodoPago}
            ${if (!transaccion.observaciones.isNullOrBlank()) "Observaciones: ${transaccion.observaciones}" else ""}
            
            Emitido por: ${transaccion.empleadoNombre ?: "Empleado"}
            Fecha de impresión: ${fechaActual.substring(0, 16)}
            
            ¡Gracias por su compra!
        """.trimIndent()
    }
}
