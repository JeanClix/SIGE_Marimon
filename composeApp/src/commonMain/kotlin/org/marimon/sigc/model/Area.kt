package org.marimon.sigc.model

data class Area(
    val id: Int,
    val nombre: String,
    val descripcion: String? = null,
    val activo: Boolean = true
)