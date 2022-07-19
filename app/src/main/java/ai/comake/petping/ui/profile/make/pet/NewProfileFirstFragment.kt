package ai.comake.petping.ui.profile.make.pet

import ai.comake.petping.*
import ai.comake.petping.databinding.FragmentNewProfileFirstBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.widget.MaleFemaleView
import ai.comake.petping.util.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.model.SemanticAttributes
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

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
        super.onDestroyView()
    }

    private fun setUI() {
        with(binding) {


            /**
             * 입력 후 다른화면 갔다가 다시 돌아왔을 때 TextInputLayout 세팅
             */
            if (shareViewModel.petName.value.toString() != "") {
                petNameWrapper.hint = "이름"
                petNameWrapper.endIconMode = TextInputLayout.END_ICON_NONE
                setTextInputLayoutHintColor(
                    petNameWrapper,
                    requireContext(),
                    R.color.greyscale_9_aaa
                )
            }

            maleFemale.initialize(
                shareViewModel.gender.value!!,
                object : MaleFemaleView.GenderChangeListener {
                    override fun onGenderChanged(gender: Int) {
                        shareViewModel.gender.value = gender
                        shareViewModel.petGender.value = if(gender == 1) {
                            "남자"
                        } else {
                            "여자"
                        }

                        outSide.clearFocus()
                    }
                })

            setEditText(
                requireContext(),
                petNameWrapper,
                petNameEdit,
                Pattern.compile(HANGUEL_PATTERN_NEW_FIX),
                "영문, 숫자, 특수문자는 사용할 수 없습니다.",
                "이름이 무엇인가요?",
                "이름",
                "최대 10자의 한글만 사용해 주세요."
            )

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
                        smoothScrollTo(scrollX, petNameWrapper.top)
                    }
                },
                onHideKeyboard = {
                    outSide.clearFocus()
                }
            )
        }
    }
}