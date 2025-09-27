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
import org.marimon.sigc.data.model.UserRole

@Serializable
data class DualSupabaseAuthResponse(
    val access_token: String? = null,
    val token_type: String? = null,
    val expires_in: Int? = null,
    val refresh_token: String? = null,
    val user: DualSupabaseUser? = null,
    val error: String? = null,
    val error_description: String? = null
)

@Serializable
data class DualSupabaseUser(
    val id: String,
    val email: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val user_metadata: Map<String, String>? = null
)

@Serializable
data class EmpleadoLoginResponse(
    val id: Int,
    val nombre: String,
    val email_corporativo: String,
    val area_id: Int,
    val password: String,
    val imagen_url: String? = null,
    val activo: Boolean = true,
    val created_at: String? = null,
    val updated_at: String? = null
)

class DualAuthRepository {
    
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
            
            // Primero intentar autenticación como administrador con Supabase
            val adminResult = tryLoginAsAdmin(loginRequest)
            if (adminResult is AuthResult.Success) {
                return adminResult
            }
            
            // Si no es admin, intentar como empleado
            val employeeResult = tryLoginAsEmployee(loginRequest)
            if (employeeResult is AuthResult.Success) {
                return employeeResult
            }
            
            // Si ninguno funciona, retornar error
            AuthResult.Error("Credenciales incorrectas")
            
        } catch (e: Exception) {
            AuthResult.Error("Error de conexión: ${e.message}")
        }
    }
    
    private suspend fun tryLoginAsAdmin(loginRequest: LoginRequest): AuthResult {
        return try {
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
                val authResponse = response.body<DualSupabaseAuthResponse>()
                
                if (authResponse.user != null) {
                    val user = User(
                        id = authResponse.user.id,
                        username = authResponse.user.email ?: "",
                        email = authResponse.user.email ?: "",
                        firstName = authResponse.user.user_metadata?.get("first_name") ?: "Admin",
                        lastName = authResponse.user.user_metadata?.get("last_name") ?: "Sistema",
                        role = UserRole.ADMIN,
                        createdAt = authResponse.user.created_at,
                        updatedAt = authResponse.user.updated_at
                    )
                    
                    AuthResult.Success(user)
                } else {
                    AuthResult.Error("Error al obtener datos del usuario")
                }
            } else {
                AuthResult.Error("Credenciales de administrador incorrectas")
            }
            
        } catch (e: Exception) {
            AuthResult.Error("Error de conexión con Supabase: ${e.message}")
        }
    }
    
    private suspend fun tryLoginAsEmployee(loginRequest: LoginRequest): AuthResult {
        return try {
            // Buscar empleado por email y verificar contraseña
            println("DEBUG: Buscando empleado con email: ${loginRequest.email}")
            val response = httpClient.get("${SupabaseConfig.SUPABASE_URL}/rest/v1/Empleado") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}")
                    append("apikey", SupabaseConfig.SUPABASE_ANON_KEY)
                    append("Content-Type", "application/json")
                }
                parameter("email_corporativo", "eq.${loginRequest.email}")
                parameter("activo", "eq.true")
            }
            
            println("DEBUG: Respuesta de búsqueda de empleado - Status: ${response.status}")
            
            if (response.status.isSuccess()) {
                val responseBody = response.bodyAsText()
                println("DEBUG: Respuesta completa de Supabase: $responseBody")
                
                val empleados = response.body<List<EmpleadoLoginResponse>>()
                println("DEBUG: Empleados encontrados: ${empleados.size}")
                
                if (empleados.isNotEmpty()) {
                    val empleado = empleados.first()
                    println("DEBUG: Empleado encontrado: $empleado")
                    println("DEBUG: Empleado activo: ${empleado.activo}")
                    
                    // Verificar contraseña directamente desde Supabase
                    println("DEBUG: Verificando contraseña para ${empleado.email_corporativo}")
                    println("DEBUG: Contraseña almacenada: '${empleado.password}'")
                    println("DEBUG: Contraseña ingresada: '${loginRequest.password}'")
                    println("DEBUG: ¿Son iguales? ${empleado.password == loginRequest.password}")
                    
                    if (empleado.password == loginRequest.password) {
                        val user = User(
                            id = empleado.id.toString(),
                            username = empleado.nombre,
                            email = empleado.email_corporativo,
                            firstName = empleado.nombre.split(" ").firstOrNull() ?: empleado.nombre,
                            lastName = empleado.nombre.split(" ").drop(1).joinToString(" "),
                            role = UserRole.EMPLOYEE,
                            createdAt = null,
                            updatedAt = null
                        )
                        
                        println("DEBUG: Usuario empleado creado: $user")
                        println("DEBUG: Rol asignado: ${user.role}")
                        println("DEBUG: ¿Es EMPLOYEE? ${user.role == UserRole.EMPLOYEE}")
                        
                        AuthResult.Success(user)
                    } else {
                        AuthResult.Error("Contraseña incorrecta")
                    }
                } else {
                    println("DEBUG: No se encontraron empleados con ese email")
                    AuthResult.Error("Empleado no encontrado o inactivo")
                }
            } else {
                println("DEBUG: Error en respuesta de Supabase: ${response.status}")
                val errorBody = response.bodyAsText()
                println("DEBUG: Error body: $errorBody")
                AuthResult.Error("Error al buscar empleado")
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
                    lastName = "",
                    role = UserRole.EMPLOYEE
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
