package com.example.appaccesibilidad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appaccesibilidad.components.GrillaMenu
import com.example.appaccesibilidad.components.OpcionMenu
import com.example.appaccesibilidad.model.Usuario
import com.example.appaccesibilidad.util.TextoAVoz
import com.example.appaccesibilidad.util.vibrar

/** HomeMenú: saludo, ajuste de accesibilidad activo y grilla con las funciones de la app. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHome(navController: NavController, usuario: Usuario, onCerrarSesion: () -> Unit) {
    val context = LocalContext.current
    val voz = remember { TextoAVoz(context) }
    DisposableEffect(Unit) { onDispose { voz.liberar() } }

    val opciones = remember {
        listOf(
            OpcionMenu("Escribir", "Escribe y reproduce mensajes en voz alta", Icons.Default.Create, "escribir"),
            OpcionMenu("Hablar", "Convierte tu voz en texto", Icons.Default.Call, "hablar"),
            OpcionMenu("Buscar dispositivo", "Registra, busca y haz sonar tus dispositivos", Icons.Default.LocationOn, "buscar"),
            OpcionMenu("Ayuda", "Tutorial paso a paso de la app", Icons.Default.Info, "ayuda")
        )
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Menú Principal") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("¡Bienvenido/a, ${usuario.nombre}!", style = MaterialTheme.typography.headlineSmall)
            Text(usuario.email, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("Ajuste de accesibilidad activo:", style = MaterialTheme.typography.titleSmall)
                    Text(
                        usuario.preferenciaAccesibilidad,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    when (usuario.preferenciaAccesibilidad) {
                        "Subtítulos Visuales" -> Box(
                            Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.inverseSurface)
                                .padding(12.dp)
                        ) {
                            Text(
                                "[SUBTÍTULO] Las notificaciones se muestran en alto contraste.",
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                fontSize = 16.sp
                            )
                        }
                        "Vibración / Alertas" -> OutlinedButton(onClick = { context.vibrar() }) {
                            Text("Probar alerta háptica")
                        }
                        else -> OutlinedButton(onClick = { voz.hablar("Hola ${usuario.nombre}, la lectura en voz alta está activa.") }) {
                            Text("Probar lectura en voz alta")
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("¿Qué quieres hacer?", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            GrillaMenu(
                opciones = opciones,
                onOpcion = { navController.navigate(it.ruta) },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.height(12.dp))
            Button(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) { Text("Cerrar Sesión") }
        }
    }
}
