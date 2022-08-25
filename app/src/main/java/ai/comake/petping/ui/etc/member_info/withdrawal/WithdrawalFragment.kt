package ai.comake.petping.ui.etc.member_info.withdrawal

import ai.comake.petping.*
import ai.comake.petping.databinding.FragmentWithdrawalBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.DoubleBtnDialog
import ai.comake.petping.ui.common.dialog.SelectBottomDialogFragment
import ai.comake.petping.util.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: WithdrawalFragment
 * Created by cliff on 2022/03/18.
 *
 * Description:
 */
@AndroidEntryPoint
class WithdrawalFragment :
    BaseFragment<FragmentWithdrawalBinding>(FragmentWithdrawalBinding::inflate) {

    private val viewModel: WithdrawalViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    private val loginType by lazy {
        arguments?.getInt("loginType") ?: -1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        LogUtil.log("loginType : $loginType")

        with(viewModel) {

            loadData()

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            selectReason.observeEvent(viewLifecycleOwner) {
                SelectBottomDialogFragment(
                    getString(R.string.withdrawal_reason_popup_title),
                    getString(R.string.confirm),
                    leaveType,
                    binding.inputLeaveSpinner.text.toString()
                ) { leaveString ->
                    if (leaveString.isNotEmpty()) {
                        binding.inputLeaveSpinner.setText(leaveString)
                        viewModel.reason.value = leaveString

                        if (leaveString.contains("직접 입력")) {
                            binding.otherReasonTextView.visibility = View.VISIBLE
                        } else {
                            binding.otherReasonTextView.visibility = View.GONE
                        }
                    }
                }.show(childFragmentManager, "")
            }

            moveToLogin.observeEvent(viewLifecycleOwner) {
                when(loginType) {
                    2 -> deleteKakaoToken()
                    3 -> deleteNaverToken()
                }

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_withdrawalFragment_to_loginGraph)
            }

            withdrawalPopup.observeEvent(viewLifecycleOwner) {
                DoubleBtnDialog(
                    requireContext(),
                    getString(R.string.withdrawal_popup_title),
                    getString(R.string.withdrawal_popup_desc),
                    cancelCallback = { requireActivity().backStack(R.id.nav_main) },
                    okCallback = { goToWithdrawal() }
                ).show()
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
            otherReasonTextView.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    otherReasonTextView.background = requireActivity().getDrawable(R.drawable.btn_outline_black)
                    otherReasonTextView.hint = ""
                } else {
                    otherReasonTextView.background = requireActivity().getDrawable(R.drawable.btn_outline_ddd)
                    otherReasonTextView.hint = "사유를 입력해 주세요. (최대 1,000자)"
                }
            }

            otherReasonTextView.setOnTouchListener { v, event ->
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        true
                    }
                }
                false
            }

            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onHideKeyboard = {
                    outSide.clearFocus()
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
                requireActivity().backStack(R.id.nav_main)
            }
        }
    }

    private fun deleteNaverToken() {
        NidOAuthLogin().callDeleteTokenApi(requireContext(), object : OAuthLoginCallback {
            override fun onSuccess() {
                //서버에서 토큰 삭제에 성공한 상태입니다.
            }

            override fun onFailure(httpStatus: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                LogUtil.log("errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                LogUtil.log("errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}")
            }

            override fun onError(errorCode: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                onFailure(errorCode, message)
            }
        })
    }

    private fun deleteKakaoToken() {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                LogUtil.log("연결 끊기 실패, $error")
            } else {
                LogUtil.log("연결 끊기 성고. SDK에서 토큰 삭제 됨")
            }
        }
    }
}