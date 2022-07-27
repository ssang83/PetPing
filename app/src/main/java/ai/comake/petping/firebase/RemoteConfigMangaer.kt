package ai.comake.petping.firebase

import ai.comake.petping.BuildConfig
import android.app.Activity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

/**
 * android-petping-2
 * Class: RemoteConfigManager
 * Created by cliff on 2022/07/27.
 *
 * Description:
 */
class RemoteConfigMangaer {

    private val ONE_MIN = 60
    private val THREE_MIN = 60 * 3
    private val TEN_MIN = 60 * 10
    private val ONE_DAY_SECONDS = 24 * 60 * 60

    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
    private lateinit var configSetting: FirebaseRemoteConfigSettings

    fun init() {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        if (BuildConfig.DEBUG) {
            configSetting = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(ONE_MIN.toLong())
                .build()
        } else {
            configSetting = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(THREE_MIN.toLong())
                .build()
        }

        firebaseRemoteConfig.setConfigSettingsAsync(configSetting)
    }

    fun fetchAndActivate(
        activity: Activity,
        onCompleteListener: OnCompleteListener<Boolean>
    ) {
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(activity, onCompleteListener)
    }

    fun getString(key:String) = firebaseRemoteConfig.getString(key)

    companion object {

        @Volatile
        private var instance:RemoteConfigMangaer? = null

        fun getInstance(): RemoteConfigMangaer {
            if (instance == null) {
                instance = RemoteConfigMangaer()
            }

            return instance as RemoteConfigMangaer
        }
    }
}