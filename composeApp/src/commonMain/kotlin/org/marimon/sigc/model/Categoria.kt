package org.marimon.sigc.model

import kotlinx.serialization.Serializable

@Serializable
data class Categoria(
    val id: Int,
    val nombre: String,
    val descripcion: String? = null,
    val activo: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

// DTO para crear categoría
@Serializable
data class CategoriaCreate(
    val nombre: String,
    val descripcion: String? = null,
    val activo: Boolean = true
)
