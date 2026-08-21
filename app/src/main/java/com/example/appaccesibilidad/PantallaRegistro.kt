package com.example.appaccesibilidad

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PantallaRegistro(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }

    var expandirMenu by remember { mutableStateOf(false) }
    var preferenciaSeleccionada by remember { mutableStateOf("Texto a Voz") }
    val opciones = listOf("Texto a Voz", "Vibración / Alertas", "Subtítulos Visuales")

    var aceptaTerminos by remember { mutableStateOf(false) }
    var tipoPerfil by remember { mutableStateOf("Usuario") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Registro de Usuario", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Combo Box (DropdownMenu)
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { expandirMenu = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Preferencia UI: $preferenciaSeleccionada")
            }
            DropdownMenu(expanded = expandirMenu, onDismissRequest = { expandirMenu = false }) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            preferenciaSeleccionada = opcion
                            expandirMenu = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Radio Buttons
        Text("Tipo de Cuenta:")
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = (tipoPerfil == "Usuario"), onClick = { tipoPerfil = "Usuario" })
            Text("Personal")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = (tipoPerfil == "Acompañante"), onClick = { tipoPerfil = "Acompañante" })
            Text("Acompañante")
        }

        // Checkbox
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = aceptaTerminos, onCheckedChange = { aceptaTerminos = it })
            Text("Acepto los términos de accesibilidad")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (aceptaTerminos && email.isNotEmpty() && password.isNotEmpty()) {
                    listaUsuarios.add(Usuario(email, password, nombre, preferenciaSeleccionada))
                    navController.popBackStack()
                }
            },
            enabled = aceptaTerminos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Registro")
        }
        TextButton(onClick = { navController.navigate("login") }) {
            Text("Volver")
        }
    }

}