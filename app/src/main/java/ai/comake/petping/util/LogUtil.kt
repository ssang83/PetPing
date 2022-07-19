package ai.comake.petping.util

import ai.comake.petping.BuildConfig
import android.annotation.SuppressLint
import android.util.Log

/**
 * 로그가 찍힌 부분을 클릭시 해당 라인으로 이동
 */
object LogUtil {
    @SuppressLint("LogNotTimber")
    fun log(tag: String?, message: String) {
        if (BuildConfig.BUILD_TYPE == "debug") {
            val filename = String.format(
                " (%s:%s)",
                Throwable().stackTrace[1].fileName,
                Throwable().stackTrace[1].lineNumber
            )
            val methodName = String.format(" %s() ", Throwable().stackTrace[1].methodName)
            Log.d(tag, methodName + message + filename)
        }
    }

    @SuppressLint("LogNotTimber")
    fun log(message: String) {
        if (BuildConfig.BUILD_TYPE == "debug") {
            val filename = String.format(
                " (%s:%s)",
                Throwable().stackTrace[1].fileName,
                Throwable().stackTrace[1].lineNumber
            )
            val methodName = String.format(" %s() ", Throwable().stackTrace[1].methodName)
            Log.d("PetPing", methodName + message + filename)
        }
    }
}