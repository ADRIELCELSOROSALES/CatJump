# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# --- Conservar líneas para stack traces legibles en Play Console ---
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- Eliminar TODOS los logs en release (Log.d/v/i/w/e) ---
# R8 los remueve al no tener efectos secundarios.
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
    public static boolean isLoggable(...);
}

# --- Modelos de dominio (data classes usadas por el motor y la UI) ---
# Seguridad extra por si en el futuro se serializan.
-keep class com.example.catjump.domain.model.** { *; }
