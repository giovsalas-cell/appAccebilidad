package com.example.appaccesibilidad.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import androidx.core.content.contentValuesOf
import androidx.core.net.toUri

/**
 * Content Provider de "frases rápidas": textos frecuentes que la persona usuaria puede tocar
 * en la vista Escribir para comunicarse más rápido. Persiste en SQLite y notifica cambios.
 */
class FrasesProvider : ContentProvider() {

    private lateinit var helper: FrasesDbHelper

    override fun onCreate(): Boolean {
        helper = FrasesDbHelper(requireNotNull(context))
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val (where, args) = filtroPorUri(uri, selection, selectionArgs)
        val cursor = helper.readableDatabase.query(
            TABLA, projection, where, args, null, null, sortOrder ?: "$COL_ID DESC"
        )
        cursor.setNotificationUri(requireNotNull(context).contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (COINCIDENCIAS.match(uri) != FRASES) return null
        val texto = values?.getAsString(COL_TEXTO)?.trim()
        if (texto.isNullOrEmpty()) return null
        val id = helper.writableDatabase.insert(TABLA, null, contentValuesOf(COL_TEXTO to texto))
        if (id < 0) return null
        val nueva = ContentUris.withAppendedId(CONTENT_URI, id)
        requireNotNull(context).contentResolver.notifyChange(nueva, null)
        return nueva
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val (where, args) = filtroPorUri(uri, selection, selectionArgs)
        val filas = helper.writableDatabase.update(TABLA, values, where, args)
        if (filas > 0) requireNotNull(context).contentResolver.notifyChange(uri, null)
        return filas
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val (where, args) = filtroPorUri(uri, selection, selectionArgs)
        val filas = helper.writableDatabase.delete(TABLA, where, args)
        if (filas > 0) requireNotNull(context).contentResolver.notifyChange(uri, null)
        return filas
    }

    override fun getType(uri: Uri): String? = when (COINCIDENCIAS.match(uri)) {
        FRASES -> "vnd.android.cursor.dir/vnd.$AUTHORITY.frases"
        FRASE_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.frases"
        else -> null
    }

    private fun filtroPorUri(uri: Uri, selection: String?, args: Array<out String>?): Pair<String?, Array<out String>?> =
        when (COINCIDENCIAS.match(uri)) {
            FRASES -> selection to args
            FRASE_ID -> {
                val id = ContentUris.parseId(uri).toString()
                val where = if (selection.isNullOrEmpty()) "$COL_ID = ?" else "$COL_ID = ? AND ($selection)"
                where to (arrayOf(id) + (args ?: emptyArray()))
            }
            else -> throw IllegalArgumentException("URI desconocida: $uri")
        }

    companion object {
        const val AUTHORITY = "com.example.appaccesibilidad.provider"
        const val TABLA = "frases"
        const val COL_ID = "_id"
        const val COL_TEXTO = "texto"
        val CONTENT_URI: Uri = "content://$AUTHORITY/$TABLA".toUri()

        private const val FRASES = 1
        private const val FRASE_ID = 2
        private val COINCIDENCIAS = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, TABLA, FRASES)
            addURI(AUTHORITY, "$TABLA/#", FRASE_ID)
        }
    }
}

private class FrasesDbHelper(context: Context) : SQLiteOpenHelper(context, "frases.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE ${FrasesProvider.TABLA} (" +
                "${FrasesProvider.COL_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${FrasesProvider.COL_TEXTO} TEXT NOT NULL)"
        )
        listOf(
            "Hola, soy una persona con discapacidad auditiva.",
            "Por favor, escríbeme lo que quieres decir.",
            "Gracias.",
            "Necesito ayuda."
        ).forEach { db.insert(FrasesProvider.TABLA, null, contentValuesOf(FrasesProvider.COL_TEXTO to it)) }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${FrasesProvider.TABLA}")
        onCreate(db)
    }
}
