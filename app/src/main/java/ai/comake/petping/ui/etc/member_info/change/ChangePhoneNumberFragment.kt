package ai.comake.petping.ui.etc.member_info.change

import ai.comake.petping.*
import ai.comake.petping.data.vo.CIConfig
import ai.comake.petping.data.vo.CertWebConfig
import ai.comake.petping.databinding.FragmentChangePhoneNumberBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.*
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: ChangePhoneNumberFragment
 * Created by cliff on 2022/03/17.
 *
 * Description:
 */
@AndroidEntryPoint
class ChangePhoneNumberFragment :
    BaseFragment<FragmentChangePhoneNumberBinding>(FragmentChangePhoneNumberBinding::inflate) {

    private val viewModel: ChangePhoneNumberViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private val args: ChangePhoneNumberFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        getNavigationResultLiveData<CIConfig>("key")?.observe(viewLifecycleOwner) {
            if (it != null) {
                handlePhoneCert(it)
            } else {
                SingleBtnDialog(
                    requireContext(),
                    "본인인증에 실패했습니다.",
                    "입력된 이름, 생년월일, 성별과 휴대폰 명의자 정보를 한 번 더 확인해 주세요."
                ).show()
            }
        }

        LogUtil.log("name : ${args.config.name}, birthAndGender : ${args.config.birthAndGender}")
        with(viewModel) {

            name.value = args.config.name
            id.value = args.config.birthAndGender

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            phoneAuthSuccess.observeEvent(viewLifecycleOwner) {
                requireActivity().backStack(R.id.nav_main)
            }

            phoneAuthFail.observeEvent(viewLifecycleOwner) { errorBody ->
                SingleBtnDialog(
                    requireContext(),
                    "본인 확인 오류",
                    errorBody.message
                ) {
                    requireActivity().backStack(R.id.nav_main)
                }.show()
            }

            moveToCert.observeEvent(viewLifecycleOwner) {
                val config = CertWebConfig(
                    name = name.value.toString(),
                    birth = id.value.toString().substring(0,6),
                    gender = id.value.toString().substring(6)
                )

                requireActivity().findNavController(R.id.nav_main).navigate(
                    ChangePhoneNumberFragmentDirections.actionChangePhoneNumberFragmentToCertWebFragment(
                        config
                    )
                )
            }
        }

        binding.header.btnBack.setSafeOnClickListener {
            requireActivity().backStack(R.id.nav_main)
        }

        binding.idEdit.disableAll(viewModel.id.value.toString())
    }

    private fun handlePhoneCert(data:CIConfig) {
        LogUtil.log("phoneNumber : ${data.phoneNumber}, ci : ${data.ci}")
        viewModel.phoneNumber.value = data.phoneNumber
        viewModel.ci = data.ci

        binding.phoneCert.text = "휴대폰 본인인증 완료"
        binding.phoneCert.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_bbbbbb))
        binding.phoneCert.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_outline)
    }
}