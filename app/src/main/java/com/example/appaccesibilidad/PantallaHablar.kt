package com.example.appaccesibilidad

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.fragment.compose.AndroidFragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appaccesibilidad.fragment.HistorialFragment
import com.example.appaccesibilidad.model.Mensaje
import com.example.appaccesibilidad.model.Usuario
import com.example.appaccesibilidad.util.vibrar
import com.example.appaccesibilidad.viewmodel.MensajesViewModel

/** Vista Hablar: la persona (o su interlocutor) habla y la app lo muestra como subtítulo grande y de alto contraste. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHablar(navController: NavController, usuario: Usuario) {
    val context = LocalContext.current
    val viewModel: MensajesViewModel = viewModel(
        key = "mensajes-${usuario.uid}",
        factory = MensajesViewModel.factory(usuario.uid)
    )
    val aviso by viewModel.aviso.collectAsStateWithLifecycle()

    var transcripcion by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val lanzador = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        if (resultado.resultCode == Activity.RESULT_OK) {
            val texto = resultado.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
                .orEmpty()
            if (texto.isNotBlank()) {
                transcripcion = texto
                error = null
                context.vibrar(200L) // aviso háptico: la transcripción está lista
            }
        }
    }

    fun iniciarDictado() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-CL")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora")
        }
        try {
            lanzador.launch(intent)
        } catch (e: ActivityNotFoundException) {
            error = "Tu dispositivo no tiene un servicio de reconocimiento de voz instalado."
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hablar") },
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
            // Subtítulo en alto contraste
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp)
                    .background(MaterialTheme.colorScheme.inverseSurface)
                    .padding(16.dp)
            ) {
                Text(
                    text = transcripcion.ifBlank { "Presiona \"Iniciar dictado\" y habla. El texto aparecerá aquí." },
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    fontSize = 24.sp,
                    lineHeight = 32.sp
                )
            }

            Spacer(Modifier.height(12.dp))
            Button(onClick = { iniciarDictado() }, modifier = Modifier.fillMaxWidth()) { Text("Iniciar dictado") }

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { if (viewModel.guardar(transcripcion, Mensaje.TIPO_HABLADO)) transcripcion = "" },
                    enabled = transcripcion.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) { Text("Guardar transcripción") }
                OutlinedButton(
                    onClick = { transcripcion = "" },
                    enabled = transcripcion.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) { Text("Limpiar") }
            }

            error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            aviso?.let { Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall) }

            Spacer(Modifier.height(16.dp))
            Text("Historial de transcripciones", style = MaterialTheme.typography.titleMedium)

            AndroidFragment<HistorialFragment>(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                arguments = bundleOf(
                    HistorialFragment.ARG_UID to usuario.uid,
                    HistorialFragment.ARG_TIPO to Mensaje.TIPO_HABLADO
                )
            )
        }
    }
}
