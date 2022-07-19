package ai.comake.petping.ui.etc.mission_pet

import ai.comake.petping.AirbridgeManager
import ai.comake.petping.R
import ai.comake.petping.databinding.FragmentMissionPetGuideBinding
import ai.comake.petping.util.repeatOnStarted
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController

/**
 * android-petping-2
 * Class: MissionPetGuideFragment
 * Created by cliff on 2022/07/13.
 *
 * Description:
 */
class MissionPetGuideFragment : Fragment() {

    private lateinit var binding: FragmentMissionPetGuideBinding
    private val viewModel: MissionPetGuideViewModel by viewModels()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            goToHome()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMissionPetGuideBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)

        with(viewModel) {

            repeatOnStarted {
                eventFlow.collect { event -> handleEvent(event) }
            }
        }
    }

    override fun onDestroyView() {
        onBackPressedCallback.remove()
        super.onDestroyView()
    }

    private fun goToHome() {
        requireActivity().findNavController(R.id.nav_main)
            .navigate(R.id.action_profile_to_home)
    }

    private fun goToMissionPet() {
        requireActivity().findNavController(R.id.nav_main)
            .navigate(MissionPetGuideFragmentDirections.actionProfileToMissionPetFragment(true))
    }

    private fun handleEvent(event: MissionPetGuideViewModel.MissionPetGuideEvent) = when (event) {
        is MissionPetGuideViewModel.MissionPetGuideEvent.MoveToHome -> {
            AirbridgeManager.trackEvent(
                "airbridge.missionpet.set",
                "missionpetremind_button_next",
                "missionpetremind_button_next_label"
            )

            goToHome()
        }
        is MissionPetGuideViewModel.MissionPetGuideEvent.MoveToGoSetting -> {
            AirbridgeManager.trackEvent(
                "airbridge.missionpet.set",
                "missionpetremind_button_nowset",
                "missionpetremind_button_nowset_label"
            )

           goToMissionPet()
        }
    }
}