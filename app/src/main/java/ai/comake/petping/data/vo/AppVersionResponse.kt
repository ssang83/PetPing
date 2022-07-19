package ai.comake.petping.data.vo

/**
 * petping
 * Class: AppVersionResponse
 * Created by kimjoonsung on 2021/05/12.
 *
 * Description:
 */
data class AppVersionResponse(
    val data: Data,
    val result: Boolean,
    val status: Int
) {
    data class Data(
        val appUpdateVersion: AppUpdateVersion
    ) {
        data class AppUpdateVersion(
            val forceUpdateVerAos: String,
            val forceUpdateVerIos: String,
            val newAppVersionAos: String,
            val newAppVersionIos: String,
            val selectUpdateVerAos: String,
            val selectUpdateVerIos: String
        )
    }
}


