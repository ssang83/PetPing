package ai.comake.petping.ui.login

import ai.comake.petping.*
import ai.comake.petping.data.vo.WebConfig
import ai.comake.petping.databinding.FragmentAgreementBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateWhiteStatusBar
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: AgreementFragment
 * Created by cliff on 2022/03/11.
 *
 * Description:
 */
@AndroidEntryPoint
class AgreementFragment :
    BaseFragment<FragmentAgreementBinding>(FragmentAgreementBinding::inflate) {

    private val viewModel: AgreementViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private val args: AgreementFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            loadData(args)

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            emailAuthNeedPopup.observeEvent(viewLifecycleOwner) {
                SingleBtnDialog(
                    requireContext(),
                    getString(R.string.email_auth_need_popup_title),
                    getString(R.string.email_auth_need_popup_desc),
                    false
                ) {
                    requireActivity().findNavController(R.id.nav_main)
                        .navigate(R.id.action_global_homeScreen)
                }.show()
            }

            emailRegisterPopup.observeEvent(viewLifecycleOwner) {
                SingleBtnDialog(
                    requireContext(),
                    getString(R.string.email_register_popup_title),
                    getString(R.string.email_register_popup_desc),
                    false
                ) {
                    requireActivity().findNavController(R.id.nav_main)
                        .navigate(R.id.action_global_homeScreen)
                }.show()
            }

            loginErrorPopup.observeEvent(viewLifecycleOwner) { errorBody ->
                SingleBtnDialog(
                    requireContext(),
                    getString(R.string.login_error),
                    errorBody.message,
                    false
                ) {
                    requireActivity().backStack(R.id.nav_main)
                }.show()
            }

            moveToPolicyPage.observeEvent(viewLifecycleOwner) { url ->
                val config = WebConfig(
                    url = url
                )
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(AgreementFragmentDirections.actionGlobalContentsWebFragment(config))
            }

            moveToHome.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_global_homeScreen)
            }

            moveToLogin.observeEvent(viewLifecycleOwner) {
                requireActivity().backStack(R.id.nav_main)
            }
        }

        setUpUi()
    }

    private fun setUpUi() {
        with(binding) {

            header.btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }

            agreementViewButton.paintFlags =
                agreementViewButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            agreementViewButton2.paintFlags =
                agreementViewButton2.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            agreementViewButton3.paintFlags =
                agreementViewButton3.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        }

        LogUtil.log(
            "사용자 정보 요청 성공" +
                    "\n회원번호: ${args.config.authWord}" +
                    "\n이메일: ${args.config.emailKey}" +
                    "\n토큰: ${args.config.snsToken}" +
                    "\n타입: ${args.config.signUpType}"
        )
    }
}