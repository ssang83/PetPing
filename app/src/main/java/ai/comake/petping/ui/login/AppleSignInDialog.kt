package ai.comake.petping.ui.login

import ai.comake.petping.data.vo.AppleLoginConfig
import ai.comake.petping.util.LogUtil
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatDialog

/**
 * android-petping-2
 * Class: AppleSignInDialog
 * Created by cliff on 2022/03/10.
 *
 * Description:
 */
class AppleSignInDialog(
    context: Context,
    private val callBack: ((config:AppleLoginConfig) -> Unit)? = null
) : AppCompatDialog(context) {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(context)
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false

        webView.apply {
            settings.apply {
                javaScriptEnabled = true
            }

            webViewClient = AppleWebClient()

            loadUrl("https://dev.petping.com/oauth/apple/login")
        }

        setContentView(webView)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    inner class AppleWebClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            view?.loadUrl(request?.url.toString())
            return true
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            //petpingapple://?email=devyeony@gmail.com
            // &authWord=000994.3da820954aad4215bf6b5378a0f781ad.0146
            // &authorizationCode=ejxmKhGGg+K3SvwFWgCeJnD/I71ouVAtC77N2bLN3ItAfhNez0j3X/N8nJFqTLuJjUArS61QPLf5XvYVC5t76w==
            url?.let {
                if (it.startsWith("petpingapple")) {
                    val uri = Uri.parse(it)
                    LogUtil.log("email : ${uri.getQueryParameter("email")}, authWord : ${uri.getQueryParameter("authWord")}, authorizationCode : ${uri.getQueryParameter("authorizationCode")}")
                    val config = AppleLoginConfig(
                        email = uri.getQueryParameter("email") ?: "",
                        authWord = uri.getQueryParameter("authWord") ?: "",
                        authCode = uri.getQueryParameter("authorizationCode") ?: ""
                    )
                    callBack?.invoke(config)
                    dismiss()
                }
            }

            val displayRect = Rect()
            val window = window
            window?.decorView?.getWindowVisibleDisplayFrame(displayRect)

            // Set height of the Dialog to 90% of the screen
            val layoutParams = view?.layoutParams
            layoutParams?.height = (displayRect.height() * 0.9f).toInt()
            view?.layoutParams = layoutParams
        }
    }
}