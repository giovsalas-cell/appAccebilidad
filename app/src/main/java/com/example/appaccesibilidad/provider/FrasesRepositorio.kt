package com.example.appaccesibilidad.provider

import android.content.Context
import androidx.core.content.contentValuesOf
import androidx.core.database.getStringOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Cliente del [FrasesProvider] a través de ContentResolver. */
class FrasesRepositorio(context: Context) {

    private val resolver = context.applicationContext.contentResolver

    suspend fun listar(): List<String> = withContext(Dispatchers.IO) {
        val frases = mutableListOf<String>()
        resolver.query(FrasesProvider.CONTENT_URI, arrayOf(FrasesProvider.COL_TEXTO), null, null, null)?.use { c ->
            while (c.moveToNext()) c.getStringOrNull(0)?.let(frases::add)
        }
        frases
    }

    /** @return true si la frase se guardó. */
    suspend fun agregar(texto: String): Boolean = withContext(Dispatchers.IO) {
        resolver.insert(FrasesProvider.CONTENT_URI, contentValuesOf(FrasesProvider.COL_TEXTO to texto)) != null
    }
}
