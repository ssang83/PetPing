package ai.comake.petping.ui.etc.member_info.change

import ai.comake.petping.*
import ai.comake.petping.databinding.FragmentChangePasswordBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

/**
 * android-petping-2
 * Class: ChangePasswordFragment
 * Created by cliff on 2022/03/07.
 *
 * Description:
 */
@AndroidEntryPoint
class ChangePasswordFragment :
    BaseFragment<FragmentChangePasswordBinding>(FragmentChangePasswordBinding::inflate) {

    private val viewModel: ChangePasswordViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

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

            showSuccessPopup.observeEvent(viewLifecycleOwner) {
                SingleBtnDialog(
                    requireContext(),
                    getString(R.string.change_password_complete),
                    getString(R.string.change_password_complete_desc),
                    btnCallback = {
                        requireActivity().backStack(R.id.nav_main)
                    }
                ).show()
            }
        }

        setUI()
    }

    override fun onDestroyView() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroyView()
    }

    private fun setUI() {
        with(binding) {

            header.btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }

            outSide.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> view.let {
                        hideKeyboard()
                        outSide.clearFocus()
                    }
                }
                true
            }

            keyboardVisibilityUtils = KeyboardVisibilityUtils(
                requireActivity().window,
                onHideKeyboard = {
                    outSide.clearFocus()
                })
        }
    }
}