package ai.comake.petping.ui.etc

import ai.comake.petping.R
import ai.comake.petping.data.vo.CIConfig
import ai.comake.petping.databinding.FragmentCertWebBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setNavigationResult
import ai.comake.petping.util.updateWhiteStatusBar
import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs

/**
 * android-petping-2
 * Class: CertWebFragment
 * Created by cliff on 2022/03/18.
 *
 * Description:
 */
class CertWebFragment : BaseFragment<FragmentCertWebBinding>(FragmentCertWebBinding::inflate) {

    private val args: CertWebFragmentArgs by navArgs()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setNavigationResult(null, "key")
            requireActivity().backStack(R.id.nav_main)
        }
    }

    @SuppressLint("JavascriptInterface")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)

        WebView.setWebContentsDebuggingEnabled(true)

        with(binding) {
            webView.apply {
                settings.apply {
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    javaScriptEnabled = true
                    setSupportMultipleWindows(true)
                }

                setBackgroundColor(Color.TRANSPARENT)
                setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

                loadUrl(
                    "https://www.petping.com/petping/cert?name=${
                        args.config.name
                    }&birth=${
                        args.config.birth
                    }&gender=${
                        args.config.gender
                    }"
                )

                addJavascriptInterface(object : WebViewBridge() {
                    override fun CallAndroid(
                        status: String,
                        message: String,
                        phoneNumber: String,
                        ci: String
                    ) {
                        if (status == "200") {
                            val config = CIConfig(
                                phoneNumber = phoneNumber,
                                ci = ci
                            )
                            setNavigationResult(config, "key")
                            requireActivity().backStack(R.id.nav_main)
                        } else {
                            SingleBtnDialog(requireContext(), "휴대폰 본인인증 오류", message) {
                                requireActivity().backStack(R.id.nav_main)
                            }.show()
                        }
                    }

                    override fun CallAndroidCertExit() {
                        requireActivity().backStack(R.id.nav_main)
                    }
                }, "BRIDGE")

                webChromeClient = object : WebChromeClient() {
                    override fun onCreateWindow(
                        view: WebView?,
                        isDialog: Boolean,
                        isUserGesture: Boolean,
                        resultMsg: Message?
                    ): Boolean {
                        if (!isDialog) return false
                        val newWebView = WebView(requireContext())
                        val webSettings = newWebView.settings
                        webSettings.javaScriptEnabled = true

                        val dialog = Dialog(requireContext())
                        dialog.setContentView(newWebView)

                        val params: ViewGroup.LayoutParams = dialog.window?.attributes!!
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT
                        params.height = ViewGroup.LayoutParams.MATCH_PARENT
                        dialog.window?.attributes = params as WindowManager.LayoutParams
                        dialog.show()
                        newWebView.webChromeClient = object : WebChromeClient() {
                            override fun onCloseWindow(window: WebView) {
                                dialog.dismiss()
                            }
                        }

                        newWebView.webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                request: WebResourceRequest
                            ): Boolean {
                                return false
                            }
                        }

                        (resultMsg?.obj as WebView.WebViewTransport).webView = newWebView
                        resultMsg.sendToTarget()
                        return true
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        onBackPressedCallback.remove()
        super.onDestroyView()
    }

    private abstract class WebViewBridge {
        abstract fun CallAndroidCertExit()
        abstract fun CallAndroid(status: String, message: String, phoneNumber: String, ci: String)
    }
}