package org.marimon.sigc.data.repository

import kotlinx.coroutines.delay
import org.marimon.sigc.data.model.AuthResult
import org.marimon.sigc.data.model.LoginRequest
import org.marimon.sigc.data.model.User

class AuthRepository {
    
    // Simulación de usuarios en memoria (en una app real esto vendría de una API o base de datos)
    private val mockUsers = listOf(
        User(
            id = "1",
            username = "admin",
            email = "admin@sige.com",
            firstName = "Administrador",
            lastName = "Sistema"
        ),
        User(
            id = "2",
            username = "usuario",
            email = "usuario@sige.com",
            firstName = "Usuario",
            lastName = "Prueba"
        )
    )
    
    suspend fun login(loginRequest: LoginRequest): AuthResult {
        // Simular delay de red
        delay(1000)
        
        return when {
            loginRequest.email.isBlank() || loginRequest.password.isBlank() -> {
                AuthResult.Error("Email y contraseña son requeridos")
            }
            loginRequest.email == "admin@sige.com" && loginRequest.password == "admin123" -> {
                AuthResult.Success(mockUsers[0])
            }
            loginRequest.email == "usuario@sige.com" && loginRequest.password == "user123" -> {
                AuthResult.Success(mockUsers[1])
            }
            else -> {
                AuthResult.Error("Credenciales incorrectas")
            }
        }
    }
    
    fun logout(): AuthResult {
        return AuthResult.Success(
            User(
                id = "",
                username = "",
                email = "",
                firstName = "",
                lastName = ""
            )
        )
    }
}
