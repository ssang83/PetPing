package ai.comake.petping

import ai.comake.petping.data.vo.MenuLink
import ai.comake.petping.databinding.FragmentMainBinding
import ai.comake.petping.ui.common.dialog.DoubleBtnDialog
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.LogUtil
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
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by viewModels()

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
                eventFlow.collect { event -> handleEvent(event) }
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
//            setUpDeepLink()
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
                        findNavController().navigate(MainFragmentDirections.actionMainToHome().setMenulink(arg))
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

    private fun checkSystemInfo() {
        //TODO : 시스템 점검 API 로 적용할지 아니면 Firebase RemoteConfig를 사용할 지 결정되면 적용 진행

        viewModel.testLogin()
    }

    private fun initAppConstant() {
        AppConstants.closeWelcomeKitAlert = false
        AppConstants.closeMissionAlert = false
        AppConstants.closeMissionPetAlert = false
    }

    private fun handleEvent(event: MainViewModel.MainEvent) = when (event) {
        is MainViewModel.MainEvent.ForceUpdate -> { showForceUpdatePopup() }
        is MainViewModel.MainEvent.SelectUpdate -> { showUpdatePopup() }
        is MainViewModel.MainEvent.SystemCheck -> { checkSystemInfo() }
    }

//    private fun setUpDeepLink() {
//        val arg = MeneLink.Airbridge("Airbridge")
//        findNavController().navigate(MainFragmentDirections.actionMainToHome().setDeeplink(arg))
//    }
}
