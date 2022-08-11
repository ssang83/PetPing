package ai.comake.petping.ui.common.webview.content

import ai.comake.petping.AppConstants
import ai.comake.petping.R
import ai.comake.petping.data.vo.MenuLink
import ai.comake.petping.databinding.FragmentContentWebviewBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.DoubleBtnDialog
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.*
import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File
import java.net.URISyntaxException

/**
 * android-petping-2
 * Class: ContentsWebFragment
 * Created by cliff on 2022/02/16.
 *
 * Description:
 */
class ContentsWebFragment :
    BaseFragment<FragmentContentWebviewBinding>(FragmentContentWebviewBinding::inflate) {

    private val args:ContentsWebFragmentArgs by navArgs()

    // 웹뷰 파일 첨부 진입 시 사용되는 변수들..
    var uploadMessage: ValueCallback<Array<Uri>>? = null
    private var mUploadMessage: ValueCallback<Uri>? = null
    private val REQUEST_SELECT_FILE = 100
    private val FILECHOOSER_RESULTCODE = 1

    private var networkPopup: Dialog? = null
    private var downloadImageUrl = ""

    private val WRITE_EXTERNAL_STORAGE = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val selectFileChooser: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult() // ◀ StartActivityForResult 처리를 담당
    ) { activityResult ->
        if (uploadMessage == null) return@registerForActivityResult
        uploadMessage?.onReceiveValue(
            WebChromeClient.FileChooserParams.parseResult(
                activityResult.resultCode,
                activityResult.data
            )
        )
        uploadMessage = null
    }

    val permReqLuncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.entries.all { it.value }
        if (isGranted) {
            LogUtil.log("권한 있음")
            downloadImage()
        } else {
            LogUtil.log("권한 없음")
            SingleBtnDialog(
                requireContext(),
                getString(R.string.permission_error_title),
                getString(R.string.permission_error_desc),
            ) {}.show()
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            super.onLost(network)
            checkNetwork()
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            LogUtil.log("TAG", "handleOnBackPressed")
            goBack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.config.insurance) {
            requireActivity().window.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.color_f7f7f7)
        } else {
            updateWhiteStatusBar(requireActivity().window)
        }

        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)

        with(binding) {

            header.btnBack.setSafeOnClickListener { goBack() }

            btnClose.setSafeOnClickListener {
                if (args.config.insuranceType == "join") {
                    DoubleBtnDialog(
                        requireContext(),
                        "가입 신청을 종료하시겠어요?",
                        "가입 신청을 종료하면 앱 화면으로 돌아가요.",
                        okCallback = { requireActivity().backStack(R.id.nav_main) }
                    ).show()
                } else {
                    DoubleBtnDialog(
                        requireContext(),
                        "청구 신청을 종료하시겠어요?",
                        "청구 신청을 종료하면 앱 화면으로 돌아가요.",
                        okCallback = { requireActivity().backStack(R.id.nav_main) }
                    ).show()
                }
            }

            try {
                val cookieManager = CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(webView, true)
            } catch (e:Exception) {}

            webView.apply {
                settings.apply {
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    setAppCacheEnabled(true)
                    loadsImagesAutomatically = true
                    blockNetworkImage = false
                    allowContentAccess = true
                    allowFileAccess = true
                }

                setBackgroundColor(Color.TRANSPARENT)
                setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

                var token = hashMapOf<String, String>()
                if (AppConstants.AUTH_KEY.isNotEmpty()) {
                    token = HashMap<String, String>().apply {
                        put("petping_token", AppConstants.AUTH_KEY.split(" ")[1])
                    }
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        LogUtil.log("TAG", "onPageFinished url : $url")
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        LogUtil.log("TAG", "shouldOverrideUrlLoading url : ${request?.url}")
                        val url = request?.url?.toString()
                        if(url != null && URLUtil.isNetworkUrl(url).not() && URLUtil.isJavaScriptUrl(url).not()) {
                            val uri = try {
                                Uri.parse(url)
                            } catch (e: Exception) {
                                return false
                            }

                            return when (uri.scheme) {
                                "intent" -> {
                                    startSchemeIntent(requireContext(), url)
                                }
                                else -> {
                                    return try {
                                        startActivity(Intent(Intent.ACTION_VIEW, uri))
                                        true
                                    } catch (e: Exception) {
                                        false
                                    }
                                }
                            }
                        } else {
                            view?.loadUrl(url.toString())
                        }
                        return true
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        LogUtil.log("TAG", "onPageStarted url : $url")
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onShowFileChooser(
                        webView: WebView?,
                        filePathCallback: ValueCallback<Array<Uri>>?,
                        fileChooserParams: FileChooserParams?
                    ): Boolean {
                        uploadMessage = filePathCallback
                        val intent = fileChooserParams!!.createIntent()
                        try {
                            requireActivity().startActivityForResult(intent, REQUEST_SELECT_FILE)
//                            selectFileChooser.launch(intent)
                        } catch (e: ActivityNotFoundException) {
                            uploadMessage = null
                            Toast.makeText(
                                requireContext(),
                                "Cannot Open File Chooser",
                                Toast.LENGTH_LONG
                            ).show()
                            return false
                        }
                        return true
                    }
                }

                addJavascriptInterface(object : BackKeyInterface() {
                    @JavascriptInterface
                    override fun GoToBack() {
                        if (args.config.fromWelcomKit) {
                            requireActivity().backStack(R.id.nav_main)
                        } else {
                            goBack()
                        }
                    }

                    @JavascriptInterface
                    override fun GoToLogin() {
                        SingleBtnDialog(
                            requireContext(),
                            getString(R.string.logout_title),
                            getString(R.string.logout_desc)
                        ) {
                            requireActivity().findNavController(R.id.nav_main)
                                .navigate(R.id.action_contentsWebFragment_to_loginGraph)
                        }.show()
                    }

                    @JavascriptInterface
                    override fun Exit() {
                        requireActivity().backStack(R.id.nav_main)
                    }
                }, "BackKeyInterface")

                when {
                    args.config.fromWelcomKit == true -> {
                        binding.header.root.visibility = View.VISIBLE
                        binding.insuranceLayer.visibility = View.GONE

                        addJavascriptInterface(object : WelcomeKitInterface() {
                            @JavascriptInterface
                            override fun GoToEtc() {
                                if (args.config.fromHome) {
                                    requireActivity().findNavController(R.id.nav_main)
                                        .navigate(R.id.action_contentsWebFragment_to_etcFragment)
                                } else {
                                    requireActivity().backStack(R.id.nav_main)
                                }
                            }

                            @JavascriptInterface
                            override fun GoToWalk() {
                                val arg = MenuLink.PetPing("walk")
                                requireActivity().findNavController(R.id.nav_main)
                                    .navigate(ContentsWebFragmentDirections.actionContentsWebFragmentToHomeScreen().setMenulink(arg))
                            }
                        }, "WelcomeKitInterface")
                    }
                    args.config.rewardCashBack == true -> {
                        binding.header.root.visibility = View.VISIBLE
                        binding.insuranceLayer.visibility = View.GONE

                        addJavascriptInterface(object : RewardInterface() {
                            @JavascriptInterface
                            override fun GoToRewardDetail() {
                                requireActivity().findNavController(R.id.nav_main)
                                    .navigate(R.id.action_contentsWebFragment_to_rewardHistoryFragment)
                            }

                            @JavascriptInterface
                            override fun Exit() {
                                requireActivity().backStack(R.id.nav_main)
                            }
                        }, "RewardInterface")
                    }
                    args.config.insurance == true -> {
                        binding.header.root.visibility = View.GONE
                        binding.insuranceLayer.visibility = View.VISIBLE
                        binding.title.apply {
                            if (args.config.insuranceType == "join") {
                                text = "펫보험 가입"
                            } else {
                                text = "펫보험 청구 신청"
                            }
                        }

                        addJavascriptInterface(object : InsuranceInterface() {
                            @JavascriptInterface
                            override fun Exit() {
                                requireActivity().setResult(Activity.RESULT_OK)
                                requireActivity().backStack(R.id.nav_main)
                            }

                            @JavascriptInterface
                            override fun GoToProfile(petId: Int) {
                                requireActivity().findNavController(R.id.nav_main)
                                    .navigate(R.id.action_contentsWebFragment_to_profileGraph)
                            }

                            @JavascriptInterface
                            override fun GoToInsuranceList() {
                                requireActivity().findNavController(R.id.nav_main)
                                    .navigate(R.id.action_contentsWebFragment_to_insuranceHistoryFragment)
                            }

                            @JavascriptInterface
                            override fun CallDownloadImage(url: String) {
                                downloadImageUrl = url
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                    requestStoragePermission()
                                } else {
                                    downloadImage()
                                }
                            }

                            override fun GoToInsurancePolicy(url: String) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                requireActivity().startActivity(intent)
                            }
                        }, "PetInsuranceInterface")
                    }
                    else -> {
                        binding.header.root.visibility = View.VISIBLE
                        binding.insuranceLayer.visibility = View.GONE

                        addJavascriptInterface(object : CommonInterface() {
                            @JavascriptInterface
                            override fun GoToMissionPet() {
                                requireActivity().findNavController(R.id.nav_main)
                                    .navigate(R.id.action_contentsWebFragment_to_missionPetFragment)
                            }

                            @JavascriptInterface
                            override fun GoToInsuranceSetting() {
                                requireActivity().findNavController(R.id.nav_main)
                                    .navigate(R.id.action_contentsWebFragment_to_insuranceHistoryFragment)
                            }

                            @JavascriptInterface
                            override fun GoToRewardDetail() {
                                requireActivity().findNavController(R.id.nav_main)
                                    .navigate(R.id.action_contentsWebFragment_to_rewardHistoryFragment)
                            }

                            @JavascriptInterface
                            override fun GoToSetting() {
                                requireActivity().findNavController(R.id.nav_main)
                                    .navigate(R.id.action_contentsWebFragment_to_appSettingFragment)
                            }

                            @JavascriptInterface
                            override fun GoToFAQ() {
                                requireActivity().findNavController(R.id.nav_main)
                                    .navigate(R.id.action_contentsWebFragment_to_questionFragment)
                            }

                            @JavascriptInterface
                            override fun GoToNotice() {
                                requireActivity().findNavController(R.id.nav_main)
                                    .navigate(R.id.action_contentsWebFragment_to_noticeFragment)
                            }

                            @JavascriptInterface
                            override fun GoToOutLink(url: String) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                requireActivity().startActivity(intent)
                            }

                            @JavascriptInterface
                            override fun GoToShare(url: String) {
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

                            @JavascriptInterface
                            override fun Exit() {
                                requireActivity().backStack(R.id.nav_main)
                            }
                        }, "CommonInterface")
                    }
                }

                loadUrl(args.config.url, token)
            }
        }

        networkPopup = DoubleBtnDialog(
            requireContext(),
            getString(R.string.network_error_title),
            getString(R.string.network_error_desc),
            false,
            getString(R.string.finish_app),
            getString(R.string.retry_connect),
            {
                requireActivity().backStack(R.id.nav_main)
            },
            {
                checkNetwork()
            }
        )
    }

    override fun onResume() {
        super.onResume()
        checkNetwork()

        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            networkCallback
        )
    }

    override fun onStop() {
        super.onStop()
        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkCallback)
        onBackPressedCallback.remove()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            REQUEST_SELECT_FILE -> {
                if (uploadMessage == null) return
                uploadMessage?.onReceiveValue(
                    WebChromeClient.FileChooserParams.parseResult(
                        resultCode,
                        intent
                    )
                )
                uploadMessage = null
            }

            FILECHOOSER_RESULTCODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (uploadMessage == null) return
                    val result = if (intent == null) null else intent.data
                    mUploadMessage?.onReceiveValue(result)
                    mUploadMessage = null
                }
            }
        }
    }

    private fun checkNetwork() {
        if (NetworkUtil.isNetworkConnected(requireContext())
                .not() && networkPopup?.isShowing!!.not()
        ) {
            networkPopup?.show()
        }
    }

    private fun goBack() {
        when {
            binding.webView.canGoBack() -> binding.webView.goBack()
            else -> requireActivity().backStack(R.id.nav_main)
        }
    }

    private fun requestStoragePermission() {
        when {
            hasPermission(requireContext(), WRITE_EXTERNAL_STORAGE) -> {
                LogUtil.log("이미 권한 있음")
                downloadImage()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                LogUtil.log("한번 거절")
                permReqLuncher.launch(WRITE_EXTERNAL_STORAGE)
            }
            else -> {
                permReqLuncher.launch(WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun downloadImage() {
        Glide.with(requireActivity()).asBitmap().load(downloadImageUrl)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    val filePath = requireContext().copyBitmapToFile(
                        resource,
                        "fileName.png"
                    )

                    if (filePath != null) {
                        val file = File(filePath.path!!)
                        MediaScannerConnection.scanFile(
                            requireContext(),
                            arrayOf(file.absolutePath),
                            arrayOf("image/jpg"),
                            { path, uri ->
                                LogUtil.log("TAG", "path : $path, scan success")
                            }
                        )
                    } else {
                        SingleBtnDialog(
                            requireContext(),
                            getString(R.string.error),
                            getString(R.string.file_save_fail),
                        ) {}.show()
                    }
                }
            })
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
        } catch (e: PackageManager.NameNotFoundException) { }

        return false
    }

    private fun startSchemeIntent(context: Context, url: String): Boolean {
        val schemeIntent: Intent = try {
            Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
        } catch (e: URISyntaxException) {
            return false
        }
        try {
            context.startActivity(schemeIntent)
            return true
        } catch (e: ActivityNotFoundException) {
            val packageName = schemeIntent.getPackage()

            if (!packageName.isNullOrBlank()) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
                return true
            }
        }
        return false
    }

    private abstract class WebViewBridge {
        abstract fun CallAndroidPingshopExit()
        abstract fun CallDownloadTicket(url: String)
    }

    private abstract class WelcomeKitInterface {
        abstract fun GoToWalk()
        abstract fun GoToEtc()
    }

    private abstract class RewardInterface {
        abstract fun GoToRewardDetail()
        abstract fun Exit()
    }

    private abstract class BackKeyInterface {
        abstract fun GoToBack()
        abstract fun GoToLogin()
        abstract fun Exit()
    }

    private abstract class InsuranceInterface {
        abstract fun Exit()
        abstract fun GoToProfile(petId:Int)
        abstract fun GoToInsuranceList()
        abstract fun CallDownloadImage(url: String)
        abstract fun GoToInsurancePolicy(url: String)
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
        abstract fun Exit()
    }
}