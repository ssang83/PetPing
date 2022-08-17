package ai.comake.petping.ui.login

import ai.comake.petping.*
import ai.comake.petping.data.vo.AgreementConfig
import ai.comake.petping.databinding.FragmentSignupEmailBinding
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
 * Class: SignUpEmailFragment
 * Created by cliff on 2022/03/14.
 *
 * Description:
 */
@AndroidEntryPoint
class SignUpEmailFragment :
    BaseFragment<FragmentSignupEmailBinding>(FragmentSignupEmailBinding::inflate) {

    private val viewModel: SignUpEmailViewModel by viewModels()
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

            emailSuccess.observeEvent(viewLifecycleOwner) {
                val config = AgreementConfig(
                    authWord = password.value.toString(),
                    signUpType = 1,
                    emailKey = email.value.toString(),
                    sendAuthMailFlag = true
                )

                requireActivity().findNavController(R.id.nav_main).navigate(
                    SignUpEmailFragmentDirections.actionSignUpEmailFragmentToAgreementFragment(
                        config
                    )
                )
            }

            emailErrorPopup.observeEvent(viewLifecycleOwner) { errorBody ->
                when (errorBody.code) {
                    "C3000" -> {
                        SingleBtnDialog(
                            requireContext(),
                            getString(R.string.login_error),
                            getString(R.string.email_address_error)
                        ) {
                            isDuplicate.value = true
                        }.show()
                    }
                }
            }

            passwordUiUpdate.observeEvent(viewLifecycleOwner) {
                binding.editPasswd.setSelection(password.value!!.length)
                if (binding.editPasswd.text!!.isNotEmpty() && binding.editPasswdConfirm.text!!.isNotEmpty()) {
                    if (binding.editPasswd.text.toString() == binding.editPasswdConfirm.text.toString()) {
                        passwdConfirmValidation.value = true
                    } else {
                        passwdConfirmValidation.value = false
                    }
                }
            }

            passwordConfirmUiUpdate.observeEvent(viewLifecycleOwner) {
                binding.editPasswdConfirm.setSelection(passwordConfirm.value!!.length)
            }
        }

        setUi()
    }

    override fun onDestroyView() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroyView()
    }

    private fun setUi() {
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

            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onShowKeyboard = { keyboardHeight ->
                    root.run {
                        smoothScrollTo(scrollX, editEmail.top)
                    }
                    btnNext.visibility = View.GONE
                })

            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onHideKeyboard = {
                    btnNext.visibility = View.VISIBLE
                    outSide.clearFocus()
                })
        }
    }
}