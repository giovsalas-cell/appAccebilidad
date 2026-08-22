package com.example.appaccesibilidad

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAdmin(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Panel Administrador") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Usuarios Registrados",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Grilla de usuarios (2 columnas)
            listaUsuarios.chunked(2).forEach { fila ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    fila.forEach { usuario ->
                        Card(modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = usuario.nombre, style = MaterialTheme.typography.bodyLarge)
                                Text(text = usuario.email, style = MaterialTheme.typography.bodySmall)
                                Text(text = usuario.preferenciaAccesibilidad, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    // Si la fila queda impar, se rellena el espacio para mantener el ancho
                    if (fila.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Resumen por Preferencia de Accesibilidad",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            TablaResumenPreferencias(usuarios = listaUsuarios)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Componente de tabla: agrupa a los usuarios registrados por su preferencia
 * de accesibilidad (Texto a Voz, Vibración / Alertas, Subtítulos Visuales)
 * y muestra la cantidad de usuarios por cada una en formato de tabla,
 * con encabezado y bordes entre filas y columnas.
 */
@Composable
fun TablaResumenPreferencias(usuarios: List<Usuario>) {
    val resumen = usuarios
        .groupingBy { it.preferenciaAccesibilidad }
        .eachCount()
        .toList()
        .sortedByDescending { it.second }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        // Fila de encabezado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            CeldaTabla(
                texto = "Preferencia",
                weight = 0.65f,
                esEncabezado = true
            )
            CeldaTabla(
                texto = "Usuarios",
                weight = 0.35f,
                esEncabezado = true
            )
        }

        if (resumen.isEmpty()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                CeldaTabla(texto = "Sin usuarios registrados", weight = 1f)
            }
        } else {
            resumen.forEachIndexed { index, (preferencia, cantidad) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (index % 2 == 0) Color.Transparent
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                ) {
                    CeldaTabla(texto = preferencia, weight = 0.65f)
                    CeldaTabla(texto = cantidad.toString(), weight = 0.35f)
                }
            }
        }
    }
}

@Composable
private fun RowScope.CeldaTabla(
    texto: String,
    weight: Float,
    esEncabezado: Boolean = false
) {
    Text(
        text = texto,
        modifier = Modifier
            .weight(weight)
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        style = if (esEncabezado) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
        fontWeight = if (esEncabezado) FontWeight.Bold else FontWeight.Normal
    )
}