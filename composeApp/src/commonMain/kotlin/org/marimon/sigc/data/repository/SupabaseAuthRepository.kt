package org.marimon.sigc.data.repository

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.delay
import org.marimon.sigc.config.SupabaseClient
import org.marimon.sigc.data.model.AuthResult
import org.marimon.sigc.data.model.LoginRequest
import org.marimon.sigc.data.model.User

class SupabaseAuthRepository {
    
    private val supabase = SupabaseClient.client
    private val auth = supabase.auth
    private val postgrest = supabase.postgrest
    
    suspend fun login(loginRequest: LoginRequest): AuthResult {
        return try {
            // Simular delay de red
            delay(1000)
            
            // Validar campos
            if (loginRequest.email.isBlank() || loginRequest.password.isBlank()) {
                return AuthResult.Error("Email y contraseña son requeridos")
            }
            
            // Intentar login con Supabase Auth
            val response = auth.signInWith(loginRequest.email) {
                password = loginRequest.password
            }
            
            // Obtener información del usuario
            val userInfo = response.user
            val user = User(
                id = userInfo.id,
                username = userInfo.email ?: "",
                email = userInfo.email ?: "",
                firstName = userInfo.userMetadata?.get("first_name")?.toString() ?: "",
                lastName = userInfo.userMetadata?.get("last_name")?.toString() ?: "",
                createdAt = userInfo.createdAt,
                updatedAt = userInfo.updatedAt
            )
            
            AuthResult.Success(user)
            
        } catch (e: Exception) {
            AuthResult.Error("Error de autenticación: ${e.message}")
        }
    }
    
    suspend fun logout(): AuthResult {
        return try {
            auth.signOut()
            AuthResult.Success(
                User(
                    id = "",
                    username = "",
                    email = "",
                    firstName = "",
                    lastName = ""
                )
            )
        } catch (e: Exception) {
            AuthResult.Error("Error al cerrar sesión: ${e.message}")
        }
    }
    
    suspend fun getCurrentUser(): User? {
        return try {
            val session = auth.currentSessionOrNull()
            if (session != null) {
                val userInfo = session.user
                User(
                    id = userInfo.id,
                    username = userInfo.email ?: "",
                    email = userInfo.email ?: "",
                    firstName = userInfo.userMetadata?.get("first_name")?.toString() ?: "",
                    lastName = userInfo.userMetadata?.get("last_name")?.toString() ?: "",
                    createdAt = userInfo.createdAt,
                    updatedAt = userInfo.updatedAt
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun isLoggedIn(): Boolean {
        return try {
            auth.currentSessionOrNull() != null
        } catch (e: Exception) {
            false
        }
    }
}
