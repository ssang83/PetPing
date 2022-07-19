package ai.comake.petping.ui.login

import ai.comake.petping.*
import ai.comake.petping.data.vo.AgreementConfig
import ai.comake.petping.databinding.FragmentSignupEmailBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.*
import android.os.Bundle
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
                        SingleBtnDialog(requireContext(), getString(R.string.login_error), getString(R.string.email_address_error)){
                            binding.emailWrapper.error = null
                            binding.emailWrapper.isErrorEnabled = true
                            binding.emailWrapper.error = getString(R.string.email_user_exists)
                            isDuplicate.value = true
                        }.show()
                    }
                }
            }

            emailError.observeEvent(viewLifecycleOwner) { errorBody ->
                when (errorBody.code) {
                    "C3000" -> {
                        binding.emailWrapper.error = null
                        binding.emailWrapper.isErrorEnabled = true
                        binding.emailWrapper.error = getString(R.string.email_user_exists)
                    }
                }
            }

            passwordUiUpdate.observeEvent(viewLifecycleOwner) {
                binding.passwordEdit.setSelection(password.value!!.length)
                if (binding.passwordEdit.text!!.isNotEmpty() && binding.passwordConfirmEdit.text!!.isNotEmpty()) {
                    if (binding.passwordEdit.text.toString() == binding.passwordConfirmEdit.text.toString()) {
                        binding.passwordConfirmWrapper.isErrorEnabled = false
                    } else {
                        binding.passwordConfirmWrapper.isErrorEnabled = true
                        binding.passwordConfirmWrapper.error = "비밀번호 불일치"
                    }
                }
            }

            passwordConfirmUiUpdate.observeEvent(viewLifecycleOwner) {
                binding.passwordConfirmEdit.setSelection(passwordConfirm.value!!.length)
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

            setEditText(
                requireContext(),
                emailWrapper,
                emailEdit,
                Pattern.compile(EMAIL_PATTERN),
                "잘못된 주소 형식입니다.",
                "이메일 주소를 입력하세요",
                "이메일 주소"
            )

            setEditText(
                requireContext(),
                passwordWrapper,
                passwordEdit,
                Pattern.compile(PASSWORD_PATTERN),
                "8자 이상 영문, 숫자, 특수문자가 포함돼야 합니다.",
                "비밀번호를 입력하세요",
                "비밀번호",
                "8자 이상 영문, 숫자, 특수문자를 포함해 만들어 주세요."
            )

            setEditText(
                requireContext(),
                passwordConfirmWrapper,
                passwordConfirmEdit,
                passwordEdit,
                "비밀번호 불일치",
                "한 번 더 입력하세요",
                "비밀번호 확인"
            )

            header.btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }

            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onShowKeyboard = { keyboardHeight ->
                    root.run {
                        smoothScrollTo(scrollX, emailWrapper.top)
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