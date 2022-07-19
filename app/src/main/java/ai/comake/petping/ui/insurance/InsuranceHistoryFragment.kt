package ai.comake.petping.ui.insurance

import ai.comake.petping.*
import ai.comake.petping.data.vo.WebConfig
import ai.comake.petping.databinding.FragmentInsuranceHistoryBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.DoubleBtnDialog
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.ui.common.dialog.insurance.InsurancePetSelectBottomDialogFragment
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setNavigationResult
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateWhiteStatusBar
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: InsuranceHistoryFragment
 * Created by cliff on 2022/03/22.
 *
 * Description:
 */
@AndroidEntryPoint
class InsuranceHistoryFragment :
    BaseFragment<FragmentInsuranceHistoryBinding>(FragmentInsuranceHistoryBinding::inflate) {

    private val viewModel by viewModels<InsuranceHistoryViewModel>()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            backPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)

        with(viewModel) {

            loadData()

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            moveToAuthScreen.observeEvent(viewLifecycleOwner) { petId ->
                requireActivity().findNavController(R.id.nav_main).navigate(
                    InsuranceHistoryFragmentDirections.actionInsuranceHistoryFragmentToConnectFragment(
                        petId
                    )
                )
            }

            moveToInsuranceScreen.observeEvent(viewLifecycleOwner) { url ->
                val config = WebConfig(
                    url = url,
                    insurance = true
                )
                requireActivity().findNavController(R.id.nav_main).navigate(
                    InsuranceHistoryFragmentDirections.actionInsuranceHistoryFragmentToContentsWebFragment(
                        config
                    )
                )
            }

            petSelectPopup.observeEvent(viewLifecycleOwner) { petList ->
                InsurancePetSelectBottomDialogFragment(petList, viewModel).show(
                    childFragmentManager,
                    "InsuracePetSelect"
                )
            }

            petEmptyPopup.observeEvent(viewLifecycleOwner) {
                SingleBtnDialog(
                    requireContext(),
                    "인증할 수 없습니다.",
                    "인증할 반려견 프로필을 만들어 주세요."
                ) {
                    requireActivity().backStack(R.id.nav_main)
                }.show()
            }

            petAuthErrorPopup.observeEvent(viewLifecycleOwner) { errorBody ->
                when (errorBody.code) {
                    "C2370" -> {
                        SingleBtnDialog(
                            requireContext(),
                            "이메일 확인이 필요해요.",
                            errorBody.message
                        )
                        {}.show()
                    }
                    "C1470" -> {
                        SingleBtnDialog(
                            requireContext(),
                            "본인 확인이 필요해요.",
                            errorBody.message,
                            btnCallback = {
                                requireActivity().findNavController(R.id.nav_main)
                                    .navigate(R.id.action_insuranceHistoryFragment_to_certificationFragment)
                            }
                        ).show()
                    }
                    else -> {
                        SingleBtnDialog(requireContext(), "가입할 수 없습니다.", errorBody.message) {
                            requireActivity().backStack(R.id.nav_main)
                        }.show()
                    }
                }
            }

            petJoinErrorPopup.observeEvent(viewLifecycleOwner) { errorBody ->
                when (errorBody.code) {
                    "C5090" -> {
                        SingleBtnDialog(
                            requireContext(),
                            "이메일 인증이 필요해요.",
                            errorBody.message,
                            btnCallback = {
                                requireActivity().findNavController(R.id.nav_main)
                                    .navigate(R.id.action_insuranceHistoryFragment_to_memberInfoFragment)
                            }
                        ).show()
                    }
                    "C5100" -> {
                        SingleBtnDialog(
                            requireContext(),
                            "가입 가능한 반려견이 없습니다.",
                            errorBody.message,
                            btnCallback = {
                                requireActivity().backStack(R.id.nav_main)
                            }
                        ).show()
                    }
                    else -> {
                        SingleBtnDialog(
                            requireContext(),
                            "펫보험 가입 실패",
                            errorBody.message
                        ).show()
                    }
                }
            }

            petAuthPopup.observeEvent(viewLifecycleOwner) {
                DoubleBtnDialog(
                    requireContext(),
                    getString(R.string.pet_insurance_auth_title),
                    getString(R.string.pet_insurance_auth_desc),
                    true,
                    okCallback = { viewModel.petInsuranceAuth() },
                ).show()
            }

            moveToInsurnaceDetail.observeEvent(viewLifecycleOwner) { item ->
                when(item.state) {
                    4 -> {
                        SingleBtnDialog(
                            requireContext(),
                            "인수 심사가 거절됐어요.",
                            "종합검진 결과 특이 소견이 발견되었거나 필수 항목 중 일부가 누락된 경우 인수 심사가 거절될 수 있습니다. 자세한 문의는 \'1:1 문의하기\'를 이용해 주세요."
                        ) {}.show()
                    }

                    else -> {
                        val config = WebConfig(
                            url = item.url,
                            insurance = true
                        )
                        requireActivity().findNavController(R.id.nav_main).navigate(
                            InsuranceHistoryFragmentDirections.actionInsuranceHistoryFragmentToContentsWebFragment(
                                config
                            )
                        )
                    }
                }
            }
        }

        binding.header.btnBack.setSafeOnClickListener {
            backPressed()
        }
    }

    override fun onDestroyView() {
        onBackPressedCallback.remove()
        super.onDestroyView()
    }

    private fun backPressed() {
        setNavigationResult("from_pet_insurance_screen", "key")
        requireActivity().backStack(R.id.nav_main)
    }
}