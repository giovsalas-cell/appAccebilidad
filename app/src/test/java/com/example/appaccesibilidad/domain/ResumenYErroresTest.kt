package com.example.appaccesibilidad.domain

import com.example.appaccesibilidad.model.Usuario
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import org.junit.Assert.assertEquals
import org.junit.Test

class ResumenYErroresTest {

    @Test
    fun resumenPorPreferencia_cuentaUsuariosPorCategoria() {
        val usuarios = listOf(
            Usuario(uid = "1", preferenciaAccesibilidad = "Texto a Voz"),
            Usuario(uid = "2", preferenciaAccesibilidad = "Texto a Voz"),
            Usuario(uid = "3", preferenciaAccesibilidad = "Vibración / Alertas")
        )
        val resumen = resumenPorPreferencia(usuarios)
        assertEquals(2, resumen["Texto a Voz"])
        assertEquals(1, resumen["Vibración / Alertas"])
        assertEquals(null, resumen["Subtítulos Visuales"])
    }

    @Test
    fun resumenPorPreferencia_listaVacia() {
        assertEquals(emptyMap<String, Int>(), resumenPorPreferencia(emptyList()))
    }

    @Test
    fun traducirError_mapeaExcepcionesDeFirebase() {
        assertEquals("Usuario o contraseña incorrectos.",
            traducirError(FirebaseAuthInvalidCredentialsException("ERROR_INVALID_CREDENTIAL", "x")))
        assertEquals("No existe una cuenta con ese correo.",
            traducirError(FirebaseAuthInvalidUserException("ERROR_USER_NOT_FOUND", "x")))
        assertEquals("El correo ingresado ya se encuentra registrado.",
            traducirError(FirebaseAuthUserCollisionException("ERROR_EMAIL_ALREADY_IN_USE", "x")))
        assertEquals("Sin conexión a internet. Revisa tu red e inténtalo nuevamente.",
            traducirError(FirebaseNetworkException("sin red")))
    }

    @Test
    fun traducirError_errorDesconocido() {
        assertEquals("Ocurrió un error inesperado. Inténtalo nuevamente.", traducirError(IllegalStateException()))
    }
}
