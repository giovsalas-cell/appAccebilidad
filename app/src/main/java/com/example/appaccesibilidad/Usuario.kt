package com.example.appaccesibilidad

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

// Modelo de datos para los usuarios
data class Usuario(
    val email: String,
    val contrasena: String,
    val nombre: String = "",
    val preferenciaAccesibilidad: String = "Texto a voz"
)

// Lista global que almacena inicialmente los usuarios de prueba requeridos
// Código corregido con los 5 usuarios exigidos
val listaUsuarios: SnapshotStateList<Usuario> = mutableStateListOf(
    Usuario("admin@duoc.cl", "123456", "Administrador"),
    Usuario("user1@duoc.cl", "123456", "Juan Pérez"),
    Usuario("user2@duoc.cl", "123456", "Maria González"),
    Usuario("user3@duoc.cl", "123456", "Carlos Tapia"),
    Usuario("user4@duoc.cl", "123456", "Ana Silva")
)