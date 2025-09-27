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

class AuthViewModel : ViewModel() {
    
    private val authRepository = DualAuthRepository()
    
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
            val loggedIn = authRepository.isLoggedIn()
            println("DEBUG: AuthViewModel - checkCurrentSession - loggedIn: $loggedIn")
            _isLoggedIn.value = loggedIn

            if (loggedIn) {
                val user = authRepository.getCurrentUser()
                println("DEBUG: AuthViewModel - checkCurrentSession - user: $user")
                _currentUser.value = user
            }
        }
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            
            val loginRequest = LoginRequest(email, password)
            val result = authRepository.login(loginRequest)
            
            _authState.value = result
            
            if (result is AuthResult.Success) {
                println("DEBUG: AuthViewModel - Login exitoso")
                println("DEBUG: AuthViewModel - Usuario: ${result.user}")
                println("DEBUG: AuthViewModel - Rol: ${result.user.role}")
                
                _isLoggedIn.value = true
                _currentUser.value = result.user
                
                println("DEBUG: AuthViewModel - Estado actualizado - isLoggedIn: true")
                println("DEBUG: AuthViewModel - Estado actualizado - currentUser: ${_currentUser.value}")
            } else {
                println("DEBUG: AuthViewModel - Login falló: ${result}")
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            val result = authRepository.logout()
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
