package com.example.appaccesibilidad.model

const val ROL_ADMIN = "admin"
const val ROL_USUARIO = "usuario"

val PREFERENCIAS_ACCESIBILIDAD = listOf("Texto a Voz", "Vibración / Alertas", "Subtítulos Visuales")

/** Perfil del usuario. La contraseña NO se guarda aquí: la administra Firebase Authentication. */
data class Usuario(
    val uid: String = "",
    val email: String = "",
    val nombre: String = "",
    val preferenciaAccesibilidad: String = PREFERENCIAS_ACCESIBILIDAD.first(),
    val tipoCuenta: String = "Personal",
    val rol: String = ROL_USUARIO
) {
    val esAdmin: Boolean get() = rol == ROL_ADMIN
}

/** Mensaje escrito (vista Escribir) o transcripción de voz (vista Hablar). */
data class Mensaje(
    val id: String = "",
    val texto: String = "",
    val tipo: String = TIPO_ESCRITO,
    val fecha: Long = 0L
) {
    companion object {
        const val TIPO_ESCRITO = "escrito"
        const val TIPO_HABLADO = "hablado"
    }
}

/** Dispositivo registrado por la persona (vista BuscarDispositivo). */
data class Dispositivo(
    val id: String = "",
    val nombre: String = "",
    val modelo: String = "",
    val fecha: Long = 0L
)

// ---------- Conversión hacia/desde Firestore (funciones puras, fáciles de testear) ----------

fun Usuario.aMapa(): Map<String, Any> = mapOf(
    "email" to email,
    "nombre" to nombre,
    "preferenciaAccesibilidad" to preferenciaAccesibilidad,
    "tipoCuenta" to tipoCuenta,
    "rol" to rol
)

fun Map<String, Any?>.aUsuario(uid: String): Usuario = Usuario(
    uid = uid,
    email = this["email"] as? String ?: "",
    nombre = this["nombre"] as? String ?: "",
    preferenciaAccesibilidad = this["preferenciaAccesibilidad"] as? String
        ?: PREFERENCIAS_ACCESIBILIDAD.first(),
    tipoCuenta = this["tipoCuenta"] as? String ?: "Personal",
    rol = this["rol"] as? String ?: ROL_USUARIO
)

fun Mensaje.aMapa(): Map<String, Any> = mapOf("texto" to texto, "tipo" to tipo, "fecha" to fecha)

fun Map<String, Any?>.aMensaje(id: String): Mensaje = Mensaje(
    id = id,
    texto = this["texto"] as? String ?: "",
    tipo = this["tipo"] as? String ?: Mensaje.TIPO_ESCRITO,
    fecha = (this["fecha"] as? Number)?.toLong() ?: 0L
)

fun Dispositivo.aMapa(): Map<String, Any> = mapOf("nombre" to nombre, "modelo" to modelo, "fecha" to fecha)

fun Map<String, Any?>.aDispositivo(id: String): Dispositivo = Dispositivo(
    id = id,
    nombre = this["nombre"] as? String ?: "",
    modelo = this["modelo"] as? String ?: "",
    fecha = (this["fecha"] as? Number)?.toLong() ?: 0L
)
