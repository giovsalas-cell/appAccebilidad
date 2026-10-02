package com.example.appaccesibilidad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appaccesibilidad.data.AppContainer
import com.example.appaccesibilidad.data.DispositivoRepository
import com.example.appaccesibilidad.model.Dispositivo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Filtra dispositivos por nombre o modelo (búsqueda sin distinguir mayúsculas). */
fun filtrarDispositivos(lista: List<Dispositivo>, consulta: String): List<Dispositivo> {
    val q = consulta.trim()
    if (q.isEmpty()) return lista
    return lista.filter { it.nombre.contains(q, ignoreCase = true) || it.modelo.contains(q, ignoreCase = true) }
}

/** Lógica de BuscarDispositivo: registrar, buscar, renombrar y eliminar dispositivos (CRUD en Firestore). */
class DispositivosViewModel(
    private val repo: DispositivoRepository,
    private val uid: String
) : ViewModel() {

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    val dispositivos: StateFlow<List<Dispositivo>> = repo.observar(uid)
        .catch { _aviso.value = "No se pudo cargar la lista de dispositivos." }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun registrar(nombre: String, modelo: String): Boolean {
        if (nombre.isBlank()) {
            _aviso.value = "Ingresa un nombre para el dispositivo."
            return false
        }
        ejecutar("Dispositivo registrado.") { repo.agregar(uid, nombre, modelo) }
        return true
    }

    fun renombrar(d: Dispositivo, nombre: String) {
        if (nombre.isBlank()) {
            _aviso.value = "Ingresa un nombre para el dispositivo."
            return
        }
        ejecutar("Dispositivo actualizado.") { repo.renombrar(uid, d.id, nombre) }
    }

    fun eliminar(d: Dispositivo) = ejecutar("Dispositivo eliminado.") { repo.eliminar(uid, d.id) }

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
            initializer { DispositivosViewModel(AppContainer.dispositivos, uid) }
        }
    }
}
