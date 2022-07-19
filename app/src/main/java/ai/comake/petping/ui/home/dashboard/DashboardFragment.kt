package ai.comake.petping.ui.home.dashboard

import ai.comake.petping.AirbridgeManager
import ai.comake.petping.AppConstants
import ai.comake.petping.R
import ai.comake.petping.data.vo.DashboardAnimationInfo
import ai.comake.petping.data.vo.PetProfileConfig
import ai.comake.petping.data.vo.WebConfig
import ai.comake.petping.databinding.FragmentDashboardBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.common.dialog.HomePopupDialogFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.ui.home.HomeFragmentDirections
import ai.comake.petping.ui.home.HomeShareViewModel
import ai.comake.petping.util.*
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.model.SemanticAttributes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private val homeShareViewModel: HomeShareViewModel by activityViewModels()

    private var pageLoaded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getPingTip()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.log("TAG","")
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        with(viewModel) {

            getPetList()

            petListSuccess.observeEvent(viewLifecycleOwner) {
                // Location 가져오는 로직은 추후에 추가 예정....
                getDashboard(
                    requireContext(),
                    AppConstants.lastLocation.latitude,
                    AppConstants.lastLocation.longitude
                )
            }

            profileHeaderVisibleEvent.observeEvent(viewLifecycleOwner) { isVisible ->
                if (isVisible || AppConstants.LOGIN_HEADER_IS_VISIBLE) {
                    updateBlackStatusBar(requireActivity().window)
                } else {
                    updateWhiteStatusBar(requireActivity().window)
                }
            }

            updateAnimation.observeEvent(viewLifecycleOwner) { info ->
                updateAnimationInfo(info)
            }

            moveToEtc.asLiveData().observe(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_homeScreen_to_etcFragment)
            }

            showProfilePopup.observeEvent(viewLifecycleOwner) {
                AirbridgeManager.trackEvent(
                    "homeviewd_profile_click",
                    "homeviewd_profile_click_action",
                    "homeviewd_profile_click_label"
                )

                if (profileList.value!!.isNotEmpty() && petInfoList.isNotEmpty() == true) {
                    val bottomDialogFragment = PetSelectBottomDialogFragment(
                        profileList.value!!,
                        petInfoList[0].id,
                        viewModel
                    )

                    bottomDialogFragment.show(parentFragmentManager, "petSelectDialog")
                } else {
                    SingleBtnDialog(
                        requireContext(),
                        "서버 오류",
                        "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."
                    ).show()
                }
            }

            showHomePopup.observeEvent(viewLifecycleOwner) { popupList ->
                val bottomDialog = HomePopupDialogFragment(popupList)
                bottomDialog.show(childFragmentManager, "HomePopupDialog")
            }

            moveToMakeProfile.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(HomeFragmentDirections.actionHomeScreenToProfileGraph(true))
            }

            moveToProfile.observeEvent(viewLifecycleOwner) { petId ->
                AirbridgeManager.trackEvent(
                    "homeviewd_profile_setting_click",
                    "homeviewd_profile_setting_click_action",
                    "homeviewd_profile_setting_click_label"
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(HomeFragmentDirections.actionHomeScreenToSettingFragment(petId))
            }

            moveToWalkHistory.observeEvent(viewLifecycleOwner) { data ->
                val config = PetProfileConfig(
                    petId = data.id,
                    viewMode = data.viewMode
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(HomeFragmentDirections.actionHomeScreenToDogProfileFragment(config))
            }

            moveToPingTipAll.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_homeScreen_to_tipAllFragment)
            }

            moveToPingTip.observeEvent(viewLifecycleOwner) { url ->
                val config = WebConfig(
                    url = url
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(HomeFragmentDirections.actionHomeScreenToContentsWebFragment(config))
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.mutableStateFlow.collect {
                LogUtil.log("TAG", "mutableLiveData $it")
            }
        }

        setUpUi()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        LogUtil.log("TAG", "DashboardFragment $hidden")
    }

    private fun setUpUi() {
        with(binding) {

            val webViewWrapper = webViewWrapper
            val layoutParams = webViewWrapper.layoutParams
            layoutParams?.height = (resources.displayMetrics.widthPixels - 40 * resources.displayMetrics.densityDpi) * 9 / 8
            webViewWrapper.layoutParams = layoutParams
            webViewWrapper.requestLayout()

            webView.apply {
                settings.apply {
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    javaScriptEnabled = true
                }

                setBackgroundColor(Color.TRANSPARENT)
                setLayerType(WebView.LAYER_TYPE_HARDWARE, null)
                loadUrl("file:///android_asset/www/pattern.html")

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        pageLoaded = true
                    }
                }

                addJavascriptInterface(object : WebViewBridge() {
                    @JavascriptInterface
                    override fun onClickBalloon() {
                        val config = WebConfig(
                            url = viewModel?.speechBubbleUrl ?: ""
                        )

                        requireActivity().findNavController(R.id.nav_main)
                            .navigate(HomeFragmentDirections.actionHomeScreenToContentsWebFragment(config))
                    }
                }, "AndroidBridge")
            }

            noLoginLayer.login.setSafeOnClickListener {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_homeScreen_to_loginGraph)
            }

            noProfileLayer.makeProfile.setSafeOnClickListener {
                Toast.makeText(requireContext(), "반려견 프로필 만들기로 이동", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateAnimationInfo(info: DashboardAnimationInfo) {
        val skyStateInfo = arrayOf(
            "'sunny'",
            "'night'",
            "'cloudy'",
            "'rainy'",
            "'snow'",
            "'rainy snow'"
        )
        val defaultType = arrayOf("type_a", "type_b", "type_c", "type_c", "type_c")
        val bodyType = arrayOf(" ", " half ", " half ", " half ", " half ")
        val patternType = arrayOf("pt1", "pt2", "pt3", "pt3", "pt3")

        if (pageLoaded.not()) {
            Handler(Looper.myLooper()!!).postDelayed({
                if (isDetached.not()) {
                    updateAnimationInfo(info)
                }
            }, 0)
        } else {
            if (isDetached.not()) {
                addJavascriptForWebView("setWeather(${skyStateInfo[info.skyState.toInt() - 1]})")
                addJavascriptForWebView("setPetMotion('pet_0${info.dashboardData.petMotionState.toInt()}')")
                addJavascriptForWebView("setCharacter('${defaultType[info.dashboardData.pet.defaultType.toInt()]}${bodyType[info.dashboardData.pet.bodyType.toInt()]}${patternType[info.dashboardData.pet.patternType.toInt()]}')")
                addJavascriptForWebView("setCharacterColor('${info.dashboardData.pet.defaultColor}')")
                addJavascriptForWebView("setCharacterHalfColor('${info.dashboardData.pet.bodyColor}')")
                addJavascriptForWebView("setCharacterPatternColor('${info.dashboardData.pet.patternColor}')")
                if (info.dashboardData.speechBubble == null) {
                    addJavascriptForWebView("setBalloonVisible(false)")
                } else {
                    addJavascriptForWebView("setBalloonVisible(true)")
                    addJavascriptForWebView("setBalloonText('${info.dashboardData.speechBubble.text}')")
                }

                binding.webView.visibility = View.VISIBLE
            }
        }
    }

    private fun addJavascriptForWebView(script: String) {
        binding.webView.loadUrl("javascript:$script")
    }

    private abstract class WebViewBridge {
        abstract fun onClickBalloon()
    }
}