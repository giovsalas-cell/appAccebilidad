 package com.example.appaccesibilidad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appaccesibilidad.data.AppContainer
import com.example.appaccesibilidad.data.AuthService
import com.example.appaccesibilidad.data.UsuarioRepository
import com.example.appaccesibilidad.domain.Validaciones
import com.example.appaccesibilidad.domain.traducirError
import com.example.appaccesibilidad.model.Usuario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val cargando: Boolean = false,
    val error: String? = null,
    val info: String? = null
)

/** Lógica de Login, Registro, Recuperación y sesión activa. */
class AuthViewModel(
    private val auth: AuthService,
    private val usuarios: UsuarioRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    private val _sesion = MutableStateFlow<Usuario?>(null)
    val sesion: StateFlow<Usuario?> = _sesion.asStateFlow()

    fun limpiarMensajes() = _ui.update { it.copy(error = null, info = null) }

    /** Si Firebase conserva una sesión abierta, carga el perfil y avisa para navegar. */
    fun restaurarSesion(onListo: (Usuario) -> Unit) {
        val uid = auth.uidActual ?: return
        viewModelScope.launch {
            try {
                usuarios.obtener(uid)?.let { perfil ->
                    _sesion.value = perfil
                    onListo(perfil)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Sin red: se queda en Login y la persona puede reintentar.
            }
        }
    }

    fun iniciarSesion(email: String, password: String, onExito: (Usuario) -> Unit) {
        Validaciones.validarLogin(email, password)?.let { msg ->
            _ui.value = AuthUiState(error = msg)
            return
        }
        viewModelScope.launch {
            _ui.value = AuthUiState(cargando = true)
            try {
                val uid = auth.iniciarSesion(email.trim(), password)
                val perfil = usuarios.obtener(uid)
                if (perfil == null) {
                    auth.cerrarSesion()
                    _ui.value = AuthUiState(error = "Tu cuenta no tiene un perfil registrado.")
                } else {
                    _sesion.value = perfil
                    _ui.value = AuthUiState()
                    onExito(perfil)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _ui.value = AuthUiState(error = traducirError(e))
            }
        }
    }

    fun registrar(
        nombre: String,
        email: String,
        password: String,
        preferencia: String,
        tipoCuenta: String,
        aceptaTerminos: Boolean,
        onExito: () -> Unit
    ) {
        Validaciones.validarRegistro(nombre, email, password, aceptaTerminos)?.let { msg ->
            _ui.value = AuthUiState(error = msg)
            return
        }
        viewModelScope.launch {
            _ui.value = AuthUiState(cargando = true)
            try {
                val uid = auth.registrar(email.trim(), password)
                usuarios.crear(
                    Usuario(
                        uid = uid,
                        email = email.trim(),
                        nombre = nombre.trim(),
                        preferenciaAccesibilidad = preferencia,
                        tipoCuenta = tipoCuenta
                    )
                )
                auth.cerrarSesion() // se vuelve al Login para ingresar con la cuenta nueva
                _ui.value = AuthUiState(info = "Registro exitoso. Ya puedes iniciar sesión.")
                onExito()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _ui.value = AuthUiState(error = traducirError(e))
            }
        }
    }

    fun recuperarPassword(email: String) {
        if (!Validaciones.emailValido(email)) {
            _ui.value = AuthUiState(error = "Por favor, ingresa un correo válido.")
            return
        }
        viewModelScope.launch {
            _ui.value = AuthUiState(cargando = true)
            try {
                auth.enviarRecuperacion(email.trim())
                _ui.value = AuthUiState(info = "Se ha enviado un enlace de recuperación a ${email.trim()}")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _ui.value = AuthUiState(error = traducirError(e))
            }
        }
    }

    fun cerrarSesion() {
        auth.cerrarSesion()
        _sesion.value = null
        _ui.value = AuthUiState()
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { AuthViewModel(AppContainer.auth, AppContainer.usuarios) }
        }
    }
}
