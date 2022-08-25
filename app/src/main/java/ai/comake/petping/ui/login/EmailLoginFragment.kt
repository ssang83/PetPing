package ai.comake.petping.ui.login

import ai.comake.petping.*
import ai.comake.petping.databinding.FragmentEmailLoginBinding
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
 * Class: EmailLoginFragment
 * Created by cliff on 2022/03/10.
 *
 * Description:
 */
@AndroidEntryPoint
class EmailLoginFragment :
    BaseFragment<FragmentEmailLoginBinding>(FragmentEmailLoginBinding::inflate) {

    private val viewModel: EmailLoginViewModel by viewModels()
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

            emailErrorPopup.observeEvent(viewLifecycleOwner) { errorBody ->
                SingleBtnDialog(
                    requireContext(),
                    "로그인할 수 없습니다.",
                    errorBody.message
                ) {
                    emailErrorUI()
                }.show()
            }

            passwordErrorPopup.observeEvent(viewLifecycleOwner) { errorBody ->
                SingleBtnDialog(
                    requireContext(),
                    "로그인할 수 없습니다.",
                    errorBody.message
                ) {
                    passwordErrorUI()
                }.show()
            }

            loginErrorPopup.observeEvent(viewLifecycleOwner) { errorResponse ->
                SingleBtnDialog(
                    requireContext(),
                    errorResponse.title,
                    errorResponse.message
                ).show()
            }

            moveToHome.observeEvent(viewLifecycleOwner) {
                mainShareViewModel.registFCMToken()

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_global_homeScreen)
            }

            moveToFindPasswd.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_emailLoginFragment_to_findPasswordFragment)
            }

            moveToSignUp.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_emailLoginFragment_to_signUpEmailFragment)
            }
        }

        setUi()
    }

    override fun onDestroyView() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroyView()
    }

    /**
     * email error가 나면 email, password input field 초기화 후 email input field 포커스
     */
    private fun emailErrorUI() {
        binding.apply {
            editEmail.setText("")
            editPasswd.setText("")
            editEmail.requestFocus()
            editPasswd.requestFocus()
            showKeyboardOnView(editEmail)
        }
    }

    /**
     * password error가 나면 password input field만 초기화
     */
    private fun passwordErrorUI() {
        binding.apply {
            editPasswd.setText("")
            editPasswd.requestFocus()
            showKeyboardOnView(editPasswd)
        }
    }

    private fun setUi() {
        with(binding) {

            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onHideKeyboard = {
                    outSide.clearFocus()
                })

            outSide.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> view.let {
                        hideKeyboard()
                        outSide.clearFocus()
                    }
                }
                true
            }

            header.btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }
        }
    }
}