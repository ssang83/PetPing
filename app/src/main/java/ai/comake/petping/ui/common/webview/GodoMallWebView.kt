package ai.comake.petping.ui.common.webview

import ai.comake.petping.AppConstants
import ai.comake.petping.BuildConfig
import ai.comake.petping.data.vo.GodoMallConfig
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.webkit.*
import java.net.URISyntaxException

/**
 * android-petping-2
 * Class: GodoMallWebView
 * Created by cliff on 2022/02/09.
 *
 * Description:
 */
class GodoMallWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    private var listener: GodoMallWebViewListener? = null
    private var cookieManager: CookieManager
    private var godoProductUrl = ""

    init {
        settings.apply {
            loadWithOverviewMode = true
            useWideViewPort = true
            @SuppressWarnings("SetJavaScriptEnabled")
            javaScriptEnabled = true
            domStorageEnabled = true
            allowContentAccess = true
            allowFileAccess = true
            setAppCacheEnabled(true)
            javaScriptCanOpenWindowsAutomatically = true
            cacheMode = WebSettings.LOAD_DEFAULT
            setSupportMultipleWindows(true)
            userAgentString = "${userAgentString}APP_PETPING_AOS"
        }

        cookieManager = CookieManager.getInstance()
        cookieManager.apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(this@GodoMallWebView, true)
            removeAllCookies(null)
        }

        setBackgroundColor(Color.TRANSPARENT)
        setLayerType(LAYER_TYPE_HARDWARE, null)
        setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        setNetworkAvailable(true)

        isFocusable = true
        isFocusableInTouchMode = true

        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val cookies = cookieManager.getCookie(url.toString())
                if (cookies != null) {
                    val temp = cookies.split(";")
                    for (ar1 in temp) {
                        if (ar1.contains("recentKeywordMobile")) {
                            AppConstants.GODOMALL_KEYWORD_COOKIE = ar1
                            break
                        }
                    }
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (godoProductUrl.isNotEmpty()) {
                    view?.loadUrl(godoProductUrl)
                    godoProductUrl = ""
                }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url?.toString()
                try {
                    if((url != null && URLUtil.isNetworkUrl(url).not() && URLUtil.isJavaScriptUrl(url).not())
                        && (url.startsWith("intent:")
                                || url.contains("market://")
                                || url.contains("vguard")
                                || url.contains("droidxantivirus")
                                || url.contains("v3mobile")
                                || url.contains(".apk")
                                || url.contains("mvaccine")
                                || url.contains("smartwall://")
                                || url.contains("http://m.ahnlab.com/kr/site/download")) ) {

                        var intent: Intent? = null

                        try {
                            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        } catch (e: URISyntaxException) {
                            println("error : " + e.printStackTrace())
                        }

                        if( view?.context?.packageManager?.resolveActivity(intent!!, 0) == null ) {
                            val pkgName = intent?.`package`
                            if( pkgName != null ) {
                                val uri = Uri.parse("market://search?q=pname:" + pkgName)
                                intent = Intent(Intent.ACTION_VIEW, uri)
                                view?.context?.startActivity(intent)
                            }
                        } else {
                            val uri = Uri.parse(intent?.dataString)
                            intent = Intent(Intent.ACTION_VIEW, uri)
                            view.context?.startActivity(intent)
                        }
                    } else {
                        view?.loadUrl(url.toString())
                    }
                } catch (e: Exception) {
                    return false
                }

                return true
            }
        }

        addJavascriptInterface(object : GodoMallInterface() {
            @JavascriptInterface
            override fun goToLogin() {
                listener?.goToLogin()
            }
        }, "GodoMallInterface")
    }

    fun load(config:GodoMallConfig) {
        this.godoProductUrl = config.productUrl

        val cookieString = "${AppConstants.SHOP_SESSION_NAME}=${AppConstants.SHOP_SESSION_KEY}; path=/; domain=m.shop.petping.com;"
        cookieManager.setCookie(config.url, cookieString)
        if (AppConstants.GODOMALL_KEYWORD_COOKIE.isNotEmpty()) {
            val keywordCookie = "${AppConstants.GODOMALL_KEYWORD_COOKIE}; path=/; domain=.shop.petping.com;"
            cookieManager.setCookie(config.url, keywordCookie)
        }

        loadUrl(config.url)
    }

    fun setGodoMallWebViewListener(listener: GodoMallWebViewListener) {
        this.listener = listener
    }

    private abstract class GodoMallInterface {
        abstract fun goToLogin()
    }

    interface GodoMallWebViewListener {
        fun goToLogin()
    }
}