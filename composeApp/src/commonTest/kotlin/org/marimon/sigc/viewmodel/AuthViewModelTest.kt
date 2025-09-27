package org.marimon.sigc.viewmodel

import kotlinx.coroutines.test.runTest
import org.marimon.sigc.data.model.AuthResult
import org.marimon.sigc.data.model.UserRole
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthViewModelTest {

    @Test
    fun `test initial state`() = runTest {
        val authViewModel = AuthViewModel()
        
        // Test initial state
        assertFalse(authViewModel.isLoggedIn.value, "Should not be logged in initially")
        assertEquals(null, authViewModel.currentUser.value, "Current user should be null initially")
    }

    @Test
    fun `test clearError functionality`() = runTest {
        val authViewModel = AuthViewModel()
        
        // Simulate an error state
        authViewModel.login("test@test.com", "wrongpassword")
        
        // Wait a bit for the async operation
        Thread.sleep(100)
        
        // Clear the error
        authViewModel.clearError()
        
        // The error should be cleared (empty string)
        val authState = authViewModel.authState.value
        if (authState is AuthResult.Error) {
            assertEquals("", authState.message, "Error message should be empty after clearError")
        }
    }

    @Test
    fun `test logout functionality`() = runTest {
        val authViewModel = AuthViewModel()
        
        // Logout should work even when not logged in
        authViewModel.logout()
        
        // Wait for async operation
        Thread.sleep(100)
        
        // Should still not be logged in
        assertFalse(authViewModel.isLoggedIn.value, "Should not be logged in after logout")
        assertEquals(null, authViewModel.currentUser.value, "Current user should be null after logout")
    }

    @Test
    fun `test UserRole enum values`() = runTest {
        // Test UserRole enum
        assertEquals("ADMIN", UserRole.ADMIN.name)
        assertEquals("EMPLOYEE", UserRole.EMPLOYEE.name)
        
        // Test enum values
        val adminRole = UserRole.ADMIN
        val employeeRole = UserRole.EMPLOYEE
        
        assertTrue(adminRole == UserRole.ADMIN)
        assertTrue(employeeRole == UserRole.EMPLOYEE)
        assertTrue(adminRole != employeeRole)
    }

    @Test
    fun `test AuthResult types`() = runTest {
        // Test AuthResult.Success
        val user = org.marimon.sigc.data.model.User(
            id = "1",
            username = "test",
            email = "test@test.com",
            firstName = "Test",
            lastName = "User",
            role = UserRole.EMPLOYEE
        )
        
        val successResult = AuthResult.Success(user)
        assertTrue(successResult is AuthResult.Success)
        assertEquals(user, successResult.user)
        
        // Test AuthResult.Error
        val errorResult = AuthResult.Error("Test error")
        assertTrue(errorResult is AuthResult.Error)
        assertEquals("Test error", errorResult.message)
        
        // Test AuthResult.Loading
        val loadingResult = AuthResult.Loading
        assertTrue(loadingResult is AuthResult.Loading)
    }
}
