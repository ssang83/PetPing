package ai.comake.petping.util

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import androidx.core.net.toUri
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.io.*
import kotlin.jvm.Throws

@Throws(IOException::class)
fun getBitmapFromUri(context: Context, uri: Uri): Bitmap {
    val parcelFileDescriptor: ParcelFileDescriptor? =
        context.contentResolver.openFileDescriptor(uri, "r")
    val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor
    val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
    parcelFileDescriptor?.close()
    return image
}

@Throws(IOException::class)
fun getBitmapFromFile(file: File): Bitmap? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.createSource(file).let {
            ImageDecoder.decodeBitmap(it)
        }
    } else {
        BitmapFactory.decodeFile(file.path)
    }
}

@Throws(AssertionError::class)
fun getBase64EncodeFromFile(file: File): String {
    return Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
}

@Throws(IllegalArgumentException::class)
fun getBitmapFromView(view: View?): Bitmap? {
    view?.let { view ->
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
    return null
}

fun Bitmap.toByteArray(): ByteArray {
    ByteArrayOutputStream().apply {
        compress(Bitmap.CompressFormat.JPEG, 100, this)
        return toByteArray()
    }
}

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, size)
}

/*
  imageFilePath : /storage/emulated/0/DCIM/...
 */
fun getResizeBitmapWithMatrix(imageFilePath: String, maxLength: Int): Bitmap? {
    try {
        val bitmap = BitmapFactory.decodeFile(imageFilePath)
        val orientation = getOrientation(imageFilePath)
        val matrix = Matrix()
        matrix.postRotate(orientation)
        if (bitmap.height >= bitmap.width) {
            val scaleRatio = maxLength.toFloat() / bitmap.height.toFloat()
            matrix.postScale(scaleRatio,scaleRatio)
            return Bitmap.createBitmap(bitmap,0,0, bitmap.width, bitmap.height, matrix,true)
        } else {
            val scaleRatio = maxLength.toFloat() / bitmap.width.toFloat()
            matrix.postScale(scaleRatio,scaleRatio)
            return Bitmap.createBitmap(bitmap,0,0, bitmap.width, bitmap.height, matrix,true)
        }
    } catch (e: Exception) {
        return null
    }
}

fun getOrientation(filePath: String): Float {
    val orientationTag = ExifInterface(filePath).getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )
    val orientation = when (orientationTag) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }
    return orientation.toFloat()
}

fun getBitmapFromInputStream(
    inputStream: InputStream,
    context: Context,
    imageFile: String
): ByteArrayOutputStream {
    val bitmap = BitmapFactory.decodeStream(inputStream)

    val width = bitmap.width
    val height = bitmap.height
    val maxResolution = 1280
    var newWidth = width
    var newHeight = height
    var rate = 0.0f

    var orientation = 0

    var cursor: Cursor? = context.contentResolver
        .query(
            Uri.parse(imageFile),
            arrayOf(MediaStore.Images.ImageColumns.ORIENTATION),
            null,
            null,
            null
        )

    if (cursor?.count != 1) {
        cursor?.close()

        if (imageFile.contains("file:")) {
            val orientationTag =
                ExifInterface(imageFile.removePrefix("file://")).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

            orientation = when (orientationTag) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } else if (imageFile.contains("/DCIM")) {
            val orientationTag = ExifInterface(imageFile).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            orientation = when (orientationTag) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        }
    } else {
        cursor.moveToFirst()
        orientation = cursor.getInt(0)
        cursor.close()
    }

    val m = Matrix()
    m.setRotate(orientation.toFloat())

    if (width > height) {
        if (maxResolution < width) {
            rate = maxResolution / width.toFloat()
            newHeight = (height * rate).toInt()
            newWidth = maxResolution
        }
    } else {
        if (maxResolution < height) {
            rate = maxResolution / height.toFloat()
            newWidth = (width * rate).toInt()
            newHeight = maxResolution
        }
    }

    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    val rotatedBitmap = Bitmap.createBitmap(
        resizedBitmap,
        0,
        0,
        resizedBitmap.width,
        resizedBitmap.height,
        m,
        true
    )
    val outputStream = ByteArrayOutputStream()
    rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    return outputStream
}

fun getResizeBitmap(
    context: Context,
    imageFile: String
): ByteArray? {
    try {
        val bitmap = BitmapFactory.decodeFile(imageFile.toUri().path)
        val width = bitmap.width
        val height = bitmap.height
        var fixWidth = width
        var fixHeight = height
        val maxWidth = 1024
        val maxHeight = 1024
        var rate = 0.0f
        if (width > height) {
            rate = maxWidth / width.toFloat()
            fixWidth = maxWidth
            fixHeight = (height * rate).toInt()
        } else if (height > width) {
            rate = maxHeight / height.toFloat()
            fixHeight = maxHeight
            fixWidth = (width * rate).toInt()
        } else {
            fixWidth = maxWidth
            fixHeight = maxHeight
        }
        val orientationTag = ExifInterface(imageFile.toUri().path.toString()).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        val orientation = when (orientationTag) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }

        val matrix = Matrix()
        matrix.postRotate(orientation.toFloat())
        matrix.postScale(fixWidth / width.toFloat(), fixHeight / height.toFloat())
        val rotateBitmap = Bitmap.createBitmap(
            bitmap,
            0,
            0,
            width,
            height,
            matrix,
            true
        )

        val outputStream = ByteArrayOutputStream()
        rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        return null
    }
}