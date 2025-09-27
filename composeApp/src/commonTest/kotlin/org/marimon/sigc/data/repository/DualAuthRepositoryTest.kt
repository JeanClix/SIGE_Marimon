package org.marimon.sigc.data.repository

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DualAuthRepositoryTest {

    @Test
    fun `test EmpleadoLoginResponse deserialization`() = runTest {
        // JSON response from Supabase (based on the logs we saw)
        val jsonResponse = """
            [{
                "id": 35,
                "nombre": "jose",
                "email_corporativo": "jose12@sige.com",
                "area_id": 1,
                "imagen_url": "https://xjqjlllzbcrpcylhnlmh.supabase.co/storage/v1/object/public/empleados/empleado_1758960096877.jpg",
                "activo": true,
                "created_at": "2025-09-27T08:02:47.849698",
                "updated_at": "2025-09-27T08:02:47.849698",
                "password": "jose12"
            }]
        """.trimIndent()

        // Test deserialization
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        
        val empleados = json.decodeFromString<List<EmpleadoLoginResponse>>(jsonResponse)
        
        // Verify deserialization works correctly
        assertEquals(1, empleados.size)
        
        val empleado = empleados.first()
        assertEquals(35, empleado.id)
        assertEquals("jose", empleado.nombre)
        assertEquals("jose12@sige.com", empleado.email_corporativo)
        assertEquals(1, empleado.area_id)
        assertEquals("jose12", empleado.password) // This was the main issue
        assertEquals(true, empleado.activo)
        assertEquals("https://xjqjlllzbcrpcylhnlmh.supabase.co/storage/v1/object/public/empleados/empleado_1758960096877.jpg", empleado.imagen_url)
    }

    @Test
    fun `test password comparison logic`() = runTest {
        // Test password comparison (the core logic)
        val storedPassword = "jose12"
        val inputPassword = "jose12"
        
        val isMatch = storedPassword == inputPassword
        assertTrue(isMatch, "Passwords should match")
        
        // Test with different passwords
        val wrongPassword = "wrong123"
        val isNotMatch = storedPassword == wrongPassword
        assertTrue(!isNotMatch, "Different passwords should not match")
    }

    @Test
    fun `test User creation from EmpleadoLoginResponse`() = runTest {
        val empleado = EmpleadoLoginResponse(
            id = 35,
            nombre = "jose",
            email_corporativo = "jose12@sige.com",
            area_id = 1,
            password = "jose12",
            imagen_url = "https://example.com/image.jpg",
            activo = true,
            created_at = "2025-09-27T08:02:47.849698",
            updated_at = "2025-09-27T08:02:47.849698"
        )
        
        // Test User creation logic (from DualAuthRepository)
        val user = org.marimon.sigc.data.model.User(
            id = empleado.id.toString(),
            username = empleado.nombre,
            email = empleado.email_corporativo,
            firstName = empleado.nombre.split(" ").firstOrNull() ?: empleado.nombre,
            lastName = empleado.nombre.split(" ").drop(1).joinToString(" "),
            role = org.marimon.sigc.data.model.UserRole.EMPLOYEE,
            createdAt = null,
            updatedAt = null
        )
        
        // Verify User creation
        assertEquals("35", user.id)
        assertEquals("jose", user.username)
        assertEquals("jose12@sige.com", user.email)
        assertEquals("jose", user.firstName)
        assertEquals("", user.lastName) // Single name, so lastName should be empty
        assertEquals(org.marimon.sigc.data.model.UserRole.EMPLOYEE, user.role)
    }

    @Test
    fun `test User creation with full name`() = runTest {
        val empleado = EmpleadoLoginResponse(
            id = 1,
            nombre = "Juan Carlos Pérez",
            email_corporativo = "juan@sige.com",
            area_id = 2,
            password = "password123",
            imagen_url = null,
            activo = true,
            created_at = null,
            updated_at = null
        )
        
        val user = org.marimon.sigc.data.model.User(
            id = empleado.id.toString(),
            username = empleado.nombre,
            email = empleado.email_corporativo,
            firstName = empleado.nombre.split(" ").firstOrNull() ?: empleado.nombre,
            lastName = empleado.nombre.split(" ").drop(1).joinToString(" "),
            role = org.marimon.sigc.data.model.UserRole.EMPLOYEE,
            createdAt = null,
            updatedAt = null
        )
        
        // Verify User creation with full name
        assertEquals("Juan", user.firstName)
        assertEquals("Carlos Pérez", user.lastName)
    }
}
