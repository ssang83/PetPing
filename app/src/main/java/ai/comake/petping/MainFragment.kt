package ai.comake.petping

import ai.comake.petping.AppConstants.AUTH_KEY
import ai.comake.petping.AppConstants.FCM_LINK
import ai.comake.petping.AppConstants.FCM_TYPE
import ai.comake.petping.AppConstants.ID
import ai.comake.petping.AppConstants.SAPA_KEY
import ai.comake.petping.AppConstants.SYSTEM_CHECKING_INFO
import ai.comake.petping.AppConstants.closeMissionAlert
import ai.comake.petping.AppConstants.closeMissionPetAlert
import ai.comake.petping.data.db.walk.Walk
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.data.vo.MenuLink
import ai.comake.petping.data.vo.WalkFinishRequest
import ai.comake.petping.databinding.FragmentMainBinding
import ai.comake.petping.firebase.RemoteConfigMangaer
import ai.comake.petping.google.database.room.walk.WalkDBRepository
import ai.comake.petping.ui.common.dialog.DoubleBtnDialog
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.ui.home.HomeFragmentDirections
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.SharedPreferencesManager
import ai.comake.petping.util.repeatOnStarted
import ai.comake.petping.util.updateDarkStatusBar
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {
    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    @Inject
    lateinit var walkDBRepository: WalkDBRepository

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    var walkData = listOf<Walk>()

    var menuLink: MenuLink? = null
    var isWalking = LocationUpdatesService._isStartWalk.value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpObserver()
        initAppConstant()
//        checkMenuDirection()

        with(viewModel) {

            repeatOnStarted {
                eventFlow.collect { event ->
                    handleEvent(event)
                }
            }

            isSucceedWalkFinish.observeEvent(viewLifecycleOwner) { walkfinish ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {
                        walkfinish.pictures = walkData[0].pictures
                        requireActivity().findNavController(R.id.nav_main).navigate(
                            HomeFragmentDirections.actionHomeToWalkrecordattach()
                                .setWalkFinish(walkfinish)
                        )
                    }
                }
            }

            isFailedWalkFinish.observeEvent(viewLifecycleOwner) { isFailed ->
                if (isFailed) {
                    checkSavedLogin()
                }
            }

            tokenRefresh.observeEvent(viewLifecycleOwner) {
                checkSavedLogin()
            }
        }


        viewModel.checkAppVersion()
        LogUtil.log("TAG", "onViewCreated")
    }

    override fun onResume() {
        super.onResume()
        //산책종료 로직 순서를 위해 임시적으로 막아둠 QA전달 완

        // 강제 업데이트 팝업 발생 시 스토어 이동 후 다시 펫핑 앱 실행 시
        // 앱버전 체크가 되지 않아서 강제 업데이트 팝업이 발생 되지 않아서 호출 위치 변경함.
//        if (isWalking.not()) { // 산책 중 푸시로 진입 시 업데이트 팝업 발생되면 안됨..
//            viewModel.checkAppVersion()
//        } else {
//            료
//        }
    }

    private fun setUpObserver() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")
            ?.observe(viewLifecycleOwner) {
                LogUtil.log("TAG", "it $it")
            }

        viewModel.openHomeScreen.observeEvent(viewLifecycleOwner) {
            LogUtil.log("TAG", "it $it")
            findNavController().navigate(R.id.action_main_to_home)
        }
    }

    private fun checkMenuDirection() {
        LogUtil.log("TAG", "checkMenuDirection ")
        activity?.let {
            if (it.intent?.hasExtra("menu") == true) {
                LogUtil.log("TAG", "" + it.intent.getStringExtra("menu"))
                when (it.intent.getStringExtra("menu")) {
                    "walk" -> {
                        val arg = MenuLink.PetPing("walk")
                        findNavController().navigate(
                            MainFragmentDirections.actionMainToHome().setMenulink(arg)
                        )
                    }
                }
            }
        }
    }

    /**
     * 강제 업데이트 팝업 표시
     *
     */
    private fun showForceUpdatePopup() {
        SingleBtnDialog(
            requireContext(),
            getString(R.string.app_update_title),
            getString(R.string.app_force_update_desc),
            btnCallback = {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("market://details?id=${requireActivity().packageName}")
                })
            }
        ).show()
    }

    /**
     * 선택 업데이트 팝업 표시
     *
     */
    private fun showUpdatePopup() {
        DoubleBtnDialog(
            requireContext(),
            getString(R.string.app_update_title),
            getString(R.string.app_update_desc),
            true,
            getString(R.string.app_update_next),
            getString(R.string.app_update_do),
            okCallback = {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("market://details?id=${requireActivity().packageName}")
                })
            },
            cancelCallback = { checkSystemInfo() }
        ).show()
    }

    //여기서 순서대로 실행
    private fun checkSystemInfo() {
        RemoteConfigMangaer.getInstance().init()
        RemoteConfigMangaer.getInstance().fetchAndActivate(
            requireActivity()
        ) { task ->
            if (task.isSuccessful) {
                try {
                    val info = RemoteConfigMangaer.getInstance().getString(SYSTEM_CHECKING_INFO)
                    val obj = JSONObject(info)
                    val modeObj = obj["isCheckingMode"] as JSONObject
                    val mode = modeObj["Android"] as Boolean
                    if (mode) {
                        viewModel.systemCheckMode.value = true
                        viewModel.systemCheckTitle.value = obj["title"].toString()
                        viewModel.systemCheckDesc.value = obj["contents"].toString()
                    } else {
                        viewModel.systemCheckMode.value = false
                        if (sharedPreferencesManager.getAuthorityPopup().not()) {
                            goToGuide()
                        } else {
                            checkUnProcessedWalk()
                        }
                    }
                } catch (e: Exception) {
                    if (sharedPreferencesManager.getAuthorityPopup().not()) {
                        goToGuide()
                    } else {
                        checkUnProcessedWalk()
                    }
                }
            }
        }
    }

    private fun initAppConstant() {
        closeMissionAlert = false
        closeMissionPetAlert = false

        val type = activity?.intent?.getStringExtra("type") ?: ""
        val link = activity?.intent?.getStringExtra("link") ?: ""
        menuLink = MenuLink.Fcm(type, link)

//        FCM_TYPE = activity?.intent?.getStringExtra("type") ?: ""
//        FCM_LINK = activity?.intent?.getStringExtra("link") ?: ""

        LogUtil.log("TAG", "ID: $ID")
        LogUtil.log("TAG", "AUTH_KEY: $AUTH_KEY")
        LogUtil.log("TAG", "FCM_TYPE: $FCM_TYPE")
        LogUtil.log("TAG", "FCM_LINK: $FCM_LINK")
    }

    private fun handleEvent(event: MainViewModel.MainEvent) = when (event) {
        is MainViewModel.MainEvent.ForceUpdate -> {
            showForceUpdatePopup()
        }
        is MainViewModel.MainEvent.SelectUpdate -> {
            showUpdatePopup()
        }
        is MainViewModel.MainEvent.SystemCheck -> {
            checkSystemInfo()
        }
        is MainViewModel.MainEvent.ExpireToken -> {
            findNavController().navigate(R.id.action_main_to_login)
        }
    }

    private fun goToGuide() {
        requireActivity().findNavController(R.id.nav_main)
            .navigate(R.id.action_mainScreen_to_userGuideFragment)
    }

    private fun checkUnProcessedWalk() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            walkData = walkDBRepository.selectAll()
            LogUtil.log("TAG", "walkData $walkData")
            if (walkData.isNotEmpty()) {
                val id = walkData[0].walkId
                LogUtil.log("TAG", "id $id")
                val body =
                    WalkFinishRequest(
                        walkData[0].distance,
                        walkData[0].time,
                        walkData[0].endState,
                        walkData[0].path,
                        walkData[0].walkEndDatetimeMilli
                    )
                viewModel.asyncWalkFinish(SAPA_KEY, id, body)
            } else {
                withContext(Dispatchers.Main) {
                    checkSavedLogin()
//                    viewModel.checkAccessToken()
                }
            }
        }
    }

    private fun checkSavedLogin() {
        if (sharedPreferencesManager.hasLoginDataStore()) {
            if (menuLink != null) {
                findNavController().navigate(
                    MainFragmentDirections.actionMainToHome().setMenulink(menuLink)
                )
            } else {
                findNavController().navigate(R.id.action_main_to_home)
            }

        } else {
            findNavController().navigate(R.id.action_main_to_login)
        }
    }

    private fun getIntentMessage() {
        val type = activity?.intent?.getStringExtra("type")
        val link = activity?.intent?.getStringExtra("link")

        LogUtil.log("TAG", "type: $type")
        LogUtil.log("TAG", "link: $link")
    }

//    private fun setUpDeepLink() {
//        val arg = MeneLink.Airbridge("Airbridge")
//        findNavController().navigate(MainFragmentDirections.actionMainToHome().setDeeplink(arg))
//    }
}