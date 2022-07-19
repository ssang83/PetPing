package ai.comake.petping.ui.common.webview.event

import ai.comake.petping.AppConstants
import ai.comake.petping.R
import ai.comake.petping.databinding.FragmentEventWebviewBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateWhiteStatusBar
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.greenrobot.eventbus.EventBus

/**
 * android-petping-2
 * Class: EventWebFragment
 * Created by cliff on 2022/02/16.
 *
 * Description:
 */
class EventWebFragment :
    BaseFragment<FragmentEventWebviewBinding>(FragmentEventWebviewBinding::inflate) {

    private val args:EventWebFragmentArgs by navArgs()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            LogUtil.log("TAG", "handleOnBackPressed")
            goBack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)

        with(binding) {

            header.btnBack.setSafeOnClickListener { goBack() }

            webView.apply {
                settings.apply {
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    setAppCacheEnabled(true)
                    loadsImagesAutomatically = true
                    blockNetworkImage = false
                }

                setBackgroundColor(Color.TRANSPARENT)
                setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        LogUtil.log("TAG", "onPageFinished url : $url")
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        LogUtil.log("TAG","shouldOverrideUrlLoading url : ${request?.url}")
                        return super.shouldOverrideUrlLoading(view, request)
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        LogUtil.log("TAG", "onPageStarted url : $url")
                    }
                }

                addJavascriptInterface(object : BackKeyInterface() {
                    @JavascriptInterface
                    override fun GoToLogin() {
                        SingleBtnDialog(
                            requireContext(),
                            getString(R.string.logout_title),
                            getString(R.string.logout_desc)
                        ) {
                            requireActivity().findNavController(R.id.nav_main)
                                .navigate(R.id.action_eventWebFragment_to_loginGraph)
                        }.show()
                    }

                    @JavascriptInterface
                    override fun Exit() {
                        requireActivity().backStack(R.id.nav_main)
                    }

                    @JavascriptInterface
                    override fun GoToBack() {
                        goBack()
                    }
                }, "BackKeyInterface")

                addJavascriptInterface(object : CommonInterface() {
                    @JavascriptInterface
                    override fun GoToMissionPet() {
                        requireActivity().findNavController(R.id.nav_main)
                            .navigate(R.id.action_eventWebFragment_to_missionPetFragment)
                    }

                    @JavascriptInterface
                    override fun GoToInsuranceSetting() {
                        requireActivity().findNavController(R.id.nav_main)
                            .navigate(R.id.action_eventWebFragment_to_insuranceHistoryFragment)
                    }

                    @JavascriptInterface
                    override fun GoToRewardDetail() {
                        requireActivity().findNavController(R.id.nav_main)
                            .navigate(R.id.action_eventWebFragment_to_rewardHistoryFragment)
                    }

                    @JavascriptInterface
                    override fun GoToSetting() {
                        requireActivity().findNavController(R.id.nav_main)
                            .navigate(R.id.action_eventWebFragment_to_appSettingFragment)
                    }

                    @JavascriptInterface
                    override fun GoToFAQ() {
                        requireActivity().findNavController(R.id.nav_main)
                            .navigate(R.id.action_eventWebFragment_to_questionFragment)
                    }

                    @JavascriptInterface
                    override fun GoToNotice() {
                        requireActivity().findNavController(R.id.nav_main)
                            .navigate(R.id.action_eventWebFragment_to_noticeFragment)
                    }

                    @JavascriptInterface
                    override fun GoToOutLink(url: String) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        requireActivity().startActivity(intent)
                    }

                    @JavascriptInterface
                    override fun GoToShare(url:String) {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, url)
                            type = "text/plain"
                        }

                        val shareIntent = Intent.createChooser(sendIntent, null)
                        requireActivity().startActivity(shareIntent)
                    }

                    @JavascriptInterface
                    override fun GoToApp() {
                        val isAppInstalled = appInstalledOrNot(requireActivity().packageName)
                        if (isAppInstalled) {
                            val LaunchIntent = requireActivity().packageManager
                                .getLaunchIntentForPackage(requireActivity().packageName)
                            startActivity(LaunchIntent)
                        } else {
                            startActivity(Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("market://details?id=${requireActivity().packageName}")
                            })
                        }
                    }
                }, "CommonInterface")

                var token = hashMapOf<String, String>()
                if (AppConstants.AUTH_KEY.isNotEmpty()) {
                    token = HashMap<String, String>().apply {
                        put("petping_token", AppConstants.AUTH_KEY.split(" ")[1])
                    }
                }

                try {
                    loadUrl(args.config.url, token)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }
    }

    private fun goBack() {
        when {
            binding.webView.canGoBack() -> binding.webView.goBack()
            else -> requireActivity().backStack(R.id.nav_main)
        }
    }

    /**
     * 펫핑 앱 설치 유무 판단한다.
     *
     * @param uri
     * @return
     */
    private fun appInstalledOrNot(uri: String) : Boolean {
        val pm = requireActivity().packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }

        return false
    }

    private abstract class CommonInterface {
        abstract fun GoToMissionPet()
        abstract fun GoToInsuranceSetting()
        abstract fun GoToRewardDetail()
        abstract fun GoToSetting()
        abstract fun GoToFAQ()
        abstract fun GoToNotice()
        abstract fun GoToOutLink(url: String)
        abstract fun GoToShare(url: String)
        abstract fun GoToApp()
    }

    private abstract class BackKeyInterface {
        abstract fun GoToLogin()
        abstract fun Exit()
        abstract fun GoToBack()
    }
}