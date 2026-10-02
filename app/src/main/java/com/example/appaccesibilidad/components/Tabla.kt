package com.example.appaccesibilidad.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Definición de una columna de la tabla: título y ancho relativo. */
data class ColumnaTabla(val titulo: String, val peso: Float = 1f)

/**
 * Tabla genérica: fila de encabezado fija + filas dinámicas (LazyColumn) con columnas
 * alineadas, separadores y filas alternadas ("cebra") para facilitar la lectura.
 *
 * @param celda contenido de la celda de la columna [Int] para la fila [T].
 */
@Composable
fun <T> Tabla(
    columnas: List<ColumnaTabla>,
    filas: List<T>,
    modifier: Modifier = Modifier,
    clave: ((T) -> Any)? = null,
    celda: @Composable BoxScope.(fila: T, columna: Int) -> Unit
) {
    Column(modifier = modifier) {
        // Encabezado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            columnas.forEach { col ->
                Text(
                    text = col.titulo,
                    modifier = Modifier
                        .weight(col.peso)
                        .padding(horizontal = 4.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        // Filas
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            val claveIndexada: ((Int, T) -> Any)? = clave?.let { k -> { _, fila -> k(fila) } }
            itemsIndexed(items = filas, key = claveIndexada) { indice, fila ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (indice % 2 == 0) MaterialTheme.colorScheme.surface
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    columnas.forEachIndexed { i, col ->
                        Box(
                            modifier = Modifier
                                .weight(col.peso)
                                .padding(horizontal = 4.dp)
                        ) { celda(this, fila, i) }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}
