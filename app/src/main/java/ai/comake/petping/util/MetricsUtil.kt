package ai.comake.petping.util

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue

// extension function to convert dp to equivalent pixels
fun Int.dpToPixels(context: Context):Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,this.toFloat(),context.resources.displayMetrics
)

// input float dp value to convert equivalent pixels
fun Float.dpToPixels(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics
)

// extension function to convert pixels to equivalent dp
fun Int.pixelsToDp(context: Context): Float =
    this / (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT).toFloat()

// input float pixels value to convert equivalent dp
fun Float.pixelsToDp(context: Context): Float =
    this / (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT).toFloat()
