package ai.comake.petping.ui.etc.inquiry

import ai.comake.petping.MainShareViewModel
import ai.comake.petping.R
import ai.comake.petping.UiState
import ai.comake.petping.databinding.FragmentInquiryBinding
import ai.comake.petping.emit
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SelectBottomDialogFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.*
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * android-petping-2
 * Class: InquriyFragment
 * Created by cliff on 2022/06/28.
 *
 * Description:
 */
@AndroidEntryPoint
class InquriyFragment : BaseFragment<FragmentInquiryBinding>(FragmentInquiryBinding::inflate) {

    private val viewModel: InquiryViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private lateinit var mAdapter:InquiryImageAdapter

    private val WRITE_EXTERNAL_STORAGE = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    /**
     * 이미지 요청 후 결과 처리
     */
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult() // ◀ StartActivityForResult 처리를 담당
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val data = activityResult.data
            viewModel.imageList.add(data?.data.toString())
            updateImage()
            viewModel.imageMargin.value = true
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getType()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        with(viewModel) {

            repeatOnStarted {
                eventFlow.collect { event -> handleEvent(event) }
                uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                        else -> mainShareViewModel.showPopUp.emit(false)
                    }
                }
            }
        }

        setUi()
    }

    override fun onDestroyView() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroyView()
    }

    private fun setUi() {
        with(binding) {

            setEditText(
                requireContext(),
                subjectWrapper,
                subjectEdit,
                "제목을 입력해 주세요",
                "문의 제목"
            )

            mAdapter = InquiryImageAdapter(viewModel!!)
            imageList.apply {
                layoutManager = LinearLayoutManager(requireContext()).apply { orientation = LinearLayoutManager.HORIZONTAL }
                itemAnimator = DefaultItemAnimator()
                adapter = mAdapter
            }

            inputInquiryType.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    SelectBottomDialogFragment(
                        "문의 유형 선택",
                        "확인",
                        viewModel?.inquiryList!!,
                        viewModel?.inquiryType?.value!!
                    ) { type ->
                        if (type.isNotEmpty()) {
                            viewModel?.inquiryType?.value = type
                            viewModel?.typeInputStatus?.value = true
                        } else {
                            viewModel?.typeInputStatus?.value = false
                        }
                    }.show(childFragmentManager, "")
                }
                true
            }

            otherReasonTextview.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    otherReasonTextview.apply {
                        background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_outline_black)
                        hint = ""
                    }
                } else {
                    otherReasonTextview.apply {
                        background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_outline_ddd)
                        hint = "문의 내용을 입력해 주세요. (최대 1,000자)"
                    }
                }
            }

            outSide.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        hideKeyboard()
                        outSide.clearFocus()
                    }
                }
                true
            }

            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onShowKeyboard = { keyboardHeight ->
                    scrollView.run {
                        smoothScrollTo(scrollX, subjectWrapper.top)
                    }
                    viewModel?.isBottomBtnShow?.value = false
                })
            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onHideKeyboard = {
                    viewModel?.isBottomBtnShow?.value = true
                    outSide.clearFocus()
                })

            header.btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }
        }
    }

    private fun updateImage() {
        mAdapter.addItem(viewModel.imageList)

        with(binding) {

            addImage.apply {
                isEnabled = viewModel?.imageList?.size!! < 3
                when {
                    viewModel?.imageList?.size!! < 3 -> {
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.greyscale_9_111))
                        background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_outline_666)
                    }
                    else -> {
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.greyscale_9_aaa))
                        background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_outline_ddd)
                    }
                }
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
            hasPermission(requireContext(), WRITE_EXTERNAL_STORAGE) -> {
                LogUtil.log("TAG","이미 권한 있음")
                imagePick()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                LogUtil.log("TAG","한번 거절")
                permReqLuncher.launch(WRITE_EXTERNAL_STORAGE)
            }
            else -> {
                permReqLuncher.launch(WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun handleEvent(event: InquiryViewModel.InquiryEvent) = when(event) {
        is InquiryViewModel.InquiryEvent.CloseScreen -> {
            requireActivity().backStack(R.id.nav_main)
        }
        is InquiryViewModel.InquiryEvent.UploadImage -> {
            updateImage()
        }
        is InquiryViewModel.InquiryEvent.PickPicture -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                requestPermission()
            } else {
                imagePick()
            }
        }
    }
}