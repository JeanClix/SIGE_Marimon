package org.marimon.sigc.data.repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import org.marimon.sigc.config.SupabaseConfig
import org.marimon.sigc.data.model.AuthResult
import org.marimon.sigc.data.model.LoginRequest
import org.marimon.sigc.data.model.User

@Serializable
data class SupabaseAuthResponse(
    val access_token: String? = null,
    val token_type: String? = null,
    val expires_in: Int? = null,
    val refresh_token: String? = null,
    val user: SupabaseUser? = null,
    val error: String? = null,
    val error_description: String? = null
)

@Serializable
data class SupabaseUser(
    val id: String,
    val email: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val user_metadata: Map<String, String>? = null
)

class SupabaseAuthRepository {
    
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
    
    suspend fun login(loginRequest: LoginRequest): AuthResult {
        return try {
            // Simular delay de red
            delay(1000)
            
            // Validar campos
            if (loginRequest.email.isBlank() || loginRequest.password.isBlank()) {
                return AuthResult.Error("Email y contraseña son requeridos")
            }
            
            // Hacer llamada a la API de autenticación de Supabase
            val response = httpClient.post("${SupabaseConfig.SUPABASE_URL}/auth/v1/token?grant_type=password") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}")
                    append(HttpHeaders.ContentType, "application/json")
                    append("apikey", SupabaseConfig.SUPABASE_ANON_KEY)
                }
                setBody(loginRequest)
            }
            
            if (response.status.isSuccess()) {
                val authResponse = response.body<SupabaseAuthResponse>()
                
                if (authResponse.user != null) {
                    val user = User(
                        id = authResponse.user.id,
                        username = authResponse.user.email ?: "",
                        email = authResponse.user.email ?: "",
                        firstName = authResponse.user.user_metadata?.get("first_name") ?: "",
                        lastName = authResponse.user.user_metadata?.get("last_name") ?: "",
                        createdAt = authResponse.user.created_at,
                        updatedAt = authResponse.user.updated_at
                    )
                    
                    AuthResult.Success(user)
                } else {
                    AuthResult.Error("Error al obtener datos del usuario")
                }
            } else {
                AuthResult.Error("Credenciales incorrectas")
            }
            
        } catch (e: Exception) {
            AuthResult.Error("Error de conexión: ${e.message}")
        }
    }
    
    suspend fun logout(): AuthResult {
        return try {
            // Simular logout
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
            // Por ahora retornamos null, en una implementación real
            // se verificaría el token almacenado
            null
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun isLoggedIn(): Boolean {
        return try {
            // Por ahora retornamos false, en una implementación real
            // se verificaría si hay un token válido almacenado
            false
        } catch (e: Exception) {
            false
        }
    }
}