package ai.comake.petping.ui.profile.make.family

import ai.comake.petping.*
import ai.comake.petping.databinding.FragmentFamilyConfirmBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setNavigationResult
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateWhiteStatusBar
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.model.SemanticAttributes
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: FamilyConfirmFragment
 * Created by cliff on 2022/06/08.
 *
 * Description:
 */
@AndroidEntryPoint
class FamilyConfirmFragment :
    BaseFragment<FragmentFamilyConfirmBinding>(FragmentFamilyConfirmBinding::inflate) {

    private val viewModel: FamilyConfirmViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private val args:FamilyConfirmFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            init(args.config.familyCode, args.config.profile)

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            showSuccessPopup.observeEvent(viewLifecycleOwner) {
                SingleBtnDialog(requireContext(), "가족으로 등록되었어요.", "홈으로 이동합니다.") {
                    val eventValue = 10f
                    val eventAttributes = mutableMapOf<String, String>()
                    val semanticAttributes = SemanticAttributes()
                    Airbridge.trackEvent(
                        "airbridge.petprofile.make",
                        "familycode_popup_confirm",
                        "familycode_popup_confirm_label",
                        eventValue,
                        eventAttributes,
                        semanticAttributes.toMap()
                    )

                    requireActivity().findNavController(R.id.nav_main)
                        .navigate(R.id.action_profile_to_home)
                }.show()
            }

            showErrorPopup.observeEvent(viewLifecycleOwner) { message ->
                SingleBtnDialog(requireContext(), "가족으로 등록할 수 없습니다.", message) {
                    val eventValue = 10f
                    val eventAttributes = mutableMapOf<String, String>()
                    val semanticAttributes = SemanticAttributes()
                    Airbridge.trackEvent(
                        "airbridge.petprofile.make",
                        "familycode_popup_recheck",
                        "familycode_popup_recheck_label",
                        eventValue,
                        eventAttributes,
                        semanticAttributes.toMap()
                    )

                    setNavigationResult("refresh_family_info", "key")
                    requireActivity().backStack(R.id.nav_main)
                }.show()
            }
        }

        binding.header.btnBack.setSafeOnClickListener {
            requireActivity().backStack(R.id.nav_main)
        }
    }
}