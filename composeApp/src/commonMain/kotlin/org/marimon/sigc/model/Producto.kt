package org.marimon.sigc.model

import kotlinx.serialization.Serializable

@Serializable
data class Producto(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val descripcion: String? = null,
    val especificaciones: String? = null,
    val precio: Double,
    val cantidad: Int,
    val imagenUrl: String? = null,
    val activo: Boolean = true,
    val fechaCreacion: String? = null,
    val fechaActualizacion: String? = null,
    val categoriaId: Int? = null,
    val categoriaNombre: String? = null
)

// DTO para crear producto
@Serializable
data class ProductoCreate(
    val codigo: String,
    val nombre: String,
    val descripcion: String? = null,
    val especificaciones: String? = null,
    val precio: Double,
    val cantidad: Int,
    val imagenUrl: String? = null,
    val activo: Boolean = true,
    val categoriaId: Int? = null
)