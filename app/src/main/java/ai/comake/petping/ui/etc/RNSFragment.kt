package ai.comake.petping.ui.etc

import ai.comake.petping.*
import ai.comake.petping.databinding.FragmentRnsBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.*
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

/**
 * android-petping-2
 * Class: RNSFragment
 * Created by cliff on 2022/03/21.
 *
 * Description:
 */
@AndroidEntryPoint
class RNSFragment : BaseFragment<FragmentRnsBinding>(FragmentRnsBinding::inflate) {

    private val viewModel by viewModels<RNSViewModel>()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private val args:RNSFragmentArgs by navArgs()

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            petId = args.petId

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            moveToMissionPetScreen.observeEvent(viewLifecycleOwner) {
                setNavigationResult("after_register_pet_number", "key")
                requireActivity().backStack(R.id.nav_main)
            }

            showRNSSaveErrorPopup.observeEvent(viewLifecycleOwner) { errorBody ->
                when (errorBody.code) {
                    "C2050" -> {
                        SingleBtnDialog(
                            requireContext(),
                            "저장할 수 없습니다.",
                            errorBody.message
                        ).show()
                    }
                    "C2100", "C2060" -> {
                        SingleBtnDialog(
                            requireContext(),
                            "저장할 수 없습니다.",
                            errorBody.message
                        ).show()
                    }
                    else -> {
                        SingleBtnDialog(
                            requireContext(),
                            "저장할 수 없습니다.",
                            errorBody.message
                        ).show()
                    }
                }
            }

            moveToOutLink.observeEvent(viewLifecycleOwner) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.animal.go.kr"))
                startActivity(intent)
            }

            helperTextUpdate.observeEvent(viewLifecycleOwner) { text ->
                binding.familyCodeWrapper.helperText = text
            }
        }

        setUpUi()
    }

    override fun onResume() {
        super.onResume()
        if (binding.familyCodeEdit.hasFocus() == true) {
            showKeyboardOnView(binding.familyCodeEdit)
        }
    }

    override fun onDestroyView() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroyView()
    }

    private fun setUpUi() {
        with(binding) {

            setEditText(
                requireContext(),
                familyCodeWrapper,
                familyCodeEdit,
                Pattern.compile(NUMBER_PATTERN),
                "숫자만 입력할 수 있습니다.",
                "동물등록번호를 입력하세요",
                "동물등록번호",
                "15자리의 숫자를 입력해 주세요."
            )

            setEditText(
                requireContext(),
                nameWrapper,
                nameEdit,
                Pattern.compile(CERTIFICATION_NAME_PATTERN),
                "정확한 이름을 입력해 주세요.",
                "보호자 이름을 입력하세요",
                "보호자 이름",
                "동물 등록을 신청한 보호자의 이름을 입력해 주세요."
            )

            /**
             * 키보드가 내려갈 때 clear focus, 15자리 미만이면 숫자인지 체크 후 에러메시지
             */
            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onHideKeyboard = {
                    binding.outSide.clearFocus()
                    if (viewModel?.registerNumber?.value.toString().length < 15) {
                        viewModel?.isValidRNS?.value = false
                        if (Pattern.compile(NUMBER_PATTERN).matcher(viewModel?.registerNumber?.value.toString()).matches()) {
                            familyCodeWrapper.error = "동물등록번호를 확인해 주세요."
                        }
                    }
                }
            )

            header.btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }
        }
    }
}