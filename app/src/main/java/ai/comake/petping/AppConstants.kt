package ai.comake.petping

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.provider.Settings

object AppConstants {

    var AUTH_KEY = ""
    var ID = ""
    var EMAIL = ""
    var NAME = ""
    var PHONE = ""
    var UUID = ""
    var FCM_TOKEN = ""

    // 고도몰 웹뷰 정보
    var GODOMALL_KEYWORD_COOKIE = ""
    var SHOP_SESSION_NAME = ""
    var SHOP_SESSION_KEY = ""
    var SHOP_UNIQUE_ID = ""
    var SHOP_EMP_NO = -1

    var LOGIN_HEADER_IS_VISIBLE = true
    var PROFILE_HEADER_IS_VISIBLE = true

    var closeMissionAlert = false
    var closeWelcomeKitAlert = false
    var closeMissionPetAlert = false

    //기본 위도 경도 좌표 (시청)
    var defaultLocation = Location("CityHall").apply {
        latitude = 37.566573
        longitude = 126.978179
    }

    var lastLocation = defaultLocation

    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    val colorList: List<String> = listOf(
        "#f9ecd7",
        "#f7c893",
        "#f09e58",
        "#c6753f",
        "#9f653f",
        "#7c604d",
        "#ffffff",
        "#dcdcdc",
        "#ababab",
        "#737373",
        "#636363",
        "#ffee35",
        "#7ee4dd",
        "#5396fa",
        "#a181f7",
        "#f773ad",
        "#ff4857",
        "#ff7a35"
    )

    val colorMap: Map<String, Int> = mapOf(
        Pair("#f9ecd7", 0),
        Pair("#f7c893", 1),
        Pair("#f09e58", 2),
        Pair("#c6753f", 3),
        Pair("#9f653f", 4),
        Pair("#7c604d", 5),
        Pair("#ffffff", 6),
        Pair("#dcdcdc", 7),
        Pair("#ababab", 8),
        Pair("#737373", 9),
        Pair("#636363", 10),
        Pair("#ffee35", 11),
        Pair("#7ee4dd", 12),
        Pair("#5396fa", 13),
        Pair("#a181f7", 14),
        Pair("#f773ad", 15),
        Pair("#ff4857", 16),
        Pair("#ff7a35", 17)
    )
}