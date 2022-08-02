package ai.comake.petping

import ai.comake.petping.AppConstants.AUTH_KEY
import ai.comake.petping.AppConstants.FCM_TOKEN
import ai.comake.petping.AppConstants.ID
import ai.comake.petping.AppConstants.SYSTEM_CHECKING_INFO
import ai.comake.petping.AppConstants.SYSTEM_CHECKING_INFO_DEV
import ai.comake.petping.data.db.walk.Walk
import ai.comake.petping.data.vo.MenuLink
import ai.comake.petping.data.vo.WalkFinishRequest
import ai.comake.petping.databinding.FragmentMainBinding
import ai.comake.petping.firebase.RemoteConfigMangaer
import ai.comake.petping.google.database.room.walk.WalkDBRepository
import ai.comake.petping.ui.common.dialog.DoubleBtnDialog
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.ui.home.HomeFragmentDirections
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.SharedPreferencesManager
import ai.comake.petping.util.repeatOnStarted
import ai.comake.petping.util.updateDarkStatusBar
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

    var walkData = listOf<Walk>()

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
        requireActivity().window.let { updateDarkStatusBar(it) }
        setUpObserver()
        checkMenuDirection()
        initAppConstant()

        with(viewModel) {

            checkAppVersion()

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
        }

        LogUtil.log("TAG", "onViewCreated")
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
        RemoteConfigMangaer.getInstance().fetchAndActivate(requireActivity(),
            object : OnCompleteListener<Boolean> {
                override fun onComplete(task: Task<Boolean>) {
                    if (task.isSuccessful) {
                        try {
                            val info = RemoteConfigMangaer.getInstance().getString(
                                if (BuildConfig.DEBUG) {
                                    SYSTEM_CHECKING_INFO_DEV
                                } else {
                                    SYSTEM_CHECKING_INFO
                                }
                            )

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
        )
    }

    private fun initAppConstant() {
        AppConstants.closeMissionAlert = false
        AppConstants.closeMissionPetAlert = false
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
                viewModel.asyncWalkFinish(AUTH_KEY, id, body)
            } else {
                withContext(Dispatchers.Main) {
                    checkSavedLogin()
                }
            }
        }
    }

    private fun checkSavedLogin() {
        if (sharedPreferencesManager.hasLoginDataStore()) {
            ID = sharedPreferencesManager.getDataStoreLoginId()
            AUTH_KEY = sharedPreferencesManager.getDataStoreAccessToken()

            findNavController().navigate(R.id.action_main_to_home)
        } else {
            findNavController().navigate(R.id.action_main_to_login)
        }
    }

//    private fun setUpDeepLink() {
//        val arg = MeneLink.Airbridge("Airbridge")
//        findNavController().navigate(MainFragmentDirections.actionMainToHome().setDeeplink(arg))
//    }
}