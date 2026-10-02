package com.example.appaccesibilidad

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appaccesibilidad.components.ColumnaTabla
import com.example.appaccesibilidad.components.Tabla
import com.example.appaccesibilidad.model.Dispositivo
import com.example.appaccesibilidad.model.Usuario
import com.example.appaccesibilidad.util.hacerSonar
import com.example.appaccesibilidad.viewmodel.DispositivosViewModel
import com.example.appaccesibilidad.viewmodel.filtrarDispositivos
import java.text.DateFormat
import java.util.Date

/**
 * Vista BuscarDispositivo (sin geolocalización): busca entre los dispositivos registrados,
 * permite registrar/renombrar/eliminar y hacer sonar y vibrar este teléfono para encontrarlo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBuscarDispositivo(navController: NavController, usuario: Usuario) {
    val context = LocalContext.current
    val viewModel: DispositivosViewModel = viewModel(
        key = "dispositivos-${usuario.uid}",
        factory = DispositivosViewModel.factory(usuario.uid)
    )
    val todos by viewModel.dispositivos.collectAsStateWithLifecycle()
    val aviso by viewModel.aviso.collectAsStateWithLifecycle()

    var consulta by remember { mutableStateOf("") }
    var nombreNuevo by remember { mutableStateOf("") }
    var enEdicion by remember { mutableStateOf<Dispositivo?>(null) }
    val visibles = remember(todos, consulta) { filtrarDispositivos(todos, consulta) }
    val formato = remember { DateFormat.getDateInstance(DateFormat.SHORT) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar dispositivo") },
                navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text("Volver") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Button(onClick = { context.hacerSonar() }, modifier = Modifier.fillMaxWidth()) {
                Text("Hacer sonar y vibrar este teléfono")
            }

            Spacer(Modifier.height(12.dp))
            Text("Registrar este dispositivo", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nombreNuevo,
                    onValueChange = { nombreNuevo = it },
                    label = { Text("Nombre (ej. Mi teléfono)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(onClick = {
                    if (viewModel.registrar(nombreNuevo, "${Build.MANUFACTURER} ${Build.MODEL}")) nombreNuevo = ""
                }) { Text("Registrar") }
            }

            aviso?.let { Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall) }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = consulta,
                onValueChange = { consulta = it },
                label = { Text("Buscar por nombre o modelo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            if (visibles.isEmpty()) {
                Text("No hay dispositivos que coincidan.", style = MaterialTheme.typography.bodyMedium)
            } else {
                Tabla(
                    columnas = listOf(
                        ColumnaTabla("Nombre", 2f),
                        ColumnaTabla("Modelo", 2.4f),
                        ColumnaTabla("Registro", 1.6f),
                        ColumnaTabla("Acciones", 2.2f)
                    ),
                    filas = visibles,
                    clave = { it.id },
                    modifier = Modifier.weight(1f)
                ) { d, columna ->
                    when (columna) {
                        0 -> Text(d.nombre, style = MaterialTheme.typography.bodyMedium)
                        1 -> Text(d.modelo, style = MaterialTheme.typography.bodySmall)
                        2 -> Text(formato.format(Date(d.fecha)), style = MaterialTheme.typography.bodySmall)
                        else -> Column {
                            TextButton(onClick = { enEdicion = d }) { Text("Renombrar") }
                            TextButton(onClick = { viewModel.eliminar(d) }) { Text("Eliminar") }
                        }
                    }
                }
            }
        }
    }

    enEdicion?.let { d ->
        var nombre by remember(d.id) { mutableStateOf(d.nombre) }
        AlertDialog(
            onDismissRequest = { enEdicion = null },
            title = { Text("Renombrar dispositivo") },
            text = { OutlinedTextField(value = nombre, onValueChange = { nombre = it }, modifier = Modifier.fillMaxWidth()) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.renombrar(d, nombre)
                    enEdicion = null
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { enEdicion = null }) { Text("Cancelar") } }
        )
    }
}
