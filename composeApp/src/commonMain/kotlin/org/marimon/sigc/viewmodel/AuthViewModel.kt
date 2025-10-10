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
import org.marimon.sigc.model.Empleado

class AuthViewModel : ViewModel() {
    
    private val authRepository = SupabaseAuthRepository()
    
    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Error(""))
    val authState: StateFlow<AuthResult> = _authState.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _currentEmpleado = MutableStateFlow<Empleado?>(null)
    val currentEmpleado: StateFlow<Empleado?> = _currentEmpleado.asStateFlow()
    
    private val _userType = MutableStateFlow<String?>(null) // "admin" o "empleado"
    val userType: StateFlow<String?> = _userType.asStateFlow()
    
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
            
            when (result) {
                is AuthResult.Success -> {
                    _isLoggedIn.value = true
                    _currentUser.value = result.user
                    _userType.value = "admin"
                    _currentEmpleado.value = null
                }
                is AuthResult.EmpleadoSuccess -> {
                    _isLoggedIn.value = true
                    _currentEmpleado.value = result.empleado
                    _userType.value = "empleado"
                    _currentUser.value = null
                }
                else -> {
                    _isLoggedIn.value = false
                    _currentUser.value = null
                    _currentEmpleado.value = null
                    _userType.value = null
                }
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            _isLoggedIn.value = false
            _currentUser.value = null
            _currentEmpleado.value = null
            _userType.value = null
            _authState.value = AuthResult.Error("")
        }
    }
    
    fun clearError() {
        if (_authState.value is AuthResult.Error) {
            _authState.value = AuthResult.Error("")
        }
    }
}
