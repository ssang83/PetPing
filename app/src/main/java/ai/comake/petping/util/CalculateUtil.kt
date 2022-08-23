package ai.comake.petping.util

import ai.comake.petping.data.vo.WalkPath
import ai.comake.petping.ui.home.walk.PAUSE_OVER_TIME_MINUTE
import ai.comake.petping.ui.home.walk.STOP_WALK_OVER_TIME_MINUTE
import ai.comake.petping.ui.home.walk.WALK_MAX_SPEED_MS
import android.text.InputFilter
import android.text.Spanned
import java.util.concurrent.TimeUnit

fun Float.format(digits: Int) = "%.${digits}f".format(this)

fun List<WalkPath>.calculateWalkDistance(): Float {
    var ret = 0.0f
    if (this.size >= 2) {
        for (index in 0 until this.size - 1) {
            ret += this[index].location.distanceTo(this[index + 1].location)
        }
    }
    return ret / 1000.0f
}

fun Long.toWalkTimeFormat(): String {
    val diff = this

    val seconds = (diff / 1000) % 60
    val minutes = (diff / (60 * 1000)) % 60
    val hours = (diff / (60 * 60 * 1000)) % 24
    return when {
        hours == 0L -> "%02d:%02d".format(minutes, seconds)
        diff >= 18000000 -> "05:00:00"
        else -> "%02d:%02d:%02d".format(hours, minutes, seconds)
    }
}

fun Long.toHHMMSSFormat(): String {
    val diff = this
    val seconds = (diff / 1000) % 60
    val minutes = (diff / (60 * 1000)) % 60
    val hours = (diff / (60 * 60 * 1000)) % 24
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

//산책중 움직임이 없는 시점
fun hasOverTimePassedSinceTheStopWalk(walkPathList: List<WalkPath>, walkTime: Long): Boolean {
    val copyList = walkPathList.toMutableList()
    if (copyList.size > 1) {
        val startPoint = copyList[0].location
        val distance = copyList.map { endPoint ->
            startPoint.distanceTo(endPoint.location).toInt()
        }.maxOrNull() ?: 0

        if (walkTime > TimeUnit.MINUTES.toMillis(
                STOP_WALK_OVER_TIME_MINUTE
            )
        ) {
            if (distance < 50) {
                return true
            }
        }
    } else {
        if (walkTime > TimeUnit.MINUTES.toMillis(
                STOP_WALK_OVER_TIME_MINUTE
            )
        ) {
            if (copyList.size <= 1) {
                return true
            }
        }
    }
    return false
}

//제한속도 초과
fun hasOverWalkSpeed(walkPathList: List<WalkPath>): Boolean {
    val copyList = walkPathList.toMutableList()
    if (copyList.size >= 5) {
        val checkOverSpeed = copyList.zipWithNext { startPoint, endPoint ->
            val distance = endPoint.location.distanceTo(startPoint.location)
            val time =
                TimeUnit.MILLISECONDS.toSeconds(endPoint.location.time - startPoint.location.time)
            distance / time
        }.all { ms ->
            ms > WALK_MAX_SPEED_MS
        }

        return checkOverSpeed
    }
    return false
}

//산책 최대시간 제한
fun hasOverMaxWalkTime(totalTime : Long): Boolean {
    return totalTime >= 18000000
}

//산책 일사중지 버튼 클릭 후 10분 경과
fun hasOverTimePassedSinceThePauseWalk(pauseTime : Long): Boolean {
    return pauseTime > TimeUnit.MINUTES.toMillis(PAUSE_OVER_TIME_MINUTE)
}

//fun List<WalkPath>.calculateWalkDistance(): Float {
//    var ret = 0.0f
//    if (this.size >= 2) {
//        for (index in 0 until this.size - 1) {
//            val distance = FloatArray(3)
//            if (this[index].state == 3 || this[index + 1].state == 3) continue
//            Location.distanceBetween(
//                this[index].location.latitude,
//                this[index].location.longitude,
//                this[index + 1].location.latitude,
//                this[index + 1].location.longitude,
//                distance
//            )
//            ret += distance[0]
//        }
//    }
//    return ret / 1000.0f
//}

/**
 * 몸무게 소수점 제한 필터 class
 *
 * @property decimalDigits
 */
class MyDecimalDigitsInputFilter
/**
 * Constructor.
 *
 * @param decimalDigits maximum decimal digits
 */(private val decimalDigits: Int) : InputFilter {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        var dotPos = -1
        val len = dest.length
        for (i in 0 until len) {
            val c = dest[i]
            if (c == '.' || c == ',') {
                dotPos = i
                break
            }
        }

        if (dotPos >= 0) {
            // protects against many dots
            if (source == "." || source == ",") {
                return ""
            }
            // if the text is entered before the dot
            if (dend <= dotPos && len < 5) {
                return null
            }
            if (len - dotPos > decimalDigits) {
                return ""
            }
        } else {
            if (source == "." || source == "," || source.toString() == ".") {
                return null
            }

            if (len + 1 > 3) {
                return ""
            }
        }

        return null
    }
}