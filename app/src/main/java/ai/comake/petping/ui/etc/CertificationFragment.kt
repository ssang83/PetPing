package ai.comake.petping.ui.etc

import ai.comake.petping.*
import ai.comake.petping.data.vo.CIConfig
import ai.comake.petping.data.vo.CertWebConfig
import ai.comake.petping.databinding.FragmentCertificationBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.*
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

/**
 * android-petping-2
 * Class: CertficationFragment
 * Created by cliff on 2022/03/21.
 *
 * Description:
 */
@AndroidEntryPoint
class CertificationFragment :
    BaseFragment<FragmentCertificationBinding>(FragmentCertificationBinding::inflate) {

    private val viewModel by viewModels<CertificationViewModel>()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        getNavigationResultLiveData<CIConfig>("key")?.observe(viewLifecycleOwner) {
            if (it != null) {
                handelPhoneCert(it)
            }
        }

        with(viewModel) {

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            moveToNext.observeEvent(viewLifecycleOwner) {
                requireActivity().backStack(R.id.nav_main)
            }

            moveToCetWeb.observeEvent(viewLifecycleOwner) {
                val config = CertWebConfig(
                    name = name.value.toString(),
                    birth = id.value.toString().substring(0,6),
                    gender = id.value.toString().substring(6)
                )

                requireActivity().findNavController(R.id.nav_main).navigate(
                    CertificationFragmentDirections.actionCertificationFragmentToCertWebFragment(
                        config
                    )
                )
            }

            phoneAuthSuccess.observeEvent(viewLifecycleOwner) {
                binding.phoneCert.text = "휴대폰 본인인증 완료"
                binding.tvGuideText.visibility = View.GONE
                binding.phoneCert.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.greyscale_g_5_bbb
                    )
                )
                binding.phoneCert.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_outline)
                binding.personalId.disableAll()
                binding.nameWrapper.endIconMode = TextInputLayout.END_ICON_NONE
            }

            phoneAuthFail.observeEvent(viewLifecycleOwner) { errorBody ->
                SingleBtnDialog(
                    requireContext(),
                    "본인 확인 오류",
                    errorBody.message,
                    btnCallback = {
                        phoneNumber.value = ""
                        name.value = ""
                        binding.personalId.clearAll()

                        // 타이밍을 주지 않으면 nameEdit에 바로 포커스가 가지 않음
                        // 원인을 모르겠음....다니엘 알면 도와줘요...ㅠㅠ
                        Handler(requireContext().mainLooper).postDelayed({
                            binding.nameEdit.setText("")
                            binding.nameEdit.requestFocus()
                            showKeyboardOnView(binding.nameEdit)
                        }, 100)
                    }
                ).show()
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

            setEditText(
                requireContext(),
                nameWrapper,
                nameEdit,
                Pattern.compile(CERTIFICATION_NAME_PATTERN),
                "정확한 이름을 입력해 주세요.",
                "이름을 입력하세요",
                "이름",
                "본인 이름을 입력해 주세요."
            )

            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onHideKeyboard = {
                    outSide.clearFocus()
                })
        }
    }

    private fun handelPhoneCert(data:CIConfig) {
        with(viewModel) {

            phoneNumber.value = data.phoneNumber
            ci = data.ci
            viewModel.id

            // send certification data to server
            memberPhoneAuth()
        }
    }
}