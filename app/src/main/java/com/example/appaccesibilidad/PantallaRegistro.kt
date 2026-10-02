package com.example.appaccesibilidad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.appaccesibilidad.model.PREFERENCIAS_ACCESIBILIDAD
import com.example.appaccesibilidad.viewmodel.AuthViewModel

@Composable
fun PantallaRegistro(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var expandirMenu by remember { mutableStateOf(false) }
    var preferenciaSeleccionada by remember { mutableStateOf(PREFERENCIAS_ACCESIBILIDAD.first()) }
    var aceptaTerminos by remember { mutableStateOf(false) }
    var tipoPerfil by remember { mutableStateOf("Personal") }
    val ui by authViewModel.ui.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
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
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña (mínimo 6 caracteres)") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Selector tipo Combo Box
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { expandirMenu = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Preferencia UI: $preferenciaSeleccionada")
            }
            DropdownMenu(expanded = expandirMenu, onDismissRequest = { expandirMenu = false }) {
                PREFERENCIAS_ACCESIBILIDAD.forEach { opcion ->
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
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            RadioButton(selected = (tipoPerfil == "Personal"), onClick = { tipoPerfil = "Personal" })
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

        ui.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                authViewModel.registrar(
                    nombre, email, password, preferenciaSeleccionada, tipoPerfil, aceptaTerminos
                ) { navController.popBackStack() }
            },
            enabled = aceptaTerminos && !ui.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Registro")
        }

        TextButton(onClick = { authViewModel.limpiarMensajes(); navController.popBackStack() }) {
            Text("Volver")
        }
    }
}
