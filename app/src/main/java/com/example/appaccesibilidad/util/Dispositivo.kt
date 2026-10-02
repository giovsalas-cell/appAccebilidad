package com.example.appaccesibilidad.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import androidx.core.content.getSystemService
import java.util.Locale

/** Vibración como respuesta háptica para personas con discapacidad auditiva. */
fun Context.vibrar(milisegundos: Long = 400L) {
    val vibrador: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getSystemService<VibratorManager>()?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService<Vibrator>()
    }
    if (vibrador?.hasVibrator() != true) return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrador.vibrate(VibrationEffect.createOneShot(milisegundos, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrador.vibrate(milisegundos)
    }
}

/** Envoltorio de TextToSpeech (texto a voz). Llamar a [liberar] al destruir la pantalla. */
class TextoAVoz(context: Context) {

    private var listo = false
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext) { estado ->
        if (estado == TextToSpeech.SUCCESS) {
            tts?.language = Locale.forLanguageTag("es-CL")
            listo = true
        }
    }

    /** @return false si el motor de voz aún no está disponible. */
    fun hablar(texto: String): Boolean {
        if (!listo || texto.isBlank()) return false
        tts?.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "mensaje")
        return true
    }

    fun liberar() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        listo = false
    }
}

/** Hace sonar el tono de llamada durante unos segundos y vibra: sirve para encontrar el teléfono cerca. */
fun Context.hacerSonar(duracionMs: Long = 4000L) {
    val uri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_RINGTONE)
    val tono = android.media.RingtoneManager.getRingtone(this, uri)
    tono?.play()
    vibrar(duracionMs)
    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ tono?.stop() }, duracionMs)
}
