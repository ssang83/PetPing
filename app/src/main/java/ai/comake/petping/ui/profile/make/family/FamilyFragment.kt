package ai.comake.petping.ui.profile.make.family

import ai.comake.petping.BuildConfig
import ai.comake.petping.R
import ai.comake.petping.data.vo.FamilyConfrimConfig
import ai.comake.petping.data.vo.WebConfig
import ai.comake.petping.databinding.FragmentFamilyBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.model.SemanticAttributes
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: FamilyFragment
 * Created by cliff on 2022/06/08.
 *
 * Description:
 */
@AndroidEntryPoint
class FamilyFragment : BaseFragment<FragmentFamilyBinding>(FragmentFamilyBinding::inflate) {

    private val viewModel : FamilyViewModel by viewModels()

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        getNavigationResultLiveData<String>("key")?.observe(viewLifecycleOwner) {
            if("refresh_family_info" == it) {
                setUI()
            }

            binding.editCode.requestFocus()
            showKeyboardOnView(binding.editCode)
        }

        with(viewModel) {

            showErrorPopup.observeEvent(viewLifecycleOwner) { errorBody ->
                SingleBtnDialog(
                    requireContext(),
                    "가족으로 등록할 수 없습니다.",
                    errorBody.message
                ).show()
            }

            moveToFamilyConfirm.observeEvent(viewLifecycleOwner) { familyProfile ->
                val eventValue = 10f
                val eventAttributes = mutableMapOf<String, String>()
                val semanticAttributes = SemanticAttributes()
                Airbridge.trackEvent(
                    "airbridge.petprofile.make",
                    "familycode_button_confirm",
                    "familycode_button_confirm_label",
                    eventValue,
                    eventAttributes,
                    semanticAttributes.toMap()
                )

                val config = FamilyConfrimConfig(
                    familyCode.value.toString(),
                    familyProfile
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(FamilyFragmentDirections.actionFamilyFragmentToFamilyConfirmFragment(config))
            }

            moveToFamilyCodeGuide.observeEvent(viewLifecycleOwner) {
                val config = WebConfig(
                    url = BuildConfig.FAMILY_CODE_GUIDE_URL
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(FamilyFragmentDirections.actionFamilyCodeGuideContentsWebFragment(config))
            }
        }

        setUI()
    }

    override fun onDestroyView() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroyView()
    }

    private fun setUI() {
        with(binding) {

            editCode.setText("")
            editCode.requestFocus()
            showKeyboardOnView(editCode)

            btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }

            outSide.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> view.let {
                        hideKeyboard()
                        outSide.clearFocus()
                    }
                }
                true
            }

            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onHideKeyboard = {
                    outSide.clearFocus()
                })
        }
    }
}