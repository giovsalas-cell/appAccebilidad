package com.example.appaccesibilidad

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import com.example.appaccesibilidad.model.Usuario
import com.example.appaccesibilidad.ui.theme.AppAccesibilidadTheme
import com.example.appaccesibilidad.viewmodel.AuthViewModel

/** FragmentActivity (extiende ComponentActivity) para poder incrustar Fragments dentro de Compose. */
class MainActivity : FragmentActivity() {

    private val authViewModel: AuthViewModel by viewModels { AuthViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val destino = intent?.getStringExtra(EXTRA_DESTINO) // llega desde el widget
        setContent {
            AppAccesibilidadTheme {
                NavegacionApp(authViewModel, destino)
            }
        }
    }

    companion object {
        const val EXTRA_DESTINO = "destino"
    }
}

private val DESTINOS_WIDGET = setOf("escribir", "hablar", "buscar")

@Composable
fun NavegacionApp(authViewModel: AuthViewModel, destinoInicial: String? = null) {
    val navController = rememberNavController()

    // Si Firebase conserva la sesión, se entra directo al menú (persistencia de sesión)
    LaunchedEffect(Unit) {
        val destinoWidget = destinoInicial?.takeIf { it in DESTINOS_WIDGET }
        authViewModel.restaurarSesion { usuario ->
            val ruta = if (usuario.esAdmin) "admin" else (destinoWidget ?: "home")
            navController.navigate(ruta) { popUpTo("login") { inclusive = true } }
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { PantallaLogin(navController, authViewModel) }
        composable("registro") { PantallaRegistro(navController, authViewModel) }
        composable("recuperar") { PantallaRecuperar(navController, authViewModel) }

        composable("home") {
            ConSesion(authViewModel, navController) { usuario ->
                PantallaHome(navController, usuario) { cerrarSesion(authViewModel, navController) }
            }
        }
        composable("escribir") { ConSesion(authViewModel, navController) { PantallaEscribir(navController, it) } }
        composable("hablar") { ConSesion(authViewModel, navController) { PantallaHablar(navController, it) } }
        composable("buscar") { ConSesion(authViewModel, navController) { PantallaBuscarDispositivo(navController, it) } }
        composable("ayuda") { ConSesion(authViewModel, navController) { PantallaAyuda(navController) } }
        composable("admin") {
            ConSesion(authViewModel, navController) { usuario ->
                PantallaAdmin(usuario) { cerrarSesion(authViewModel, navController) }
            }
        }
    }
}

private fun cerrarSesion(authViewModel: AuthViewModel, navController: NavController) {
    authViewModel.cerrarSesion()
    navController.navigate("login") { popUpTo(0) { inclusive = true } }
}

/** Protege las pantallas: si no hay sesión activa, vuelve al Login. */
@Composable
private fun ConSesion(
    authViewModel: AuthViewModel,
    navController: NavController,
    contenido: @Composable (Usuario) -> Unit
) {
    val usuario by authViewModel.sesion.collectAsStateWithLifecycle()
    val actual = usuario
    if (actual == null) {
        LaunchedEffect(Unit) {
            navController.navigate("login") { popUpTo(0) { inclusive = true } }
        }
    } else {
        contenido(actual)
    }
}
