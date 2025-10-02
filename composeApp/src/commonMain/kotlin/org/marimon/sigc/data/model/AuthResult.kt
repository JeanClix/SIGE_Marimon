package org.marimon.sigc.data.model

import org.marimon.sigc.model.Empleado

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class EmpleadoSuccess(val empleado: Empleado) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}
