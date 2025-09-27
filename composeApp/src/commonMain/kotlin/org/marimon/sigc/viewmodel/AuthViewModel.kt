package org.marimon.sigc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.marimon.sigc.data.model.AuthResult
import org.marimon.sigc.data.model.LoginRequest
import org.marimon.sigc.data.model.User
import org.marimon.sigc.data.repository.DualAuthRepository
import org.marimon.sigc.data.repository.AuthRepository

class AuthViewModel : ViewModel() {
    
    private val dualAuthRepository = DualAuthRepository()
    private val originalAuthRepository = AuthRepository()
    
    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Error(""))
    val authState: StateFlow<AuthResult> = _authState.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    init {
        // Verificar si ya hay una sesión activa
        checkCurrentSession()
    }
    
    private fun checkCurrentSession() {
        viewModelScope.launch {
            println("DEBUG: AuthViewModel - checkCurrentSession iniciado")
            val loggedIn = dualAuthRepository.isLoggedIn()
            println("DEBUG: AuthViewModel - checkCurrentSession - loggedIn: $loggedIn")
            _isLoggedIn.value = loggedIn

            if (loggedIn) {
                val user = dualAuthRepository.getCurrentUser()
                println("DEBUG: AuthViewModel - checkCurrentSession - user: $user")
                _currentUser.value = user
            }
        }
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            
            val loginRequest = LoginRequest(email, password)
            
            // Primero intentar con el sistema dual (Supabase + empleados)
            val dualResult = dualAuthRepository.login(loginRequest)
            
            if (dualResult is AuthResult.Success) {
                println("DEBUG: AuthViewModel - Login exitoso con sistema dual")
                println("DEBUG: AuthViewModel - Usuario: ${dualResult.user}")
                println("DEBUG: AuthViewModel - Rol: ${dualResult.user.role}")
                
                _authState.value = dualResult
                _isLoggedIn.value = true
                _currentUser.value = dualResult.user
                
                println("DEBUG: AuthViewModel - Estado actualizado - isLoggedIn: true")
                println("DEBUG: AuthViewModel - Estado actualizado - currentUser: ${_currentUser.value}")
            } else {
                // Si falla el sistema dual, intentar con el sistema original
                println("DEBUG: AuthViewModel - Sistema dual falló, intentando sistema original")
                val originalResult = originalAuthRepository.login(loginRequest)
                
                if (originalResult is AuthResult.Success) {
                    // Convertir usuario original a usuario con rol ADMIN
                    val userWithRole = originalResult.user.copy(role = org.marimon.sigc.data.model.UserRole.ADMIN)
                    
                    println("DEBUG: AuthViewModel - Login exitoso con sistema original")
                    println("DEBUG: AuthViewModel - Usuario: $userWithRole")
                    
                    _authState.value = AuthResult.Success(userWithRole)
                    _isLoggedIn.value = true
                    _currentUser.value = userWithRole
                } else {
                    println("DEBUG: AuthViewModel - Ambos sistemas fallaron")
                    _authState.value = originalResult
                }
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            val result = dualAuthRepository.logout()
            _authState.value = result
            
            _isLoggedIn.value = false
            _currentUser.value = null
        }
    }
    
    fun clearError() {
        if (_authState.value is AuthResult.Error) {
            _authState.value = AuthResult.Error("")
        }
    }
    
    fun setLoggedInUser(user: User) {
        println("DEBUG: AuthViewModel - setLoggedInUser llamado con: $user")
        _isLoggedIn.value = true
        _currentUser.value = user
        _authState.value = AuthResult.Success(user)
        println("DEBUG: AuthViewModel - Estado configurado - isLoggedIn: true, currentUser: $user")
    }
}
