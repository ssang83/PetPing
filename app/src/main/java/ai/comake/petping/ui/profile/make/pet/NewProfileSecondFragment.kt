package ai.comake.petping.ui.profile.make.pet

import ai.comake.petping.*
import ai.comake.petping.databinding.FragmentNewProfileSecondBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.util.*
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.model.SemanticAttributes
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

/**
 * android-petping-2
 * Class: NewProfileSecondFragment
 * Created by cliff on 2022/06/08.
 *
 * Description:
 */
@AndroidEntryPoint
class NewProfileSecondFragment :
    BaseFragment<FragmentNewProfileSecondBinding>(FragmentNewProfileSecondBinding::inflate) {

    lateinit var sharedViewModel:ProfileSharedViewModel
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private lateinit var mAdapter: FilterAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(ProfileSharedViewModel::class.java)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = sharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(sharedViewModel) {

            getBreedList()

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            moveToFirst.observeEvent(viewLifecycleOwner) {
                requireActivity().backStack(R.id.nav_main)
            }

            moveToLast.observeEvent(viewLifecycleOwner) {
                val eventValue = 10f
                val eventAttributes = mutableMapOf<String, String>()
                val semanticAttributes = SemanticAttributes()
                Airbridge.trackEvent(
                    "airbridge.petprofile.make",
                    "petbirth_button_nextstep",
                    "petbirth_button_nextstep_label",
                    eventValue,
                    eventAttributes,
                    semanticAttributes.toMap()
                )

                petAge.value = birth.value.toString().toAge().replace("세", "살")

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_newProfileSecondFragment_to_newProfileThirdFragment)
            }

            birthErrorUI.observeEvent(viewLifecycleOwner) { errorTxt ->
                binding.petBirthWrapper.error = errorTxt
            }

            clickBreed.observeEvent(viewLifecycleOwner) { breed ->
                binding.breed.setText(breed)
                binding.filterList.visibility = View.GONE
                binding.dropDownAutoComplete.isEndIconVisible = false
                hideKeyboard()
                binding.outSide.clearFocus()
            }

            breedUpdate.observeEvent(viewLifecycleOwner) {
                updateBreedList()
            }
        }

        setUI()
    }

    override fun onDestroyView() {
//        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroyView()
    }

    private fun setUI() {
        with(binding) {

            /**
             * 입력 후 다른화면 갔다가 다시 돌아왔을 때 TextInputLayout 세팅
             */
            if (sharedViewModel.breed.value.toString().isNotEmpty()) {
                dropDownAutoComplete.hint = "종류"
                dropDownAutoComplete.endIconMode = TextInputLayout.END_ICON_NONE
                setTextInputLayoutHintColor(
                    dropDownAutoComplete,
                    requireContext(),
                    R.color.greyscale_9_aaa
                )
            }

            if (sharedViewModel.birth.value.toString().isNotEmpty()) {
                petBirthWrapper.hint = "생년월일"
                petBirthWrapper.endIconMode = TextInputLayout.END_ICON_NONE
                setTextInputLayoutHintColor(
                    petBirthWrapper,
                    requireContext(),
                    R.color.greyscale_9_aaa
                )
            }

            setEditText(
                requireContext(),
                dropDownAutoComplete,
                breed,
                Pattern.compile(HANGUEL_PATTERN_ADD_SPACE),
                "영문, 숫자, 특수문자는 입력할 수 없습니다.",
                "어떤 종류인가요?",
                "종류"
            )

            setEditText(
                requireContext(),
                petBirthWrapper,
                petBirthEdit,
                Pattern.compile(NUMBER_PATTERN),
                "숫자만 입력할 수 있습니다.",
                "YYMMDD 언제 태어났나요?",
                "생년월일(6자리 숫자)",
                "생년월일은 언제든 수정할 수 있어요."
            )

            breed.setOnEditorActionListener { v, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_NEXT -> {
                        LogUtil.log("IME_ACTION_NEXT clicked!!")
                        true
                    }

                    else -> Unit
                }
                false
            }

            outSide.setOnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_UP -> view.let {
                        hideKeyboard()
                        outSide.clearFocus()
                        binding.filterList.visibility = View.GONE
                    }
                }
                true
            }

            header.btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }

//            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
//                onShowKeyboard = {
//                    scrollView.run {
//                        smoothScrollTo(scrollX, dropDownAutoComplete.top)
//                    }
//                    bottomButton.visibility = View.GONE
//                },
//                onHideKeyboard = {
//                    bottomButton.visibility = View.VISIBLE
//                    outSide.clearFocus()
//                })
        }
    }

    private fun updateBreedList() {
        mAdapter = FilterAdapter(sharedViewModel, sharedViewModel.breedList)

        with(binding) {
            filterList.apply {
                adapter = mAdapter
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
            }

            breed.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    mAdapter.filter.filter(s.toString())
                    filterList.visibility = View.VISIBLE
                }
            })
        }
    }
}