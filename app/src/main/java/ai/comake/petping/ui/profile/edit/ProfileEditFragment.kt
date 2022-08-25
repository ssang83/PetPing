package ai.comake.petping.ui.profile.edit

import ai.comake.petping.*
import ai.comake.petping.databinding.FragmentProfileEditBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.DoubleBtnDialog
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.ui.common.widget.MaleFemaleView
import ai.comake.petping.ui.home.walk.WalkFragment
import ai.comake.petping.util.*
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

/**
 * android-petping-2
 * Class: ProfileEditFragment
 * Created by cliff on 2022/03/29.
 *
 * Description:
 */
@AndroidEntryPoint
class ProfileEditFragment :
    BaseFragment<FragmentProfileEditBinding>(FragmentProfileEditBinding::inflate) {

    private val viewModel: ProfileEditViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private val args:ProfileEditFragmentArgs by navArgs()

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    private val WRITE_EXTERNAL_STORAGE = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    /**
     * 이미지 요청 후 결과 처리
     */
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val data = activityResult.data
            viewModel.petProfileImage.value = data?.data?.toString()
            viewModel.needPhotoUpdated = true
        }
    }

    val permReqLuncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.entries.all { it.value }
        if (isGranted) {
            LogUtil.log("권한 있음")
            pickGallery()
        } else {
            LogUtil.log("권한 없음")
            SingleBtnDialog(
                requireContext(),
                getString(R.string.permission_error_title),
                getString(R.string.permission_error_desc),
            ) {}.show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            getBreedList()
            loadData(args.petId)

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            breedUpdated.observeEvent(viewLifecycleOwner) {
                updateBreedList()
            }

            petWalkableNot.observe(viewLifecycleOwner) { status ->
                if (status.not()) {
                    hideKeyboard()
                    binding.outSide.clearFocus()
                }
            }

            deletePopup.observeEvent(viewLifecycleOwner) {
                val description = if (profileType.value!! == 1) {
                    "반려견 프로필을 삭제하면 모든 산책 기록이 삭제되고 펫보험 연결도 해제됩니다."
                } else {
                    "반려견 프로필을 삭제하면 모든 산책 기록이 삭제됩니다."
                }
                DoubleBtnDialog(requireContext(), "프로필을 삭제하시겠어요?", description, okCallback = {
                    delete()
                }).show()
            }

            deleteErrorPopup.observeEvent(viewLifecycleOwner) { errorBody ->
                SingleBtnDialog(
                    requireContext(),
                    "프로필을 삭제할 수 없습니다.",
                    errorBody.message
                ) {}.show()
            }

            moveToBack.observeEvent(viewLifecycleOwner) {
                requireActivity().backStack(R.id.nav_main)
            }

            moveToHome.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_profileEditFragment_to_homeScreen)
            }

            disableInputByInsurance.observeEvent(viewLifecycleOwner) { genderValue ->
                disableInputByInsuraceUI(genderValue)
            }

            pickPicture.observeEvent(viewLifecycleOwner) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    requestPermission()
                } else {
                    pickGallery()
                }
            }

            birthErrorUI.observeEvent(viewLifecycleOwner) {
                binding.petBirthWrapper.error = "정확한 생년월일을 입력해 주세요."
            }
        }

        setUpUi()
    }

    override fun onDestroyView() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroyView()
    }

    private fun setUpUi() {
        with(binding) {

//            petNameWrapper.endIconMode = TextInputLayout.END_ICON_NONE
            dropDownAutoComplete.endIconMode = TextInputLayout.END_ICON_NONE
            petBirthWrapper.endIconMode = TextInputLayout.END_ICON_NONE
            petWeightWrapper.endIconMode = TextInputLayout.END_ICON_NONE

//            setTextInputLayoutHintColor(petNameWrapper, requireContext(), R.color.greyscale_9_aaa)
            setTextInputLayoutHintColor(dropDownAutoComplete, requireContext(), R.color.greyscale_9_aaa)
            setTextInputLayoutHintColor(petBirthWrapper, requireContext(), R.color.greyscale_9_aaa)
            setTextInputLayoutHintColor(petWeightWrapper, requireContext(), R.color.greyscale_9_aaa)

//            setEditText(
//                requireContext(),
//                petNameWrapper,
//                name,
//                Pattern.compile(HANGUEL_PATTERN_NEW),
//                "영문, 숫자, 특수문자는 사용할 수 없습니다.",
//                "이름이 무엇인가요?",
//                "이름",
//                "최대 10자의 한글만 사용해 주세요."
//            )
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
                birth,
                Pattern.compile(NUMBER_PATTERN),
                "정확한 생년월일을 입력해 주세요.",
                "YYMMDD 언제 태어났나요?",
                "생년월일",
                "예시) 2016년 5월 4일 -> 160504"
            )
            setEditText(
                requireContext(),
                petWeightWrapper,
                petWeightEdit,
                Pattern.compile(NUMBER_DOT_PATTERN),
                "숫자와 소수점만 입력할 수 있습니다.",
                "몸무게는 몇 kg인가요?",
                "몸무게 (kg)",
                "소수점 한 자리까지 숫자로 입력해 주세요."
            )

            maleFemale.initialize(-1, object : MaleFemaleView.GenderChangeListener {
                override fun onGenderChanged(gender: Int) {
                    viewModel?.gender?.value = gender
                }
            })

            /**
             * 몸무게 로직
             */
            petWeightEdit.filters = arrayOf<InputFilter>(MyDecimalDigitsInputFilter(1))

            /**
             * 줄바꿈 불가
             */
            val filterList = ArrayList<InputFilter>()
            filterList.addAll(reasonEditText.filters)
            filterList.add(InputFilter { source, _, _, _, _, _ ->
                if (source == null || !source.contains("\n")) {
                    source
                } else {
                    source.toString().replace("\n", "")
                }
            })
            reasonEditText.filters = filterList.toTypedArray()

            /**
             * 산책 불가 사유 입력 필드 포커스 변화에 따른 인터렉션 가이드 적용
             */
            reasonEditText.apply {
                setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.btn_outline_black
                        )
                        hint = ""
                    } else {
                        background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.btn_outline_ddd
                        )
                        hint = "산책 불가 사유를 알려 주세요. (최대 100자)"
                    }
                }
            }

            /**
             * 산책 불가 사유 입력창 비활성화 상태일 때 스위치 off후 on하면 활성화 및 텍스트 초기화
             */
            walkSwitch.setOnClickListener {
                if (reasonEditText.isEnabled == false) {
                    reasonEditText.isEnabled = true
                    viewModel?.reason?.value = ""
                }
            }

            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onShowKeyboard = { keyboardHeight ->
                    bottomButton.visibility = View.GONE
                })
            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onHideKeyboard = {
                    bottomButton.visibility = View.VISIBLE
                    outSide.clearFocus()
                })

            header.btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }

            outSide.setOnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_UP -> view.let {
                        hideKeyboard()
                        outSide.clearFocus()
                    }
                }
                true
            }
        }
    }

    private fun updateBreedList() {
        val autoCompleteView = binding.breed
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, viewModel.breedList?: listOf())
        autoCompleteView.setAdapter(adapter)
        autoCompleteView.setOnItemClickListener { parent, view, position, id ->
            hideKeyboard()
            binding.outSide.clearFocus()
        }
    }

    private fun pickGallery() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickImage.launch(intent)
    }

    private fun requestPermission() {
        when {
            hasPermission(requireContext(), WRITE_EXTERNAL_STORAGE) -> {
                LogUtil.log("이미 권한 있음")
                pickGallery()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                LogUtil.log("TAG", "한번 거절")
                permReqLuncher.launch(WRITE_EXTERNAL_STORAGE)
            }
            else -> {
                permReqLuncher.launch(WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    /**
     * 펫보험이 연결된 프로필일 때 textInputLayout 세팅
     */
    private fun disableInputByInsuraceUI(gender:Int) {
//        binding.name.isEnabled = false
        binding.maleFemale.setDisable(true)
        binding.maleFemale.setGender(gender)
        binding.breed.isEnabled = false
        binding.birth.isEnabled = false
//        binding.petNameWrapper.endIconMode = TextInputLayout.END_ICON_NONE
        binding.dropDownAutoComplete.endIconMode = TextInputLayout.END_ICON_NONE
        binding.petBirthWrapper.endIconMode = TextInputLayout.END_ICON_NONE
    }
}