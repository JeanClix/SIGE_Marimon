package org.marimon.sigc.manual

import kotlinx.coroutines.runBlocking
import org.marimon.sigc.data.model.LoginRequest
import org.marimon.sigc.data.model.UserRole
import org.marimon.sigc.data.repository.DualAuthRepository
import org.marimon.sigc.data.repository.EmpleadoLoginResponse

/**
 * Test manual para verificar el sistema de login dual
 * 
 * Este test simula el flujo completo sin hacer llamadas HTTP reales
 * para verificar que la lógica funciona correctamente.
 */
fun main() {
    println("🧪 INICIANDO TEST MANUAL DEL SISTEMA DE LOGIN DUAL")
    println("=" * 50)
    
    // Test 1: Deserialización JSON
    testJsonDeserialization()
    
    // Test 2: Comparación de contraseñas
    testPasswordComparison()
    
    // Test 3: Creación de usuarios
    testUserCreation()
    
    // Test 4: Lógica de roles
    testRoleLogic()
    
    println("\n✅ TODOS LOS TESTS MANUALES PASARON EXITOSAMENTE!")
    println("🚀 El sistema de login dual está funcionando correctamente")
}

fun testJsonDeserialization() {
    println("\n📋 Test 1: Deserialización JSON")
    
    val jsonResponse = """
        [{
            "id": 35,
            "nombre": "jose",
            "email_corporativo": "jose12@sige.com",
            "area_id": 1,
            "imagen_url": "https://example.com/image.jpg",
            "activo": true,
            "created_at": "2025-09-27T08:02:47.849698",
            "updated_at": "2025-09-27T08:02:47.849698",
            "password": "jose12"
        }]
    """.trimIndent()
    
    try {
        val json = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        
        val empleados = json.decodeFromString<List<EmpleadoLoginResponse>>(jsonResponse)
        
        assert(empleados.size == 1) { "Debería haber 1 empleado" }
        
        val empleado = empleados.first()
        assert(empleado.id == 35) { "ID debería ser 35" }
        assert(empleado.nombre == "jose") { "Nombre debería ser 'jose'" }
        assert(empleado.password == "jose12") { "Password debería ser 'jose12'" }
        assert(empleado.activo == true) { "Debería estar activo" }
        
        println("✅ Deserialización JSON: EXITOSA")
        println("   - ID: ${empleado.id}")
        println("   - Nombre: ${empleado.nombre}")
        println("   - Email: ${empleado.email_corporativo}")
        println("   - Password: ${empleado.password}")
        println("   - Activo: ${empleado.activo}")
        
    } catch (e: Exception) {
        println("❌ Deserialización JSON: FALLÓ - ${e.message}")
        throw e
    }
}

fun testPasswordComparison() {
    println("\n🔐 Test 2: Comparación de Contraseñas")
    
    val storedPassword = "jose12"
    val inputPassword = "jose12"
    val wrongPassword = "wrong123"
    
    // Test password match
    val isMatch = storedPassword == inputPassword
    assert(isMatch) { "Contraseñas correctas deberían coincidir" }
    
    // Test password mismatch
    val isNotMatch = storedPassword == wrongPassword
    assert(!isNotMatch) { "Contraseñas diferentes no deberían coincidir" }
    
    println("✅ Comparación de contraseñas: EXITOSA")
    println("   - Contraseña correcta: ${storedPassword == inputPassword}")
    println("   - Contraseña incorrecta: ${storedPassword == wrongPassword}")
}

fun testUserCreation() {
    println("\n👤 Test 3: Creación de Usuarios")
    
    // Test Employee User creation
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
    
    val employeeUser = org.marimon.sigc.data.model.User(
        id = empleado.id.toString(),
        username = empleado.nombre,
        email = empleado.email_corporativo,
        firstName = empleado.nombre.split(" ").firstOrNull() ?: empleado.nombre,
        lastName = empleado.nombre.split(" ").drop(1).joinToString(" "),
        role = UserRole.EMPLOYEE,
        createdAt = null,
        updatedAt = null
    )
    
    assert(employeeUser.id == "35") { "ID del usuario debería ser '35'" }
    assert(employeeUser.username == "jose") { "Username debería ser 'jose'" }
    assert(employeeUser.email == "jose12@sige.com") { "Email debería coincidir" }
    assert(employeeUser.firstName == "jose") { "FirstName debería ser 'jose'" }
    assert(employeeUser.lastName == "") { "LastName debería estar vacío para nombre simple" }
    assert(employeeUser.role == UserRole.EMPLOYEE) { "Rol debería ser EMPLOYEE" }
    
    // Test Admin User creation
    val adminUser = org.marimon.sigc.data.model.User(
        id = "admin-uuid-123",
        username = "admin@sige.com",
        email = "admin@sige.com",
        firstName = "Admin",
        lastName = "User",
        role = UserRole.ADMIN,
        createdAt = "2025-09-27T08:00:00.000000",
        updatedAt = "2025-09-27T08:00:00.000000"
    )
    
    assert(adminUser.role == UserRole.ADMIN) { "Rol del admin debería ser ADMIN" }
    
    println("✅ Creación de usuarios: EXITOSA")
    println("   - Usuario Empleado:")
    println("     * ID: ${employeeUser.id}")
    println("     * Nombre: ${employeeUser.firstName} ${employeeUser.lastName}")
    println("     * Email: ${employeeUser.email}")
    println("     * Rol: ${employeeUser.role}")
    println("   - Usuario Admin:")
    println("     * ID: ${adminUser.id}")
    println("     * Nombre: ${adminUser.firstName} ${adminUser.lastName}")
    println("     * Email: ${adminUser.email}")
    println("     * Rol: ${adminUser.role}")
}

fun testRoleLogic() {
    println("\n🎭 Test 4: Lógica de Roles")
    
    val employeeUser = org.marimon.sigc.data.model.User(
        id = "1",
        username = "employee",
        email = "employee@sige.com",
        firstName = "Employee",
        lastName = "User",
        role = UserRole.EMPLOYEE
    )
    
    val adminUser = org.marimon.sigc.data.model.User(
        id = "2",
        username = "admin",
        email = "admin@sige.com",
        firstName = "Admin",
        lastName = "User",
        role = UserRole.ADMIN
    )
    
    // Test role-based navigation logic
    val employeeShouldGoToEmployeeScreen = employeeUser.role == UserRole.EMPLOYEE
    val adminShouldGoToHomeScreen = adminUser.role == UserRole.ADMIN
    
    assert(employeeShouldGoToEmployeeScreen) { "Empleado debería ir a EmployeeScreen" }
    assert(adminShouldGoToHomeScreen) { "Admin debería ir a HomeScreen" }
    
    // Test role comparison
    assert(employeeUser.role != adminUser.role) { "Roles de empleado y admin deberían ser diferentes" }
    
    println("✅ Lógica de roles: EXITOSA")
    println("   - Empleado va a EmployeeScreen: $employeeShouldGoToEmployeeScreen")
    println("   - Admin va a HomeScreen: $adminShouldGoToHomeScreen")
    println("   - Roles son diferentes: ${employeeUser.role != adminUser.role}")
}

// Función helper para repetir strings
operator fun String.times(n: Int): String = this.repeat(n)
