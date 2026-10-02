package com.example.appaccesibilidad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appaccesibilidad.data.AppContainer
import com.example.appaccesibilidad.data.UsuarioRepository
import com.example.appaccesibilidad.model.Usuario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Gestión de usuarios (CRUD) para el Panel Administrador. */
class AdminViewModel(private val repo: UsuarioRepository) : ViewModel() {

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    val usuarios: StateFlow<List<Usuario>> = repo.observarTodos()
        .catch { _mensaje.value = "No se pudo cargar la lista de usuarios." }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun eliminar(usuario: Usuario) = ejecutar("Usuario ${usuario.email} eliminado.") {
        repo.eliminar(usuario.uid)
    }

    fun actualizar(usuario: Usuario) = ejecutar("Usuario ${usuario.email} actualizado.") {
        repo.actualizar(usuario)
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    private fun ejecutar(exito: String, bloque: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                bloque()
                _mensaje.value = exito
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value = "No se pudo completar la operación."
            }
        }
    }

    companion object {
        val Factory = viewModelFactory { initializer { AdminViewModel(AppContainer.usuarios) } }
    }
}
