package org.marimon.sigc.model

import kotlinx.serialization.Serializable

/**
 * Modelo para códigos de recuperación de contraseña
 */
@Serializable
data class RecuperacionCodigo(
    val id: Int = 0,
    val empleadoId: Int,
    val codigo: String,
    val emailCorporativo: String,
    val fechaCreacion: String,
    val fechaExpiracion: String,
    val usado: Boolean = false,
    val activo: Boolean = true
)

/**
 * Request para crear un código de recuperación
 */
data class RecuperacionRequest(
    val emailCorporativo: String
)

/**
 * Request para verificar código
 */
data class VerificarCodigoRequest(
    val emailCorporativo: String,
    val codigo: String
)

/**
 * Request para cambiar contraseña
 */
data class CambiarPasswordRequest(
    val emailCorporativo: String,
    val codigo: String,
    val nuevaPassword: String
)

/**
 * Respuesta de operaciones de recuperación
 */
sealed class RecuperacionResult {
    object Idle : RecuperacionResult()  // Estado inicial
    data class Success(val mensaje: String) : RecuperacionResult()
    data class Error(val mensaje: String) : RecuperacionResult()
    object Loading : RecuperacionResult()
}
