package ai.comake.petping.ui.insurance

import ai.comake.petping.*
import ai.comake.petping.data.vo.CIConfig
import ai.comake.petping.data.vo.CertWebConfig
import ai.comake.petping.data.vo.WebConfig
import ai.comake.petping.databinding.FragmentConnectBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.*
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: ConnectFragment
 * Created by cliff on 2022/03/22.
 *
 * Description:
 */
@AndroidEntryPoint
class ConnectFragment : BaseFragment<FragmentConnectBinding>(FragmentConnectBinding::inflate) {

    private val viewModel by viewModels<ConnectViewModel>()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private val args:ConnectFragmentArgs by navArgs()

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        LogUtil.log("petId : ${args.petId}")
        getNavigationResultLiveData<CIConfig>("key")?.observe(viewLifecycleOwner) { config ->
            if (config != null) {
                viewModel.phoneNumber.value = config.phoneNumber
                viewModel.ci = config.ci
                viewModel.checkValidationCI()
            }
        }

        with(viewModel) {

            getMemberInfo(args.petId)

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            moveToAgreeScreen.observeEvent(viewLifecycleOwner) { url ->
                val config = WebConfig(
                    url = url
                )

                requireActivity().findNavController(R.id.nav_main).navigate(
                    ConnectFragmentDirections.actionConnectFragmentToContentsWebFragment(config)
                )
            }

            authCompleteScreen.observeEvent(viewLifecycleOwner) {
                requireActivity().backStack(R.id.nav_main)
            }

            authCompleteErrorPopup.observeEvent(viewLifecycleOwner) { errorBody ->
                when (errorBody.code) {
                    "C2110" -> {
                        SingleBtnDialog(
                            requireContext(),
                            "인증할 수 없습니다.",
                            errorBody.message
                        ) {
                            requireActivity().backStack(R.id.nav_main)
                        }.show()
                    }
                    "C2120" -> {
                        SingleBtnDialog(
                            requireContext(),
                            "인증할 수 없습니다.",
                            errorBody.message
                        ) {
                            requireActivity().backStack(R.id.nav_main)
                        }.show()
                    }
                    else -> {
                        SingleBtnDialog(
                            requireContext(),
                            "인증할 수 없습니다.",
                            errorBody.message
                        ) {
                            requireActivity().backStack(R.id.nav_main)
                        }.show()
                    }
                }
            }

            checkCISuccess.observe(viewLifecycleOwner) {
                binding.phoneCert.text = "휴대폰 본인인증 완료"
                binding.fullId.disableAll()
            }

            checkCIErrorPopup.observeEvent(viewLifecycleOwner) {
                SingleBtnDialog(
                    requireContext(),
                    "인증할 수 없습니다.",
                    "회원 정보와 일치하지 않아 연결할 수 없습니다. 계약자 정보를 한 번 더 확인해주세요."
                ).show()
            }

            moveToCertWeb.observeEvent(viewLifecycleOwner) {
                val config = CertWebConfig(
                    name = name.value.toString(),
                    birth = id.value.toString().substring(0, 6),
                    gender = id.value.toString().substring(6, 7)
                )
                requireActivity().findNavController(R.id.nav_main).navigate(
                    ConnectFragmentDirections.actionConnectFragmentToCertWebFragment(config)
                )
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

            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onHideKeyboard = {
                    outSide.clearFocus()
                })

            header.btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }
        }
    }
}