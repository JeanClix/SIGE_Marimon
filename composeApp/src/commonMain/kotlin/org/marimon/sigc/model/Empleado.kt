package org.marimon.sigc.model

data class Empleado(
    val id: Int,
    val nombre: String,
    val emailCorporativo: String,
    val areaId: Int,
    val areaNombre: String,
    val imagenUrl: String? = null,
    val activo: Boolean = true,
    val password: String? = null
)

data class EmpleadoCreate(
    val nombre: String,
    val emailCorporativo: String,
    val areaId: Int,
    val imagenUrl: String? = null,
    val activo: Boolean = true,
    val password: String? = null
)

