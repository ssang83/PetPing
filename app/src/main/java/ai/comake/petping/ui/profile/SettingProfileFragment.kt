package ai.comake.petping.ui.profile

import ai.comake.petping.*
import ai.comake.petping.databinding.FragmentSettingProfileBinding
import ai.comake.petping.ui.base.BaseFragment
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: SettingProfileFragment
 * Created by cliff on 2022/03/23.
 *
 * Description:
 */
@AndroidEntryPoint
class SettingProfileFragment(
    private val petId: Int
) : BaseFragment<FragmentSettingProfileBinding>(FragmentSettingProfileBinding::inflate) {

    private val viewModel: SettingProfileViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            loadData(petId)

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            moveToEdit.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main).navigate(
                    SettingFragmentDirections.actionSettingFragmentToProfileEditFragment(petId)
                )
            }

            moveToRegisterRNS.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main).navigate(
                    SettingFragmentDirections.actionSettingFragmentToRNSFragment(petId)
                )
            }

            moveToMissionPet.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main).navigate(
                    R.id.action_settingFragment_to_missionPetFragment
                )
            }
        }
    }
}