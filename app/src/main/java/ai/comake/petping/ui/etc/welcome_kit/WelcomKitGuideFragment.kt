package ai.comake.petping.ui.etc.welcome_kit

import ai.comake.petping.*
import ai.comake.petping.data.vo.WebConfig
import ai.comake.petping.databinding.FragmentWelcomKitGuideBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateWhiteStatusBar
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: WelcomKitGuideFragment
 * Created by cliff on 2022/02/23.
 *
 * Description:
 */
@AndroidEntryPoint
class WelcomKitGuideFragment :
    BaseFragment<FragmentWelcomKitGuideBinding>(FragmentWelcomKitGuideBinding::inflate) {

    private val viewModel: WelcomeKitGuideViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            welcomeKitApplyErrorPopup.observeEvent(viewLifecycleOwner) { errorBody ->
                SingleBtnDialog(
                    requireContext(),
                    getString(R.string.welcome_kit_error_titile),
                    errorBody.message
                ).show()
            }

            moveToWelcomeKitApply.observeEvent(viewLifecycleOwner) { url ->
                val config = WebConfig(
                    url = url,
                    fromWelcomKit = true
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(WelcomKitGuideFragmentDirections.actionWelcomKitGuideFragmentToContentsWebFragment(config))
            }

            moveToMissionPet.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_welcomKitGuideFragment_to_missionPetFragment)
            }
        }

        binding.header.btnBack.setSafeOnClickListener {
            requireActivity().backStack(R.id.nav_main)
        }
    }
}