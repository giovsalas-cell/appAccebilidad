package com.example.appaccesibilidad

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/** Un tema del tutorial: título, ícono y los pasos que se muestran al expandirlo. */
private data class TemaAyuda(val titulo: String, val icono: ImageVector, val pasos: List<String>)

private val TEMAS_AYUDA = listOf(
    TemaAyuda(
        "Escribir y leer en voz alta", Icons.Default.Create,
        listOf(
            "Escribe tu mensaje en el recuadro o toca una frase rápida.",
            "Pulsa «Leer en voz alta» para que el teléfono lo diga.",
            "Pulsa «Guardar» para dejarlo en tu historial.",
            "En el historial puedes editar o borrar cada mensaje."
        )
    ),
    TemaAyuda(
        "Hablar y ver el texto", Icons.Default.Call,
        listOf(
            "Pulsa «Iniciar dictado» y habla cerca del teléfono.",
            "Tu voz aparece como texto grande y de alto contraste.",
            "Pulsa «Guardar transcripción» para conservarla.",
            "Pulsa «Limpiar» para empezar de nuevo."
        )
    ),
    TemaAyuda(
        "Buscar dispositivo", Icons.Default.LocationOn,
        listOf(
            "Escribe un nombre y pulsa «Registrar» para guardar este teléfono.",
            "Usa el buscador para filtrar por nombre o modelo.",
            "Pulsa «Hacer sonar y vibrar este teléfono» para ubicarlo.",
            "Con «Renombrar» o «Eliminar» administras tu lista."
        )
    ),
    TemaAyuda(
        "Menú y widget", Icons.Default.Info,
        listOf(
            "Desde el menú principal eliges Escribir, Hablar, Buscar o Ayuda.",
            "Agrega el widget «Acceso rápido» a tu pantalla de inicio: mantén presionado un espacio vacío, elige Widgets y arrastra AppAccesibilidad.",
            "El widget abre directamente Escribir, Hablar o Buscar."
        )
    )
)

/** Tutorial de uso: temas desplegables con animación. Se abre desde el menú principal. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAyuda(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayuda") },
                navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text("Volver") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Tutorial de uso", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Toca un tema para ver los pasos.",
                style = MaterialTheme.typography.bodyLarge
            )
            TEMAS_AYUDA.forEach { tema -> TarjetaAyuda(tema) }
            Spacer(Modifier.height(4.dp))
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                Text("Volver al menú")
            }
        }
    }
}

@Composable
private fun TarjetaAyuda(tema: TemaAyuda) {
    var abierto by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { abierto = !abierto },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(tema.icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.padding(horizontal = 8.dp))
                Text(tema.titulo, style = MaterialTheme.typography.titleMedium)
            }
            AnimatedVisibility(
                visible = abierto,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    tema.pasos.forEachIndexed { i, paso ->
                        Text("${i + 1}. $paso", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
