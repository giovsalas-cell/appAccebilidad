package com.example.appaccesibilidad.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pruebas JUnit de las reglas de validación (escritas primero, ciclo TDD rojo-verde-refactor). */
class ValidacionesTest {

    @Test
    fun emailValido_aceptaFormatosCorrectos() {
        assertTrue(Validaciones.emailValido("user1@duoc.cl"))
        assertTrue(Validaciones.emailValido("  giovanni.salas@gmail.com  "))
    }

    @Test
    fun emailValido_rechazaFormatosIncorrectos() {
        assertFalse(Validaciones.emailValido(""))
        assertFalse(Validaciones.emailValido("sin-arroba.cl"))
        assertFalse(Validaciones.emailValido("a@b"))
        assertFalse(Validaciones.emailValido("@duoc.cl"))
    }

    @Test
    fun extensionEsEmailValido_usaLaMismaRegla() {
        assertTrue("admin@duoc.cl".esEmailValido())
        assertFalse("admin".esEmailValido())
    }

    @Test
    fun passwordValida_exigeMinimoSeisCaracteres() {
        assertFalse(Validaciones.passwordValida("12345"))
        assertTrue(Validaciones.passwordValida("123456"))
    }

    @Test
    fun validarLogin_camposVacios() {
        assertEquals("Ingresa tu correo y contraseña.", Validaciones.validarLogin("", ""))
        assertEquals("Ingresa tu correo y contraseña.", Validaciones.validarLogin("a@b.cl", " "))
    }

    @Test
    fun validarLogin_emailInvalido() {
        assertNotNull(Validaciones.validarLogin("correo", "123456"))
    }

    @Test
    fun validarLogin_datosCorrectos_devuelveNull() {
        assertNull(Validaciones.validarLogin("user1@duoc.cl", "123456"))
    }

    @Test
    fun validarRegistro_detectaCadaError() {
        assertEquals("Todos los campos son obligatorios.", Validaciones.validarRegistro("", "a@b.cl", "123456", true))
        assertEquals("El formato del correo electrónico es inválido.", Validaciones.validarRegistro("Ana", "ana", "123456", true))
        assertEquals("La contraseña debe tener al menos 6 caracteres.", Validaciones.validarRegistro("Ana", "ana@duoc.cl", "123", true))
        assertEquals("Debes aceptar los términos de accesibilidad.", Validaciones.validarRegistro("Ana", "ana@duoc.cl", "123456", false))
    }

    @Test
    fun validarRegistro_datosCorrectos_devuelveNull() {
        assertNull(Validaciones.validarRegistro("Ana Silva", "ana@duoc.cl", "123456", true))
    }

    @Test
    fun validarMensaje_vacioYDemasiadoLargo() {
        assertNotNull(Validaciones.validarMensaje("   "))
        assertNotNull(Validaciones.validarMensaje("x".repeat(Validaciones.LARGO_MAXIMO_MENSAJE + 1)))
        assertNull(Validaciones.validarMensaje("Hola"))
    }
}
