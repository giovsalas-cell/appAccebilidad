package com.example.appaccesibilidad

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PantallaRecuperar(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var esError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Recuperar Contraseña", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo de recuperación") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val emailLimpio = email.trim()
                if (!emailLimpio.esEmailValido()) {
                    mensaje = "Por favor, ingresa un correo válido."
                    esError = true
                } else {
                    // Verificación contra la lista precargada
                    val existe = listaUsuarios.any { it.email.equals(emailLimpio, ignoreCase = true) }
                    if (existe) {
                        mensaje = "Se ha enviado un enlace de recuperación a $emailLimpio"
                        esError = false
                    } else {
                        mensaje = "El correo ingresado no corresponde a ningún usuario registrado."
                        esError = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar Correo")
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = mensaje,
                color = if (esError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Volver al Login")
        }
    }
}