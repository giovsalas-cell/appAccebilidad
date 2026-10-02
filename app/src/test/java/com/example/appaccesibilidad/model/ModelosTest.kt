package com.example.appaccesibilidad.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelosTest {

    @Test
    fun usuario_idaYVueltaConMapaDeFirestore() {
        val original = Usuario("uid1", "ana@duoc.cl", "Ana Silva", "Texto a Voz", "Acompañante", ROL_USUARIO)
        val recuperado = original.aMapa().aUsuario("uid1")
        assertEquals(original, recuperado)
    }

    @Test
    fun usuario_noGuardaContrasena() {
        val mapa = Usuario("u", "a@b.cl", "A").aMapa()
        assertFalse(mapa.keys.any { it.contains("contrasena", ignoreCase = true) || it.contains("password", ignoreCase = true) })
    }

    @Test
    fun usuario_camposFaltantesUsanValoresPorDefecto() {
        val u = emptyMap<String, Any?>().aUsuario("x")
        assertEquals(ROL_USUARIO, u.rol)
        assertEquals("Texto a Voz", u.preferenciaAccesibilidad)
        assertFalse(u.esAdmin)
    }

    @Test
    fun usuario_esAdminSegunRol() {
        assertTrue(Usuario(rol = ROL_ADMIN).esAdmin)
    }

    @Test
    fun mensaje_idaYVuelta() {
        val m = Mensaje("id1", "Hola", Mensaje.TIPO_HABLADO, 1700000000000L)
        assertEquals(m, m.aMapa().aMensaje("id1"))
    }

    @Test
    fun dispositivo_idaYVuelta() {
        val d = Dispositivo("d1", "Mi teléfono", "Samsung Galaxy A54", 1700000000000L)
        assertEquals(d, d.aMapa().aDispositivo("d1"))
    }
}
