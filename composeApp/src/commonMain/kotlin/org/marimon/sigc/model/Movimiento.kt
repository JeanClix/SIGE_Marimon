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
    val nota: String? = null
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