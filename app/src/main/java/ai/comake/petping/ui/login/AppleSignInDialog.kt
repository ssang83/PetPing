package ai.comake.petping.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
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
    private val callBack: ((token:String) -> Unit)? = null
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

            loadUrl(
                "https://appleid.apple.com/auth/authorize" +
                        "?client_id=kr.co.petdoc.auth" +
                        "&redirect_uri=https://auth.petdoc.co.kr/auth/appleapp" +
                        "&response_type=code%20id_token" +
                        "&state=&scope=name%20email" +
                        "&response_mode=form_post"
            )
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
            //petdoc://?userKey=001252.54e066d2f1c04beba9267648f48c0a7f.0934
            url?.let {
                if (it.startsWith("petdoc")) {
                    val tempUrl = it.split("=")
                    callBack?.invoke(tempUrl[1])
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