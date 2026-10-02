package com.example.appaccesibilidad.data

import com.example.appaccesibilidad.model.Mensaje
import com.example.appaccesibilidad.model.Dispositivo
import com.example.appaccesibilidad.model.Usuario
import kotlinx.coroutines.flow.Flow

/** Autenticación de usuarios (Firebase Authentication). */
interface AuthService {
    val uidActual: String?
    suspend fun iniciarSesion(email: String, password: String): String
    suspend fun registrar(email: String, password: String): String
    suspend fun enviarRecuperacion(email: String)
    fun cerrarSesion()
}

/** CRUD de perfiles de usuario (Cloud Firestore, colección "usuarios"). */
interface UsuarioRepository {
    suspend fun crear(usuario: Usuario)
    suspend fun obtener(uid: String): Usuario?
    fun observarTodos(): Flow<List<Usuario>>
    suspend fun actualizar(usuario: Usuario)
    suspend fun eliminar(uid: String)
}

/** CRUD de mensajes de un usuario (vistas Escribir y Hablar). */
interface MensajeRepository {
    fun observar(uid: String): Flow<List<Mensaje>>
    suspend fun agregar(uid: String, texto: String, tipo: String)
    suspend fun actualizar(uid: String, id: String, texto: String)
    suspend fun eliminar(uid: String, id: String)
}

/** CRUD de dispositivos registrados (vista BuscarDispositivo). */
interface DispositivoRepository {
    fun observar(uid: String): Flow<List<Dispositivo>>
    suspend fun agregar(uid: String, nombre: String, modelo: String)
    suspend fun renombrar(uid: String, id: String, nombre: String)
    suspend fun eliminar(uid: String, id: String)
}
