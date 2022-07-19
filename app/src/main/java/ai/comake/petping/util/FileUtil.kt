package ai.comake.petping.util

import ai.comake.petping.data.vo.Download
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.loader.content.CursorLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import kotlin.jvm.Throws

@Throws(IOException::class)
fun readTextFromUri(context: Context, uri: Uri): String {
    val stringBuilder = StringBuilder()
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line: String? = reader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = reader.readLine()
            }
        }
    }
    return stringBuilder.toString()
}

fun getAudioGuideList(context: Context): List<String> {
    val list = arrayListOf<String>()
    val dir = File(context?.filesDir?.absolutePath)

    if (dir.exists()) {
        if (dir.listFiles() != null) {
            for (f in dir.listFiles()) {
                if (f.isFile) {
                    if (f.name.contains(".mp3")) {
                        list.add(f.name.replace(".mp3", ""))
                    }
                }
            }
        }
    }

    return list
}

fun ResponseBody.downloadToFileWithProgress(
    directory: File,
    filename: String,
    position: Int
): Flow<Download> =
    flow {
        emit(Download.Progress(0))

        // flag to delete file if download errors or is cancelled
        var deleteFile = true
        val file = File(directory, "${filename}.mp3")
        try {
            byteStream().use { inputStream ->
                file.outputStream().use { outputStream ->
                    val totalBytes = contentLength()
                    val data = ByteArray(8_192)
                    var progressBytes = 0L
                    while (true) {
                        val bytes = inputStream.read(data)

                        if (bytes == -1) {
                            break
                        }

                        outputStream.channel
                        outputStream.write(data, 0, bytes)
                        progressBytes += bytes

                        emit(Download.Progress(percent = ((progressBytes * 100) / totalBytes).toInt()))
                    }

                    when {
                        progressBytes < totalBytes ->
                            throw Exception("missing bytes")
                        progressBytes > totalBytes ->
                            throw Exception("too many bytes")
                        else ->
                            deleteFile = false
                    }
                }
            }
            emit(Download.Finished(file))
        } catch (e: Exception) {
            LogUtil.log("TAG", "e ${e.message}")
        } finally {
            // check if download was successful
            if (deleteFile) {
                file.delete()
            }
        }
    }
        .flowOn(Dispatchers.IO)
        .distinctUntilChanged()


fun getFileFromURI(uri: Uri, context: Context): String? {
    var result: String? = null
    try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(context, uri, proj, null, null, null)
        val cursor: Cursor? = loader.loadInBackground()
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        result = cursor?.getString(columnIndex!!)
        cursor?.close()
    } catch (e: Exception) {
        return null
    }
    return result
}
