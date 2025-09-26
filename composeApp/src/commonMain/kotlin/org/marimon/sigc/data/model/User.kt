package org.marimon.sigc.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
