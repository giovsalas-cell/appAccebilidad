package com.example.appaccesibilidad.data

/** Contenedor simple de dependencias (evita instanciar Firebase en cada pantalla). */
object AppContainer {
    val auth: AuthService by lazy { FirebaseAuthService() }
    val usuarios: UsuarioRepository by lazy { FirestoreUsuarioRepository() }
    val mensajes: MensajeRepository by lazy { FirestoreMensajeRepository() }
    val dispositivos: DispositivoRepository by lazy { FirestoreDispositivoRepository() }
}
