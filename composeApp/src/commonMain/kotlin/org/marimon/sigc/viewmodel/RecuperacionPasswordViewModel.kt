package org.marimon.sigc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.marimon.sigc.model.RecuperacionResult
import org.marimon.sigc.repository.RecuperacionPasswordRepository

class RecuperacionPasswordViewModel : ViewModel() {

    private val repository = RecuperacionPasswordRepository()

    // Estado para Pantalla 1: Solicitar código
    private val _solicitarState = MutableStateFlow<RecuperacionResult>(RecuperacionResult.Idle)
    val solicitarState: StateFlow<RecuperacionResult> = _solicitarState.asStateFlow()

    // Estado para Pantalla 2: Verificar código
    private val _verificarState = MutableStateFlow<RecuperacionResult>(RecuperacionResult.Idle)
    val verificarState: StateFlow<RecuperacionResult> = _verificarState.asStateFlow()

    // Estado para Pantalla 3: Cambiar contraseña
    private val _cambiarState = MutableStateFlow<RecuperacionResult>(RecuperacionResult.Idle)
    val cambiarState: StateFlow<RecuperacionResult> = _cambiarState.asStateFlow()

    // Email temporal para pasar entre pantallas
    private val _emailTemporal = MutableStateFlow("")
    val emailTemporal: StateFlow<String> = _emailTemporal.asStateFlow()

    // Código temporal para pasar a la última pantalla
    private val _codigoTemporal = MutableStateFlow("")
    val codigoTemporal: StateFlow<String> = _codigoTemporal.asStateFlow()

    /**
     * Pantalla 1: Solicitar código de recuperación
     */
    fun solicitarRecuperacion(emailCorporativo: String) {
        viewModelScope.launch {
            _solicitarState.value = RecuperacionResult.Loading

            val (exito, mensaje) = repository.solicitarRecuperacion(emailCorporativo)

            if (exito) {
                _emailTemporal.value = emailCorporativo
                _solicitarState.value = RecuperacionResult.Success(mensaje)
            } else {
                _solicitarState.value = RecuperacionResult.Error(mensaje)
            }
        }
    }

    /**
     * Pantalla 2: Verificar código
     */
    fun verificarCodigo(codigo: String) {
        viewModelScope.launch {
            _verificarState.value = RecuperacionResult.Loading

            val email = _emailTemporal.value
            if (email.isEmpty()) {
                _verificarState.value = RecuperacionResult.Error("Email no encontrado. Reinicia el proceso.")
                return@launch
            }

            val (exito, mensaje) = repository.verificarCodigo(email, codigo)

            if (exito) {
                _codigoTemporal.value = codigo
                _verificarState.value = RecuperacionResult.Success(mensaje)
            } else {
                _verificarState.value = RecuperacionResult.Error(mensaje)
            }
        }
    }

    /**
     * Pantalla 3: Cambiar contraseña
     */
    fun cambiarPassword(nuevaPassword: String, confirmarPassword: String) {
        viewModelScope.launch {
            // Validaciones locales
            if (nuevaPassword.isEmpty() || confirmarPassword.isEmpty()) {
                _cambiarState.value = RecuperacionResult.Error("Completa todos los campos")
                return@launch
            }

            if (nuevaPassword != confirmarPassword) {
                _cambiarState.value = RecuperacionResult.Error("Las contraseñas no coinciden")
                return@launch
            }

            if (nuevaPassword.length < 6) {
                _cambiarState.value = RecuperacionResult.Error("La contraseña debe tener al menos 6 caracteres")
                return@launch
            }

            _cambiarState.value = RecuperacionResult.Loading

            val email = _emailTemporal.value
            val codigo = _codigoTemporal.value

            if (email.isEmpty() || codigo.isEmpty()) {
                _cambiarState.value = RecuperacionResult.Error("Datos incompletos. Reinicia el proceso.")
                return@launch
            }

            val (exito, mensaje) = repository.cambiarPassword(email, codigo, nuevaPassword)

            if (exito) {
                _cambiarState.value = RecuperacionResult.Success(mensaje)
                limpiarDatos()
            } else {
                _cambiarState.value = RecuperacionResult.Error(mensaje)
            }
        }
    }

    /**
     * Reenviar código
     */
    fun reenviarCodigo() {
        val email = _emailTemporal.value
        if (email.isNotEmpty()) {
            solicitarRecuperacion(email)
        }
    }

    /**
     * Limpiar estados
     */
    fun limpiarEstados() {
        _solicitarState.value = RecuperacionResult.Idle
        _verificarState.value = RecuperacionResult.Idle
        _cambiarState.value = RecuperacionResult.Idle
    }

    /**
     * Limpiar todos los datos
     */
    private fun limpiarDatos() {
        _emailTemporal.value = ""
        _codigoTemporal.value = ""
    }

    /**
     * Establecer email manualmente (para testing)
     */
    fun setEmailTemporal(email: String) {
        _emailTemporal.value = email
    }
}
