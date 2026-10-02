package com.example.appaccesibilidad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appaccesibilidad.data.AppContainer
import com.example.appaccesibilidad.data.MensajeRepository
import com.example.appaccesibilidad.domain.Validaciones
import com.example.appaccesibilidad.model.Mensaje
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** CRUD de mensajes para las vistas Escribir, Hablar y el fragment de historial. */
class MensajesViewModel(
    private val repo: MensajeRepository,
    private val uid: String
) : ViewModel() {

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    val mensajes: StateFlow<List<Mensaje>> = repo.observar(uid)
        .catch { _aviso.value = "No se pudo cargar el historial." }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** @return true si el mensaje fue válido y se envió a guardar. */
    fun guardar(texto: String, tipo: String): Boolean {
        Validaciones.validarMensaje(texto)?.let {
            _aviso.value = it
            return false
        }
        ejecutar("Mensaje guardado.") { repo.agregar(uid, texto, tipo) }
        return true
    }

    fun editar(mensaje: Mensaje, nuevoTexto: String) {
        Validaciones.validarMensaje(nuevoTexto)?.let {
            _aviso.value = it
            return
        }
        ejecutar("Mensaje actualizado.") { repo.actualizar(uid, mensaje.id, nuevoTexto) }
    }

    fun eliminar(mensaje: Mensaje) = ejecutar("Mensaje eliminado.") { repo.eliminar(uid, mensaje.id) }

    fun limpiarAviso() {
        _aviso.value = null
    }

    private fun ejecutar(exito: String, bloque: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                bloque()
                _aviso.value = exito
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _aviso.value = "No se pudo completar la operación."
            }
        }
    }

    companion object {
        fun factory(uid: String): ViewModelProvider.Factory = viewModelFactory {
            initializer { MensajesViewModel(AppContainer.mensajes, uid) }
        }
    }
}
