package com.example.appaccesibilidad.viewmodel

import com.example.appaccesibilidad.data.MensajeRepository
import com.example.appaccesibilidad.data.UsuarioRepository
import com.example.appaccesibilidad.model.Mensaje
import com.example.appaccesibilidad.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class AdminYMensajesViewModelTest {

    @Before
    fun preparar() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun limpiar() = Dispatchers.resetMain()

    @Test
    fun admin_eliminar_llamaAlRepositorioEInforma() = runTest {
        val repo: UsuarioRepository = mock { on { observarTodos() } doReturn emptyFlow() }
        val vm = AdminViewModel(repo)
        val u = Usuario("u1", "user1@duoc.cl", "Juan")

        vm.eliminar(u)

        verify(repo).eliminar("u1")
        assertEquals("Usuario user1@duoc.cl eliminado.", vm.mensaje.value)
    }

    @Test
    fun admin_actualizar_error_informaFalla() = runTest {
        val repo: UsuarioRepository = mock { on { observarTodos() } doReturn emptyFlow() }
        repo.stub { onBlocking { actualizar(any()) } doThrow IllegalStateException("sin permisos") }
        val vm = AdminViewModel(repo)

        vm.actualizar(Usuario("u1", "user1@duoc.cl", "Juan"))

        assertEquals("No se pudo completar la operación.", vm.mensaje.value)
    }

    @Test
    fun mensajes_guardarVacio_noLlamaAlRepositorio() = runTest {
        val repo: MensajeRepository = mock { on { observar("u1") } doReturn emptyFlow() }
        val vm = MensajesViewModel(repo, "u1")

        assertFalse(vm.guardar("   ", Mensaje.TIPO_ESCRITO))

        verify(repo, never()).agregar(any(), any(), any())
        assertEquals("El mensaje no puede estar vacío.", vm.aviso.value)
    }

    @Test
    fun mensajes_guardarValido_llamaAlRepositorio() = runTest {
        val repo: MensajeRepository = mock { on { observar("u1") } doReturn MutableStateFlow(emptyList()) }
        val vm = MensajesViewModel(repo, "u1")

        assertTrue(vm.guardar("Hola", Mensaje.TIPO_HABLADO))

        verify(repo).agregar("u1", "Hola", Mensaje.TIPO_HABLADO)
        assertEquals("Mensaje guardado.", vm.aviso.value)
    }

    @Test
    fun mensajes_editarYEliminar() = runTest {
        val repo: MensajeRepository = mock { on { observar("u1") } doReturn emptyFlow() }
        val vm = MensajesViewModel(repo, "u1")
        val m = Mensaje("m1", "Hola", Mensaje.TIPO_ESCRITO, 1L)

        vm.editar(m, "Hola de nuevo")
        vm.eliminar(m)

        verify(repo).actualizar("u1", "m1", "Hola de nuevo")
        verify(repo).eliminar("u1", "m1")
    }
}
