package com.example.appaccesibilidad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appaccesibilidad.ui.theme.AppAccesibilidadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppAccesibilidadTheme {
                NavegacionApp()
            }
        }
    }
}

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { PantallaLogin(navController) }
        composable("registro") { PantallaRegistro(navController) }
        composable("recuperar") { PantallaRecuperar(navController) }
        composable("admin") { PantallaAdmin(navController) }

        // Pasamos el email del usuario autenticado a su panel
        composable(
            route = "usuario/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            PantallaUsuario(navController, email)
        }
    }
}