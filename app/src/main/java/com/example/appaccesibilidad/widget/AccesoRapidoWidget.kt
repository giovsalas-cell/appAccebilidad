package com.example.appaccesibilidad.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.appaccesibilidad.MainActivity
import com.example.appaccesibilidad.R

/** Widget de pantalla de inicio con accesos directos a Escribir, Hablar y Buscar dispositivo. */
class AccesoRapidoWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { id -> appWidgetManager.updateAppWidget(id, construirVista(context)) }
    }

    companion object {
        fun construirVista(context: Context): RemoteViews =
            RemoteViews(context.packageName, R.layout.widget_acceso_rapido).apply {
                setOnClickPendingIntent(R.id.widget_btn_escribir, abrir(context, "escribir"))
                setOnClickPendingIntent(R.id.widget_btn_hablar, abrir(context, "hablar"))
                setOnClickPendingIntent(R.id.widget_btn_buscar, abrir(context, "buscar"))
            }

        private fun abrir(context: Context, destino: String): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_DESTINO, destino)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            return PendingIntent.getActivity(
                context,
                destino.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
