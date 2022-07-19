package ai.comake.petping.util

fun Int.toHHMMSSFormat(): String {
    val diff = this
    val seconds = (diff / 1000) % 60
    val minutes = (diff / (60 * 1000))
    return "%02d:%02d".format(minutes, seconds)
}
