package com.example.appaccesibilidad.viewmodel

import com.example.appaccesibilidad.data.AuthService
import com.example.appaccesibilidad.data.UsuarioRepository
import com.example.appaccesibilidad.model.ROL_ADMIN
import com.example.appaccesibilidad.model.Usuario
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify

/** Pruebas con Mockito: AuthService y UsuarioRepository se reemplazan por dobles (mocks). */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var auth: AuthService
    private lateinit var usuarios: UsuarioRepository
    private lateinit var vm: AuthViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        auth = mock()
        usuarios = mock()
        vm = AuthViewModel(auth, usuarios)
    }

    @After
    fun limpiar() = Dispatchers.resetMain()

    @Test
    fun iniciarSesion_camposVacios_noLlamaAFirebase() = runTest {
        var navego = false
        vm.iniciarSesion("", "") { navego = true }
        assertEquals("Ingresa tu correo y contraseña.", vm.ui.value.error)
        assertTrue(!navego)
        verify(auth, never()).iniciarSesion(any(), any())
    }

    @Test
    fun iniciarSesion_exitoso_guardaSesionYNavega() = runTest {
        val perfil = Usuario("u1", "user1@duoc.cl", "Juan")
        auth.stub { onBlocking { iniciarSesion("user1@duoc.cl", "123456") } doReturn "u1" }
        usuarios.stub { onBlocking { obtener("u1") } doReturn perfil }

        var recibido: Usuario? = null
        vm.iniciarSesion(" user1@duoc.cl ", "123456") { recibido = it }

        assertEquals(perfil, recibido)
        assertEquals(perfil, vm.sesion.value)
        assertNull(vm.ui.value.error)
    }

    @Test
    fun iniciarSesion_adminDetectadoPorRol() = runTest {
        val admin = Usuario("a1", "admin@duoc.cl", "Admin", rol = ROL_ADMIN)
        auth.stub { onBlocking { iniciarSesion(any(), any()) } doReturn "a1" }
        usuarios.stub { onBlocking { obtener("a1") } doReturn admin }

        var esAdmin = false
        vm.iniciarSesion("admin@duoc.cl", "123456") { esAdmin = it.esAdmin }
        assertTrue(esAdmin)
    }

    @Test
    fun iniciarSesion_credencialesInvalidas_muestraError() = runTest {
        auth.stub {
            onBlocking { iniciarSesion(any(), any()) } doAnswer { throw FirebaseAuthInvalidCredentialsException("ERROR", "x") }
        }
        vm.iniciarSesion("user1@duoc.cl", "mala-clave") { }
        assertEquals("Usuario o contraseña incorrectos.", vm.ui.value.error)
        assertNull(vm.sesion.value)
    }

    @Test
    fun iniciarSesion_sinPerfil_cierraSesion() = runTest {
        auth.stub { onBlocking { iniciarSesion(any(), any()) } doReturn "u9" }
        usuarios.stub { onBlocking { obtener("u9") } doReturn null }
        vm.iniciarSesion("x@duoc.cl", "123456") { }
        verify(auth).cerrarSesion()
        assertNotNull(vm.ui.value.error)
    }

    @Test
    fun registrar_exitoso_creaPerfilConRolUsuario() = runTest {
        auth.stub { onBlocking { registrar("nuevo@duoc.cl", "123456") } doReturn "n1" }
        var ok = false
        vm.registrar("Nuevo", "nuevo@duoc.cl", "123456", "Texto a Voz", "Personal", true) { ok = true }

        assertTrue(ok)
        verify(usuarios).crear(argThat { uid == "n1" && rol == "usuario" && nombre == "Nuevo" })
        verify(auth).cerrarSesion()
    }

    @Test
    fun registrar_sinAceptarTerminos_noRegistra() = runTest {
        vm.registrar("Nuevo", "nuevo@duoc.cl", "123456", "Texto a Voz", "Personal", false) { }
        assertEquals("Debes aceptar los términos de accesibilidad.", vm.ui.value.error)
        verify(auth, never()).registrar(any(), any())
    }

    @Test
    fun recuperarPassword_correoInvalido_muestraError() = runTest {
        vm.recuperarPassword("correo-malo")
        assertEquals("Por favor, ingresa un correo válido.", vm.ui.value.error)
        verify(auth, never()).enviarRecuperacion(any())
    }

    @Test
    fun recuperarPassword_exitoso_informa() = runTest {
        vm.recuperarPassword("user1@duoc.cl")
        verify(auth).enviarRecuperacion("user1@duoc.cl")
        assertTrue(vm.ui.value.info!!.contains("user1@duoc.cl"))
    }

    @Test
    fun cerrarSesion_limpiaLaSesion() = runTest {
        val perfil = Usuario("u1", "user1@duoc.cl", "Juan")
        auth.stub { onBlocking { iniciarSesion(any(), any()) } doReturn "u1" }
        usuarios.stub { onBlocking { obtener("u1") } doReturn perfil }
        vm.iniciarSesion("user1@duoc.cl", "123456") { }
        vm.cerrarSesion()
        assertNull(vm.sesion.value)
        verify(auth).cerrarSesion()
    }
}
