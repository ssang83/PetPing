package ai.comake.petping.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun hasPermission(context: Context, perms: Array<String>, ) = perms.all {
    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}

val LOCATION_PERMISSION = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION
)
val STORAGE_PERMISSION = arrayOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

