package ai.comake.petping.ui.etc.member_info.change

import ai.comake.petping.*
import ai.comake.petping.databinding.FragmentChangePasswordBinding
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

            passwordUiUpdate.observeEvent(viewLifecycleOwner) { passowrd ->
                setPasswordTextUI(passowrd)
            }

            passwordHasFocus.observeEvent(viewLifecycleOwner) { hasFocus ->
                setPasswrodFocus(hasFocus)
            }

            passwordConfirmUiUpdate.observeEvent(viewLifecycleOwner) { passordConfirm ->
                setPasswordConfirmUI(passordConfirm)
            }

            passwordConfirmHasFocus.observeEvent(viewLifecycleOwner) { hasFocus ->
                setPasswordConfirmFocus(hasFocus)
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

            keyboardVisibilityUtils = KeyboardVisibilityUtils(
                requireActivity().window,
                onHideKeyboard = {
                    outSide.clearFocus()
                })
        }
    }

    private fun setPasswordTextUI(password:String) {
        with(binding) {
            if (password.isEmpty()) {
                passwordWrapper.isErrorEnabled = true
                passwordWrapper.setErrorTextColor(requireActivity().resources.getColorStateList(
                    R.color.greyscale_9_aaa, requireActivity().theme
                ))
                passwordWrapper.error = "8자 이상 영문, 숫자, 특수문자를 포함해 만들어 주세요."
            } else {
                if(passwordConfirmEdit.text.toString().isNotEmpty()){
                    if(isSamePassWord(binding.passwordEdit.text.toString(),binding.passwordConfirmEdit.text.toString()).not()){
                        passwordConfirmWrapper.isErrorEnabled = true
                        passwordConfirmWrapper.error = "비밀번호 불일치"
                    } else {
                        passwordConfirmWrapper.isErrorEnabled = false
                    }
                }

                if (Pattern.compile(PASSWORD_PATTERN).matcher(password).matches().not()) {
                    passwordWrapper.isErrorEnabled = true
                    passwordWrapper.setErrorTextColor(requireActivity().resources.getColorStateList(
                        R.color.primary_pink,
                        requireActivity().theme
                    ))
                    passwordWrapper.error = "8자 이상 영문, 숫자, 특수문자가 포함돼야 합니다."
                } else {
                    passwordWrapper.isErrorEnabled = false
                }
            }
        }
    }

    private fun setPasswrodFocus(hasFocus: Boolean) {
        with(binding) {
            if (hasFocus) {
                if (passwordEdit.text.toString().isEmpty()) {
                    passwordWrapper.hint = "비밀번호"
                    passwordWrapper.defaultHintTextColor = requireActivity().resources.getColorStateList(
                        R.color.greyscale_9_aaa,
                        requireActivity().theme
                    )
                    passwordWrapper.isErrorEnabled = true
                    passwordWrapper.setErrorTextColor(requireActivity().resources.getColorStateList(
                        R.color.greyscale_9_aaa,
                        requireActivity().theme
                    ))
                    passwordWrapper.error = "8자 이상 영문, 숫자, 특수문자를 포함해 만들어 주세요."
                }
            } else {
                if (passwordEdit.text.toString().isEmpty()) {
                    passwordWrapper.hint = "비밀번호를 입력하세요"
                    passwordWrapper.defaultHintTextColor = requireActivity().resources.getColorStateList(
                        R.color.greyscale_4_ddd,
                        requireActivity().theme
                    )
                    passwordWrapper.isErrorEnabled = false
                }
            }
        }
    }

    private fun setPasswordConfirmUI(text:String) {
        with(binding) {
            if (text.isEmpty()) {
                passwordConfirmWrapper.isErrorEnabled = false
            } else {
                if (isSamePassWord(binding.passwordEdit.text.toString(),binding.passwordConfirmEdit.text.toString()).not()) {
                    passwordConfirmWrapper.isErrorEnabled = true
                    passwordConfirmWrapper.error = "비밀번호 불일치"
                } else {
                    passwordConfirmWrapper.isErrorEnabled = false
                }
            }
        }
    }

    private fun setPasswordConfirmFocus(hasFocus:Boolean) {
        with(binding) {
            if (hasFocus) {
                if (passwordConfirmEdit.text.toString().isEmpty()) {
                    passwordConfirmWrapper.hint = "비밀번호 확인"
                    passwordConfirmWrapper.defaultHintTextColor = requireActivity().resources.getColorStateList(
                        R.color.greyscale_9_aaa,
                        requireActivity().theme
                    )
                }
            } else {
                if (passwordConfirmEdit.text.toString().isEmpty()) {
                    passwordConfirmWrapper.hint = "한 번 더 입력하세요"
                    passwordConfirmWrapper.defaultHintTextColor = requireActivity().resources.getColorStateList(
                        R.color.greyscale_4_ddd,
                        requireActivity().theme
                    )
                    passwordConfirmWrapper.isErrorEnabled = false
                }
            }
        }
    }

    private fun isSamePassWord(oldPass : String, newPass : String) : Boolean {
        return oldPass == newPass
    }
}