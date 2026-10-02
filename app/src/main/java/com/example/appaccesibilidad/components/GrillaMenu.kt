package com.example.appaccesibilidad.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class OpcionMenu(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val ruta: String
)

/** Grilla de 2 columnas (LazyVerticalGrid) con las opciones del menú principal. */
@Composable
fun GrillaMenu(
    opciones: List<OpcionMenu>,
    onOpcion: (OpcionMenu) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(opciones, key = { it.ruta }) { opcion ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clickable { onOpcion(opcion) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = opcion.icono,
                        contentDescription = null, // el texto de la tarjeta ya describe la acción
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.height(40.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(opcion.titulo, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                    Text(
                        opcion.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/** Grilla de resumen (etiqueta + cantidad) usada en el Panel Administrador. */
@Composable
fun GrillaResumen(
    datos: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        items(datos, key = { it.first }) { (etiqueta, cantidad) ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("$cantidad", style = MaterialTheme.typography.headlineSmall)
                    Text(etiqueta, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
