package com.example.appaccesibilidad

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class Usuario(
    val email: String,
    val contrasena: String,
    val nombre: String = "",
    val preferenciaAccesibilidad: String = "Texto a Voz"
)

// Lista global observable con los 5 usuarios mínimos requeridos precargados
val listaUsuarios: SnapshotStateList<Usuario> = mutableStateListOf(
    Usuario("admin@duoc.cl", "123456", "Administrador", "Subtítulos Visuales"),
    Usuario("user1@duoc.cl", "123456", "Juan Pérez", "Texto a Voz"),
    Usuario("user2@duoc.cl", "123456", "Maria González", "Vibración / Alertas"),
    Usuario("user3@duoc.cl", "123456", "Carlos Tapia", "Subtítulos Visuales"),
    Usuario("user4@duoc.cl", "123456", "Ana Silva", "Texto a Voz")
)

// Función de extensión de Kotlin para validar formato de email
fun String.esEmailValido(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}