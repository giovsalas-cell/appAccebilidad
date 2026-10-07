# AppAccesibilidad — Actividad sumativa 3 (Semana 8)

App Android (Kotlin + Jetpack Compose + Material 3) de accesibilidad para personas con discapacidad auditiva.

## Qué cambió respecto a la S5
| Área | Cambio |
|---|---|
| Tabla / grilla | `components/Tabla.kt` (tabla con encabezado y columnas: Panel Admin e Historial) y `components/GrillaMenu.kt` (`LazyVerticalGrid`: menú Home y resumen del Admin) |
| Back end | Firebase Authentication + Cloud Firestore (`data/`), reemplaza la lista en memoria |
| Vistas nuevas | Home, Escribir, Hablar, Buscar dispositivo (registro y búsqueda de dispositivos, sonar/vibrar; sin geolocalización) |
| Fragment | `fragment/HistorialFragment.kt`, incrustado con `AndroidFragment` |
| Content Provider | `provider/FrasesProvider.kt` (frases rápidas, SQLite) |
| Widget | `widget/AccesoRapidoWidget.kt` |
| KTX | core-ktx, fragment-ktx, lifecycle-ktx, `viewModels`, `getSystemService<T>()`, `toUri`, `bundleOf`, `contentValuesOf`, `Firebase.auth` / `Firebase.firestore` |
| Pruebas | JUnit, Mockito, Robolectric (`src/test`), Espresso + Compose Test (`src/androidTest`) |

## 1. Configurar Firebase (una sola vez)
1. Consola Firebase → crear proyecto → agregar app Android con el paquete `com.example.appaccesibilidad`.
2. Descargar `google-services.json` y copiarlo en `app/` (el build falla sin este archivo).
3. Authentication → Sign-in method → habilitar **Correo electrónico/contraseña**.
4. Firestore Database → crear base de datos → pestaña **Reglas** → pegar el contenido de `firestore.rules` → Publicar.
5. **Crear el administrador**: Authentication → Users → Add user (`admin@duoc.cl`, clave mínima 6 caracteres). Copiar su UID.
   En Firestore crear colección `usuarios`, documento con ID = ese UID y campos (string):
   `email`, `nombre`="Administrador", `preferenciaAccesibilidad`="Subtítulos Visuales", `tipoCuenta`="Personal", `rol`="admin".
6. Los usuarios normales se crean desde la vista Registro de la app (rol siempre `usuario`).

## 2. Ejecutar pruebas
```
./gradlew testDebugUnitTest              # JUnit + Mockito + Robolectric (JVM)
./gradlew connectedDebugAndroidTest      # Espresso / Compose Test (emulador o dispositivo)
```
Reporte HTML: `app/build/reports/tests/testDebugUnitTest/index.html`.

**Firebase Test Lab:** `./gradlew assembleDebug assembleDebugAndroidTest`, luego en la consola Firebase → Test Lab →
*Run a test* → *Instrumentation* → subir `app-debug.apk` y `app-debug-androidTest.apk`.

## 3. Generar y firmar el APK
1. Crear el keystore (guárdalo fuera de Git):
   ```
   keytool -genkeypair -v -keystore keystore/appaccesibilidad-release.jks -alias appaccesibilidad -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Copiar `keystore.properties.example` a `keystore.properties` y completar contraseñas.
3. `./gradlew assembleRelease` → `app/build/outputs/apk/release/app-release.apk` (queda firmado).
   (Alternativa en Android Studio: *Build > Generate Signed App Bundle / APK > APK*.)
4. Verificar firma: `apksigner verify --verbose app-release.apk`.
5. Antes de cada nueva publicación subir `versionCode`/`versionName` en `app/build.gradle.kts`.

## 4. Publicar (plataforma gratuita)
GitHub → repositorio → *Releases* → *Draft a new release* → tag `v2.0` → adjuntar `app-release.apk` → Publish.
El enlace del release es la URL de descarga a incluir en el informe.
