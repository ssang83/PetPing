package ai.comake.petping.util

import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsControllerCompat

@RequiresApi(Build.VERSION_CODES.M)
fun updateDarkStatusBar(window: Window) {
    window.statusBarColor = Color.TRANSPARENT
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//        window.setDecorFitsSystemWindows(false)
//        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
//    } else {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//    }
}

@RequiresApi(Build.VERSION_CODES.M)
fun updateLightStatusBar(window: Window) {
    window.statusBarColor = Color.TRANSPARENT
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//        window.setDecorFitsSystemWindows(false)
//        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
//    } else {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//    }
}

fun updateBlackStatusBar(window: Window) {
    window.statusBarColor = Color.BLACK
    window.decorView.systemUiVisibility =
        window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
}

fun updateWhiteStatusBar(window: Window) {
    window.statusBarColor = Color.WHITE
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
}