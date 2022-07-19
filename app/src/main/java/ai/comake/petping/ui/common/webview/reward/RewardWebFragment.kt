package ai.comake.petping.ui.common.webview.reward

import ai.comake.petping.*
import ai.comake.petping.data.vo.OngoingMission
import ai.comake.petping.databinding.FragmentRewardWebviewBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.ui.common.dialog.reward.RewardBottomDialogFragment
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateWhiteStatusBar
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: RewardWebFragment
 * Created by cliff on 2022/02/15.
 *
 * Description:
 */
@AndroidEntryPoint
class RewardWebFragment :
    BaseFragment<FragmentRewardWebviewBinding>(FragmentRewardWebviewBinding::inflate) {

    private val viewModel by viewModels<RewardWebViewModel>()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private val args: RewardWebFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setBinding(args.mission)

        with(viewModel) {

            LogUtil.log("TAG", "missionId : ${args.mission.id}, missionType : ${args.mission.type}, missionState : ${args.mission.missionState}")
            missionId = args.mission.id!!
            missionType.value = args.mission.type!!
            missionState.value = args.mission.missionState!!

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            showUploadPopup.observeEvent(viewLifecycleOwner) {
                val text = if(args.mission.missionState == 1) {
                    getString(R.string.send_file)
                } else {
                    getString(R.string.re_send_file)
                }
                RewardBottomDialogFragment(text) { fileList ->
                    uploadFiles(requireContext(), fileList)
                }.show(childFragmentManager, "Reward")
            }

            uploadSuccessPopup.observeEvent(viewLifecycleOwner) {
                SingleBtnDialog(
                    requireContext(),
                    getString(R.string.upload_file_success_title),
                    getString(R.string.upload_file_success_desc)
                ) {
                    missionState.value = 0
                }.show()
            }
        }
    }

    private fun setBinding(mission: OngoingMission) {
        with(binding) {
            webView.apply {
                settings.apply {
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    javaScriptEnabled = true
                }

                setBackgroundColor(Color.TRANSPARENT)
                setLayerType(WebView.LAYER_TYPE_HARDWARE, null)
            }

            var token = hashMapOf<String, String>()
            if (AppConstants.AUTH_KEY.isNotEmpty()) {
                token = HashMap<String, String>().apply {
                    put("petping_token", AppConstants.AUTH_KEY.split(" ")[1])
                }
            }

            webView.loadUrl(mission.missionDetailURL.toString(), token)

            header.btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }
        }
    }
}