package ai.comake.petping.ui.profile.edit

import ai.comake.petping.AppConstants
import ai.comake.petping.R
import ai.comake.petping.databinding.FragmentCharacterEditBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.widget.character.CharacterEditView
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setSafeOnClickListener
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: CharacterFragment
 * Created by cliff on 2022/03/24.
 *
 * Description:
 */
@AndroidEntryPoint
class CharacterEditFragment :
    BaseFragment<FragmentCharacterEditBinding>(FragmentCharacterEditBinding::inflate) {

    private val viewModel: CharacterEditViewModel by viewModels()
    private val args: CharacterEditFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            petId = args.config.petId
            petCharId = args.config.petCharId
            charDefaultType = args.config.charDefaultType
            charDefaultColor = args.config.charDefaultColor
            charPatternColor = args.config.charPatternColor
            charPatternType = args.config.charPatternType
            charBodyColor = args.config.charBodyColor
            charBodyType = args.config.charBodyType

            modifySuccess.observeEvent(viewLifecycleOwner) {
                requireActivity().backStack(R.id.nav_main)
            }
        }

        setUi()
    }

    private fun setUi() {
        with(binding) {
            val body = if(args.config.charBodyType == 0) -1 else AppConstants.colorMap[args.config.charBodyColor.lowercase()]

            binding.character.initialize(
                AppConstants.colorMap[args.config.charDefaultColor.lowercase()]?:0,
                args.config.charDefaultType,
                args.config.charPatternType,
                AppConstants.colorMap[args.config.charPatternColor.lowercase()]?:0,
                body?:-1,
                object : CharacterEditView.CharacterChangeListener {
                    override fun onColorChanged(index: Int) {
                        viewModel?.charDefaultColor = AppConstants.colorList[index]
                    }

                    override fun onTypeChanged(index: Int) {
                        viewModel?.charDefaultType = index
                    }

                    override fun onPatternChanged(index: Int) {
                        viewModel?.charPatternType = index
                    }

                    override fun onPatternColorChanged(index: Int) {
                        viewModel?.charPatternColor = AppConstants.colorList[index]
                    }

                    override fun onBodyColorChanged(index: Int) {
                        if(index == -1) {
                            viewModel?.charBodyType = 0
                            viewModel?.charBodyColor = ""
                        } else {
                            viewModel?.charBodyType = 1
                            viewModel?.charBodyColor = AppConstants.colorList[index]
                        }
                    }

                })

            header.btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }
        }
    }
}