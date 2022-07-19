package ai.comake.petping.ui.etc.mission_pet

import ai.comake.petping.*
import ai.comake.petping.databinding.FragmentMissionPetBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.ui.common.dialog.mission_pet.MissionPetSelectBottomDialogFragment
import ai.comake.petping.util.*
import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: MissionPetFragment
 * Created by cliff on 2022/02/16.
 *
 * Description:
 */
@AndroidEntryPoint
class MissionPetFragment :
    BaseFragment<FragmentMissionPetBinding>(FragmentMissionPetBinding::inflate) {

    private val viewModel by viewModels<MissionPetViewModel>()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        getNavigationResultLiveData<String>("key")?.observe(viewLifecycleOwner) {
                LogUtil.log("TAG", "it $it")
                if("after_register_pet_number" == it) {
                    viewModel.setRepresentativeMissionPets(viewModel.petId)
                } else if ("from_pet_insurance_screen" == it) {
                    viewModel.getInsuranceMissionPet()
                }
            }

        with(viewModel) {

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when(state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            moveToPetInsuranceScreen.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_missionPetFragment_to_insuranceHistoryFragment)
            }

            showPetSelectPopup.observeEvent(viewLifecycleOwner) { petList ->
                MissionPetSelectBottomDialogFragment(
                    petList,
                    btnCallback = { petId ->
                        viewModel.petId = petId
                        viewModel.setRepresentativeMissionPets(petId)
                    }
                ).show(childFragmentManager, "MissionPetSelect")
            }

            showChangePetErrorPopup.observeEvent(viewLifecycleOwner) { errorBody ->
                when (errorBody.code) {
                    "C0010" -> {
                        SingleBtnDialog(requireContext(), "error", errorBody.message).show()
                    }
                    "C1460" -> {
                        SingleBtnDialog(
                            requireContext(),
                            "본인 확인이 필요해요.",
                            errorBody.message,
                            false
                        ){
                            requireActivity().findNavController(R.id.action_missionPetFragment_to_certificationFragment)
                        }.show()
                    }
                    "C2130" -> {
                        if(isChangeable.value!!) {
                            SingleBtnDialog(
                                requireContext(),
                                "변경 가능한 반려견이 없습니다.",
                                "가족 프로필은 미션 반려견으로 설정할 수 없어요. 반려견 프로필을 만들어 보세요.",
                            ).show()
                        } else {
                            SingleBtnDialog(
                                requireContext(),
                                "설정 가능한 반려견이 없습니다.",
                                "가족 프로필은 미션 반려견으로 설정할 수 없어요. 반려견 프로필을 만들어 보세요.",
                            ).show()
                        }
                    }
                    "C2390" -> {
                        SingleBtnDialog(requireContext(), "설정할 수 없습니다.", errorBody.message).show()
                    }
                }
            }

            showErrorPopup.observeEvent(viewLifecycleOwner) { errorBody ->
                when (errorBody.code) {
                    "C0010", "C0020" -> {
                        SingleBtnDialog(requireContext(), "error", errorBody.message).show()
                    }
                    "C2100" -> {
                        SingleBtnDialog(
                            requireContext(),
                            "저장 할 수 없습니다.",
                            "다른 회원이 설정한 미션 반려견과 동일한 동물등록번호입니다."
                        ).show()
                    }
                    "C2140" -> {
                        requireActivity().findNavController(R.id.nav_main).navigate(
                            MissionPetFragmentDirections.actionMissionPetFragmentToRNSFragment(petId)
                        )
                    }
                }
            }
        }

        binding.header.btnBack.setSafeOnClickListener {
            requireActivity().backStack(R.id.nav_main)
        }
    }
}