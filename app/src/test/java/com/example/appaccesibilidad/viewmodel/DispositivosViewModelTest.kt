package com.example.appaccesibilidad.viewmodel

import com.example.appaccesibilidad.data.DispositivoRepository
import com.example.appaccesibilidad.model.Dispositivo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class DispositivosViewModelTest {

    @Before
    fun preparar() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun limpiar() = Dispatchers.resetMain()

    private val lista = listOf(
        Dispositivo("1", "Mi teléfono", "Samsung Galaxy A54", 1L),
        Dispositivo("2", "Tablet casa", "Lenovo Tab M10", 2L)
    )

    @Test
    fun filtrar_porNombreOModeloSinMayusculas() {
        assertEquals(2, filtrarDispositivos(lista, "  ").size)
        assertEquals(listOf("1"), filtrarDispositivos(lista, "TELÉFONO").map { it.id })
        assertEquals(listOf("2"), filtrarDispositivos(lista, "lenovo").map { it.id })
        assertTrue(filtrarDispositivos(lista, "xyz").isEmpty())
    }

    @Test
    fun registrar_nombreVacio_noLlamaAlRepositorio() = runTest {
        val repo: DispositivoRepository = mock { on { observar("u1") } doReturn emptyFlow() }
        val vm = DispositivosViewModel(repo, "u1")
        assertFalse(vm.registrar(" ", "Modelo"))
        verify(repo, never()).agregar(any(), any(), any())
    }

    @Test
    fun registrarRenombrarYEliminar_llamanAlRepositorio() = runTest {
        val repo: DispositivoRepository = mock { on { observar("u1") } doReturn emptyFlow() }
        val vm = DispositivosViewModel(repo, "u1")
        assertTrue(vm.registrar("Mi teléfono", "Samsung"))
        vm.renombrar(lista[0], "Teléfono principal")
        vm.eliminar(lista[0])
        verify(repo).agregar("u1", "Mi teléfono", "Samsung")
        verify(repo).renombrar("u1", "1", "Teléfono principal")
        verify(repo).eliminar("u1", "1")
    }
}
