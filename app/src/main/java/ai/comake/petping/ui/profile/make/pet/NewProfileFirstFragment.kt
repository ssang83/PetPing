package ai.comake.petping.ui.profile.make.pet

import ai.comake.petping.*
import ai.comake.petping.databinding.FragmentNewProfileFirstBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.widget.MaleFemaleView
import ai.comake.petping.util.KeyboardVisibilityUtils
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateWhiteStatusBar
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.model.SemanticAttributes
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: NewProfileFirstFragment
 * Created by cliff on 2022/06/08.
 *
 * Description:
 */
@AndroidEntryPoint
class NewProfileFirstFragment :
    BaseFragment<FragmentNewProfileFirstBinding>(FragmentNewProfileFirstBinding::inflate) {

    lateinit var shareViewModel:ProfileSharedViewModel
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            shareViewModel.resetLiveData()
            requireActivity().backStack(R.id.nav_main)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shareViewModel = ViewModelProvider(requireActivity()).get(ProfileSharedViewModel::class.java)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = shareViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)

        with(shareViewModel) {

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            moveToSecond.observeEvent(viewLifecycleOwner) {
                val eventValue = 10f
                val eventAttributes = mutableMapOf<String, String>()
                val semanticAttributes = SemanticAttributes()
                Airbridge.trackEvent(
                    "airbridge.petprofile.make",
                    "petname_button_nextstep",
                    "petname_button_nextstep_label",
                    eventValue,
                    eventAttributes,
                    semanticAttributes.toMap()
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_newProfileFirstFragment_to_newProfileSecondFragment)
            }
        }

        setUI()
    }

    override fun onDestroyView() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        onBackPressedCallback.remove()
        super.onDestroyView()
    }

    private fun setUI() {
        with(binding) {

            if (shareViewModel.petName.value.toString().isNotEmpty()) {
                shareViewModel.petNameFocusHintVisible.value = true
            }

            maleFemale.initialize(
                shareViewModel.gender.value!!,
                object : MaleFemaleView.GenderChangeListener {
                    override fun onGenderChanged(gender: Int) {
                        shareViewModel.gender.value = gender
                        shareViewModel.petGender.value = if(gender == 1) {
                            "남"
                        } else {
                            "여"
                        }

                        outSide.clearFocus()
                    }
                })


            outSide.setOnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_UP -> view.let {
                        hideKeyboard()
                        outSide.clearFocus()
                    }
                }
                true
            }

            header.btnBack.setSafeOnClickListener {
                shareViewModel.resetLiveData()
                requireActivity().backStack(R.id.nav_main)
            }

            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onShowKeyboard = { keyboardHeight ->
                    scrollView.run {
                        smoothScrollTo(scrollX, editPetName.top)
                    }
                },
                onHideKeyboard = {
                    outSide.clearFocus()
                }
            )
        }
    }
}