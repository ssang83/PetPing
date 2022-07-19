package ai.comake.petping.ui.login

import ai.comake.petping.*
import ai.comake.petping.databinding.FragmentFindPasswordBinding
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
 * Class: FindPasswordFragment
 * Created by cliff on 2022/03/11.
 *
 * Description:
 */
@AndroidEntryPoint
class FindPasswordFragment :
    BaseFragment<FragmentFindPasswordBinding>(FragmentFindPasswordBinding::inflate) {

    private val viewModel: FindPasswordViewModel by viewModels()
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

            emailSuccessPopup.observeEvent(viewLifecycleOwner) {
                SingleBtnDialog(
                    requireContext(),
                    getString(R.string.find_password_success_title),
                    getString(R.string.find_password_success_desc),
                    btnCallback = {
                        requireActivity().backStack(R.id.nav_main)
                    }
                ).show()
            }

            emailErrorPopup.observeEvent(viewLifecycleOwner) { errorBody ->
                SingleBtnDialog(
                    requireContext(),
                    getString(R.string.no_send_mail),
                    errorBody.message
                ).show()
            }

            moveToLogin.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_findPasswordFragment_to_loginFragment)
            }
        }

        setUpUi()
    }

    override fun onDestroyView() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroyView()
    }

    private fun setUpUi() {
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

            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onHideKeyboard = {
                    outSide.clearFocus()
                })

            header.btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }

            emailEdit.requestFocus()
            showKeyboardOnView(emailEdit)
        }
    }
}