package com.example.appaccesibilidad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.compose.AndroidFragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appaccesibilidad.fragment.HistorialFragment
import com.example.appaccesibilidad.model.Mensaje
import com.example.appaccesibilidad.model.Usuario
import com.example.appaccesibilidad.provider.FrasesRepositorio
import com.example.appaccesibilidad.util.TextoAVoz
import com.example.appaccesibilidad.viewmodel.MensajesViewModel
import kotlinx.coroutines.launch

/** Vista Escribir: redactar un mensaje, leerlo en voz alta, guardarlo (Firestore) y usar frases rápidas (Content Provider). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEscribir(navController: NavController, usuario: Usuario) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel: MensajesViewModel = viewModel(
        key = "mensajes-${usuario.uid}",
        factory = MensajesViewModel.factory(usuario.uid)
    )
    val aviso by viewModel.aviso.collectAsStateWithLifecycle()

    val voz = remember { TextoAVoz(context) }
    DisposableEffect(Unit) { onDispose { voz.liberar() } }

    val frasesRepo = remember { FrasesRepositorio(context) }
    var frases by remember { mutableStateOf(emptyList<String>()) }
    var refresco by remember { mutableIntStateOf(0) }
    LaunchedEffect(refresco) { frases = frasesRepo.listar() }

    var texto by remember { mutableStateOf("") }
    var errorVoz by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escribir") },
                navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text("Volver") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = texto,
                onValueChange = { texto = it },
                label = { Text("Escribe tu mensaje") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        errorVoz = if (texto.isBlank()) "Escribe algo para leerlo en voz alta."
                        else if (!voz.hablar(texto)) "El motor de voz aún no está listo. Inténtalo de nuevo."
                        else null
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Leer en voz alta") }

                Button(
                    onClick = { if (viewModel.guardar(texto, Mensaje.TIPO_ESCRITO)) texto = "" },
                    modifier = Modifier.weight(1f)
                ) { Text("Guardar") }
            }

            errorVoz?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            aviso?.let { Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall) }

            Spacer(Modifier.height(16.dp))
            Text("Frases rápidas", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(frases) { frase ->
                    AssistChip(
                        onClick = { texto = if (texto.isBlank()) frase else "$texto $frase" },
                        label = { Text(frase) }
                    )
                }
            }
            OutlinedButton(
                onClick = {
                    scope.launch {
                        if (texto.isNotBlank() && frasesRepo.agregar(texto)) refresco++
                    }
                },
                enabled = texto.isNotBlank()
            ) { Text("Guardar como frase rápida") }

            Spacer(Modifier.height(16.dp))
            Text("Historial de mensajes escritos", style = MaterialTheme.typography.titleMedium)

            // Fragment con la tabla de historial (editar / borrar)
            AndroidFragment<HistorialFragment>(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                arguments = bundleOf(
                    HistorialFragment.ARG_UID to usuario.uid,
                    HistorialFragment.ARG_TIPO to Mensaje.TIPO_ESCRITO
                )
            )
        }
    }
}
