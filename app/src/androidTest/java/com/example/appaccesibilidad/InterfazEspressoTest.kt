package com.example.appaccesibilidad

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.appaccesibilidad.components.ColumnaTabla
import com.example.appaccesibilidad.components.Tabla
import com.example.appaccesibilidad.ui.theme.AppAccesibilidadTheme
import com.google.firebase.auth.FirebaseAuth
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

/** Pruebas instrumentadas (Espresso + Compose Test). Se ejecutan en emulador o en Firebase Test Lab. */
@RunWith(AndroidJUnit4::class)
class InterfazEspressoTest {

    // Cada prueba debe partir SIN sesión iniciada (Firebase guarda la sesión en el dispositivo),
    // por eso se cierra la sesión ANTES de abrir la actividad.
    private val sinSesion = object : ExternalResource() {
        override fun before() {
            FirebaseAuth.getInstance().signOut()
        }
    }
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val reglas: RuleChain = RuleChain.outerRule(sinSesion).around(composeRule)

    @Test
    fun login_muestraCamposYBotones() {
        composeRule.onNodeWithText("Iniciar Sesión").assertIsDisplayed()
        composeRule.onNodeWithText("Correo Electrónico").assertIsDisplayed()
        composeRule.onNodeWithText("Ingresar").assertIsDisplayed()
        composeRule.onNodeWithText("¿No tienes cuenta? Regístrate").assertIsDisplayed()
    }

    @Test
    fun login_camposVacios_muestraError() {
        composeRule.onNodeWithText("Ingresar").performClick()
        composeRule.onNodeWithText("Ingresa tu correo y contraseña.").assertIsDisplayed()
    }

    @Test
    fun login_emailInvalido_muestraError() {
        composeRule.onNodeWithText("Correo Electrónico").performTextInput("correo-malo")
        composeRule.onNodeWithText("Contraseña").performTextInput("123456")
        composeRule.onNodeWithText("Ingresar").performClick()
        composeRule.onNodeWithText("El formato del correo electrónico es inválido.").assertIsDisplayed()
    }

    @Test
    fun navegacion_loginARegistroYVuelta() {
        composeRule.onNodeWithText("¿No tienes cuenta? Regístrate").performClick()
        composeRule.onNodeWithText("Registro de Usuario").assertIsDisplayed()
        Espresso.pressBack() // Espresso: botón atrás del sistema
        composeRule.onNodeWithText("Iniciar Sesión").assertIsDisplayed()
    }

    @Test
    fun recuperar_correoInvalido_muestraError() {
        composeRule.onNodeWithText("¿Olvidaste tu contraseña?").performClick()
        composeRule.onNodeWithText("Correo de recuperación").performTextInput("mal")
        composeRule.onNodeWithText("Enviar Correo").performClick()
        composeRule.onNodeWithText("Por favor, ingresa un correo válido.").assertIsDisplayed()
    }
}

/** Prueba aislada del componente Tabla (no requiere Firebase). */
@RunWith(AndroidJUnit4::class)
class TablaComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun tabla_muestraEncabezadosYFilas() {
        composeRule.setContent {
            AppAccesibilidadTheme {
                Tabla(
                    columnas = listOf(ColumnaTabla("Nombre", 1f), ColumnaTabla("Correo", 2f)),
                    filas = listOf("Juan" to "user1@duoc.cl", "Maria" to "user2@duoc.cl"),
                    clave = { it.second }
                ) { fila, columna ->
                    Text(if (columna == 0) fila.first else fila.second)
                }
            }
        }
        composeRule.onNodeWithText("Nombre").assertIsDisplayed()
        composeRule.onNodeWithText("Correo").assertIsDisplayed()
        composeRule.onNodeWithText("Juan").assertIsDisplayed()
        composeRule.onNodeWithText("user2@duoc.cl").assertIsDisplayed()
    }
}