package com.example.appaccesibilidad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaUsuario(navController: NavController, emailUsuario: String) {
    val usuario = listaUsuarios.find { it.email == emailUsuario }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Panel de Usuario") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (usuario != null) {
                Text(
                    text = "¡Bienvenido/a, ${usuario.nombre}!",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Correo: ${usuario.email}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ajuste de Accesibilidad Activo:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = usuario.preferenciaAccesibilidad,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Muestra un componente adaptado en función de la preferencia
                Text(text = "Demostración de interfaz según preferencia:", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))

                when (usuario.preferenciaAccesibilidad) {
                    "Subtítulos Visuales" -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.inverseSurface)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = " [SUBTÍTULO ACTIVADO]: Notificación visual de alto contraste.",
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                fontSize = 16.sp
                            )
                        }
                    }
                    "Texto a Voz" -> {
                        Button(onClick = { /* Lógica simulada de lector de pantalla */ }) {
                            Text("🔊 Leer contenido en voz alta")
                        }
                    }
                    "Vibración / Alertas" -> {
                        OutlinedButton(onClick = { /* Lógica simulada de patrón de vibración */ }) {
                            Text(" Probar alerta háptica (Vibración)")
                        }
                    }
                }

            } else {
                Text(text = "Usuario no encontrado", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}