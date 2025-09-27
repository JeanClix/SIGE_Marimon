package org.marimon.sigc.data.repository

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileWriter
import java.io.FileReader

@Serializable
data class EmpleadoPassword(
    val email: String,
    val password: String,
    val empleadoId: Int
)

class LocalPasswordManager {
    
    private val passwordFile = File("empleado_passwords.json")
    private val json = Json { prettyPrint = true }
    
    fun savePassword(email: String, password: String, empleadoId: Int) {
        try {
            val passwords = loadPasswords().toMutableList()
            
            // Remover contraseña existente si existe
            passwords.removeAll { it.email == email }
            
            // Agregar nueva contraseña
            passwords.add(EmpleadoPassword(email, password, empleadoId))
            
            // Guardar al archivo
            val jsonString = json.encodeToString(passwords)
            FileWriter(passwordFile).use { it.write(jsonString) }
            
            println("DEBUG: Contraseña guardada localmente para $email")
        } catch (e: Exception) {
            println("DEBUG: Error guardando contraseña: ${e.message}")
        }
    }
    
    fun getPassword(email: String): String? {
        return try {
            val passwords = loadPasswords()
            val empleadoPassword = passwords.find { it.email == email }
            empleadoPassword?.password
        } catch (e: Exception) {
            println("DEBUG: Error cargando contraseña: ${e.message}")
            null
        }
    }
    
    private fun loadPasswords(): List<EmpleadoPassword> {
        return try {
            if (!passwordFile.exists()) {
                return emptyList()
            }
            
            val jsonString = FileReader(passwordFile).readText()
            json.decodeFromString<List<EmpleadoPassword>>(jsonString)
        } catch (e: Exception) {
            println("DEBUG: Error cargando contraseñas: ${e.message}")
            emptyList()
        }
    }
}
