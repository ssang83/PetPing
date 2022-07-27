package ai.comake.petping.ui.profile

import ai.comake.petping.R
import ai.comake.petping.data.vo.PetCharacterConfig
import ai.comake.petping.data.vo.PetProfileData
import ai.comake.petping.databinding.FragmentSettingCharacterBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.base.BaseFragment
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: SettingCharacterFragment
 * Created by cliff on 2022/03/23.
 *
 * Description:
 */
@AndroidEntryPoint
class SettingCharacterFragment(
    private val petId:Int
) : BaseFragment<FragmentSettingCharacterBinding>(FragmentSettingCharacterBinding::inflate) {

    private val viewModel: SettingCharacterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            uiSetUp.observeEvent(viewLifecycleOwner) {
                setUI(petProfileData)
            }

            moveToEditCharacter.observeEvent(viewLifecycleOwner) {
                petProfileData?.let {
                    val config = PetCharacterConfig(
                        petId = it.petId,
                        petCharId = it.petCharId,
                        charDefaultType = it.charDefaultType,
                        charDefaultColor = it.charDefaultColor,
                        charPatternType = it.charPatternType,
                        charPatternColor = it.charPatternColor,
                        charBodyType = it.charBodyType,
                        charBodyColor = it.charBodyColor
                    )
                    requireActivity().findNavController(R.id.nav_main).navigate(
                        SettingFragmentDirections.actionSettingFragmentToCharacterEditFragment(
                            config
                        )
                    )
                }
            }
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            viewModel.loadData(petId)
        }
    }

    private fun setUI(petProfileData : PetProfileData?) {
        with(binding) {

            petProfileData?.let { data ->
                character.updateColor(data.charDefaultColor)
                character.updateType(data.charDefaultType)
                if (data.charBodyType != 0) {
                    character.updateBodyColor(data.charBodyColor)
                }
                character.updatePattern(data.charPatternType)
                character.updatePatternColor(data.charPatternColor)
            }
        }
    }
}