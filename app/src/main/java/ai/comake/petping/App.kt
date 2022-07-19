package ai.comake.petping

import ai.comake.petping.AppConstants.clientId
import ai.comake.petping.AppConstants.clientName
import ai.comake.petping.AppConstants.clientSecret
import ai.comake.petping.data.preference.Preference
import ai.comake.petping.data.preference.PreferenceImpl
import android.app.Application
import android.content.Context
import android.net.Uri
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.AirbridgeConfig
import co.ab180.airbridge.OnDeferredDeeplinkReceiveListener
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    lateinit var context : Context

    companion object {

        @JvmStatic
        fun getPrefernece(context: Context) : Preference {
            return PreferenceImpl(context, "pref_petping")
        }
    }

    override fun onCreate() {
        super.onCreate()

        context = this

        initNaverLogin(this)
        initKakaoLogin(this)
        initAirbridge()
    }

    private fun initNaverLogin(context: Context){
        NaverIdLoginSDK.apply {
            showDevelopersLog(true)
            initialize(context, clientId, clientSecret, clientName)
            isShowMarketLink = true
            isShowBottomTab = true
        }
    }

    private fun initKakaoLogin(context: Context){
        KakaoSdk.init(context, "f7e0a861130e653a87064f7529697d6b", BuildConfig.DEBUG)
    }

    private fun initAirbridge(){
        val config = AirbridgeConfig.Builder("monkey", "cd703209dddc435c8b63d33545a6a719")
            // android identifier
            .setAppMarketIdentifier("playStore")
            // location
            .setLocationCollectionEnabled(true)
            // deep link
            .setOnDeferredDeeplinkReceiveListener(object : OnDeferredDeeplinkReceiveListener {
                override fun shouldLaunchReceivedDeferredDeeplink(uri: Uri): Boolean {
                    // If you want to open the target activity, please return true otherwise false
                    // Default returning value = true
                    return true
                }
            })
            .setTrackAirbridgeLinkOnly(true)
            .setUserInfoHashEnabled(false)
            .setSessionTimeoutSeconds(300)
            .setFacebookDeferredAppLinkEnabled(true)
            .build()
        Airbridge.init(this, config)
    }
}