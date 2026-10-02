package com.example.appaccesibilidad.provider

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

/** Pruebas Robolectric: ejecutan el ContentProvider real (SQLite) en la JVM, sin emulador. */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class FrasesProviderTest {

    private val contexto: Context = ApplicationProvider.getApplicationContext()
    private val resolver get() = contexto.contentResolver

    @Before
    fun registrarProvider() {
        Robolectric.setupContentProvider(FrasesProvider::class.java, FrasesProvider.AUTHORITY)
    }

    private fun textos(): List<String> {
        val lista = mutableListOf<String>()
        resolver.query(FrasesProvider.CONTENT_URI, arrayOf(FrasesProvider.COL_TEXTO), null, null, null)?.use {
            while (it.moveToNext()) lista.add(it.getString(0))
        }
        return lista
    }

    @Test
    fun alCrearseTraeFrasesPorDefecto() {
        assertEquals(4, textos().size)
        assertTrue(textos().contains("Gracias."))
    }

    @Test
    fun insertarAgregaLaFrase() {
        val uri = resolver.insert(FrasesProvider.CONTENT_URI, ContentValues().apply { put(FrasesProvider.COL_TEXTO, "Buenos días") })
        assertNotNull(uri)
        assertTrue(textos().contains("Buenos días"))
    }

    @Test
    fun insertarTextoVacioEsRechazado() {
        val uri = resolver.insert(FrasesProvider.CONTENT_URI, ContentValues().apply { put(FrasesProvider.COL_TEXTO, "  ") })
        assertNull(uri)
        assertEquals(4, textos().size)
    }

    @Test
    fun eliminarPorIdBorraSoloEsaFila() {
        val uri = resolver.insert(FrasesProvider.CONTENT_URI, ContentValues().apply { put(FrasesProvider.COL_TEXTO, "Temporal") })!!
        val id = ContentUris.parseId(uri)
        val borradas = resolver.delete(ContentUris.withAppendedId(FrasesProvider.CONTENT_URI, id), null, null)
        assertEquals(1, borradas)
        assertEquals(4, textos().size)
    }

    @Test
    fun getTypeDevuelveTiposMime() {
        assertTrue(resolver.getType(FrasesProvider.CONTENT_URI)!!.startsWith("vnd.android.cursor.dir"))
        assertTrue(resolver.getType(ContentUris.withAppendedId(FrasesProvider.CONTENT_URI, 1))!!.startsWith("vnd.android.cursor.item"))
    }
}
