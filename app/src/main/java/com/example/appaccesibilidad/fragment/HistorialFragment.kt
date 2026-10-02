package com.example.appaccesibilidad.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appaccesibilidad.components.ColumnaTabla
import com.example.appaccesibilidad.components.Tabla
import com.example.appaccesibilidad.model.Mensaje
import com.example.appaccesibilidad.ui.theme.AppAccesibilidadTheme
import com.example.appaccesibilidad.viewmodel.MensajesViewModel
import java.text.DateFormat
import java.util.Date

/**
 * Fragment que muestra el historial de mensajes del usuario en una tabla (fecha, tipo, texto, acciones)
 * y permite editar y eliminar (Update / Delete). Se incrusta en Escribir y Hablar con AndroidFragment.
 *
 * Argumentos: [ARG_UID] (obligatorio) y [ARG_TIPO] (opcional: filtra por escrito/hablado).
 */
class HistorialFragment : Fragment() {

    private val uid: String by lazy { requireArguments().getString(ARG_UID).orEmpty() }
    private val tipo: String? by lazy { arguments?.getString(ARG_TIPO) }

    private val viewModel: MensajesViewModel by viewModels { MensajesViewModel.factory(uid) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppAccesibilidadTheme { HistorialContenido(viewModel, tipo) }
            }
        }

    companion object {
        const val ARG_UID = "uid"
        const val ARG_TIPO = "tipo"
    }
}

@Composable
private fun HistorialContenido(viewModel: MensajesViewModel, tipo: String?) {
    val todos by viewModel.mensajes.collectAsStateWithLifecycle()
    val mensajes = remember(todos, tipo) { if (tipo == null) todos else todos.filter { it.tipo == tipo } }
    var enEdicion by remember { mutableStateOf<Mensaje?>(null) }
    val formato = remember { DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT) }

    if (mensajes.isEmpty()) {
        Text(
            "Aún no hay mensajes guardados.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    } else {
        Tabla(
            columnas = listOf(
                ColumnaTabla("Fecha", 1.6f),
                ColumnaTabla("Tipo", 1.2f),
                ColumnaTabla("Mensaje", 3f),
                ColumnaTabla("Acciones", 2f)
            ),
            filas = mensajes,
            clave = { it.id },
            modifier = Modifier.fillMaxSize()
        ) { mensaje, columna ->
            when (columna) {
                0 -> Text(formato.format(Date(mensaje.fecha)), style = MaterialTheme.typography.bodySmall)
                1 -> Text(mensaje.tipo, style = MaterialTheme.typography.bodySmall)
                2 -> Text(mensaje.texto, style = MaterialTheme.typography.bodyMedium)
                else -> Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                    TextButton(onClick = { enEdicion = mensaje }) { Text("Editar") }
                    TextButton(onClick = { viewModel.eliminar(mensaje) }) { Text("Borrar") }
                }
            }
        }
    }

    enEdicion?.let { mensaje ->
        var texto by remember(mensaje.id) { mutableStateOf(mensaje.texto) }
        AlertDialog(
            onDismissRequest = { enEdicion = null },
            title = { Text("Editar mensaje") },
            text = { OutlinedTextField(value = texto, onValueChange = { texto = it }, modifier = Modifier.fillMaxWidth()) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.editar(mensaje, texto)
                    enEdicion = null
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { enEdicion = null }) { Text("Cancelar") } }
        )
    }
}
