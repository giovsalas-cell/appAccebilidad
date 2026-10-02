package com.example.appaccesibilidad.domain

import com.example.appaccesibilidad.model.Usuario
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

/** Reglas de validación de formularios. No dependen de Android, por eso se prueban con JUnit puro. */
object Validaciones {
    const val LARGO_MINIMO_PASSWORD = 6
    const val LARGO_MAXIMO_MENSAJE = 500

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun emailValido(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

    fun passwordValida(password: String): Boolean = password.length >= LARGO_MINIMO_PASSWORD

    /** Devuelve el mensaje de error o null si los datos son válidos. */
    fun validarLogin(email: String, password: String): String? = when {
        email.isBlank() || password.isBlank() -> "Ingresa tu correo y contraseña."
        !emailValido(email) -> "El formato del correo electrónico es inválido."
        else -> null
    }

    fun validarRegistro(nombre: String, email: String, password: String, aceptaTerminos: Boolean): String? = when {
        nombre.isBlank() || email.isBlank() || password.isBlank() -> "Todos los campos son obligatorios."
        !emailValido(email) -> "El formato del correo electrónico es inválido."
        !passwordValida(password) -> "La contraseña debe tener al menos $LARGO_MINIMO_PASSWORD caracteres."
        !aceptaTerminos -> "Debes aceptar los términos de accesibilidad."
        else -> null
    }

    fun validarMensaje(texto: String): String? = when {
        texto.isBlank() -> "El mensaje no puede estar vacío."
        texto.length > LARGO_MAXIMO_MENSAJE -> "El mensaje supera los $LARGO_MAXIMO_MENSAJE caracteres."
        else -> null
    }
}

/** Extensión Kotlin usada por las pantallas para validar el correo. */
fun String.esEmailValido(): Boolean = Validaciones.emailValido(this)

/** Cuenta cuántos usuarios hay por cada preferencia de accesibilidad (resumen del panel admin). */
fun resumenPorPreferencia(usuarios: List<Usuario>): Map<String, Int> =
    usuarios.groupingBy { it.preferenciaAccesibilidad }.eachCount()

/** Traduce excepciones de Firebase a mensajes comprensibles para la persona usuaria. */
fun traducirError(e: Throwable): String = when (e) {
    is FirebaseAuthInvalidUserException -> "No existe una cuenta con ese correo."
    is FirebaseAuthInvalidCredentialsException -> "Usuario o contraseña incorrectos."
    is FirebaseAuthUserCollisionException -> "El correo ingresado ya se encuentra registrado."
    is FirebaseNetworkException -> "Sin conexión a internet. Revisa tu red e inténtalo nuevamente."
    else -> "Ocurrió un error inesperado. Inténtalo nuevamente."
}
