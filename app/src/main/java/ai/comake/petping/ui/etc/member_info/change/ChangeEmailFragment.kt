package ai.comake.petping.ui.etc.member_info.change

import ai.comake.petping.*
import ai.comake.petping.databinding.FragmentChangeEmailBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.*
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: ChangeEmailFragment
 * Created by cliff on 2022/03/04.
 *
 * Description:
 */
@AndroidEntryPoint
class ChangeEmailFragment :
    BaseFragment<FragmentChangeEmailBinding>(FragmentChangeEmailBinding::inflate) {

    private val viewModel: ChangeEmailViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            init(arguments?.getString("email") ?: "")

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            showSuccessPopup.observeEvent(viewLifecycleOwner) {
                SingleBtnDialog(
                    requireContext(),
                    getString(R.string.send_auth_email_title),
                    getString(R.string.send_auth_email_desc)
                ) {
                    setNavigationResult("refresh_member_info", "key")
                    requireActivity().backStack(R.id.nav_main)
                }.show()
            }
        }

        binding.header.btnBack.setSafeOnClickListener {
            requireActivity().backStack(R.id.nav_main)
        }

        keyboardVisibilityUtils = KeyboardVisibilityUtils(
            requireActivity().window,
            onHideKeyboard = {
                binding.outSide.clearFocus()
            })
    }

    override fun onDestroyView() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroyView()
    }
}