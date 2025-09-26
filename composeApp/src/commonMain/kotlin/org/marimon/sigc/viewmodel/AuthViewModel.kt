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
import org.marimon.sigc.data.repository.SupabaseAuthRepository

class AuthViewModel : ViewModel() {
    
    private val authRepository = SupabaseAuthRepository()
    
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
            val loggedIn = authRepository.isLoggedIn()
            _isLoggedIn.value = loggedIn
            
            if (loggedIn) {
                val user = authRepository.getCurrentUser()
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
                _isLoggedIn.value = true
                _currentUser.value = result.user
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            val result = authRepository.logout()
            _isLoggedIn.value = false
            _currentUser.value = null
            _authState.value = AuthResult.Error("")
        }
    }
    
    fun clearError() {
        if (_authState.value is AuthResult.Error) {
            _authState.value = AuthResult.Error("")
        }
    }
}
