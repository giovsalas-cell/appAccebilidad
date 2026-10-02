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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appaccesibilidad.components.ColumnaTabla
import com.example.appaccesibilidad.components.GrillaResumen
import com.example.appaccesibilidad.components.Tabla
import com.example.appaccesibilidad.domain.resumenPorPreferencia
import com.example.appaccesibilidad.model.PREFERENCIAS_ACCESIBILIDAD
import com.example.appaccesibilidad.model.ROL_ADMIN
import com.example.appaccesibilidad.model.ROL_USUARIO
import com.example.appaccesibilidad.model.Usuario
import com.example.appaccesibilidad.viewmodel.AdminViewModel

/**
 * Panel Administrador: total de cuentas, GRILLA de resumen por preferencia y TABLA de usuarios
 * con acciones de edición y eliminación (CRUD sobre Firestore).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAdmin(usuarioActual: Usuario, onCerrarSesion: () -> Unit) {
    val viewModel: AdminViewModel = viewModel(factory = AdminViewModel.Factory)
    val usuarios by viewModel.usuarios.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()
    var aEliminar by remember { mutableStateOf<Usuario?>(null) }
    var aEditar by remember { mutableStateOf<Usuario?>(null) }

    val resumen = remember(usuarios) {
        val conteo = resumenPorPreferencia(usuarios)
        PREFERENCIAS_ACCESIBILIDAD.map { it to (conteo[it] ?: 0) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total de usuarios en sistema:", style = MaterialTheme.typography.titleMedium)
                    Text("${usuarios.size}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Usuarios por preferencia de accesibilidad", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))
            GrillaResumen(datos = resumen, modifier = Modifier.height(84.dp))

            Spacer(Modifier.height(8.dp))
            Text("Tabla de Cuentas del Sistema", style = MaterialTheme.typography.titleMedium)

            mensaje?.let {
                Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(4.dp))

            Tabla(
                columnas = listOf(
                    ColumnaTabla("Nombre", 2f),
                    ColumnaTabla("Correo", 2.6f),
                    ColumnaTabla("Preferencia", 2f),
                    ColumnaTabla("Rol", 1.4f),
                    ColumnaTabla("Acciones", 2.2f)
                ),
                filas = usuarios,
                clave = { it.uid },
                modifier = Modifier.weight(1f)
            ) { usuario, columna ->
                when (columna) {
                    0 -> Text(usuario.nombre.ifBlank { "Sin nombre" }, style = MaterialTheme.typography.bodySmall)
                    1 -> Text(usuario.email, style = MaterialTheme.typography.bodySmall)
                    2 -> Text(usuario.preferenciaAccesibilidad, style = MaterialTheme.typography.bodySmall)
                    3 -> Text(usuario.rol, style = MaterialTheme.typography.bodySmall)
                    else -> Column {
                        TextButton(onClick = { aEditar = usuario }) { Text("Editar") }
                        // El administrador no puede eliminarse a sí mismo
                        if (usuario.uid != usuarioActual.uid) {
                            TextButton(onClick = { aEliminar = usuario }) { Text("Eliminar") }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Button(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) { Text("Cerrar Sesión Admin") }
        }
    }

    aEliminar?.let { usuario ->
        AlertDialog(
            onDismissRequest = { aEliminar = null },
            title = { Text("Eliminar usuario") },
            text = { Text("¿Eliminar el perfil de ${usuario.email}? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminar(usuario)
                    aEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { aEliminar = null }) { Text("Cancelar") } }
        )
    }

    aEditar?.let { usuario ->
        DialogoEditarUsuario(
            usuario = usuario,
            puedeCambiarRol = usuario.uid != usuarioActual.uid,
            onCancelar = { aEditar = null },
            onGuardar = {
                viewModel.actualizar(it)
                aEditar = null
            }
        )
    }
}

@Composable
private fun DialogoEditarUsuario(
    usuario: Usuario,
    puedeCambiarRol: Boolean,
    onCancelar: () -> Unit,
    onGuardar: (Usuario) -> Unit
) {
    var nombre by remember(usuario.uid) { mutableStateOf(usuario.nombre) }
    var preferencia by remember(usuario.uid) { mutableStateOf(usuario.preferenciaAccesibilidad) }
    var esAdmin by remember(usuario.uid) { mutableStateOf(usuario.esAdmin) }
    var menu by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Editar usuario") },
        text = {
            Column {
                Text(usuario.email, style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Box {
                    OutlinedButton(onClick = { menu = true }, modifier = Modifier.fillMaxWidth()) { Text(preferencia) }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        PREFERENCIAS_ACCESIBILIDAD.forEach { opcion ->
                            DropdownMenuItem(text = { Text(opcion) }, onClick = { preferencia = opcion; menu = false })
                        }
                    }
                }
                if (puedeCambiarRol) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = esAdmin, onCheckedChange = { esAdmin = it })
                        Text("Administrador")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = nombre.isNotBlank(),
                onClick = {
                    onGuardar(
                        usuario.copy(
                            nombre = nombre.trim(),
                            preferenciaAccesibilidad = preferencia,
                            rol = if (esAdmin) ROL_ADMIN else ROL_USUARIO
                        )
                    )
                }
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
