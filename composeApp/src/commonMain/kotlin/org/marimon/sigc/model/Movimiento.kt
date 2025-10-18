package org.marimon.sigc.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Movimiento(
    val id: Int,
    val tipo: TipoMovimiento,
    val productoId: Int,
    val empleadoId: Int,
    val cantidad: Int,
    val nota: String? = null,
    val fechaRegistro: String,
    val createdAt: String,
    val updatedAt: String,
    val activo: Boolean = true,
    val transaccionId: Int? = null, 
    
    // Información adicional del producto (para mostrar en la lista)
    val productoNombre: String? = null,
    val productoCodigo: String? = null,
    val productoImagenUrl: String? = null,
    
    // Información adicional del empleado
    val empleadoNombre: String? = null
)

@Serializable
enum class TipoMovimiento(val valor: String) {
    ENTRADA("ENTRADA"),
    SALIDA("SALIDA")
}

@Serializable
data class MovimientoCreate(
    val tipo: TipoMovimiento,
    val productoId: Int,
    val empleadoId: Int,
    val cantidad: Int,
    val nota: String? = null,
    val transaccionId: Int? = null 
)

@Serializable
data class MovimientoDetalle(
    val id: Int,
    val tipo: TipoMovimiento,
    val cantidad: Int,
    val nota: String?,
    val fechaRegistro: String,
    @Contextual val producto: Producto,
    @Contextual val empleado: Empleado
)

/**
 * Genera un movimiento de salida automático basado en una transacción
 */
fun generarMovimientoDesdeTransaccion(transaccion: Transaccion): MovimientoCreate {
    // Limpiar el nombre del cliente para evitar caracteres problemáticos en JSON
    val nombreClienteLimpio = transaccion.nombreCliente.trim()
        .replace("\t", " ")     // Reemplazar tabs por espacios
        .replace("\n", " ")     // Reemplazar saltos de línea por espacios
        .replace("\r", " ")     // Reemplazar retornos de carro por espacios
        .replace("  ", " ")     // Reemplazar espacios dobles por espacios simples
    
    return MovimientoCreate(
        tipo = TipoMovimiento.SALIDA,
        productoId = transaccion.productoId,
        empleadoId = transaccion.empleadoId,
        cantidad = transaccion.cantidad,
        nota = "Salida automática por transacción ${transaccion.id} - Cliente: $nombreClienteLimpio",
        transaccionId = transaccion.id
    )
}

/**
 * Genera un movimiento de salida automático basado en una transacción en proceso de creación
 * Nota: El transaccionId se debe asignar después de crear la transacción
 */
fun generarMovimientoDesdeTransaccionCreate(
    transaccionCreate: TransaccionCreate, 
    transaccionId: Int? = null
): MovimientoCreate {
    // Limpiar el nombre del cliente para evitar caracteres problemáticos en JSON
    val nombreClienteLimpio = transaccionCreate.nombreCliente.trim()
        .replace("\t", " ")     // Reemplazar tabs por espacios
        .replace("\n", " ")     // Reemplazar saltos de línea por espacios
        .replace("\r", " ")     // Reemplazar retornos de carro por espacios
        .replace("  ", " ")     // Reemplazar espacios dobles por espacios simples
    
    return MovimientoCreate(
        tipo = TipoMovimiento.SALIDA,
        productoId = transaccionCreate.productoId,
        empleadoId = transaccionCreate.empleadoId,
        cantidad = transaccionCreate.cantidad,
        nota = "Salida automática por venta - Cliente: $nombreClienteLimpio",
        transaccionId = transaccionId
    )
}