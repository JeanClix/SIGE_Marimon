package org.marimon.sigc.model

import kotlinx.serialization.Serializable

@Serializable
data class Transaccion(
    val id: Int,
    val dniRuc: String,
    val nombreCliente: String,
    val direccion: String,
    val correoElectronico: String,
    val fechaEmision: String,
    val productoId: Int,
    val precio: Double,
    val cantidad: Int,
    val metodoPago: String,
    val observaciones: String? = null,
    val empleadoId: Int,
    val tipoComprobante: TipoComprobante,
    val activo: Boolean = true,
    val fechaCreacion: String? = null,
    val fechaActualizacion: String? = null,
    
    // Información adicional del producto
    val productoNombre: String? = null,
    val productoCodigo: String? = null,
    
    // Información adicional del empleado
    val empleadoNombre: String? = null
)

@Serializable
enum class TipoComprobante(val valor: String, val descripcion: String) {
    BOLETA("BOLETA", "Boleta de Venta"),
    FACTURA("FACTURA", "Factura")
}

@Serializable
data class TransaccionCreate(
    val dniRuc: String,
    val nombreCliente: String,
    val direccion: String,
    val correoElectronico: String,
    val fechaEmision: String,
    val productoId: Int,
    val precio: Double,
    val cantidad: Int,
    val metodoPago: String,
    val observaciones: String? = null,
    val empleadoId: Int
)

@Serializable
enum class MetodoPago(val valor: String, val descripcion: String) {
    EFECTIVO("EFECTIVO", "Efectivo"),
    TARJETA_CREDITO("TARJETA_CREDITO", "Tarjeta de Crédito"),
    TARJETA_DEBITO("TARJETA_DEBITO", "Tarjeta de Débito"),
    TRANSFERENCIA("TRANSFERENCIA", "Transferencia Bancaria"),
    YAPE("YAPE", "Yape"),
    PLIN("PLIN", "Plin")
}
