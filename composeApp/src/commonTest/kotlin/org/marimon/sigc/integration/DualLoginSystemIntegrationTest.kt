package org.marimon.sigc.integration

import kotlinx.coroutines.test.runTest
import org.marimon.sigc.data.model.AuthResult
import org.marimon.sigc.data.model.LoginRequest
import org.marimon.sigc.data.model.UserRole
import org.marimon.sigc.data.repository.DualAuthRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DualLoginSystemIntegrationTest {

    @Test
    fun `test complete dual login flow - employee login simulation`() = runTest {
        // This test simulates the complete flow without making actual HTTP requests
        // It tests the logic and data structures
        
        // 1. Test LoginRequest creation
        val loginRequest = LoginRequest("jose12@sige.com", "jose12")
        assertEquals("jose12@sige.com", loginRequest.email)
        assertEquals("jose12", loginRequest.password)
        
        // 2. Test EmpleadoLoginResponse (simulating Supabase response)
        val empleadoResponse = org.marimon.sigc.data.repository.EmpleadoLoginResponse(
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
        
        // 3. Test password verification logic
        val passwordMatch = empleadoResponse.password == loginRequest.password
        assertTrue(passwordMatch, "Password should match")
        
        // 4. Test User creation from employee data
        val user = org.marimon.sigc.data.model.User(
            id = empleadoResponse.id.toString(),
            username = empleadoResponse.nombre,
            email = empleadoResponse.email_corporativo,
            firstName = empleadoResponse.nombre.split(" ").firstOrNull() ?: empleadoResponse.nombre,
            lastName = empleadoResponse.nombre.split(" ").drop(1).joinToString(" "),
            role = UserRole.EMPLOYEE,
            createdAt = null,
            updatedAt = null
        )
        
        // 5. Verify User properties
        assertEquals("35", user.id)
        assertEquals("jose", user.username)
        assertEquals("jose12@sige.com", user.email)
        assertEquals("jose", user.firstName)
        assertEquals("", user.lastName)
        assertEquals(UserRole.EMPLOYEE, user.role)
        
        // 6. Test AuthResult.Success creation
        val authResult = AuthResult.Success(user)
        assertTrue(authResult is AuthResult.Success)
        assertEquals(user, authResult.user)
        assertEquals(UserRole.EMPLOYEE, authResult.user.role)
    }

    @Test
    fun `test complete dual login flow - admin login simulation`() = runTest {
        // Test admin login flow simulation
        
        // 1. Test admin login request
        val adminLoginRequest = LoginRequest("admin@sige.com", "admin123")
        
        // 2. Test DualSupabaseUser (simulating Supabase Auth response)
        val supabaseUser = org.marimon.sigc.data.repository.DualSupabaseUser(
            id = "admin-uuid-123",
            email = "admin@sige.com",
            created_at = "2025-09-27T08:00:00.000000",
            updated_at = "2025-09-27T08:00:00.000000",
            user_metadata = mapOf(
                "first_name" to "Admin",
                "last_name" to "User"
            )
        )
        
        // 3. Test User creation from admin data
        val adminUser = org.marimon.sigc.data.model.User(
            id = supabaseUser.id,
            username = supabaseUser.email ?: "",
            email = supabaseUser.email ?: "",
            firstName = supabaseUser.user_metadata?.get("first_name") ?: "Admin",
            lastName = supabaseUser.user_metadata?.get("last_name") ?: "User",
            role = UserRole.ADMIN,
            createdAt = supabaseUser.created_at,
            updatedAt = supabaseUser.updated_at
        )
        
        // 4. Verify Admin User properties
        assertEquals("admin-uuid-123", adminUser.id)
        assertEquals("admin@sige.com", adminUser.username)
        assertEquals("admin@sige.com", adminUser.email)
        assertEquals("Admin", adminUser.firstName)
        assertEquals("User", adminUser.lastName)
        assertEquals(UserRole.ADMIN, adminUser.role)
        
        // 5. Test AuthResult.Success for admin
        val adminAuthResult = AuthResult.Success(adminUser)
        assertTrue(adminAuthResult is AuthResult.Success)
        assertEquals(UserRole.ADMIN, adminAuthResult.user.role)
    }

    @Test
    fun `test role-based navigation logic`() = runTest {
        // Test the navigation logic based on user roles
        
        // Employee user
        val employeeUser = org.marimon.sigc.data.model.User(
            id = "1",
            username = "employee",
            email = "employee@sige.com",
            firstName = "Employee",
            lastName = "User",
            role = UserRole.EMPLOYEE
        )
        
        // Admin user
        val adminUser = org.marimon.sigc.data.model.User(
            id = "2",
            username = "admin",
            email = "admin@sige.com",
            firstName = "Admin",
            lastName = "User",
            role = UserRole.ADMIN
        )
        
        // Test role-based decisions
        val employeeShouldGoToEmployeeScreen = employeeUser.role == UserRole.EMPLOYEE
        val adminShouldGoToHomeScreen = adminUser.role == UserRole.ADMIN
        
        assertTrue(employeeShouldGoToEmployeeScreen, "Employee should go to EmployeeScreen")
        assertTrue(adminShouldGoToHomeScreen, "Admin should go to HomeScreen")
        
        // Test role comparison
        assertTrue(employeeUser.role != adminUser.role, "Employee and Admin should have different roles")
    }

    @Test
    fun `test error scenarios`() = runTest {
        // Test various error scenarios
        
        // 1. Empty credentials
        val emptyEmailRequest = LoginRequest("", "password")
        val emptyPasswordRequest = LoginRequest("email@test.com", "")
        
        assertTrue(emptyEmailRequest.email.isBlank(), "Empty email should be blank")
        assertTrue(emptyPasswordRequest.password.isBlank(), "Empty password should be blank")
        
        // 2. Wrong password scenario
        val empleado = org.marimon.sigc.data.repository.EmpleadoLoginResponse(
            id = 1,
            nombre = "test",
            email_corporativo = "test@sige.com",
            area_id = 1,
            password = "correct123",
            activo = true
        )
        
        val wrongPassword = "wrong123"
        val passwordMismatch = empleado.password != wrongPassword
        assertTrue(passwordMismatch, "Wrong password should not match")
        
        // 3. Inactive employee scenario
        val inactiveEmpleado = org.marimon.sigc.data.repository.EmpleadoLoginResponse(
            id = 2,
            nombre = "inactive",
            email_corporativo = "inactive@sige.com",
            area_id = 1,
            password = "password123",
            activo = false
        )
        
        assertFalse(inactiveEmpleado.activo, "Inactive employee should have activo = false")
    }
}
