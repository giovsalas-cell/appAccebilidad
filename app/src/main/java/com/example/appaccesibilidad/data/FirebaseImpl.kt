package com.example.appaccesibilidad.data

import com.example.appaccesibilidad.model.Mensaje
import com.example.appaccesibilidad.model.Dispositivo
import com.example.appaccesibilidad.model.Usuario
import com.example.appaccesibilidad.model.aMapa
import com.example.appaccesibilidad.model.aMensaje
import com.example.appaccesibilidad.model.aDispositivo
import com.example.appaccesibilidad.model.aUsuario
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val COL_USUARIOS = "usuarios"
private const val SUB_MENSAJES = "mensajes"
private const val SUB_DISPOSITIVOS = "dispositivos"

class FirebaseAuthService(
    private val auth: FirebaseAuth = Firebase.auth
) : AuthService {

    override val uidActual: String? get() = auth.currentUser?.uid

    override suspend fun iniciarSesion(email: String, password: String): String =
        auth.signInWithEmailAndPassword(email, password).await().user?.uid
            ?: error("No se pudo obtener el usuario autenticado")

    override suspend fun registrar(email: String, password: String): String =
        auth.createUserWithEmailAndPassword(email, password).await().user?.uid
            ?: error("No se pudo crear el usuario")

    override suspend fun enviarRecuperacion(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override fun cerrarSesion() = auth.signOut()
}

class FirestoreUsuarioRepository(
    private val db: FirebaseFirestore = Firebase.firestore
) : UsuarioRepository {

    private val coleccion get() = db.collection(COL_USUARIOS)

    override suspend fun crear(usuario: Usuario) {
        coleccion.document(usuario.uid).set(usuario.aMapa()).await()
    }

    override suspend fun obtener(uid: String): Usuario? {
        val doc = coleccion.document(uid).get().await()
        return doc.data?.aUsuario(doc.id)
    }

    override fun observarTodos(): Flow<List<Usuario>> = callbackFlow {
        val registro = coleccion.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val lista = snapshot?.documents.orEmpty()
                .mapNotNull { d -> d.data?.aUsuario(d.id) }
                .sortedBy { it.nombre.lowercase() }
            trySend(lista)
        }
        awaitClose { registro.remove() }
    }

    override suspend fun actualizar(usuario: Usuario) {
        coleccion.document(usuario.uid).update(usuario.aMapa()).await()
    }

    override suspend fun eliminar(uid: String) {
        coleccion.document(uid).delete().await()
    }
}

class FirestoreMensajeRepository(
    private val db: FirebaseFirestore = Firebase.firestore
) : MensajeRepository {

    private fun coleccion(uid: String) =
        db.collection(COL_USUARIOS).document(uid).collection(SUB_MENSAJES)

    override fun observar(uid: String): Flow<List<Mensaje>> = callbackFlow {
        val registro = coleccion(uid)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents.orEmpty().mapNotNull { d -> d.data?.aMensaje(d.id) })
            }
        awaitClose { registro.remove() }
    }

    override suspend fun agregar(uid: String, texto: String, tipo: String) {
        val mensaje = Mensaje(texto = texto.trim(), tipo = tipo, fecha = System.currentTimeMillis())
        coleccion(uid).add(mensaje.aMapa()).await()
    }

    override suspend fun actualizar(uid: String, id: String, texto: String) {
        coleccion(uid).document(id).update("texto", texto.trim()).await()
    }

    override suspend fun eliminar(uid: String, id: String) {
        coleccion(uid).document(id).delete().await()
    }
}

class FirestoreDispositivoRepository(
    private val db: FirebaseFirestore = Firebase.firestore
) : DispositivoRepository {

    private fun coleccion(uid: String) =
        db.collection(COL_USUARIOS).document(uid).collection(SUB_DISPOSITIVOS)

    override fun observar(uid: String): Flow<List<Dispositivo>> = callbackFlow {
        val registro = coleccion(uid)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents.orEmpty().mapNotNull { d -> d.data?.aDispositivo(d.id) })
            }
        awaitClose { registro.remove() }
    }

    override suspend fun agregar(uid: String, nombre: String, modelo: String) {
        val d = Dispositivo(nombre = nombre.trim(), modelo = modelo, fecha = System.currentTimeMillis())
        coleccion(uid).add(d.aMapa()).await()
    }

    override suspend fun renombrar(uid: String, id: String, nombre: String) {
        coleccion(uid).document(id).update("nombre", nombre.trim()).await()
    }

    override suspend fun eliminar(uid: String, id: String) {
        coleccion(uid).document(id).delete().await()
    }
}
