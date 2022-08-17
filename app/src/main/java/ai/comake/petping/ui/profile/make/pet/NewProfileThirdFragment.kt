package ai.comake.petping.ui.profile.make.pet

import ai.comake.petping.*
import ai.comake.petping.databinding.FragmentNewProfileThirdBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
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
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

/**
 * android-petping-2
 * Class: NewProfileThirdFragment
 * Created by cliff on 2022/06/08.
 *
 * Description:
 */
@AndroidEntryPoint
class NewProfileThirdFragment :
    BaseFragment<FragmentNewProfileThirdBinding>(FragmentNewProfileThirdBinding::inflate) {

    lateinit var sharedViewModel:ProfileSharedViewModel
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils


    /**
     * 이미지 요청 후 결과 처리
     */
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult() // ◀ StartActivityForResult 처리를 담당
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val data = activityResult.data
            sharedViewModel.imageSrc.value = data?.data?.toString()
        }
    }

    val permReqLuncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.entries.all { it.value }
        if (isGranted) {
            LogUtil.log("권한 있음")
            imagePick()
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
        sharedViewModel = ViewModelProvider(requireActivity()).get(ProfileSharedViewModel::class.java)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = sharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(sharedViewModel) {

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            imagePick.observeEvent(viewLifecycleOwner) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    requestPermission()
                } else {
                    imagePick()
                }
            }

            moveToHome.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_profile_to_home)
            }

            moveToSecond.observeEvent(viewLifecycleOwner) {
                requireActivity().backStack(R.id.nav_main)
            }

            moveToFirst.observeEvent(viewLifecycleOwner) {
                requireActivity().multipleBackStack(R.id.nav_main, R.id.newProfileFirstFragment)
            }

            moveToMissionPet.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_newProfileThirdFragment_to_missionPetGuideFragment)
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

            if (sharedViewModel.weight.value.toString().isNotEmpty()) {
                sharedViewModel.weightFocusHintVisible.value = true
            }

            /**
             * 몸무게 숫자 3자리 입력시 숫자 입력 불가
             * 몸무게 숫자 3자리 + 소수점 1자리 입력 시 입력 불가
             * 커서 앞으로 이동하여 지우고 수정 가능
             */
            editWeight.filters = arrayOf<InputFilter>(MyDecimalDigitsInputFilter(1))
            editWeight.setOnEditorActionListener { v, actionId, event ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard()
                    outSide.clearFocus()
                    handled = true
                }
                handled
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

            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onShowKeyboard = { keyboardHeight ->
                    scrollView.run {
                        smoothScrollTo(scrollX, editWeight.top)
                    }
                },
                onHideKeyboard = {
                    outSide.clearFocus()
                }
            )

            header.btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }
        }
    }

    private fun imagePick() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickImage.launch(intent)
    }

    private fun requestPermission() {
        when {
            hasPermission(requireContext(), STORAGE_PERMISSION) -> {
                LogUtil.log("이미 권한 있음")
                imagePick()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                LogUtil.log("한번 거절")
                permReqLuncher.launch(STORAGE_PERMISSION)
            }
            else -> {
                permReqLuncher.launch(STORAGE_PERMISSION)
            }
        }
    }
}